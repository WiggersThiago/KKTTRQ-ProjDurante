package br.com.patinhas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoRequestDTO {

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 150)
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(max = 2000)
    private String descricao;

    @NotBlank(message = "O local é obrigatório.")
    @Size(max = 255)
    private String local;

    @NotNull(message = "A data do evento é obrigatória.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataEvento;

    private Boolean ativo;
}
