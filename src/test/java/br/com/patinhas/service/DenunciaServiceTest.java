package br.com.patinhas.service;

import br.com.patinhas.dto.request.DenunciaRequestDTO;
import br.com.patinhas.dto.request.DenunciaUpdateStatusDTO;
import br.com.patinhas.dto.response.DenunciaResponseDTO;
import br.com.patinhas.entity.Denuncia;
import br.com.patinhas.entity.DenunciaHistorico;
import br.com.patinhas.entity.enums.StatusDenuncia;
import br.com.patinhas.repository.DenunciaHistoricoRepository;
import br.com.patinhas.repository.DenunciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DenunciaServiceTest {

    @Mock
    private DenunciaRepository denunciaRepository;

    @Mock
    private DenunciaHistoricoRepository denunciaHistoricoRepository;

    @InjectMocks
    private DenunciaService denunciaService;

    @Test
    void registrarAnonima_deveSalvarComStatusPendenteECriarHistorico() {
        DenunciaRequestDTO dto = DenunciaRequestDTO.builder()
                .descricao("Animal abandonado na rua principal")
                .local("Centro")
                .build();

        when(denunciaRepository.save(any(Denuncia.class))).thenAnswer(invocation -> {
            Denuncia denuncia = invocation.getArgument(0);
            denuncia.setId(1L);
            return denuncia;
        });
        when(denunciaHistoricoRepository.save(any(DenunciaHistorico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DenunciaResponseDTO resultado = denunciaService.registrarAnonima(dto);

        ArgumentCaptor<Denuncia> denunciaCaptor = ArgumentCaptor.forClass(Denuncia.class);
        verify(denunciaRepository).save(denunciaCaptor.capture());
        assertEquals(StatusDenuncia.PENDENTE, denunciaCaptor.getValue().getStatus());

        ArgumentCaptor<DenunciaHistorico> historicoCaptor = ArgumentCaptor.forClass(DenunciaHistorico.class);
        verify(denunciaHistoricoRepository).save(historicoCaptor.capture());
        assertEquals(StatusDenuncia.PENDENTE, historicoCaptor.getValue().getStatusNovo());
        assertEquals("Denúncia recebida pelo site.", historicoCaptor.getValue().getAnotacao());

        assertNotNull(resultado);
        assertEquals(StatusDenuncia.PENDENTE, resultado.getStatus());
    }

    @Test
    void atualizarStatus_semMudancaDeStatusESemAnotacao_naoDeveChamarSave() {
        Denuncia denuncia = Denuncia.builder()
                .id(1L)
                .descricao("Descrição da denúncia")
                .local("Bairro Norte")
                .status(StatusDenuncia.PENDENTE)
                .build();

        when(denunciaRepository.findById(1L)).thenReturn(Optional.of(denuncia));

        DenunciaUpdateStatusDTO dto = DenunciaUpdateStatusDTO.builder()
                .status(StatusDenuncia.PENDENTE)
                .observacoesInternas(null)
                .build();

        denunciaService.atualizarStatus(1L, dto);

        verify(denunciaRepository, never()).save(any());
        verify(denunciaHistoricoRepository, never()).save(any());
    }
}
