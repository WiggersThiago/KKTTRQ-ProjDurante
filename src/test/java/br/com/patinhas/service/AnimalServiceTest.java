package br.com.patinhas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
        assertNotNull(resultado);
        assertEquals("Rex", resultado.getNome());
        assertTrue(resultado.getAtivo());
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
