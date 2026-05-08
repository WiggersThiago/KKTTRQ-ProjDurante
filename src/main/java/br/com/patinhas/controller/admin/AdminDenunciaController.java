package br.com.patinhas.controller.admin;

import br.com.patinhas.dto.request.DenunciaUpdateStatusDTO;
import br.com.patinhas.entity.enums.StatusDenuncia;
import br.com.patinhas.service.DenunciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/denuncias")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDenunciaController {

    private final DenunciaService denunciaService;

    @GetMapping
    public String listar(@RequestParam(required = false) StatusDenuncia status, Model model) {
        if (status != null) {
            model.addAttribute("denuncias", denunciaService.listarPorStatus(status));
        } else {
            model.addAttribute("denuncias", denunciaService.listarTodas());
        }
        model.addAttribute("statusList", StatusDenuncia.values());
        model.addAttribute("statusFiltro", status);
        return "admin/denuncias";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        var denuncia = denunciaService.buscarPorId(id);
        model.addAttribute("denuncia", denuncia);
        if (!model.containsAttribute("atualizacao")) {
            DenunciaUpdateStatusDTO dto = new DenunciaUpdateStatusDTO();
            dto.setStatus(denuncia.getStatus());
            dto.setObservacoesInternas(denuncia.getObservacoesInternas());
            model.addAttribute("atualizacao", dto);
        }
        model.addAttribute("statusList", StatusDenuncia.values());
        return "admin/denuncia-detalhe";
    }

    @PostMapping("/{id}")
    public String atualizarStatus(@PathVariable Long id,
                                  @Valid @ModelAttribute("atualizacao") DenunciaUpdateStatusDTO dto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("denuncia", denunciaService.buscarPorId(id));
            model.addAttribute("statusList", StatusDenuncia.values());
            return "admin/denuncia-detalhe";
        }
        denunciaService.atualizarStatus(id, dto);
        redirectAttributes.addFlashAttribute("sucesso", "Denúncia atualizada com sucesso.");
        return "redirect:/admin/denuncias/" + id;
    }
}
