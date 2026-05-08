package br.com.patinhas.controller.api;

import br.com.patinhas.dto.request.EventoRequestDTO;
import br.com.patinhas.dto.response.ApiResponse;
import br.com.patinhas.dto.response.EventoResponseDTO;
import br.com.patinhas.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventoApiController {

    private final EventoService eventoService;

    @GetMapping("/api/v1/public/eventos")
    public ApiResponse<List<EventoResponseDTO>> listarPublicos() {
        return ApiResponse.ok(eventoService.listarTodosAtivos());
    }

    @GetMapping("/api/v1/public/eventos/proximos")
    public ApiResponse<List<EventoResponseDTO>> proximos() {
        return ApiResponse.ok(eventoService.listarProximos());
    }

    @GetMapping("/api/v1/admin/eventos")
    public ApiResponse<List<EventoResponseDTO>> listarAdmin() {
        return ApiResponse.ok(eventoService.listarAdmin());
    }

    @PostMapping("/api/v1/admin/eventos")
    public ResponseEntity<ApiResponse<EventoResponseDTO>> criar(@Valid @RequestBody EventoRequestDTO dto) {
        EventoResponseDTO criado = eventoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Evento cadastrado.", criado));
    }

    @PutMapping("/api/v1/admin/eventos/{id}")
    public ApiResponse<EventoResponseDTO> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody EventoRequestDTO dto) {
        return ApiResponse.ok("Evento atualizado.", eventoService.atualizar(id, dto));
    }

    @DeleteMapping("/api/v1/admin/eventos/{id}")
    public ApiResponse<Void> remover(@PathVariable Long id) {
        eventoService.remover(id);
        return ApiResponse.ok("Evento removido.", null);
    }
}
