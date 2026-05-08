package br.com.patinhas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class InformacaoONGRequestDTO {

    @NotBlank(message = "O nome da ONG é obrigatório.")
    @Size(max = 150)
    private String nomeONG;

    @Size(max = 4000)
    private String quemSomos;

    @Size(max = 4000)
    private String proposito;

    @Size(max = 255)
    private String pixDoacao;

    @Size(max = 500)
    private String enderecoDoacao;

    @Size(max = 50)
    private String telefoneContato;

    @Email(message = "E-mail de contato inválido.")
    @Size(max = 150)
    private String emailContato;

    @Size(max = 255)
    private String instagram;

    @Size(max = 255)
    private String facebook;
}
