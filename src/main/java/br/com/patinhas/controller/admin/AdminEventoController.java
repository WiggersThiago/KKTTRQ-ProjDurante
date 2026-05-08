package br.com.patinhas.controller.admin;

import br.com.patinhas.dto.request.EventoRequestDTO;
import br.com.patinhas.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/eventos")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminEventoController {

    private final EventoService eventoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("eventos", eventoService.listarAdmin());
        return "admin/eventos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("evento")) {
            model.addAttribute("evento", new EventoRequestDTO());
        }
        model.addAttribute("modoEdicao", false);
        return "admin/evento-form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("evento") EventoRequestDTO dto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "admin/evento-form";
        }
        eventoService.cadastrar(dto);
        redirectAttributes.addFlashAttribute("sucesso", "Evento cadastrado com sucesso!");
        return "redirect:/admin/eventos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var evento = eventoService.buscarPorId(id);
        EventoRequestDTO dto = EventoRequestDTO.builder()
                .titulo(evento.getTitulo())
                .descricao(evento.getDescricao())
                .local(evento.getLocal())
                .dataEvento(evento.getDataEvento())
                .ativo(evento.getAtivo())
                .build();
        model.addAttribute("evento", dto);
        model.addAttribute("eventoId", id);
        model.addAttribute("modoEdicao", true);
        return "admin/evento-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("evento") EventoRequestDTO dto,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventoId", id);
            model.addAttribute("modoEdicao", true);
            return "admin/evento-form";
        }
        eventoService.atualizar(id, dto);
        redirectAttributes.addFlashAttribute("sucesso", "Evento atualizado!");
        return "redirect:/admin/eventos";
    }

    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventoService.remover(id);
        redirectAttributes.addFlashAttribute("sucesso", "Evento removido com sucesso.");
        return "redirect:/admin/eventos";
    }
}
