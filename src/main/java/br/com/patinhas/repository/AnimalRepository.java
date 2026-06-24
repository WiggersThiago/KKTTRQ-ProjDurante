package br.com.patinhas.repository;

import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.enums.StatusAdocao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findAllByOrderByAtivoDescDataCadastroDesc();

    Page<Animal> findAllByAtivoTrueOrderByDataCadastroDesc(Pageable pageable);

    List<Animal> findAllByStatusAdocaoAndAtivoTrueOrderByDataCadastroDesc(StatusAdocao statusAdocao);

    long countByStatusAdocaoAndAtivoTrue(StatusAdocao statusAdocao);

    long countByAtivoTrue();

    List<Animal> findAllByAtivoTrueAndDestaqueTrueOrderByDataCadastroDesc();

    @Query("""
            SELECT a FROM Animal a
            WHERE a.ativo = true
              AND (:nomePattern IS NULL OR LOWER(a.nome) LIKE :nomePattern)
              AND (:status IS NULL OR a.statusAdocao = :status)
            ORDER BY a.dataCadastro DESC
            """)
    Page<Animal> buscarComFiltros(@Param("nomePattern") String nomePattern,
                                  @Param("status") StatusAdocao status,
                                  Pageable pageable);
}
