package br.com.patinhas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.patinhas.entity.MovimentacaoEstoque;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    boolean existsByProdutoId(Long produtoId);

    @Query("""
            select m.produto.id, m.tipo, m.quantidade
            from MovimentacaoEstoque m
            """)
    List<Object[]> quantidades();

    @Query("""
            select m.tipo, m.quantidade
            from MovimentacaoEstoque m
            where m.produto.id = :produtoId
            """)
    List<Object[]> quantidadesDoProduto(@Param("produtoId") Long produtoId);

    @Query("""
            select m from MovimentacaoEstoque m
            join fetch m.produto p
            join fetch p.unidade
            join fetch m.usuario
            order by m.registradoEm desc, m.id desc
            """)
    List<MovimentacaoEstoque> listarTodas();

    @Query("""
            select m from MovimentacaoEstoque m
            join fetch m.produto p
            join fetch p.unidade
            join fetch m.usuario
            where p.id = :produtoId
            order by m.registradoEm desc, m.id desc
            """)
    List<MovimentacaoEstoque> listarPorProduto(@Param("produtoId") Long produtoId);
}
