package br.com.patinhas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaEstoqueRequestDTO {

    @NotBlank(message = "Informe o nome da categoria.")
    @Size(max = 100, message = "O nome da categoria deve ter no máximo 100 caracteres.")
    private String nome;
}
