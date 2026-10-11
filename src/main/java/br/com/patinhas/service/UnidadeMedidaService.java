package br.com.patinhas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.patinhas.dto.request.UnidadeMedidaRequestDTO;
import br.com.patinhas.entity.UnidadeMedida;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.ProdutoEstoqueRepository;
import br.com.patinhas.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadeMedidaService {

    private final UnidadeMedidaRepository unidadeRepository;
    private final ProdutoEstoqueRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<UnidadeMedida> listarTodas() {
        return unidadeRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public UnidadeMedida buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade de medida", id));
    }

    @Transactional
    public void salvar(Long id, UnidadeMedidaRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Os dados da unidade não foram informados.");
        }
        String nome = normalizarNome(dto.getNome());
        unidadeRepository.findByNomeNormalizado(nome).ifPresent(existente -> {
            if (id == null || !existente.getId().equals(id)) {
                throw new BusinessException("Já existe uma unidade com esse nome.");
            }
        });

        UnidadeMedida unidade = id == null ? new UnidadeMedida() : buscarPorId(id);
        unidade.setNome(nome);
        unidadeRepository.save(unidade);
    }

    @Transactional
    public void excluir(Long id) {
        buscarPorId(id);
        if (produtoRepository.existsByUnidadeId(id)) {
            throw new BusinessException("Não é possível excluir a unidade porque ela está sendo usada por um produto.");
        }
        unidadeRepository.deleteById(id);
    }

    private String normalizarNome(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("O nome da unidade é obrigatório e não pode conter apenas espaços.");
        }
        return valor.trim();
    }
}
