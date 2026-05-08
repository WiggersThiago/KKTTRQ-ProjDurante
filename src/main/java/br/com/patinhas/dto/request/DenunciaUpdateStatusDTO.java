package br.com.patinhas.dto.request;

import br.com.patinhas.entity.enums.StatusDenuncia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaUpdateStatusDTO {

    @NotNull(message = "O novo status é obrigatório.")
    private StatusDenuncia status;

    @Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres.")
    private String observacoesInternas;
}
