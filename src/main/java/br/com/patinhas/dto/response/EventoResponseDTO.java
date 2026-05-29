package br.com.patinhas.dto.response;

import br.com.patinhas.entity.Evento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponseDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private String local;
    private LocalDateTime dataEvento;
    private Boolean ativo;
    private String fotoUrl;

    public static EventoResponseDTO fromEntity(Evento evento) {
        if (evento == null) {
            return null;
        }
        return EventoResponseDTO.builder()
                .id(evento.getId())
                .titulo(evento.getTitulo())
                .descricao(evento.getDescricao())
                .local(evento.getLocal())
                .dataEvento(evento.getDataEvento())
                .ativo(evento.getAtivo())
                .fotoUrl(evento.getFotoUrl())
                .build();
    }
}
