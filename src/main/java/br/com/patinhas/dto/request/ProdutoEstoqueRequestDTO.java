package br.com.patinhas.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoEstoqueRequestDTO {

    @NotBlank(message = "Informe o nome do produto.")
    @Size(max = 150, message = "O nome do produto deve ter no máximo 150 caracteres.")
    private String nome;

    @NotNull(message = "Selecione uma categoria.")
    private Long categoriaId;

    @NotNull(message = "Selecione uma unidade.")
    private Long unidadeId;

    @NotNull(message = "Informe o estoque mínimo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O estoque mínimo não pode ser negativo.")
    @Digits(integer = 12, fraction = 3, message = "Informe um valor de até 12 dígitos e 3 casas decimais.")
    private BigDecimal estoqueMinimo;

    @NotNull(message = "Informe o estoque máximo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O estoque máximo não pode ser negativo.")
    @Digits(integer = 12, fraction = 3, message = "Informe um valor de até 12 dígitos e 3 casas decimais.")
    private BigDecimal estoqueMaximo;
}
