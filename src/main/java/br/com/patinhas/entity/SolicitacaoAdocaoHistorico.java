package br.com.patinhas.entity;

import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_adocao_historico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitacaoAdocaoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private SolicitacaoAdocao solicitacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 20)
    private StatusSolicitacaoAdocao statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 20)
    private StatusSolicitacaoAdocao statusNovo;

    @Column(length = 2000)
    private String anotacao;

    @Column(name = "registrado_em", nullable = false, updatable = false)
    private LocalDateTime registradoEm;

    @Column(name = "registrado_por", length = 150)
    private String registradoPor;

    @PrePersist
    protected void onCreate() {
        if (registradoEm == null) {
            registradoEm = LocalDateTime.now();
        }
    }
}