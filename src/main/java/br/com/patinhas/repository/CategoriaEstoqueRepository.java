package br.com.patinhas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.patinhas.entity.CategoriaEstoque;

public interface CategoriaEstoqueRepository extends JpaRepository<CategoriaEstoque, Long> {

    List<CategoriaEstoque> findAllByOrderByNomeAsc();

    @Query("select c from CategoriaEstoque c where lower(trim(c.nome)) = lower(trim(:nome))")
    Optional<CategoriaEstoque> findByNomeNormalizado(@Param("nome") String nome);
}
