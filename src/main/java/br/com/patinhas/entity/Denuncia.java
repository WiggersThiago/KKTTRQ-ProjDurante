package br.com.patinhas.entity;

import br.com.patinhas.entity.enums.StatusDenuncia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Denúncia anônima feita por qualquer cidadão à ONG.
 *
 * Não armazena dados de identificação do denunciante: a privacidade
 * é parte fundamental do fluxo, conforme requisito da ONG.
 */
@Entity
@Table(name = "denuncias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "observacoesInternas")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, length = 255)
    private String local;

    @Column(name = "data_ocorrido")
    private LocalDate dataOcorrido;

    @Column(name = "data_envio", nullable = false, updatable = false)
    private LocalDateTime dataEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusDenuncia status = StatusDenuncia.PENDENTE;

    /**
     * Observações privadas usadas pela administração para acompanhamento da denúncia.
     */
    @Column(name = "observacoes_internas", length = 2000)
    private String observacoesInternas;

    @PrePersist
    protected void onCreate() {
        this.dataEnvio = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusDenuncia.PENDENTE;
        }
    }
}
