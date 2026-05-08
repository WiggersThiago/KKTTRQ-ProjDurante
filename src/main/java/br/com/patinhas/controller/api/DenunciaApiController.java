package br.com.patinhas.controller.api;

import br.com.patinhas.dto.request.DenunciaRequestDTO;
import br.com.patinhas.dto.request.DenunciaUpdateStatusDTO;
import br.com.patinhas.dto.response.ApiResponse;
import br.com.patinhas.dto.response.DenunciaResponseDTO;
import br.com.patinhas.entity.enums.StatusDenuncia;
import br.com.patinhas.service.DenunciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DenunciaApiController {

    private final DenunciaService denunciaService;

    /**
     * Endpoint público para criação de denúncias anônimas.
     */
    @PostMapping("/api/v1/public/denuncias")
    public ResponseEntity<ApiResponse<DenunciaResponseDTO>> criarAnonima(
            @Valid @RequestBody DenunciaRequestDTO dto) {
        DenunciaResponseDTO criada = denunciaService.registrarAnonima(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Denúncia registrada anonimamente.", criada));
    }

    @GetMapping("/api/v1/admin/denuncias")
    public ApiResponse<List<DenunciaResponseDTO>> listar(
            @RequestParam(required = false) StatusDenuncia status) {
        if (status != null) {
            return ApiResponse.ok(denunciaService.listarPorStatus(status));
        }
        return ApiResponse.ok(denunciaService.listarTodas());
    }

    @GetMapping("/api/v1/admin/denuncias/{id}")
    public ApiResponse<DenunciaResponseDTO> buscar(@PathVariable Long id) {
        return ApiResponse.ok(denunciaService.buscarPorId(id));
    }

    @PutMapping("/api/v1/admin/denuncias/{id}/status")
    public ApiResponse<DenunciaResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody DenunciaUpdateStatusDTO dto) {
        return ApiResponse.ok("Status atualizado.",
                denunciaService.atualizarStatus(id, dto));
    }
}
