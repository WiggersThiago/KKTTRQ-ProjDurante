package br.com.patinhas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.patinhas.dto.request.CategoriaEstoqueRequestDTO;
import br.com.patinhas.entity.CategoriaEstoque;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.CategoriaEstoqueRepository;
import br.com.patinhas.repository.ProdutoEstoqueRepository;
import lombok.RequiredArgsConstructor;

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
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de estoque", id));
    }

    @Transactional
    public void salvar(Long id, CategoriaEstoqueRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Os dados da categoria não foram informados.");
        }
        String nome = normalizarNome(dto.getNome());
        categoriaRepository.findByNomeNormalizado(nome).ifPresent(existente -> {
            if (id == null || !existente.getId().equals(id)) {
                throw new BusinessException("Já existe uma categoria com esse nome.");
            }
        });

        CategoriaEstoque categoria = id == null ? new CategoriaEstoque() : buscarPorId(id);
        categoria.setNome(nome);
        categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        buscarPorId(id);
        if (produtoRepository.existsByCategoriaId(id)) {
            throw new BusinessException("Não é possível excluir a categoria porque ela está sendo usada por um produto.");
        }
        categoriaRepository.deleteById(id);
    }

    private String normalizarNome(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("O nome da categoria é obrigatório e não pode conter apenas espaços.");
        }
        return valor.trim();
    }
}
