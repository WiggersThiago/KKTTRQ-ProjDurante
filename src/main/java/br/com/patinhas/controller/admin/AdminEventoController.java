package br.com.patinhas.controller.admin;

import br.com.patinhas.dto.request.EventoRequestDTO;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.service.EventoService;
import br.com.patinhas.service.ImageStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/admin/eventos")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminEventoController {

    private final EventoService eventoService;
    private final ImageStorageService imageStorageService;

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
                         @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "admin/evento-form";
        }
        try {
            eventoService.cadastrar(dto, imagem);
            redirectAttributes.addFlashAttribute("sucesso", "Evento cadastrado com sucesso!");
            return "redirect:/admin/eventos";
        } catch (BusinessException e) {
            model.addAttribute("erroImagem", e.getMessage());
            model.addAttribute("modoEdicao", false);
            return "admin/evento-form";
        }
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
        model.addAttribute("fotoAtual", evento.getFotoUrl());
        model.addAttribute("modoEdicao", true);
        return "admin/evento-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("evento") EventoRequestDTO dto,
                            @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                            @RequestParam(value = "removerImagem", defaultValue = "false") boolean removerImagem,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        var existente = eventoService.buscarPorId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventoId", id);
            model.addAttribute("fotoAtual", existente.getFotoUrl());
            model.addAttribute("modoEdicao", true);
            return "admin/evento-form";
        }
        try {
            eventoService.atualizar(id, dto, imagem, removerImagem);
            redirectAttributes.addFlashAttribute("sucesso", "Evento atualizado!");
            return "redirect:/admin/eventos";
        } catch (BusinessException e) {
            model.addAttribute("erroImagem", e.getMessage());
            model.addAttribute("eventoId", id);
            model.addAttribute("fotoAtual", existente.getFotoUrl());
            model.addAttribute("modoEdicao", true);
            return "admin/evento-form";
        }
    }

    @GetMapping("/{id}/imagem/download")
    public ResponseEntity<Resource> downloadImagem(@PathVariable Long id) throws Exception {
        var evento = eventoService.buscarPorId(id);
        if (evento.getFotoUrl() == null || evento.getFotoUrl().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        Path arquivo = imageStorageService.resolverArquivo(evento.getFotoUrl());
        Resource resource = new UrlResource(arquivo.toUri());
        String contentType = Files.probeContentType(arquivo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + arquivo.getFileName().toString() + "\"")
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventoService.desativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Evento desativado com sucesso.");
        return "redirect:/admin/eventos";
    }
}
