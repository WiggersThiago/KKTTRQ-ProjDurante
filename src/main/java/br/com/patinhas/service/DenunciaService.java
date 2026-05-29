package br.com.patinhas.service;

import br.com.patinhas.dto.request.DenunciaUpdateStatusDTO;
import br.com.patinhas.dto.response.DenunciaHistoricoResponseDTO;
import br.com.patinhas.dto.response.DenunciaResponseDTO;
import br.com.patinhas.entity.Denuncia;
import br.com.patinhas.entity.DenunciaHistorico;
import br.com.patinhas.entity.enums.StatusDenuncia;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.DenunciaHistoricoRepository;
import br.com.patinhas.repository.DenunciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DenunciaService {

    private final DenunciaRepository denunciaRepository;
    private final DenunciaHistoricoRepository denunciaHistoricoRepository;

    @Transactional
    public DenunciaResponseDTO registrarAnonima(br.com.patinhas.dto.request.DenunciaRequestDTO dto) {
        Denuncia denuncia = Denuncia.builder()
                .descricao(dto.getDescricao())
                .local(dto.getLocal())
                .dataOcorrido(dto.getDataOcorrido())
                .status(StatusDenuncia.PENDENTE)
                .build();
        log.info("Nova denúncia anônima registrada para o local: {}", dto.getLocal());
        Denuncia salva = denunciaRepository.save(denuncia);
        registrarHistorico(salva, null, StatusDenuncia.PENDENTE,
                "Denúncia recebida pelo site.", "Sistema");
        return DenunciaResponseDTO.fromEntity(salva);
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

    @Transactional(readOnly = true)
    public List<DenunciaHistoricoResponseDTO> listarHistorico(Long denunciaId) {
        buscarEntidade(denunciaId);
        return denunciaHistoricoRepository.findAllByDenunciaIdOrderByRegistradoEmDesc(denunciaId)
                .stream()
                .map(DenunciaHistoricoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public DenunciaResponseDTO atualizarStatus(Long id, DenunciaUpdateStatusDTO dto) {
        Denuncia denuncia = buscarEntidade(id);
        StatusDenuncia statusAnterior = denuncia.getStatus();
        StatusDenuncia statusNovo = dto.getStatus();
        String anotacao = StringUtils.hasText(dto.getObservacoesInternas())
                ? dto.getObservacoesInternas().trim()
                : null;

        boolean statusMudou = statusAnterior != statusNovo;
        boolean temAnotacao = anotacao != null;

        if (!statusMudou && !temAnotacao) {
            return DenunciaResponseDTO.fromEntity(denuncia);
        }

        if (statusMudou) {
            denuncia.setStatus(statusNovo);
        }
        if (temAnotacao) {
            denuncia.setObservacoesInternas(anotacao);
        }

        Denuncia salva = denunciaRepository.save(denuncia);
        registrarHistorico(salva, statusMudou ? statusAnterior : null, statusNovo,
                anotacao, obterUsuarioLogado());
        log.info("Atualizado status da denúncia id={} para {}", id, statusNovo);
        return DenunciaResponseDTO.fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public long contarPendentes() {
        return denunciaRepository.countByStatus(StatusDenuncia.PENDENTE);
    }

    private void registrarHistorico(Denuncia denuncia,
                                    StatusDenuncia statusAnterior,
                                    StatusDenuncia statusNovo,
                                    String anotacao,
                                    String registradoPor) {
        denunciaHistoricoRepository.save(DenunciaHistorico.builder()
                .denuncia(denuncia)
                .statusAnterior(statusAnterior)
                .statusNovo(statusNovo)
                .anotacao(anotacao)
                .registradoPor(registradoPor)
                .build());
    }

    private String obterUsuarioLogado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "Administrador";
        }
        return auth.getName();
    }
}
