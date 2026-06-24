package br.com.patinhas.controller.api;

import br.com.patinhas.dto.request.AnimalRequestDTO;
import br.com.patinhas.dto.response.AnimalResponseDTO;
import br.com.patinhas.dto.response.ApiResponse;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de Animais.
 *
 * Os endpoints públicos (GET) ficam em /api/v1/public/animais.
 * Os endpoints de mutação (POST/PUT/DELETE) ficam em /api/v1/admin/animais
 * e exigem autenticação como ADMIN (configurado no SecurityConfig).
 */
@RestController
@RequiredArgsConstructor
public class AnimalApiController {

    private final AnimalService animalService;

    @GetMapping("/api/v1/public/animais")
    public ApiResponse<Page<AnimalResponseDTO>> listarPublicos(
            @RequestParam(required = false) StatusAdocao status,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.ok(animalService.filtrar(nome, status, PageRequest.of(page, size)));
    }

    @GetMapping("/api/v1/public/animais/{id}")
    public ApiResponse<AnimalResponseDTO> buscarPublico(@PathVariable Long id) {
        return ApiResponse.ok(animalService.buscarPorId(id));
    }

    @GetMapping("/api/v1/admin/animais")
    public ApiResponse<List<AnimalResponseDTO>> listarAdmin() {
        return ApiResponse.ok(animalService.listarAdmin());
    }

    @PostMapping("/api/v1/admin/animais")
    public ResponseEntity<ApiResponse<AnimalResponseDTO>> criar(
            @Valid @RequestBody AnimalRequestDTO dto) {
        AnimalResponseDTO criado = animalService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Animal cadastrado com sucesso.", criado));
    }

    @PutMapping("/api/v1/admin/animais/{id}")
    public ApiResponse<AnimalResponseDTO> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody AnimalRequestDTO dto) {
        return ApiResponse.ok("Animal atualizado.", animalService.atualizar(id, dto));
    }

    @DeleteMapping("/api/v1/admin/animais/{id}")
    public ApiResponse<Void> desativar(@PathVariable Long id) {
        animalService.desativar(id);
        return ApiResponse.ok("Animal desativado.", null);
    }
}
