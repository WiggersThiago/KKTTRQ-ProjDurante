package br.com.patinhas.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.patinhas.entity.enums.TipoMovimentacaoEstoque;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoEstoqueRequestDTO {

    @NotNull(message = "Informe se é entrada ou saída.")
    private TipoMovimentacaoEstoque tipo;

    @NotNull(message = "Selecione o produto.")
    private Long produtoId;

    @NotNull(message = "Informe a quantidade.")
    @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero.")
    @Digits(integer = 12, fraction = 3, message = "Informe até 12 dígitos inteiros e 3 casas decimais.")
    private BigDecimal quantidade;

    @NotNull(message = "Informe a data da movimentação.")
    private LocalDate dataMovimentacao;

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
    private String observacao;
}
