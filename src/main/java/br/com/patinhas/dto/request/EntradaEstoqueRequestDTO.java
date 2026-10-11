package br.com.patinhas.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EntradaEstoqueRequestDTO {

    @NotBlank(message = "Informe o nome do produto. Não use apenas espaços.")
    @Size(max = 150, message = "O nome do produto deve ter no máximo 150 caracteres.")
    private String nome;

    @NotNull(message = "Informe a quantidade recebida.")
    @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero.")
    @Digits(integer = 12, fraction = 3, message = "Informe até 12 dígitos inteiros e 3 casas decimais.")
    private BigDecimal quantidade;
}
