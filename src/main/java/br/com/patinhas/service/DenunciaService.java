package br.com.patinhas.service;

import br.com.patinhas.dto.request.DenunciaRequestDTO;
import br.com.patinhas.dto.request.DenunciaUpdateStatusDTO;
import br.com.patinhas.dto.response.DenunciaResponseDTO;
import br.com.patinhas.entity.Denuncia;
import br.com.patinhas.entity.enums.StatusDenuncia;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.DenunciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DenunciaService {

    private final DenunciaRepository denunciaRepository;

    /**
     * Registra uma denúncia anônima no sistema.
     *
     * Nenhuma informação do denunciante é registrada propositalmente.
     */
    @Transactional
    public DenunciaResponseDTO registrarAnonima(DenunciaRequestDTO dto) {
        Denuncia denuncia = Denuncia.builder()
                .descricao(dto.getDescricao())
                .local(dto.getLocal())
                .dataOcorrido(dto.getDataOcorrido())
                .status(StatusDenuncia.PENDENTE)
                .build();
        log.info("Nova denúncia anônima registrada para o local: {}", dto.getLocal());
        return DenunciaResponseDTO.fromEntity(denunciaRepository.save(denuncia));
    }

    @Transactional(readOnly = true)
    public List<DenunciaResponseDTO> listarTodas() {
        return denunciaRepository.findAllByOrderByDataEnvioDesc().stream()
                .map(DenunciaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DenunciaResponseDTO> listarPorStatus(StatusDenuncia status) {
        return denunciaRepository.findAllByStatusOrderByDataEnvioDesc(status).stream()
                .map(DenunciaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DenunciaResponseDTO buscarPorId(Long id) {
        return DenunciaResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Denuncia buscarEntidade(Long id) {
        return denunciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia", id));
    }

    @Transactional
    public DenunciaResponseDTO atualizarStatus(Long id, DenunciaUpdateStatusDTO dto) {
        Denuncia denuncia = buscarEntidade(id);
        denuncia.setStatus(dto.getStatus());
        if (dto.getObservacoesInternas() != null) {
            denuncia.setObservacoesInternas(dto.getObservacoesInternas());
        }
        log.info("Atualizado status da denúncia id={} para {}", id, dto.getStatus());
        return DenunciaResponseDTO.fromEntity(denunciaRepository.save(denuncia));
    }

    @Transactional(readOnly = true)
    public long contarPendentes() {
        return denunciaRepository.countByStatus(StatusDenuncia.PENDENTE);
    }
}
