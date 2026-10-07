package br.com.patinhas.controller.admin;

import br.com.patinhas.entity.CategoriaEstoque;
import br.com.patinhas.entity.ProdutoEstoque;
import br.com.patinhas.entity.UnidadeMedida;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.service.CategoriaEstoqueService;
import br.com.patinhas.service.ProdutoEstoqueService;
import br.com.patinhas.service.UnidadeMedidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/estoque")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminEstoqueController {

    private final CategoriaEstoqueService categoriaService;
    private final UnidadeMedidaService unidadeService;
    private final ProdutoEstoqueService produtoService;

    @GetMapping
    public String index(Model model) {

        model.addAttribute(
                "categorias",
                categoriaService.listarTodas()
        );

        model.addAttribute(
                "unidades",
                unidadeService.listarTodas()
        );

        model.addAttribute(
                "produtos",
                produtoService.listarTodos()
        );

        return "admin/estoque";
    }

    // ------------------------
    // CATEGORIA
    // ------------------------

    @GetMapping("/categorias/nova")
    public String novaCategoria(Model model) {

        model.addAttribute(
                "categoria",
                new CategoriaEstoque()
        );

        return "admin/estoque-categoria-form";
    }

    @PostMapping("/categorias")
    public String salvarCategoria(
            @ModelAttribute CategoriaEstoque categoria,
            RedirectAttributes redirectAttributes
    ) {

        try {

            categoriaService.salvar(categoria);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Categoria salva com sucesso."
            );

        } catch (BusinessException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/admin/estoque";
    }

    @PostMapping("/categorias/{id}/excluir")
    public String excluirCategoria(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            categoriaService.excluir(id);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Categoria excluída com sucesso."
            );

        } catch (BusinessException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/admin/estoque";
    }

    // ------------------------
    // UNIDADE
    // ------------------------

    @GetMapping("/unidades/nova")
    public String novaUnidade(Model model) {

        model.addAttribute(
                "unidade",
                new UnidadeMedida()
        );

        return "admin/estoque-unidade-form";
    }

    @PostMapping("/unidades")
    public String salvarUnidade(
            @ModelAttribute UnidadeMedida unidade,
            RedirectAttributes redirectAttributes
    ) {

        try {

            unidadeService.salvar(unidade);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Unidade salva com sucesso."
            );

        } catch (BusinessException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/admin/estoque";
    }

    @PostMapping("/unidades/{id}/excluir")
    public String excluirUnidade(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            unidadeService.excluir(id);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Unidade excluída com sucesso."
            );

        } catch (BusinessException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/admin/estoque";
    }

    // ------------------------
    // PRODUTO
    // ------------------------

    @GetMapping("/produtos/novo")
    public String novoProduto(Model model) {

        model.addAttribute(
                "produto",
                new ProdutoEstoque()
        );

        carregarListas(model);

        return "admin/estoque-produto-form";
    }

    @PostMapping("/produtos")
    public String salvarProduto(
            @ModelAttribute ProdutoEstoque produto,
            @RequestParam Long categoriaId,
            @RequestParam Long unidadeId,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        try {

            produtoService.cadastrar(
                    produto,
                    categoriaId,
                    unidadeId
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Produto cadastrado com sucesso."
            );

            return "redirect:/admin/estoque";

        } catch (BusinessException e) {

            model.addAttribute("erro", e.getMessage());
            carregarListas(model);

            return "admin/estoque-produto-form";
        }
    }

    @GetMapping("/produtos/{id}/editar")
    public String editarProduto(
            @PathVariable Long id,
            Model model
    ) {

        ProdutoEstoque produto =
                produtoService.buscarPorId(id);

        model.addAttribute("produto", produto);
        model.addAttribute("produtoId", id);
        model.addAttribute(
                "categoriaId",
                produto.getCategoria().getId()
        );
        model.addAttribute(
                "unidadeId",
                produto.getUnidade().getId()
        );

        carregarListas(model);

        return "admin/estoque-produto-form";
    }

    @PostMapping("/produtos/{id}")
    public String atualizarProduto(
            @PathVariable Long id,
            @ModelAttribute ProdutoEstoque produto,
            @RequestParam Long categoriaId,
            @RequestParam Long unidadeId,
            RedirectAttributes redirectAttributes
    ) {

        try {

            produtoService.atualizar(
                    id,
                    produto,
                    categoriaId,
                    unidadeId
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Produto atualizado com sucesso."
            );

        } catch (BusinessException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/admin/estoque";
    }

    private void carregarListas(Model model) {

        model.addAttribute(
                "categorias",
                categoriaService.listarTodas()
        );

        model.addAttribute(
                "unidades",
                unidadeService.listarTodas()
        );
    }
}