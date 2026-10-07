package br.com.patinhas.service;

import br.com.patinhas.entity.ProdutoEstoque;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.repository.CategoriaEstoqueRepository;
import br.com.patinhas.repository.ProdutoEstoqueRepository;
import br.com.patinhas.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
                .orElseThrow(() ->
                        new BusinessException(
                                "Produto de estoque não encontrado."
                        ));
    }

    @Transactional
    public void cadastrar(
            ProdutoEstoque produto,
            Long categoriaId,
            Long unidadeId
    ) {

        validarEstoque(produto);

        produto.setCategoria(
                categoriaRepository.findById(categoriaId)
                        .orElseThrow(() ->
                                new BusinessException("Categoria inválida."))
        );

        produto.setUnidade(
                unidadeRepository.findById(unidadeId)
                        .orElseThrow(() ->
                                new BusinessException("Unidade inválida."))
        );

        // Regra inicial: todo produto começa com estoque zero
        produto.setEstoqueAtual(BigDecimal.ZERO);

        produtoRepository.save(produto);
    }

    @Transactional
    public void atualizar(
            Long id,
            ProdutoEstoque dados,
            Long categoriaId,
            Long unidadeId
    ) {

        ProdutoEstoque produto = buscarPorId(id);

        validarEstoque(dados);

        produto.setNome(dados.getNome().trim());
        produto.setEstoqueMinimo(dados.getEstoqueMinimo());
        produto.setEstoqueMaximo(dados.getEstoqueMaximo());

        produto.setCategoria(
                categoriaRepository.findById(categoriaId)
                        .orElseThrow(() ->
                                new BusinessException("Categoria inválida."))
        );

        produto.setUnidade(
                unidadeRepository.findById(unidadeId)
                        .orElseThrow(() ->
                                new BusinessException("Unidade inválida."))
        );

        // IMPORTANTE:
        // não alteramos estoqueAtual aqui.

        produtoRepository.save(produto);
    }

    private void validarEstoque(ProdutoEstoque produto) {

        if (produto.getEstoqueMinimo() == null ||
                produto.getEstoqueMaximo() == null) {

            throw new BusinessException(
                    "Informe estoque mínimo e máximo."
            );
        }

        if (produto.getEstoqueMinimo().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    "O estoque mínimo não pode ser negativo."
            );
        }

        if (produto.getEstoqueMaximo().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    "O estoque máximo não pode ser negativo."
            );
        }

        if (produto.getEstoqueMinimo()
                .compareTo(produto.getEstoqueMaximo()) > 0) {

            throw new BusinessException(
                    "O estoque mínimo não pode ser maior que o máximo."
            );
        }
    }
}