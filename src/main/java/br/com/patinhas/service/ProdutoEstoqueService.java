package br.com.patinhas.service;

import java.math.BigDecimal;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<ProdutoEstoque> listarTodos() {
        return produtoRepository.findAllComRelacionamentos();
    }

    @Transactional(readOnly = true)
    public ProdutoEstoque buscarPorId(Long id) {
        return produtoRepository.findByIdComRelacionamentos(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto de estoque", id));
    }

    @Transactional(readOnly = true)
    public long contarProdutos() {
        return produtoRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarAbaixoDoMinimo() {
        return produtoRepository.countAbaixoDoMinimo();
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
                        "Já existe um produto com esse nome. Não foi criado outro produto. " +
                        "Para somar quantidade ao produto existente, use 'Registrar entrada'.");
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

        // O saldo atual nunca vem do formulário de cadastro/edição.
        // Produto novo começa em zero; edição preserva o saldo existente.
        if (produto.getEstoqueAtual() == null) {
            produto.setEstoqueAtual(BigDecimal.ZERO);
        }

        produtoRepository.save(produto);
    }

    /** Soma uma entrada ao produto já cadastrado, usando nome sem diferenciar caixa/espaços. */
    @Transactional
    public ProdutoEstoque registrarEntradaPorNome(String nomeInformado, BigDecimal quantidade) {
        String nome = normalizarNome(nomeInformado);

        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("A quantidade de entrada deve ser maior que zero.");
        }

        ProdutoEstoque produto = produtoRepository.findByNomeNormalizado(nome)
                .orElseThrow(() -> new BusinessException(
                        "Nenhum produto foi encontrado com esse nome. Cadastre o produto primeiro; " +
                        "a entrada não cria produtos automaticamente."));

        BigDecimal saldoAtual = produto.getEstoqueAtual() == null
                ? BigDecimal.ZERO : produto.getEstoqueAtual();
        produto.setEstoqueAtual(saldoAtual.add(quantidade));
        return produtoRepository.save(produto);
    }

    @Transactional
    public void excluir(Long id) {
        buscarPorId(id);
        produtoRepository.deleteById(id);
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
