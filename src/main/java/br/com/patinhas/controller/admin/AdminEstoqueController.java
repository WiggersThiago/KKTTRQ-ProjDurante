package br.com.patinhas.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.patinhas.dto.request.CategoriaEstoqueRequestDTO;
import br.com.patinhas.dto.request.EntradaEstoqueRequestDTO;
import br.com.patinhas.dto.request.ProdutoEstoqueRequestDTO;
import br.com.patinhas.dto.request.UnidadeMedidaRequestDTO;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.service.CategoriaEstoqueService;
import br.com.patinhas.service.ProdutoEstoqueService;
import br.com.patinhas.service.UnidadeMedidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/estoque")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminEstoqueController {

    private final CategoriaEstoqueService categoriaService;
    private final UnidadeMedidaService unidadeService;
    private final ProdutoEstoqueService produtoService;

    @GetMapping({"", "/"})
    public String estoque(Model model) {
        model.addAttribute("totalProdutos", produtoService.contarProdutos());
        model.addAttribute("totalCategorias", categoriaService.listarTodas().size());
        model.addAttribute("totalUnidades", unidadeService.listarTodas().size());
        model.addAttribute("produtosAbaixoMinimo", produtoService.contarAbaixoDoMinimo());
        return "admin/estoque";
    }

    @GetMapping("/entrada")
    public String formularioEntrada(Model model) {
        if (!model.containsAttribute("entrada")) {
            model.addAttribute("entrada", new EntradaEstoqueRequestDTO());
        }
        return "admin/estoque-entrada-form";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(@Valid @ModelAttribute("entrada") EntradaEstoqueRequestDTO dto,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/estoque-entrada-form";
        }
        try {
            var produto = produtoService.registrarEntradaPorNome(dto.getNome(), dto.getQuantidade());
            redirectAttributes.addFlashAttribute("sucesso",
                    "Entrada registrada: +" + dto.getQuantidade().stripTrailingZeros().toPlainString() +
                    " em " + produto.getNome() + ".");
            return "redirect:/admin/estoque/produtos";
        } catch (BusinessException e) {
            model.addAttribute("erroNegocio", e.getMessage());
            return "admin/estoque-entrada-form";
        }
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/estoque-categorias";
    }

    @GetMapping("/categorias/novo")
    public String novaCategoria(Model model) {
        if (!model.containsAttribute("categoria")) {
            model.addAttribute("categoria", new CategoriaEstoqueRequestDTO());
        }
        model.addAttribute("modoEdicao", false);
        return "admin/estoque-categoria-form";
    }

    @GetMapping("/categorias/{id}/editar")
    public String editarCategoria(@PathVariable Long id, Model model) {
        var categoria = categoriaService.buscarPorId(id);
        model.addAttribute("categoria", CategoriaEstoqueRequestDTO.builder().nome(categoria.getNome()).build());
        model.addAttribute("categoriaId", id);
        model.addAttribute("modoEdicao", true);
        return "admin/estoque-categoria-form";
    }

    @PostMapping("/categorias")
    public String salvarCategoria(@Valid @ModelAttribute("categoria") CategoriaEstoqueRequestDTO dto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "admin/estoque-categoria-form";
        }
        try {
            categoriaService.salvar(null, dto);
            redirectAttributes.addFlashAttribute("sucesso", "Categoria cadastrada com sucesso!");
            return "redirect:/admin/estoque/categorias";
        } catch (BusinessException e) {
            model.addAttribute("erroNegocio", e.getMessage());
            model.addAttribute("modoEdicao", false);
            return "admin/estoque-categoria-form";
        }
    }

    @PostMapping("/categorias/{id}")
    public String atualizarCategoria(@PathVariable Long id,
                                     @Valid @ModelAttribute("categoria") CategoriaEstoqueRequestDTO dto,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoriaId", id);
            model.addAttribute("modoEdicao", true);
            return "admin/estoque-categoria-form";
        }
        try {
            categoriaService.salvar(id, dto);
            redirectAttributes.addFlashAttribute("sucesso", "Categoria atualizada com sucesso!");
            return "redirect:/admin/estoque/categorias";
        } catch (BusinessException e) {
            model.addAttribute("categoriaId", id);
            model.addAttribute("erroNegocio", e.getMessage());
            model.addAttribute("modoEdicao", true);
            return "admin/estoque-categoria-form";
        }
    }

    @PostMapping("/categorias/{id}/excluir")
    public String excluirCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Categoria excluída com sucesso!");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/estoque/categorias";
    }

    @GetMapping("/unidades")
    public String unidades(Model model) {
        model.addAttribute("unidades", unidadeService.listarTodas());
        return "admin/estoque-unidades";
    }

    @GetMapping("/unidades/novo")
    public String novaUnidade(Model model) {
        if (!model.containsAttribute("unidade")) {
            model.addAttribute("unidade", new UnidadeMedidaRequestDTO());
        }
        model.addAttribute("modoEdicao", false);
        return "admin/estoque-unidade-form";
    }

    @GetMapping("/unidades/{id}/editar")
    public String editarUnidade(@PathVariable Long id, Model model) {
        var unidade = unidadeService.buscarPorId(id);
        model.addAttribute("unidade", UnidadeMedidaRequestDTO.builder().nome(unidade.getNome()).build());
        model.addAttribute("unidadeId", id);
        model.addAttribute("modoEdicao", true);
        return "admin/estoque-unidade-form";
    }

    @PostMapping("/unidades")
    public String salvarUnidade(@Valid @ModelAttribute("unidade") UnidadeMedidaRequestDTO dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "admin/estoque-unidade-form";
        }
        try {
            unidadeService.salvar(null, dto);
            redirectAttributes.addFlashAttribute("sucesso", "Unidade cadastrada com sucesso!");
            return "redirect:/admin/estoque/unidades";
        } catch (BusinessException e) {
            model.addAttribute("erroNegocio", e.getMessage());
            model.addAttribute("modoEdicao", false);
            return "admin/estoque-unidade-form";
        }
    }

    @PostMapping("/unidades/{id}")
    public String atualizarUnidade(@PathVariable Long id,
                                   @Valid @ModelAttribute("unidade") UnidadeMedidaRequestDTO dto,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("unidadeId", id);
            model.addAttribute("modoEdicao", true);
            return "admin/estoque-unidade-form";
        }
        try {
            unidadeService.salvar(id, dto);
            redirectAttributes.addFlashAttribute("sucesso", "Unidade atualizada com sucesso!");
            return "redirect:/admin/estoque/unidades";
        } catch (BusinessException e) {
            model.addAttribute("unidadeId", id);
            model.addAttribute("erroNegocio", e.getMessage());
            model.addAttribute("modoEdicao", true);
            return "admin/estoque-unidade-form";
        }
    }

    @PostMapping("/unidades/{id}/excluir")
    public String excluirUnidade(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            unidadeService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Unidade excluída com sucesso!");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/estoque/unidades";
    }

    @GetMapping("/produtos")
    public String produtos(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        return "admin/estoque-produtos";
    }

    @GetMapping("/produtos/novo")
    public String novoProduto(Model model) {
        if (!model.containsAttribute("produto")) {
            model.addAttribute("produto", new ProdutoEstoqueRequestDTO());
        }
        adicionarListasProduto(model);
        model.addAttribute("modoEdicao", false);
        return "admin/estoque-produto-form";
    }

    @GetMapping("/produtos/{id}/editar")
    public String editarProduto(@PathVariable Long id, Model model) {
        var produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", ProdutoEstoqueRequestDTO.builder()
                .nome(produto.getNome())
                .categoriaId(produto.getCategoria().getId())
                .unidadeId(produto.getUnidade().getId())
                .estoqueMinimo(produto.getEstoqueMinimo())
                .estoqueMaximo(produto.getEstoqueMaximo())
                .build());
        model.addAttribute("produtoId", id);
        model.addAttribute("estoqueAtual", produto.getEstoqueAtual());
        adicionarListasProduto(model);
        model.addAttribute("modoEdicao", true);
        return "admin/estoque-produto-form";
    }

    @PostMapping("/produtos")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoEstoqueRequestDTO dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            adicionarListasProduto(model);
            model.addAttribute("modoEdicao", false);
            return "admin/estoque-produto-form";
        }
        try {
            produtoService.salvar(null, dto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso! O estoque atual começou em 0.");
            return "redirect:/admin/estoque/produtos";
        } catch (BusinessException e) {
            model.addAttribute("erroNegocio", e.getMessage());
            adicionarListasProduto(model);
            model.addAttribute("modoEdicao", false);
            return "admin/estoque-produto-form";
        }
    }

    @PostMapping("/produtos/{id}")
    public String atualizarProduto(@PathVariable Long id,
                                   @Valid @ModelAttribute("produto") ProdutoEstoqueRequestDTO dto,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        var existente = produtoService.buscarPorId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("produtoId", id);
            model.addAttribute("estoqueAtual", existente.getEstoqueAtual());
            adicionarListasProduto(model);
            model.addAttribute("modoEdicao", true);
            return "admin/estoque-produto-form";
        }
        try {
            produtoService.salvar(id, dto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto atualizado com sucesso!");
            return "redirect:/admin/estoque/produtos";
        } catch (BusinessException e) {
            model.addAttribute("produtoId", id);
            model.addAttribute("estoqueAtual", existente.getEstoqueAtual());
            model.addAttribute("erroNegocio", e.getMessage());
            adicionarListasProduto(model);
            model.addAttribute("modoEdicao", true);
            return "admin/estoque-produto-form";
        }
    }

    @PostMapping("/produtos/{id}/excluir")
    public String excluirProduto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso!");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/estoque/produtos";
    }

    private void adicionarListasProduto(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("unidades", unidadeService.listarTodas());
    }
}
