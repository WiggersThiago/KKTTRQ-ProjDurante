package br.com.patinhas.repository;

import br.com.patinhas.entity.Denuncia;
import br.com.patinhas.entity.enums.StatusDenuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    List<Denuncia> findAllByOrderByDataEnvioDesc();

    List<Denuncia> findAllByStatusOrderByDataEnvioDesc(StatusDenuncia status);

    long countByStatus(StatusDenuncia status);
}
