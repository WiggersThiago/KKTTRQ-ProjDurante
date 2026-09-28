package br.com.patinhas.dto.request;

import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAdocaoUpdateStatusDTO {

    @NotNull(message = "O status é obrigatório.")
    private StatusSolicitacaoAdocao status;

    @Size(max = 2000, message = "A anotação deve ter no máximo 2000 caracteres.")
    private String anotacao;
}