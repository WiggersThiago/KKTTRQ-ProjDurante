package br.com.patinhas.controller.api;

import br.com.patinhas.dto.request.SolicitacaoAdocaoRequestDTO;
import br.com.patinhas.dto.response.ApiResponse;
import br.com.patinhas.dto.response.SolicitacaoAdocaoResponseDTO;
import br.com.patinhas.service.SolicitacaoAdocaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SolicitacaoAdocaoApiController {

    private final SolicitacaoAdocaoService solicitacaoAdocaoService;

    @PostMapping("/api/v1/public/solicitacoes-adocao")
    public ResponseEntity<ApiResponse<SolicitacaoAdocaoResponseDTO>> criar(
            @Valid @RequestBody SolicitacaoAdocaoRequestDTO dto) {

        SolicitacaoAdocaoResponseDTO solicitacao = solicitacaoAdocaoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Solicitação de adoção registrada.", solicitacao));
    }
}
