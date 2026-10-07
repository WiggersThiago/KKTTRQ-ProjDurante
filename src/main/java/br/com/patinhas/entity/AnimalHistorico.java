package br.com.patinhas.entity;

import java.time.LocalDateTime;

import br.com.patinhas.entity.enums.TipoHistoricoAnimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "animal_historico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoHistoricoAnimal tipo;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false, length = 500)
    private String texto;

    @Column(nullable = false)
    private Boolean automatico;

    @PrePersist
    protected void onCreate() {
        if (data == null) {
            data = LocalDateTime.now();
        }

        if (automatico == null) {
            automatico = true;
        }
    }
}