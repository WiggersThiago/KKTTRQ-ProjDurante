package br.com.patinhas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class SolicitacaoAdocaoRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    private String nome;

    @NotBlank(message = "O telefone/WhatsApp é obrigatório.")
    @Size(max = 50, message = "O telefone deve ter no máximo 50 caracteres.")
    private String telefone;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
    private String email;

    @NotBlank(message = "A cidade é obrigatória.")
    @Size(max = 150, message = "A cidade deve ter no máximo 150 caracteres.")
    private String cidade;

    @NotNull(message = "O animal de interesse é obrigatório.")
    private Long animalId;

    @Size(max = 2000, message = "O motivo deve ter no máximo 2000 caracteres.")
    private String motivoAdocao;

    private Boolean possuiOutrosAnimais;

    private Boolean possuiEspacoAdequado;

    private Boolean todosConcordam;

    private Boolean jaTeveAnimais;

    @Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres.")
    private String observacoes;
}
