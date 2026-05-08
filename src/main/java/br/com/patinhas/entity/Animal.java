package br.com.patinhas.entity;

import br.com.patinhas.entity.enums.PorteAnimal;
import br.com.patinhas.entity.enums.SexoAnimal;
import br.com.patinhas.entity.enums.StatusAdocao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        if (this.statusAdocao == null) {
            this.statusAdocao = StatusAdocao.DISPONIVEL;
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}
