package br.com.patinhas.dto.response;

import br.com.patinhas.entity.DenunciaHistorico;
import br.com.patinhas.entity.enums.StatusDenuncia;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaHistoricoResponseDTO {

    private Long id;
    private StatusDenuncia statusAnterior;
    private StatusDenuncia statusNovo;
    private String anotacao;
    private LocalDateTime registradoEm;
    private String registradoPor;

    public static DenunciaHistoricoResponseDTO fromEntity(DenunciaHistorico historico) {
        if (historico == null) {
            return null;
        }
        return DenunciaHistoricoResponseDTO.builder()
                .id(historico.getId())
                .statusAnterior(historico.getStatusAnterior())
                .statusNovo(historico.getStatusNovo())
                .anotacao(historico.getAnotacao())
                .registradoEm(historico.getRegistradoEm())
                .registradoPor(historico.getRegistradoPor())
                .build();
    }
}
