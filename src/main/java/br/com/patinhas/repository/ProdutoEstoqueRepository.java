package br.com.patinhas.repository;

import br.com.patinhas.entity.ProdutoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProdutoEstoqueRepository
        extends JpaRepository<ProdutoEstoque, Long> {

    @Query("""
        SELECT p
        FROM ProdutoEstoque p
        JOIN FETCH p.categoria
        JOIN FETCH p.unidade
        ORDER BY p.nome ASC
    """)
    List<ProdutoEstoque> findAllComRelacionamentos();

    @Query("""
        SELECT p
        FROM ProdutoEstoque p
        JOIN FETCH p.categoria
        JOIN FETCH p.unidade
        WHERE p.id = :id
    """)
    Optional<ProdutoEstoque> findByIdComRelacionamentos(Long id);

    Optional<ProdutoEstoque> findByNomeIgnoreCase(String nome);

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByUnidadeId(Long unidadeId);

    @Query("""
        SELECT COUNT(p)
        FROM ProdutoEstoque p
        WHERE p.estoqueAtual < p.estoqueMinimo
    """)
    long countAbaixoDoMinimo();
}