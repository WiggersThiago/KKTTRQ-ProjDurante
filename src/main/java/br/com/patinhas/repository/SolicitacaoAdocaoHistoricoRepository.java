package br.com.patinhas.repository;

import br.com.patinhas.entity.SolicitacaoAdocaoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoAdocaoHistoricoRepository
        extends JpaRepository<SolicitacaoAdocaoHistorico, Long> {

    List<SolicitacaoAdocaoHistorico> findAllBySolicitacaoIdOrderByRegistradoEmDesc(
            Long solicitacaoId
    );
}