package br.com.patinhas.controller.admin;

import br.com.patinhas.service.AnimalService;
import br.com.patinhas.service.DenunciaService;
import br.com.patinhas.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AnimalService animalService;
    private final EventoService eventoService;
    private final DenunciaService denunciaService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalAnimais", animalService.contarAtivos());
        model.addAttribute("animaisDisponiveis", animalService.contarDisponiveis());
        model.addAttribute("eventosAtivos", eventoService.contarAtivos());
        model.addAttribute("denunciasPendentes", denunciaService.contarPendentes());
        return "admin/dashboard";
    }
}
