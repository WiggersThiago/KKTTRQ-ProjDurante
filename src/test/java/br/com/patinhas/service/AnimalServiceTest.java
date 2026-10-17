package br.com.patinhas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.patinhas.dto.request.AnimalRequestDTO;
import br.com.patinhas.dto.response.AnimalResponseDTO;
import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.enums.PorteAnimal;
import br.com.patinhas.entity.enums.SexoAnimal;
import br.com.patinhas.entity.enums.SituacaoAnimal;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.entity.enums.TipoHistoricoAnimal;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.AnimalRepository;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ImageStorageService imageStorageService;
    
    @Mock
    private EspecieService especieService;

    @Mock
    private AnimalHistoricoService animalHistoricoService;

    @InjectMocks
    private AnimalService animalService;

    @Test
    void cadastrar_deveChamarSaveERetornarAnimalResponseDTO() {
        AnimalRequestDTO dto = AnimalRequestDTO.builder()
                .nome("Rex")
                .idade(3)
                .porte(PorteAnimal.MEDIO)
                .sexo(SexoAnimal.MACHO)
                .castrado(true)
                .vacinado(true)
                .build();

        when(imageStorageService.salvar(null, "animais")).thenReturn(null);
        when(especieService.buscarOuCriar(any())).thenReturn(null);
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> {
            Animal animal = invocation.getArgument(0);
            animal.setId(1L);
            animal.setDataCadastro(LocalDateTime.now());
            return animal;
        });

        AnimalResponseDTO resultado = animalService.cadastrar(dto);

        verify(animalRepository).save(any(Animal.class));
        verify(animalHistoricoService).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.CADASTRO),
                any(LocalDateTime.class),
                eq("Animal cadastrado no sistema"),
                eq(true)
        );
        verify(animalHistoricoService).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.DISPONIBILIZACAO_ADOCAO),
                any(LocalDateTime.class),
                eq("Animal ficou disponível para adoção"),
                eq(true)
        );
        verify(animalHistoricoService, never()).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.TRATAMENTO),
                any(),
                eq(true)
        );
        assertNotNull(resultado);
        assertEquals("Rex", resultado.getNome());
        assertTrue(resultado.getAtivo());
    }

    @Test
    void cadastrar_emTratamentoIndisponivel_deveRegistrarCadastroETratamento() {
        AnimalRequestDTO dto = AnimalRequestDTO.builder()
                .nome("Luna")
                .idade(2)
                .porte(PorteAnimal.PEQUENO)
                .sexo(SexoAnimal.FEMEA)
                .statusAdocao(StatusAdocao.INDISPONIVEL)
                .situacaoAnimal(SituacaoAnimal.EM_TRATAMENTO)
                .castrado(true)
                .vacinado(true)
                .build();

        when(imageStorageService.salvar(null, "animais")).thenReturn(null);
        when(especieService.buscarOuCriar(any())).thenReturn(null);
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> {
            Animal animal = invocation.getArgument(0);
            animal.setId(2L);
            animal.setDataCadastro(LocalDateTime.now());
            return animal;
        });

        animalService.cadastrar(dto);

        verify(animalHistoricoService).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.CADASTRO),
                any(LocalDateTime.class),
                eq("Animal cadastrado no sistema"),
                eq(true)
        );
        verify(animalHistoricoService).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.TRATAMENTO),
                eq("Animal entrou em tratamento"),
                eq(true)
        );
        verify(animalHistoricoService, never()).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.DISPONIBILIZACAO_ADOCAO),
                any(LocalDateTime.class),
                any(),
                eq(true)
        );
    }

    @Test
    void atualizarStatus_paraDisponivel_deveRegistrarSomenteDisponibilizacao() {
        Animal animal = Animal.builder()
                .id(1L)
                .nome("Rex")
                .idade(3)
                .porte(PorteAnimal.MEDIO)
                .sexo(SexoAnimal.MACHO)
                .statusAdocao(StatusAdocao.INDISPONIVEL)
                .ativo(true)
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(animal)).thenReturn(animal);

        animalService.atualizarStatus(1L, StatusAdocao.DISPONIVEL);

        verify(animalHistoricoService).registrar(
                eq(animal),
                eq(TipoHistoricoAnimal.DISPONIBILIZACAO_ADOCAO),
                eq("Animal ficou disponível para adoção"),
                eq(true)
        );
        verify(animalHistoricoService, never()).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.ALTERACAO_STATUS),
                any(),
                eq(true)
        );
        verify(animalHistoricoService, never()).registrar(
                any(Animal.class),
                eq(TipoHistoricoAnimal.ADOCAO_CONCLUIDA),
                any(),
                eq(true)
        );
    }

    @Test
    void atualizarStatus_paraIndisponivel_deveRegistrarAlteracaoDeStatus() {
        Animal animal = Animal.builder()
                .id(1L)
                .nome("Rex")
                .idade(3)
                .porte(PorteAnimal.MEDIO)
                .sexo(SexoAnimal.MACHO)
                .statusAdocao(StatusAdocao.DISPONIVEL)
                .ativo(true)
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(animal)).thenReturn(animal);

        animalService.atualizarStatus(1L, StatusAdocao.INDISPONIVEL);

        verify(animalHistoricoService).registrar(
                eq(animal),
                eq(TipoHistoricoAnimal.ALTERACAO_STATUS),
                eq("Status de adoção alterado de Disponível para adoção para Indisponível"),
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
    void desativar_deveSetarAtivoFalseEDestaqueFalse() {
        Animal animal = Animal.builder()
                .id(1L)
                .nome("Rex")
                .idade(3)
                .porte(PorteAnimal.MEDIO)
                .sexo(SexoAnimal.MACHO)
                .ativo(true)
                .destaque(true)
                .build();

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(animal)).thenReturn(animal);

        animalService.desativar(1L);

        assertFalse(animal.getAtivo());
        assertFalse(animal.getDestaque());
        verify(animalRepository).save(animal);
    }

    @Test
    void buscarEntidade_comIdInexistente_deveLancarResourceNotFoundException() {
        when(animalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> animalService.buscarEntidade(999L));
    }
}
