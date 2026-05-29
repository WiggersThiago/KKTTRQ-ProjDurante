package br.com.patinhas.entity;

import br.com.patinhas.entity.enums.StatusDenuncia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "denuncia_historico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenunciaHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "denuncia_id", nullable = false)
    private Denuncia denuncia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 20)
    private StatusDenuncia statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 20)
    private StatusDenuncia statusNovo;

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
