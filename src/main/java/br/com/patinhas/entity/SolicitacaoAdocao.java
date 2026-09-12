package br.com.patinhas.entity;

import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "solicitacoes_adocao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "animal")
public class SolicitacaoAdocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 50)
    private String telefone;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 150)
    private String cidade;

    @Column(name = "motivo_adocao", length = 2000)
    private String motivoAdocao;

    @Column(name = "possui_outros_animais")
    private Boolean possuiOutrosAnimais;

    @Column(name = "possui_espaco_adequado")
    private Boolean possuiEspacoAdequado;

    @Column(name = "todos_concordam")
    private Boolean todosConcordam;

    @Column(name = "ja_teve_animais")
    private Boolean jaTeveAnimais;

    @Column(length = 2000)
    private String observacoes;

    @Column(name = "data_solicitacao", nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusSolicitacaoAdocao status = StatusSolicitacaoAdocao.NOVA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @PrePersist
    protected void onCreate() {
        this.dataSolicitacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusSolicitacaoAdocao.NOVA;
        }
    }
}
