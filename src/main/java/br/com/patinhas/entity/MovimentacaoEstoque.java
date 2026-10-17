package br.com.patinhas.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.patinhas.entity.enums.TipoMovimentacaoEstoque;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movimentacoes_estoque", indexes = {
        @Index(name = "idx_mov_estoque_produto", columnList = "produto_id"),
        @Index(name = "idx_mov_estoque_registrado", columnList = "registrado_em")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false, foreignKey = @ForeignKey(name = "fk_mov_estoque_produto"))
    private ProdutoEstoque produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_mov_estoque_usuario"))
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimentacaoEstoque tipo;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidade;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDate dataMovimentacao;

    @Column(length = 500)
    private String observacao;

    @Column(name = "registrado_em", nullable = false, updatable = false)
    private LocalDateTime registradoEm;

    @PrePersist
    protected void onCreate() {
        if (registradoEm == null) {
            registradoEm = LocalDateTime.now();
        }
    }
}
