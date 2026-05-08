package br.com.patinhas.controller.admin;

import br.com.patinhas.dto.request.InformacaoONGRequestDTO;
import br.com.patinhas.entity.InformacaoONG;
import br.com.patinhas.service.InformacaoONGService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/informacoes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminInformacaoONGController {

    private final InformacaoONGService informacaoONGService;

    @GetMapping
    public String editar(Model model) {
        InformacaoONG info = informacaoONGService.obterEntidade();
        InformacaoONGRequestDTO dto = InformacaoONGRequestDTO.builder()
                .nomeONG(info.getNomeONG())
                .quemSomos(info.getQuemSomos())
                .proposito(info.getProposito())
                .pixDoacao(info.getPixDoacao())
                .enderecoDoacao(info.getEnderecoDoacao())
                .telefoneContato(info.getTelefoneContato())
                .emailContato(info.getEmailContato())
                .instagram(info.getInstagram())
                .facebook(info.getFacebook())
                .build();
        if (!model.containsAttribute("informacao")) {
            model.addAttribute("informacao", dto);
        }
        return "admin/informacoes";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("informacao") InformacaoONGRequestDTO dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/informacoes";
        }
        informacaoONGService.atualizar(dto);
        redirectAttributes.addFlashAttribute("sucesso", "Informações institucionais atualizadas com sucesso.");
        return "redirect:/admin/informacoes";
    }
}
