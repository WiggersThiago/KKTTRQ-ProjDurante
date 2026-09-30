package br.com.patinhas.repository;

import br.com.patinhas.entity.SolicitacaoAdocao;
import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoAdocaoRepository extends JpaRepository<SolicitacaoAdocao, Long> {

    List<SolicitacaoAdocao> findAllByOrderByDataSolicitacaoDesc();

    List<SolicitacaoAdocao> findAllByStatusOrderByDataSolicitacaoDesc(
            StatusSolicitacaoAdocao status
    );

    boolean existsByAnimalIdAndStatus(
            Long animalId,
            StatusSolicitacaoAdocao status
    );
    long countByStatus(StatusSolicitacaoAdocao status);
}