package br.com.patinhas.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produtos_estoque", uniqueConstraints = {
        @UniqueConstraint(name = "uk_produtos_estoque_nome", columnNames = "nome")
})
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
    @JoinColumn(name = "categoria_id", nullable = false, foreignKey = @ForeignKey(name = "fk_produtos_estoque_categoria"))
    private CategoriaEstoque categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false, foreignKey = @ForeignKey(name = "fk_produtos_estoque_unidade"))
    private UnidadeMedida unidade;

    @Column(name = "estoque_minimo", nullable = false, precision = 15, scale = 3)
    private BigDecimal estoqueMinimo;

    @Column(name = "estoque_maximo", nullable = false, precision = 15, scale = 3)
    private BigDecimal estoqueMaximo;

    @Column(name = "estoque_atual", nullable = false, precision = 15, scale = 3)
    @Builder.Default
    private BigDecimal estoqueAtual = BigDecimal.ZERO;

    @PrePersist
    protected void onCreate() {
        if (estoqueAtual == null) {
            estoqueAtual = BigDecimal.ZERO;
        }
    }

    @Transient
    public boolean estoqueAbaixoDoMinimo() {
        return estoqueAtual != null && estoqueMinimo != null && estoqueAtual.compareTo(estoqueMinimo) < 0;
    }

    @Transient
    public boolean estoqueAcimaDoMaximo() {
        return estoqueAtual != null && estoqueMaximo != null && estoqueAtual.compareTo(estoqueMaximo) > 0;
    }
}
