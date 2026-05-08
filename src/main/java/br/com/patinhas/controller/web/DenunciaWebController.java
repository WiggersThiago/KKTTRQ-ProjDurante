package br.com.patinhas.controller.web;

import br.com.patinhas.dto.request.DenunciaRequestDTO;
import br.com.patinhas.service.DenunciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/denuncia")
@RequiredArgsConstructor
public class DenunciaWebController {

    private final DenunciaService denunciaService;

    @GetMapping
    public String formulario(Model model) {
        if (!model.containsAttribute("denuncia")) {
            model.addAttribute("denuncia", new DenunciaRequestDTO());
        }
        return "denuncia";
    }

    @PostMapping
    public String enviar(@Valid @ModelAttribute("denuncia") DenunciaRequestDTO denuncia,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "denuncia";
        }
        denunciaService.registrarAnonima(denuncia);
        redirectAttributes.addFlashAttribute("sucesso",
                "Sua denúncia foi enviada de forma totalmente anônima. Obrigado!");
        return "redirect:/denuncia";
    }
}
