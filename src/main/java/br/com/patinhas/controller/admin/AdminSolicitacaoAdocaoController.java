package br.com.patinhas.controller.admin;

import br.com.patinhas.dto.request.SolicitacaoAdocaoUpdateStatusDTO;
import br.com.patinhas.dto.response.SolicitacaoAdocaoResponseDTO;
import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.service.SolicitacaoAdocaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/solicitacoes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSolicitacaoAdocaoController {

    private final SolicitacaoAdocaoService solicitacaoAdocaoService;

    @GetMapping
    public String listar(@RequestParam(required = false) StatusSolicitacaoAdocao status, Model model) {
        if (status != null) {
            model.addAttribute("solicitacoes", solicitacaoAdocaoService.listarPorStatus(status));
        } else {
            model.addAttribute("solicitacoes", solicitacaoAdocaoService.listarTodas());
        }
        model.addAttribute("statusList", StatusSolicitacaoAdocao.values());
        model.addAttribute("statusFiltro", status);
        return "admin/solicitacoes";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        montarDetalhe(id, model);
        return "admin/solicitacao-detalhe";
    }

    @PostMapping("/{id}")
    public String atualizarStatus(@PathVariable Long id,
                                  @Valid @ModelAttribute("atualizacao") SolicitacaoAdocaoUpdateStatusDTO dto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            montarDetalhe(id, model);
            return "admin/solicitacao-detalhe";
        }

        try {
            solicitacaoAdocaoService.atualizarStatus(id, dto);
            redirectAttributes.addFlashAttribute("sucesso", "Solicitação atualizada com sucesso.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/admin/solicitacoes/" + id;
    }

    private void montarDetalhe(Long id, Model model) {
        SolicitacaoAdocaoResponseDTO solicitacao = solicitacaoAdocaoService.buscarPorId(id);
        List<StatusSolicitacaoAdocao> permitidos =
                solicitacaoAdocaoService.proximosStatus(solicitacao.getStatus());

        model.addAttribute("solicitacao", solicitacao);
        model.addAttribute("statusPermitidos", permitidos);
        model.addAttribute("historico", solicitacaoAdocaoService.listarHistorico(id));

        if (!model.containsAttribute("atualizacao") && !permitidos.isEmpty()) {
            SolicitacaoAdocaoUpdateStatusDTO dto = new SolicitacaoAdocaoUpdateStatusDTO();
            dto.setStatus(permitidos.get(0));
            model.addAttribute("atualizacao", dto);
        }
    }
}
