package br.com.patinhas.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.patinhas.entity.enums.TipoHistoricoAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroHistoricoAnimalDTO {

    @NotNull(message = "Selecione o tipo de acontecimento.")
    private TipoHistoricoAnimal tipo;

    @NotNull(message = "Informe a data do acontecimento.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate data;

    @NotBlank(message = "A observação é obrigatória.")
    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
    private String texto;
}
