package br.com.patinhas.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.patinhas.dto.request.ProdutoEstoqueRequestDTO;
import br.com.patinhas.entity.CategoriaEstoque;
import br.com.patinhas.entity.ProdutoEstoque;
import br.com.patinhas.entity.UnidadeMedida;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.CategoriaEstoqueRepository;
import br.com.patinhas.repository.ProdutoEstoqueRepository;
import br.com.patinhas.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoEstoqueService {

    private final ProdutoEstoqueRepository produtoRepository;
    private final CategoriaEstoqueRepository categoriaRepository;
    private final UnidadeMedidaRepository unidadeRepository;
    private final MovimentacaoEstoqueService movimentacaoService;

    @Transactional(readOnly = true)
    public List<ProdutoEstoque> listarTodos() {
        return produtoRepository.findAllComRelacionamentos();
    }

    @Transactional(readOnly = true)
    public ProdutoEstoque buscarPorId(Long id) {
        return produtoRepository.findByIdComRelacionamentos(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto de estoque", id));
    }

    /**
     * Lista da tela de produtos: saldo das movimentações na memória e ordem do semáforo.
     * Sem @Transactional de propósito. O saldo é preenchido depois que a leitura fecha,
     * para não gravar o cache de estoque_atual.
     */
    public List<ProdutoEstoque> listarParaSemaforo() {
        List<ProdutoEstoque> produtos = new ArrayList<>(produtoRepository.findAllComRelacionamentos());
        preencherSaldos(produtos);
        produtos.sort(Comparator
                .comparingInt(ProdutoEstoqueService::prioridadeSemaforo)
                .thenComparing(ProdutoEstoque::getNome, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return produtos;
    }

    /** Grava o saldo calculado só na memória, depois que a transação de leitura já fechou. */
    public void preencherSaldos(List<ProdutoEstoque> produtos) {
        Map<Long, BigDecimal> saldos = movimentacaoService.saldosPorProduto();
        for (ProdutoEstoque produto : produtos) {
            produto.setEstoqueAtual(saldos.getOrDefault(produto.getId(), BigDecimal.ZERO));
        }
    }

    public void preencherSaldo(ProdutoEstoque produto) {
        produto.setEstoqueAtual(movimentacaoService.saldoDoProduto(produto.getId()));
    }

    @Transactional(readOnly = true)
    public long contarProdutos() {
        return produtoRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarCriticos() {
        Map<Long, BigDecimal> saldos = movimentacaoService.saldosPorProduto();
        return produtoRepository.findAllComRelacionamentos().stream()
                .filter(produto -> saldoCritico(produto, saldos.getOrDefault(produto.getId(), BigDecimal.ZERO)))
                .count();
    }

    @Transactional
    public void salvar(Long id, ProdutoEstoqueRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Os dados do produto não foram informados.");
        }

        String nome = normalizarNome(dto.getNome());
        validarFaixa(dto.getEstoqueMinimo(), dto.getEstoqueMaximo());

        produtoRepository.findByNomeNormalizado(nome).ifPresent(existente -> {
            if (id == null || !existente.getId().equals(id)) {
                throw new BusinessException(
                        "Já existe um produto com esse nome. Para mudar o saldo, registre uma movimentação.");
            }
        });

        CategoriaEstoque categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new BusinessException("A categoria selecionada não existe."));
        UnidadeMedida unidade = unidadeRepository.findById(dto.getUnidadeId())
                .orElseThrow(() -> new BusinessException("A unidade selecionada não existe."));

        ProdutoEstoque produto = id == null ? new ProdutoEstoque() : buscarPorId(id);
        produto.setNome(nome);
        produto.setCategoria(categoria);
        produto.setUnidade(unidade);
        produto.setEstoqueMinimo(dto.getEstoqueMinimo());
        produto.setEstoqueMaximo(dto.getEstoqueMaximo());

        if (produto.getEstoqueAtual() == null) {
            produto.setEstoqueAtual(BigDecimal.ZERO);
        }

        produtoRepository.saveAndFlush(produto);

        // Edição não mexe em saldo. Quantidade inicial, se vier, é a primeira entrada.
        if (id == null) {
            movimentacaoService.registrarQuantidadeInicial(produto.getId(), dto.getQuantidadeInicial());
        }
    }

    @Transactional
    public void excluir(Long id) {
        buscarPorId(id);
        if (movimentacaoService.existeMovimentacao(id)) {
            throw new BusinessException(
                    "Não é possível excluir o produto porque ele já tem movimentação. O histórico é o saldo.");
        }
        produtoRepository.deleteById(id);
    }

    private boolean saldoCritico(ProdutoEstoque produto, BigDecimal saldo) {
        return saldo != null && produto.getEstoqueMinimo() != null
                && saldo.compareTo(produto.getEstoqueMinimo()) <= 0;
    }

    private static int prioridadeSemaforo(ProdutoEstoque produto) {
        return switch (produto.getStatusEstoque()) {
            case "CRITICO" -> 0;
            case "ATENCAO" -> 1;
            case "NORMAL" -> 2;
            case "EXCEDENTE" -> 3;
            default -> 4;
        };
    }

    private void validarFaixa(BigDecimal minimo, BigDecimal maximo) {
        if (minimo == null || maximo == null) {
            throw new BusinessException("Informe o estoque mínimo e o estoque máximo.");
        }
        if (minimo.compareTo(BigDecimal.ZERO) < 0 || maximo.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Os limites de estoque não podem ser negativos.");
        }
        if (maximo.compareTo(minimo) < 0) {
            throw new BusinessException("O estoque máximo deve ser maior ou igual ao estoque mínimo.");
        }
    }

    private String normalizarNome(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("O nome do produto é obrigatório e não pode conter apenas espaços.");
        }
        return valor.trim();
    }
}
