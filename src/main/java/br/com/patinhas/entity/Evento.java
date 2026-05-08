package br.com.patinhas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Eventos promovidos pela ONG (feiras de adoção, campanhas, palestras, etc.).
 */
@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, length = 255)
    private String local;

    @Column(name = "data_evento", nullable = false)
    private LocalDateTime dataEvento;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @PrePersist
    protected void onCreate() {
        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}
