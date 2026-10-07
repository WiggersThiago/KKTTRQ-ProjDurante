package br.com.patinhas.repository;

import br.com.patinhas.entity.CategoriaEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaEstoqueRepository
        extends JpaRepository<CategoriaEstoque, Long> {

    Optional<CategoriaEstoque> findByNomeIgnoreCase(String nome);

    List<CategoriaEstoque> findAllByOrderByNomeAsc();
}