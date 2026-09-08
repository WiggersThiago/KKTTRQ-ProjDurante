package br.com.patinhas.controller.api;

import br.com.patinhas.dto.request.SolicitacaoAdocaoRequestDTO;
import br.com.patinhas.entity.SolicitacaoAdocao;
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
    public ResponseEntity<SolicitacaoAdocao> criar(
            @Valid @RequestBody SolicitacaoAdocaoRequestDTO dto) {

        SolicitacaoAdocao solicitacao = solicitacaoAdocaoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitacao);
    }
}
