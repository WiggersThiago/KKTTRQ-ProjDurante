package br.com.patinhas.controller.api;

import br.com.patinhas.dto.request.InformacaoONGRequestDTO;
import br.com.patinhas.dto.response.ApiResponse;
import br.com.patinhas.dto.response.InformacaoONGResponseDTO;
import br.com.patinhas.service.InformacaoONGService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InformacaoONGApiController {

    private final InformacaoONGService informacaoONGService;

    @GetMapping("/api/v1/public/informacoes")
    public ApiResponse<InformacaoONGResponseDTO> publica() {
        return ApiResponse.ok(informacaoONGService.obter());
    }

    @PutMapping("/api/v1/admin/informacoes")
    public ApiResponse<InformacaoONGResponseDTO> atualizar(@Valid @RequestBody InformacaoONGRequestDTO dto) {
        return ApiResponse.ok("Informações atualizadas.", informacaoONGService.atualizar(dto));
    }
}
