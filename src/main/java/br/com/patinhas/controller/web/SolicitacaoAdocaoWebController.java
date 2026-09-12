package br.com.patinhas.controller.web;

import br.com.patinhas.dto.request.SolicitacaoAdocaoRequestDTO;
import br.com.patinhas.dto.response.AnimalResponseDTO;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.service.AnimalService;
import br.com.patinhas.service.SolicitacaoAdocaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SolicitacaoAdocaoWebController {

    private final AnimalService animalService;
    private final SolicitacaoAdocaoService solicitacaoAdocaoService;

    @GetMapping("/animais/{animalId}/adotar")
    public String formulario(@PathVariable Long animalId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        AnimalResponseDTO animal;
        try {
            animal = animalService.buscarPorId(animalId);
        } catch (ResourceNotFoundException e) {
            return "redirect:/animais";
        }

        if (!Boolean.TRUE.equals(animal.getAtivo())
                || animal.getStatusAdocao() != StatusAdocao.DISPONIVEL) {
            redirectAttributes.addFlashAttribute("erro",
                    "Este animal não está disponível para adoção.");
            return "redirect:/animais/" + animalId;
        }

        if (!model.containsAttribute("solicitacao")) {
            model.addAttribute("solicitacao", SolicitacaoAdocaoRequestDTO.builder()
                    .animalId(animalId)
                    .build());
        }
        model.addAttribute("animal", animal);
        return "solicitacao-adocao";
    }

    @PostMapping("/animais/{animalId}/adotar")
    public String enviar(
            @PathVariable Long animalId,
            @ModelAttribute("solicitacao") @jakarta.validation.Valid SolicitacaoAdocaoRequestDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        dto.setAnimalId(animalId);

        AnimalResponseDTO animal;
        try {
            animal = animalService.buscarPorId(animalId);
        } catch (ResourceNotFoundException e) {
            return "redirect:/animais";
        }
        model.addAttribute("animal", animal);

        if (bindingResult.hasErrors()) {
            return "solicitacao-adocao";
        }

        try {
            solicitacaoAdocaoService.cadastrar(dto);
        } catch (BusinessException e) {
            model.addAttribute("erro", e.getMessage());
            return "solicitacao-adocao";
        }

        redirectAttributes.addFlashAttribute("sucesso",
                "Sua solicitação foi enviada com sucesso! A ONG entrará em contato com você.");
        return "redirect:/animais/" + animalId + "/adotar";
    }
}
