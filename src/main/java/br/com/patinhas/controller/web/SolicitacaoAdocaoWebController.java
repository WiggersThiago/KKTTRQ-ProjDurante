package br.com.patinhas.controller.web;

import br.com.patinhas.dto.request.SolicitacaoAdocaoRequestDTO;
import br.com.patinhas.entity.SolicitacaoAdocao;
import br.com.patinhas.service.AnimalService;
import br.com.patinhas.service.SolicitacaoAdocaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SolicitacaoAdocaoWebController {

    private final AnimalService animalService;
    private final SolicitacaoAdocaoService solicitacaoAdocaoService;

    @GetMapping("/animais/{animalId}/adotar")
    public String formulario(@PathVariable Long animalId, Model model) {
        var animal = animalService.buscarPorId(animalId);

        SolicitacaoAdocaoRequestDTO dto = SolicitacaoAdocaoRequestDTO.builder()
                .animalId(animalId)
                .build();

        model.addAttribute("animal", animal);
        model.addAttribute("solicitacao", dto);

        return "solicitacao-adocao";
    }

    @PostMapping("/animais/{animalId}/adotar")
    public String enviar(
            @PathVariable Long animalId,
            @ModelAttribute("solicitacao") @jakarta.validation.Valid SolicitacaoAdocaoRequestDTO dto,
            BindingResult bindingResult,
            Model model) {

        dto.setAnimalId(animalId);

        var animal = animalService.buscarPorId(animalId);
        model.addAttribute("animal", animal);

        if (bindingResult.hasErrors()) {
            return "solicitacao-adocao";
        }

        solicitacaoAdocaoService.cadastrar(dto);

        model.addAttribute("sucesso",
                "Sua solicitação foi enviada com sucesso! A ONG entrará em contato com você.");

        model.addAttribute("solicitacao",
                SolicitacaoAdocaoRequestDTO.builder()
                        .animalId(animalId)
                        .build());

        return "solicitacao-adocao";
    }
}
