package br.com.patinhas.repository;

import br.com.patinhas.entity.InformacaoONG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformacaoONGRepository extends JpaRepository<InformacaoONG, Long> {

    /**
     * Como esperamos apenas uma única linha (singleton lógico),
     * basta buscar a primeira pelo id ascendente.
     */
    Optional<InformacaoONG> findFirstByOrderByIdAsc();
}
