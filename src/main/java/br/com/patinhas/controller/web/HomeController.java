package br.com.patinhas.controller.web;

import br.com.patinhas.service.AnimalService;
import br.com.patinhas.service.EventoService;
import br.com.patinhas.service.InformacaoONGService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AnimalService animalService;
    private final EventoService eventoService;
    private final InformacaoONGService informacaoONGService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("animaisDestaque",
                animalService.listarDisponiveis().stream().limit(6).toList());
        model.addAttribute("proximosEventos", eventoService.listarProximos());
        model.addAttribute("informacao", informacaoONGService.obter());
        return "index";
    }

    @GetMapping("/sobre")
    public String sobre(Model model) {
        model.addAttribute("informacao", informacaoONGService.obter());
        return "sobre";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("erro", "E-mail ou senha inválidos.");
        }
        if (logout != null) {
            model.addAttribute("mensagem", "Você saiu com sucesso.");
        }
        return "login";
    }

    @GetMapping("/erro")
    public String erro(@RequestParam(required = false) String codigo, Model model) {
        model.addAttribute("codigo", codigo == null ? "" : codigo);
        return "erro";
    }
}
