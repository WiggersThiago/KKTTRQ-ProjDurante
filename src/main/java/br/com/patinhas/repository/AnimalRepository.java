package br.com.patinhas.repository;

import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.enums.StatusAdocao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.patinhas.entity.enums.SituacaoAnimal;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

  List<Animal> findAllByOrderByAtivoDescDataCadastroDesc();

  Page<Animal> findAllByAtivoTrueOrderByDataCadastroDesc(Pageable pageable);

  List<Animal> findAllByStatusAdocaoAndAtivoTrueOrderByDataCadastroDesc(StatusAdocao statusAdocao);

  long countByStatusAdocaoAndAtivoTrue(StatusAdocao statusAdocao);

  long countByAtivoTrue();

  long countBySituacaoAnimalAndAtivoTrue(SituacaoAnimal situacaoAnimal);

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

  @Query(value = """
      SELECT
          TO_CHAR(data_adocao, 'YYYY-MM') AS periodo,
          COUNT(*) AS total
      FROM animais
      WHERE data_adocao IS NOT NULL
      GROUP BY TO_CHAR(data_adocao, 'YYYY-MM')
      ORDER BY periodo
      """, nativeQuery = true)
  List<Object[]> contarAdocoesPorMes();

  @Query("""
      SELECT
          a.especie.nome,
          COUNT(a)
      FROM Animal a
      WHERE a.especie IS NOT NULL
      GROUP BY a.especie.nome
      ORDER BY COUNT(a) DESC
      """)
  List<Object[]> contarAnimaisPorEspecie();

  @Query("""
      SELECT
          a.statusAdocao,
          COUNT(a)
      FROM Animal a
      GROUP BY a.statusAdocao
      ORDER BY COUNT(a) DESC
      """)
  List<Object[]> contarAnimaisPorStatus();

  @Query(value = """
      SELECT AVG(EXTRACT(EPOCH FROM (data_adocao - data_disponivel)) / 86400)
      FROM animais
      WHERE data_disponivel IS NOT NULL
        AND data_adocao IS NOT NULL
      """, nativeQuery = true)
  Double calcularTempoMedioAteAdocao();

  @Query(value = """
      SELECT
          e.nome,
          AVG(EXTRACT(EPOCH FROM (a.data_adocao - a.data_disponivel)) / 86400)
      FROM animais a
      JOIN especies e ON e.id = a.especie_id
      WHERE a.data_disponivel IS NOT NULL
        AND a.data_adocao IS NOT NULL
      GROUP BY e.nome
      ORDER BY e.nome
      """, nativeQuery = true)
  List<Object[]> calcularTempoMedioAteAdocaoPorEspecie();
}
