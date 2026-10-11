package br.com.patinhas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.patinhas.entity.ProdutoEstoque;

public interface ProdutoEstoqueRepository extends JpaRepository<ProdutoEstoque, Long> {

    @Query("select p from ProdutoEstoque p join fetch p.categoria join fetch p.unidade order by p.nome asc")
    List<ProdutoEstoque> findAllComRelacionamentos();

    /** Compara nomes ignorando maiúsculas/minúsculas e espaços nas extremidades. */
    @Query("select p from ProdutoEstoque p where lower(trim(p.nome)) = lower(trim(:nome))")
    Optional<ProdutoEstoque> findByNomeNormalizado(@Param("nome") String nome);

    @Query("select p from ProdutoEstoque p join fetch p.categoria join fetch p.unidade where p.id = :id")
    Optional<ProdutoEstoque> findByIdComRelacionamentos(@Param("id") Long id);

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByUnidadeId(Long unidadeId);

    @Query("select count(p) from ProdutoEstoque p where p.estoqueAtual < p.estoqueMinimo")
    long countAbaixoDoMinimo();
}
