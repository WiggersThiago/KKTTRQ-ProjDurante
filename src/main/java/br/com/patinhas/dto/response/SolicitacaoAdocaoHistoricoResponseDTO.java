package br.com.patinhas.dto.response;

import br.com.patinhas.entity.SolicitacaoAdocaoHistorico;
import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAdocaoHistoricoResponseDTO {

    private Long id;
    private StatusSolicitacaoAdocao statusAnterior;
    private StatusSolicitacaoAdocao statusNovo;
    private String anotacao;
    private LocalDateTime registradoEm;
    private String registradoPor;

    public static SolicitacaoAdocaoHistoricoResponseDTO fromEntity(
            SolicitacaoAdocaoHistorico historico) {

        return SolicitacaoAdocaoHistoricoResponseDTO.builder()
                .id(historico.getId())
                .statusAnterior(historico.getStatusAnterior())
                .statusNovo(historico.getStatusNovo())
                .anotacao(historico.getAnotacao())
                .registradoEm(historico.getRegistradoEm())
                .registradoPor(historico.getRegistradoPor())
                .build();
    }
}