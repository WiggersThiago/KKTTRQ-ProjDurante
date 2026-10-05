package br.com.patinhas.controller.admin;

import br.com.patinhas.service.AnimalService;
import br.com.patinhas.service.DenunciaService;
import br.com.patinhas.service.EventoService;
import br.com.patinhas.service.SolicitacaoAdocaoService;
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
    private final SolicitacaoAdocaoService solicitacaoAdocaoService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalAnimais", animalService.contarAtivos());
        model.addAttribute("animaisDisponiveis", animalService.contarDisponiveis());
        model.addAttribute("eventosAtivos", eventoService.contarAtivos());
        model.addAttribute("denunciasPendentes", denunciaService.contarPendentes());
        model.addAttribute("animaisTratamento", animalService.contarEmTratamentoOuStandBy());
        model.addAttribute("adocoesConcluidas", animalService.contarAdocoesConcluidas());
        model.addAttribute("solicitacoesPendentes", solicitacaoAdocaoService.contarPendentes());
        model.addAttribute("adocoesPorMes", animalService.contarAdocoesPorMes());
        model.addAttribute("animaisPorEspecie", animalService.contarAnimaisPorEspecie());
        model.addAttribute("animaisPorStatus", animalService.contarAnimaisPorStatus());
        model.addAttribute("tempoMedioAteAdocao", animalService.calcularTempoMedioAteAdocao());
        return "admin/dashboard";
    }
}
