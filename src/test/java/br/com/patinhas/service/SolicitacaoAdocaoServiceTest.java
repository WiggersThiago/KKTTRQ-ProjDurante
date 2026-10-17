package br.com.patinhas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.patinhas.dto.request.SolicitacaoAdocaoRequestDTO;
import br.com.patinhas.dto.request.SolicitacaoAdocaoUpdateStatusDTO;
import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.SolicitacaoAdocao;
import br.com.patinhas.entity.enums.PorteAnimal;
import br.com.patinhas.entity.enums.SexoAnimal;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import br.com.patinhas.entity.enums.TipoHistoricoAnimal;
import br.com.patinhas.repository.AnimalRepository;
import br.com.patinhas.repository.SolicitacaoAdocaoHistoricoRepository;
import br.com.patinhas.repository.SolicitacaoAdocaoRepository;

@ExtendWith(MockitoExtension.class)
class SolicitacaoAdocaoServiceTest {

    @Mock
    private SolicitacaoAdocaoRepository solicitacaoAdocaoRepository;

    @Mock
    private SolicitacaoAdocaoHistoricoRepository historicoRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AnimalHistoricoService animalHistoricoService;

    @InjectMocks
    private SolicitacaoAdocaoService solicitacaoAdocaoService;

    @Test
    void cadastrar_deveRegistrarInteresseRecebidoNoHistoricoDoAnimal() {
        Animal animal = animalDisponivel();
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(solicitacaoAdocaoRepository.save(any(SolicitacaoAdocao.class))).thenAnswer(invocation -> {
            SolicitacaoAdocao solicitacao = invocation.getArgument(0);
            solicitacao.setId(10L);
            return solicitacao;
        });

        solicitacaoAdocaoService.cadastrar(SolicitacaoAdocaoRequestDTO.builder()
                .nome("Maria")
                .telefone("11999999999")
                .animalId(1L)
                .build());

        verify(animalHistoricoService).registrar(
                eq(animal),
                eq(TipoHistoricoAnimal.INTERESSE_RECEBIDO),
                eq("Interesse de adoção recebido"),
                eq(true)
        );
    }

    @Test
    void atualizarStatus_recusada_deveRegistrarInteresseCancelado() {
        Animal animal = animalDisponivel();
        SolicitacaoAdocao solicitacao = solicitacao(animal, StatusSolicitacaoAdocao.NOVA);
        when(solicitacaoAdocaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoAdocaoRepository.save(solicitacao)).thenReturn(solicitacao);

        solicitacaoAdocaoService.atualizarStatus(10L, SolicitacaoAdocaoUpdateStatusDTO.builder()
                .status(StatusSolicitacaoAdocao.RECUSADA)
                .build());

        verify(animalHistoricoService).registrar(
                eq(animal),
                eq(TipoHistoricoAnimal.INTERESSE_CANCELADO),
                eq("Interesse de adoção encerrado após recusa da solicitação"),
                eq(true)
        );
        verify(animalHistoricoService, never()).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.DISPONIBILIZACAO_ADOCAO),
                any(),
                eq(true)
        );
    }

    @Test
    void atualizarStatus_aprovada_deveRegistrarAdocaoAprovadaSemAlteracaoGenerica() {
        Animal animal = animalDisponivel();
        SolicitacaoAdocao solicitacao = solicitacao(animal, StatusSolicitacaoAdocao.CONTATADO);
        when(solicitacaoAdocaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoAdocaoRepository.existsByAnimalIdAndStatus(1L, StatusSolicitacaoAdocao.APROVADA))
                .thenReturn(false);
        when(solicitacaoAdocaoRepository.save(solicitacao)).thenReturn(solicitacao);

        solicitacaoAdocaoService.atualizarStatus(10L, SolicitacaoAdocaoUpdateStatusDTO.builder()
                .status(StatusSolicitacaoAdocao.APROVADA)
                .build());

        assertEquals(StatusAdocao.EM_PROCESSO, animal.getStatusAdocao());
        verify(animalHistoricoService).registrar(
                eq(animal),
                eq(TipoHistoricoAnimal.ADOCAO_APROVADA),
                eq("Adoção aprovada"),
                eq(true)
        );
        verify(animalHistoricoService, never()).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.ALTERACAO_STATUS),
                any(),
                eq(true)
        );
    }

    @Test
    void atualizarStatus_concluida_deveRegistrarAdocaoConcluida() {
        Animal animal = animalDisponivel();
        animal.setStatusAdocao(StatusAdocao.EM_PROCESSO);
        SolicitacaoAdocao solicitacao = solicitacao(animal, StatusSolicitacaoAdocao.APROVADA);
        when(solicitacaoAdocaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoAdocaoRepository.save(solicitacao)).thenReturn(solicitacao);

        solicitacaoAdocaoService.atualizarStatus(10L, SolicitacaoAdocaoUpdateStatusDTO.builder()
                .status(StatusSolicitacaoAdocao.CONCLUIDA)
                .build());

        assertEquals(StatusAdocao.ADOTADO, animal.getStatusAdocao());
        verify(animalHistoricoService).registrar(
                eq(animal),
                eq(TipoHistoricoAnimal.ADOCAO_CONCLUIDA),
                any(),
                eq("Adoção concluída"),
                eq(true)
        );
        verify(animalHistoricoService, never()).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.ALTERACAO_STATUS),
                any(),
                eq(true)
        );
    }

    @Test
    void atualizarStatus_recusadaAposAprovacao_deveCancelarInteresseEDisponibilizar() {
        Animal animal = animalDisponivel();
        animal.setStatusAdocao(StatusAdocao.EM_PROCESSO);
        SolicitacaoAdocao solicitacao = solicitacao(animal, StatusSolicitacaoAdocao.APROVADA);
        when(solicitacaoAdocaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoAdocaoRepository.save(solicitacao)).thenReturn(solicitacao);

        solicitacaoAdocaoService.atualizarStatus(10L, SolicitacaoAdocaoUpdateStatusDTO.builder()
                .status(StatusSolicitacaoAdocao.RECUSADA)
                .build());

        assertEquals(StatusAdocao.DISPONIVEL, animal.getStatusAdocao());
        verify(animalHistoricoService).registrar(
                eq(animal),
                eq(TipoHistoricoAnimal.INTERESSE_CANCELADO),
                eq("Interesse de adoção encerrado após recusa da solicitação"),
                eq(true)
        );
        verify(animalHistoricoService).registrar(
                eq(animal),
                eq(TipoHistoricoAnimal.DISPONIBILIZACAO_ADOCAO),
                eq("Animal voltou a ficar disponível para adoção após recusa da solicitação"),
                eq(true)
        );
        verify(animalHistoricoService, never()).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.ALTERACAO_STATUS),
                any(),
                eq(true)
        );
    }

    private Animal animalDisponivel() {
        return Animal.builder()
                .id(1L)
                .nome("Rex")
                .idade(3)
                .porte(PorteAnimal.MEDIO)
                .sexo(SexoAnimal.MACHO)
                .statusAdocao(StatusAdocao.DISPONIVEL)
                .ativo(true)
                .build();
    }

    private SolicitacaoAdocao solicitacao(Animal animal, StatusSolicitacaoAdocao status) {
        return SolicitacaoAdocao.builder()
                .id(10L)
                .nome("Maria")
                .telefone("11999999999")
                .email("maria@example.com")
                .cidade("São Paulo")
                .status(status)
                .animal(animal)
                .build();
    }
}
