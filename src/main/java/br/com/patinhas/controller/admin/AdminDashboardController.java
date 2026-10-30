
package br.com.patinhas.controller.admin;

import br.com.patinhas.entity.ProdutoEstoque;
import br.com.patinhas.service.AnimalService;
import br.com.patinhas.service.DenunciaService;
import br.com.patinhas.service.EventoService;
import br.com.patinhas.service.ProdutoEstoqueService;
import br.com.patinhas.service.SolicitacaoAdocaoService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AnimalService animalService;
    private final EventoService eventoService;
    private final DenunciaService denunciaService;
    private final SolicitacaoAdocaoService solicitacaoAdocaoService;

    // Novo serviço responsável pelos produtos do estoque
    private final ProdutoEstoqueService produtoEstoqueService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {

        // Dados que já existiam no dashboard
        model.addAttribute("totalAnimais",
                animalService.contarAtivos());

        model.addAttribute("animaisDisponiveis",
                animalService.contarDisponiveis());

        model.addAttribute("eventosAtivos",
                eventoService.contarAtivos());

        model.addAttribute("denunciasPendentes",
                denunciaService.contarPendentes());

        model.addAttribute("animaisTratamento",
                animalService.contarEmTratamentoOuStandBy());

        model.addAttribute("adocoesConcluidas",
                animalService.contarAdocoesConcluidas());

        model.addAttribute("solicitacoesPendentes",
                solicitacaoAdocaoService.contarPendentes());

        model.addAttribute("adocoesPorMes",
                animalService.contarAdocoesPorMes());

        model.addAttribute("animaisPorEspecie",
                animalService.contarAnimaisPorEspecie());

        model.addAttribute("animaisPorStatus",
                animalService.contarAnimaisPorStatus());

        model.addAttribute("tempoMedioAteAdocao",
                animalService.calcularTempoMedioAteAdocao());

        // NOVO: busca os produtos com os saldos calculados
        List<ProdutoEstoque> produtosEstoque =
                produtoEstoqueService.listarParaSemaforo();

        // NOVO: conta os produtos críticos
        long estoqueCriticos = produtosEstoque.stream()
                .filter(p -> "CRITICO".equals(p.getStatusEstoque()))
                .count();

        // NOVO: conta os produtos que precisam de atenção
        long estoqueAtencao = produtosEstoque.stream()
                .filter(p -> "ATENCAO".equals(p.getStatusEstoque()))
                .count();

        // NOVO: conta os produtos acima do estoque máximo
        long estoqueExcedentes = produtosEstoque.stream()
                .filter(p -> "EXCEDENTE".equals(p.getStatusEstoque()))
                .count();

        // Disponibiliza os números para o HTML
        model.addAttribute("estoqueCriticos", estoqueCriticos);
        model.addAttribute("estoqueAtencao", estoqueAtencao);
        model.addAttribute("estoqueExcedentes", estoqueExcedentes);

        return "admin/dashboard";
    }
}
