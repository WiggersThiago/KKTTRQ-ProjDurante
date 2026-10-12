package br.com.patinhas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeMedidaRequestDTO {

    @NotBlank(message = "Informe a unidade de medida.")
    @Size(max = 20, message = "A unidade deve ter no máximo 20 caracteres.")
    private String nome;
}
