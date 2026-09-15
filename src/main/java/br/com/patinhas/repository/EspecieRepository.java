package br.com.patinhas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.patinhas.entity.Especie;

public interface EspecieRepository extends JpaRepository<Especie, Long> {

    Optional<Especie> findByNomeIgnoreCase(String nome);

    List<Especie> findAllByOrderByNomeAsc();
}