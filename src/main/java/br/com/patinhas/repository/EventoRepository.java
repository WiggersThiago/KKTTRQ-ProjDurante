package br.com.patinhas.repository;

import br.com.patinhas.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findAllByAtivoTrueOrderByDataEventoAsc();

    List<Evento> findAllByOrderByDataEventoDesc();

    @Query("""
            SELECT e FROM Evento e
            WHERE e.ativo = true
              AND e.dataEvento >= :agora
            ORDER BY e.dataEvento ASC
            """)
    List<Evento> buscarProximosEventos(LocalDateTime agora);

    long countByAtivoTrue();
}
