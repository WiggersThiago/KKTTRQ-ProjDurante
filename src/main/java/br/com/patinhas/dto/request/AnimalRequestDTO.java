package br.com.patinhas.dto.request;

import br.com.patinhas.entity.enums.PorteAnimal;
import br.com.patinhas.entity.enums.SexoAnimal;
import br.com.patinhas.entity.enums.StatusAdocao;
import jakarta.validation.constraints.*;
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
public class AnimalRequestDTO {

    @NotBlank(message = "O nome do animal é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotNull(message = "A idade é obrigatória.")
    @Min(value = 0, message = "A idade não pode ser negativa.")
    @Max(value = 50, message = "A idade informada é inválida.")
    private Integer idade;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.")
    private String descricao;

    @NotNull(message = "O porte é obrigatório.")
    private PorteAnimal porte;

    @NotNull(message = "O sexo é obrigatório.")
    private SexoAnimal sexo;

    private StatusAdocao statusAdocao;

    @NotNull(message = "Informe se o animal é castrado.")
    private Boolean castrado;

    @NotNull(message = "Informe se o animal é vacinado.")
    private Boolean vacinado;

    private Boolean destaque;

    private Boolean ativo;
}
