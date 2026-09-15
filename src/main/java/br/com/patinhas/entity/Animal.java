package br.com.patinhas.entity;

import java.time.LocalDateTime;

import br.com.patinhas.entity.enums.PorteAnimal;
import br.com.patinhas.entity.enums.SexoAnimal;
import br.com.patinhas.entity.enums.SituacaoAnimal;
import br.com.patinhas.entity.enums.StatusAdocao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Representa um animal cadastrado pela ONG para adoção.
 */
@Entity
@Table(name = "animais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "descricao")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer idade;

    @ManyToOne
    @JoinColumn(name = "especie_id")
    private Especie especie;

    @Column(length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PorteAnimal porte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SexoAnimal sexo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_adocao", nullable = false, length = 20)
    @Builder.Default
    private StatusAdocao statusAdocao = StatusAdocao.DISPONIVEL;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_animal", length = 20)
    @Builder.Default
    private SituacaoAnimal situacaoAnimal = SituacaoAnimal.NORMAL;

    @Column(nullable = false)
    @Builder.Default
    private Boolean castrado = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean vacinado = false;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_disponivel")
    private LocalDateTime dataDisponivel;

    @Column(name = "data_adocao")
    private LocalDateTime dataAdocao;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean destaque = false;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        if (this.statusAdocao == null) {
            this.statusAdocao = StatusAdocao.DISPONIVEL;
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
        if (this.destaque == null) {
            this.destaque = false;
        }
    }
}
