package br.com.patinhas.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Dados institucionais da ONG, exibidos publicamente na página "Sobre".
 *
 * Em geral existirá apenas um único registro desta entidade no banco
 * (singleton lógico), gerenciado pela administração.
 */
@Entity
@Table(name = "informacao_ong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class InformacaoONG {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_ong", nullable = false, length = 150)
    private String nomeONG;

    @Column(name = "quem_somos", length = 4000)
    private String quemSomos;

    @Column(length = 4000)
    private String proposito;

    @Column(name = "pix_doacao", length = 255)
    private String pixDoacao;

    @Column(name = "endereco_doacao", length = 500)
    private String enderecoDoacao;

    @Column(name = "telefone_contato", length = 50)
    private String telefoneContato;

    @Column(name = "email_contato", length = 150)
    private String emailContato;

    @Column(length = 255)
    private String instagram;

    @Column(length = 255)
    private String facebook;
}
