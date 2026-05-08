package br.com.patinhas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaRequestDTO {

    @NotBlank(message = "Descreva a denúncia.")
    @Size(min = 10, max = 2000, message = "A descrição deve ter entre 10 e 2000 caracteres.")
    private String descricao;

    @NotBlank(message = "Informe o local da ocorrência.")
    @Size(max = 255)
    private String local;

    @PastOrPresent(message = "A data informada não pode ser futura.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataOcorrido;
}
