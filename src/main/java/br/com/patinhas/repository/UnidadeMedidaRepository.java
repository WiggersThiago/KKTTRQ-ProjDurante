package br.com.patinhas.repository;

import br.com.patinhas.entity.UnidadeMedida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnidadeMedidaRepository
        extends JpaRepository<UnidadeMedida, Long> {

    Optional<UnidadeMedida> findByNomeIgnoreCase(String nome);

    List<UnidadeMedida> findAllByOrderByNomeAsc();
}