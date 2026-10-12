package br.com.patinhas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.patinhas.entity.UnidadeMedida;

public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, Long> {

    List<UnidadeMedida> findAllByOrderByNomeAsc();

    @Query("select u from UnidadeMedida u where lower(trim(u.nome)) = lower(trim(:nome))")
    Optional<UnidadeMedida> findByNomeNormalizado(@Param("nome") String nome);
}
