package br.com.patinhas.service;

import br.com.patinhas.entity.UnidadeMedida;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.repository.ProdutoEstoqueRepository;
import br.com.patinhas.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .orElseThrow(() ->
                        new BusinessException("Unidade não encontrada."));
    }

    @Transactional
    public void salvar(UnidadeMedida unidade) {

        String nome = unidade.getNome().trim();

        unidadeRepository.findByNomeIgnoreCase(nome)
                .ifPresent(existente -> {
                    if (!existente.getId().equals(unidade.getId())) {
                        throw new BusinessException(
                                "Já existe uma unidade com esse nome."
                        );
                    }
                });

        unidade.setNome(nome);

        unidadeRepository.save(unidade);
    }

    @Transactional
    public void excluir(Long id) {

        if (produtoRepository.existsByUnidadeId(id)) {
            throw new BusinessException(
                    "Não é possível excluir uma unidade que possui produtos."
            );
        }

        unidadeRepository.deleteById(id);
    }
}