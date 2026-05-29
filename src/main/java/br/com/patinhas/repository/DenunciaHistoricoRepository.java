package br.com.patinhas.repository;

import br.com.patinhas.entity.DenunciaHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DenunciaHistoricoRepository extends JpaRepository<DenunciaHistorico, Long> {

    List<DenunciaHistorico> findAllByDenunciaIdOrderByRegistradoEmDesc(Long denunciaId);
}
