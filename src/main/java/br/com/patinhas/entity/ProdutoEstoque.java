package br.com.patinhas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "estoque_produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProdutoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEstoque categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeMedida unidade;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal estoqueMinimo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal estoqueMaximo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal estoqueAtual;
}