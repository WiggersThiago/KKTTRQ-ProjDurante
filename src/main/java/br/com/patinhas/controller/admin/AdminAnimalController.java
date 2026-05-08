package br.com.patinhas.controller.admin;

import br.com.patinhas.dto.request.AnimalRequestDTO;
import br.com.patinhas.entity.enums.PorteAnimal;
import br.com.patinhas.entity.enums.SexoAnimal;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/animais")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAnimalController {

    private final AnimalService animalService;

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
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            adicionarEnumsAoModel(model);
            model.addAttribute("modoEdicao", false);
            return "admin/animal-form";
        }
        animalService.cadastrar(dto);
        redirectAttributes.addFlashAttribute("sucesso", "Animal cadastrado com sucesso!");
        return "redirect:/admin/animais";
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
                .fotoUrl(animal.getFotoUrl())
                .build();
        model.addAttribute("animal", dto);
        model.addAttribute("animalId", id);
        model.addAttribute("modoEdicao", true);
        adicionarEnumsAoModel(model);
        return "admin/animal-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("animal") AnimalRequestDTO dto,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("animalId", id);
            model.addAttribute("modoEdicao", true);
            adicionarEnumsAoModel(model);
            return "admin/animal-form";
        }
        animalService.atualizar(id, dto);
        redirectAttributes.addFlashAttribute("sucesso", "Animal atualizado com sucesso!");
        return "redirect:/admin/animais";
    }

    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        animalService.remover(id);
        redirectAttributes.addFlashAttribute("sucesso", "Animal removido com sucesso.");
        return "redirect:/admin/animais";
    }

    private void adicionarEnumsAoModel(Model model) {
        model.addAttribute("portes", PorteAnimal.values());
        model.addAttribute("sexos", SexoAnimal.values());
        model.addAttribute("statusList", StatusAdocao.values());
    }
}
