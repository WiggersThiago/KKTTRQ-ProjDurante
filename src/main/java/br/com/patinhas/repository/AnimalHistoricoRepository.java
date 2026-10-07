package br.com.patinhas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.patinhas.entity.AnimalHistorico;
import br.com.patinhas.entity.enums.TipoHistoricoAnimal;

public interface AnimalHistoricoRepository extends JpaRepository<AnimalHistorico, Long> {

    List<AnimalHistorico> findByAnimalIdOrderByDataAsc(Long animalId);
    boolean existsByAnimalIdAndTipo(Long animalId, TipoHistoricoAnimal tipo);
}