package br.com.patinhas.dto.response;

import br.com.patinhas.entity.Denuncia;
import br.com.patinhas.entity.enums.StatusDenuncia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaResponseDTO {

    private Long id;
    private String descricao;
    private String local;
    private LocalDate dataOcorrido;
    private LocalDateTime dataEnvio;
    private StatusDenuncia status;
    private String observacoesInternas;

    public static DenunciaResponseDTO fromEntity(Denuncia denuncia) {
        if (denuncia == null) {
            return null;
        }
        return DenunciaResponseDTO.builder()
                .id(denuncia.getId())
                .descricao(denuncia.getDescricao())
                .local(denuncia.getLocal())
                .dataOcorrido(denuncia.getDataOcorrido())
                .dataEnvio(denuncia.getDataEnvio())
                .status(denuncia.getStatus())
                .observacoesInternas(denuncia.getObservacoesInternas())
                .build();
    }
}
