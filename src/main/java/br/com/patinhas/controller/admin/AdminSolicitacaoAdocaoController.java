package br.com.patinhas.controller.admin;

import br.com.patinhas.dto.request.SolicitacaoAdocaoUpdateStatusDTO;
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

@Controller
@RequestMapping("/admin/solicitacoes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSolicitacaoAdocaoController {

    private final SolicitacaoAdocaoService solicitacaoAdocaoService;

    @GetMapping
    public String listar(
            @RequestParam(required = false) StatusSolicitacaoAdocao status,
            Model model) {

        if (status != null) {
            model.addAttribute(
                    "solicitacoes",
                    solicitacaoAdocaoService.listarPorStatus(status)
            );
        } else {
            model.addAttribute(
                    "solicitacoes",
                    solicitacaoAdocaoService.listarTodas()
            );
        }

        model.addAttribute(
                "statusList",
                StatusSolicitacaoAdocao.values()
        );

        model.addAttribute("statusFiltro", status);

        return "admin/solicitacoes";
    }

    @GetMapping("/{id}")
    public String detalhe(
            @PathVariable Long id,
            Model model) {

        var solicitacao =
                solicitacaoAdocaoService.buscarPorId(id);

        model.addAttribute("solicitacao", solicitacao);

        if (!model.containsAttribute("atualizacao")) {

            SolicitacaoAdocaoUpdateStatusDTO dto =
                    new SolicitacaoAdocaoUpdateStatusDTO();

            dto.setStatus(solicitacao.getStatus());

            model.addAttribute("atualizacao", dto);
        }

        model.addAttribute(
                "statusList",
                StatusSolicitacaoAdocao.values()
        );

        model.addAttribute(
                "historico",
                solicitacaoAdocaoService.listarHistorico(id)
        );

        return "admin/solicitacao-detalhe";
    }

    @PostMapping("/{id}")
    public String atualizarStatus(
            @PathVariable Long id,
            @Valid @ModelAttribute("atualizacao")
            SolicitacaoAdocaoUpdateStatusDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "solicitacao",
                    solicitacaoAdocaoService.buscarPorId(id)
            );

            model.addAttribute(
                    "statusList",
                    StatusSolicitacaoAdocao.values()
            );

            model.addAttribute(
                    "historico",
                    solicitacaoAdocaoService.listarHistorico(id)
            );

            return "admin/solicitacao-detalhe";
        }

        try {

            solicitacaoAdocaoService.atualizarStatus(id, dto);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Solicitação atualizada com sucesso."
            );

        } catch (BusinessException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/admin/solicitacoes/" + id;
    }
}