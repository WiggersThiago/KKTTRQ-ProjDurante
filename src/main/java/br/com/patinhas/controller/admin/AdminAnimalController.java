package br.com.patinhas.controller.admin;

import br.com.patinhas.dto.request.AnimalRequestDTO;
import br.com.patinhas.entity.enums.PorteAnimal;
import br.com.patinhas.entity.enums.SexoAnimal;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.service.AnimalService;
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
@RequestMapping("/admin/animais")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAnimalController {

    private final AnimalService animalService;
    private final ImageStorageService imageStorageService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("animais", animalService.listarAdmin());
        return "admin/animais";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("animal")) {
            model.addAttribute("animal", new AnimalRequestDTO());
        }
        adicionarEnumsAoModel(model);
        model.addAttribute("modoEdicao", false);
        return "admin/animal-form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("animal") AnimalRequestDTO dto,
                         @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            adicionarEnumsAoModel(model);
            model.addAttribute("modoEdicao", false);
            return "admin/animal-form";
        }
        try {
            animalService.cadastrar(dto, imagem);
            redirectAttributes.addFlashAttribute("sucesso", "Animal cadastrado com sucesso!");
            return "redirect:/admin/animais";
        } catch (BusinessException e) {
            model.addAttribute("erroImagem", e.getMessage());
            adicionarEnumsAoModel(model);
            model.addAttribute("modoEdicao", false);
            return "admin/animal-form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var animal = animalService.buscarPorId(id);
        AnimalRequestDTO dto = AnimalRequestDTO.builder()
                .nome(animal.getNome())
                .idade(animal.getIdade())
                .descricao(animal.getDescricao())
                .porte(animal.getPorte())
                .sexo(animal.getSexo())
                .statusAdocao(animal.getStatusAdocao())
                .castrado(animal.getCastrado())
                .vacinado(animal.getVacinado())
                .destaque(animal.getDestaque())
                .ativo(animal.getAtivo())
                .build();
        model.addAttribute("animal", dto);
        model.addAttribute("animalId", id);
        model.addAttribute("fotoAtual", animal.getFotoUrl());
        model.addAttribute("modoEdicao", true);
        adicionarEnumsAoModel(model);
        return "admin/animal-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("animal") AnimalRequestDTO dto,
                            @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                            @RequestParam(value = "removerImagem", defaultValue = "false") boolean removerImagem,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        var existente = animalService.buscarPorId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("animalId", id);
            model.addAttribute("fotoAtual", existente.getFotoUrl());
            model.addAttribute("modoEdicao", true);
            adicionarEnumsAoModel(model);
            return "admin/animal-form";
        }
        try {
            animalService.atualizar(id, dto, imagem, removerImagem);
            redirectAttributes.addFlashAttribute("sucesso", "Animal atualizado com sucesso!");
            return "redirect:/admin/animais";
        } catch (BusinessException e) {
            model.addAttribute("erroImagem", e.getMessage());
            model.addAttribute("animalId", id);
            model.addAttribute("fotoAtual", existente.getFotoUrl());
            model.addAttribute("modoEdicao", true);
            adicionarEnumsAoModel(model);
            return "admin/animal-form";
        }
    }

    @GetMapping("/{id}/imagem/download")
    public ResponseEntity<Resource> downloadImagem(@PathVariable Long id) throws Exception {
        var animal = animalService.buscarPorId(id);
        if (animal.getFotoUrl() == null || animal.getFotoUrl().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        Path arquivo = imageStorageService.resolverArquivo(animal.getFotoUrl());
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
        animalService.desativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Animal desativado com sucesso.");
        return "redirect:/admin/animais";
    }

    private void adicionarEnumsAoModel(Model model) {
        model.addAttribute("portes", PorteAnimal.values());
        model.addAttribute("sexos", SexoAnimal.values());
        model.addAttribute("statusList", StatusAdocao.values());
    }
}
