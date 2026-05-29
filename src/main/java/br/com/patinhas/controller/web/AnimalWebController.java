package br.com.patinhas.controller.web;

import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.service.AnimalService;
import br.com.patinhas.service.InformacaoONGService;
import br.com.patinhas.util.WhatsappUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/animais")
@RequiredArgsConstructor
public class AnimalWebController {

    private final AnimalService animalService;
    private final InformacaoONGService informacaoONGService;

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                         @RequestParam(required = false) StatusAdocao status,
                         Model model) {
        var informacao = informacaoONGService.obter();
        model.addAttribute("animais", animalService.filtrar(
                (nome == null || nome.isBlank()) ? null : nome.trim(),
                status));
        model.addAttribute("statusFiltro", status);
        model.addAttribute("nomeFiltro", nome);
        model.addAttribute("statusList", StatusAdocao.values());
        model.addAttribute("whatsappNumero", WhatsappUtil.formatarNumero(informacao.getTelefoneContato()));
        return "animais";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("animal", animalService.buscarPorId(id));
        } catch (ResourceNotFoundException e) {
            return "redirect:/animais";
        }
        return "animal-detalhe";
    }
}
