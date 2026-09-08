package br.com.patinhas.repository;

import br.com.patinhas.entity.SolicitacaoAdocao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoAdocaoRepository extends JpaRepository<SolicitacaoAdocao, Long> {
}
