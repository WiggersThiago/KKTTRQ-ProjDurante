package br.com.patinhas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.patinhas.dto.request.MovimentacaoEstoqueRequestDTO;
import br.com.patinhas.entity.MovimentacaoEstoque;
import br.com.patinhas.entity.ProdutoEstoque;
import br.com.patinhas.entity.Usuario;
import br.com.patinhas.entity.enums.TipoMovimentacaoEstoque;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.repository.MovimentacaoEstoqueRepository;
import br.com.patinhas.repository.ProdutoEstoqueRepository;
import br.com.patinhas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoEstoqueRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> listar(Long produtoId) {
        if (produtoId == null) {
            return movimentacaoRepository.listarTodas();
        }
        return movimentacaoRepository.listarPorProduto(produtoId);
    }

    @Transactional(readOnly = true)
    public boolean existeMovimentacao(Long produtoId) {
        return movimentacaoRepository.existsByProdutoId(produtoId);
    }

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> saldosPorProduto() {
        Map<Long, BigDecimal> saldos = new HashMap<>();
        for (Object[] linha : movimentacaoRepository.quantidades()) {
            Long produtoId = (Long) linha[0];
            saldos.merge(produtoId, delta((TipoMovimentacaoEstoque) linha[1], (BigDecimal) linha[2]), BigDecimal::add);
        }
        return saldos;
    }

    @Transactional(readOnly = true)
    public BigDecimal saldoDoProduto(Long produtoId) {
        return somar(movimentacaoRepository.quantidadesDoProduto(produtoId));
    }

    @Transactional
    public MovimentacaoEstoque registrar(MovimentacaoEstoqueRequestDTO dto) {
        validar(dto);

        ProdutoEstoque produto = produtoRepository.findByIdComBloqueio(dto.getProdutoId())
                .orElseThrow(() -> new BusinessException("O produto selecionado não existe."));

        if (dto.getDataMovimentacao().isAfter(LocalDate.now())) {
            throw new BusinessException("A data da movimentação não pode ser futura.");
        }

        BigDecimal saldo = saldoDoProduto(produto.getId());
        BigDecimal efeito = delta(dto.getTipo(), dto.getQuantidade());
        BigDecimal novoSaldo = saldo.add(efeito);

        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    "Saída recusada. O saldo de " + produto.getNome() + " é " + numero(saldo) +
                    " e a saída de " + numero(dto.getQuantidade()) + " deixaria o estoque negativo.");
        }

        MovimentacaoEstoque movimentacao = movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .produto(produto)
                .usuario(usuarioLogado())
                .tipo(dto.getTipo())
                .quantidade(dto.getQuantidade())
                .dataMovimentacao(dto.getDataMovimentacao())
                .observacao(normalizarObservacao(dto.getObservacao()))
                .build());

        produto.setEstoqueAtual(novoSaldo);
        return movimentacao;
    }

    /** Quantidade informada no cadastro do produto. Vira entrada de hoje, não um saldo digitado. */
    @Transactional
    public void registrarQuantidadeInicial(Long produtoId, BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        MovimentacaoEstoqueRequestDTO dto = new MovimentacaoEstoqueRequestDTO();
        dto.setTipo(TipoMovimentacaoEstoque.ENTRADA);
        dto.setProdutoId(produtoId);
        dto.setQuantidade(quantidade);
        dto.setDataMovimentacao(LocalDate.now());
        dto.setObservacao("Quantidade inicial do cadastro");
        registrar(dto);
    }

    private void validar(MovimentacaoEstoqueRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Os dados da movimentação não foram informados.");
        }
        if (dto.getTipo() == null) {
            throw new BusinessException("Informe se é entrada ou saída.");
        }
        if (dto.getProdutoId() == null) {
            throw new BusinessException("Selecione o produto.");
        }
        if (dto.getQuantidade() == null || dto.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("A quantidade deve ser maior que zero.");
        }
        if (dto.getDataMovimentacao() == null) {
            throw new BusinessException("Informe a data da movimentação.");
        }
    }

    private BigDecimal somar(List<Object[]> linhas) {
        BigDecimal saldo = BigDecimal.ZERO;
        for (Object[] linha : linhas) {
            saldo = saldo.add(delta((TipoMovimentacaoEstoque) linha[0], (BigDecimal) linha[1]));
        }
        return saldo;
    }

    private BigDecimal delta(TipoMovimentacaoEstoque tipo, BigDecimal quantidade) {
        return tipo == TipoMovimentacaoEstoque.ENTRADA ? quantidade : quantidade.negate();
    }

    private String normalizarObservacao(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private Usuario usuarioLogado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BusinessException("Não foi possível identificar quem está movimentando o estoque.");
        }
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BusinessException(
                        "Usuário logado não encontrado para registrar a movimentação."));
    }

    private String numero(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString().replace('.', ',');
    }
}
