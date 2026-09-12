package br.com.patinhas.dto.response;

import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.SolicitacaoAdocao;
import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAdocaoResponseDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String cidade;
    private String motivoAdocao;
    private Boolean possuiOutrosAnimais;
    private Boolean possuiEspacoAdequado;
    private Boolean todosConcordam;
    private Boolean jaTeveAnimais;
    private String observacoes;
    private LocalDateTime dataSolicitacao;
    private StatusSolicitacaoAdocao status;
    private Long animalId;
    private String animalNome;

    public static SolicitacaoAdocaoResponseDTO fromEntity(SolicitacaoAdocao solicitacao) {
        if (solicitacao == null) {
            return null;
        }
        Animal animal = solicitacao.getAnimal();
        return SolicitacaoAdocaoResponseDTO.builder()
                .id(solicitacao.getId())
                .nome(solicitacao.getNome())
                .telefone(solicitacao.getTelefone())
                .email(solicitacao.getEmail())
                .cidade(solicitacao.getCidade())
                .motivoAdocao(solicitacao.getMotivoAdocao())
                .possuiOutrosAnimais(solicitacao.getPossuiOutrosAnimais())
                .possuiEspacoAdequado(solicitacao.getPossuiEspacoAdequado())
                .todosConcordam(solicitacao.getTodosConcordam())
                .jaTeveAnimais(solicitacao.getJaTeveAnimais())
                .observacoes(solicitacao.getObservacoes())
                .dataSolicitacao(solicitacao.getDataSolicitacao())
                .status(solicitacao.getStatus())
                .animalId(animal != null ? animal.getId() : null)
                .animalNome(animal != null ? animal.getNome() : null)
                .build();
    }
}
