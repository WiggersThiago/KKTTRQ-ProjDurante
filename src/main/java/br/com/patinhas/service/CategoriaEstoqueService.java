package br.com.patinhas.service;

import br.com.patinhas.entity.CategoriaEstoque;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.repository.CategoriaEstoqueRepository;
import br.com.patinhas.repository.ProdutoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaEstoqueService {

    private final CategoriaEstoqueRepository categoriaRepository;
    private final ProdutoEstoqueRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaEstoque> listarTodas() {
        return categoriaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public CategoriaEstoque buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException("Categoria não encontrada."));
    }

    @Transactional
    public void salvar(CategoriaEstoque categoria) {

        String nome = categoria.getNome().trim();

        categoriaRepository.findByNomeIgnoreCase(nome)
                .ifPresent(existente -> {
                    if (categoria.getId() == null ||
                            !existente.getId().equals(categoria.getId())) {

                        throw new BusinessException(
                                "Já existe uma categoria com esse nome."
                        );
                    }
                });

        categoria.setNome(nome);

        categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluir(Long id) {

        if (produtoRepository.existsByCategoriaId(id)) {
            throw new BusinessException(
                    "Não é possível excluir uma categoria que possui produtos."
            );
        }

        categoriaRepository.deleteById(id);
    }
}