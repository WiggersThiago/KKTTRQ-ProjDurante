package br.com.patinhas.service;

import br.com.patinhas.dto.request.SolicitacaoAdocaoRequestDTO;
import br.com.patinhas.dto.request.SolicitacaoAdocaoUpdateStatusDTO;
import br.com.patinhas.dto.response.SolicitacaoAdocaoHistoricoResponseDTO;
import br.com.patinhas.dto.response.SolicitacaoAdocaoResponseDTO;
import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.SolicitacaoAdocao;
import br.com.patinhas.entity.SolicitacaoAdocaoHistorico;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.AnimalRepository;
import br.com.patinhas.repository.SolicitacaoAdocaoHistoricoRepository;
import br.com.patinhas.repository.SolicitacaoAdocaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitacaoAdocaoService {

    private final SolicitacaoAdocaoRepository solicitacaoAdocaoRepository;
    private final SolicitacaoAdocaoHistoricoRepository historicoRepository;
    private final AnimalRepository animalRepository;

    @Transactional
    public SolicitacaoAdocaoResponseDTO cadastrar(SolicitacaoAdocaoRequestDTO dto) {

        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Animal não encontrado."));

        validarDisponivelParaAdocao(animal);

        SolicitacaoAdocao solicitacao = SolicitacaoAdocao.builder()
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .email(textoObrigatorioNoBanco(dto.getEmail()))
                .cidade(textoObrigatorioNoBanco(dto.getCidade()))
                .motivoAdocao(textoOpcional(dto.getMotivoAdocao()))
                .possuiOutrosAnimais(dto.getPossuiOutrosAnimais())
                .possuiEspacoAdequado(dto.getPossuiEspacoAdequado())
                .todosConcordam(dto.getTodosConcordam())
                .jaTeveAnimais(dto.getJaTeveAnimais())
                .observacoes(textoOpcional(dto.getObservacoes()))
                .status(StatusSolicitacaoAdocao.NOVA)
                .animal(animal)
                .build();

        solicitacao = solicitacaoAdocaoRepository.save(solicitacao);

        registrarHistorico(
                solicitacao,
                null,
                StatusSolicitacaoAdocao.NOVA,
                "Solicitação criada."
        );

        log.info(
                "Nova solicitação de adoção criada. id={}, animalId={}",
                solicitacao.getId(),
                animal.getId()
        );

        return SolicitacaoAdocaoResponseDTO.fromEntity(solicitacao);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoAdocaoResponseDTO> listarTodas() {

        return solicitacaoAdocaoRepository
                .findAllByOrderByDataSolicitacaoDesc()
                .stream()
                .map(SolicitacaoAdocaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoAdocaoResponseDTO> listarPorStatus(
            StatusSolicitacaoAdocao status) {

        return solicitacaoAdocaoRepository
                .findAllByStatusOrderByDataSolicitacaoDesc(status)
                .stream()
                .map(SolicitacaoAdocaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SolicitacaoAdocaoResponseDTO buscarPorId(Long id) {

        SolicitacaoAdocao solicitacao = buscarEntidade(id);

        return SolicitacaoAdocaoResponseDTO.fromEntity(solicitacao);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoAdocaoHistoricoResponseDTO> listarHistorico(Long id) {

        buscarEntidade(id);

        return historicoRepository
                .findAllBySolicitacaoIdOrderByRegistradoEmDesc(id)
                .stream()
                .map(SolicitacaoAdocaoHistoricoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void atualizarStatus(
            Long id,
            SolicitacaoAdocaoUpdateStatusDTO dto) {

        SolicitacaoAdocao solicitacao = buscarEntidade(id);

        StatusSolicitacaoAdocao statusAnterior = solicitacao.getStatus();
        StatusSolicitacaoAdocao novoStatus = dto.getStatus();

        if (statusAnterior == novoStatus) {
            throw new BusinessException(
                    "A solicitação já está com esse status."
            );
        }

        validarMudancaDeStatus(solicitacao, statusAnterior, novoStatus);

        aplicarRegraDoNovoStatus(solicitacao, statusAnterior, novoStatus);

        solicitacao.setStatus(novoStatus);

        solicitacaoAdocaoRepository.save(solicitacao);

        registrarHistorico(
                solicitacao,
                statusAnterior,
                novoStatus,
                dto.getAnotacao()
        );

        log.info(
                "Status da solicitação id={} alterado de {} para {}",
                id,
                statusAnterior,
                novoStatus
        );
    }

    private String textoObrigatorioNoBanco(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        return valor.trim();
    }

    private String textoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private void validarDisponivelParaAdocao(Animal animal) {

        if (!Boolean.TRUE.equals(animal.getAtivo())
                || animal.getStatusAdocao() != StatusAdocao.DISPONIVEL) {

            throw new BusinessException(
                    "Este animal não está disponível para adoção."
            );
        }
    }

    public List<StatusSolicitacaoAdocao> proximosStatus(StatusSolicitacaoAdocao atual) {
        if (atual == null) {
            return List.of();
        }
        return switch (atual) {
            case NOVA -> List.of(
                    StatusSolicitacaoAdocao.EM_ANALISE,
                    StatusSolicitacaoAdocao.RECUSADA);
            case EM_ANALISE -> List.of(
                    StatusSolicitacaoAdocao.CONTATADO,
                    StatusSolicitacaoAdocao.RECUSADA);
            case CONTATADO -> List.of(
                    StatusSolicitacaoAdocao.APROVADA,
                    StatusSolicitacaoAdocao.RECUSADA);
            case APROVADA -> List.of(
                    StatusSolicitacaoAdocao.CONCLUIDA,
                    StatusSolicitacaoAdocao.RECUSADA);
            case CONCLUIDA, RECUSADA -> List.of();
        };
    }

    private void validarMudancaDeStatus(
            SolicitacaoAdocao solicitacao,
            StatusSolicitacaoAdocao statusAnterior,
            StatusSolicitacaoAdocao novoStatus) {

        if (!proximosStatus(statusAnterior).contains(novoStatus)) {
            throw new BusinessException(
                    "Essa mudança de status não faz parte da esteira da solicitação."
            );
        }

        if (novoStatus == StatusSolicitacaoAdocao.APROVADA) {
            Animal animal = solicitacao.getAnimal();

            if (animal.getStatusAdocao() != StatusAdocao.DISPONIVEL) {
                throw new BusinessException(
                        "Este animal não está disponível para aprovação."
                );
            }

            boolean outraAprovada =
                    solicitacaoAdocaoRepository.existsByAnimalIdAndStatus(
                            animal.getId(),
                            StatusSolicitacaoAdocao.APROVADA
                    );

            if (outraAprovada) {
                throw new BusinessException(
                        "Já existe uma solicitação aprovada para este animal. "
                                + "Recuse a aprovada antes de aprovar outra."
                );
            }
        }
    }

    private void aplicarRegraDoNovoStatus(
            SolicitacaoAdocao solicitacao,
            StatusSolicitacaoAdocao statusAnterior,
            StatusSolicitacaoAdocao novoStatus) {

        Animal animal = solicitacao.getAnimal();

        if (novoStatus == StatusSolicitacaoAdocao.APROVADA) {
            animal.setStatusAdocao(StatusAdocao.EM_PROCESSO);
            animalRepository.save(animal);
        }

        if (novoStatus == StatusSolicitacaoAdocao.CONCLUIDA) {
            LocalDateTime agora = LocalDateTime.now();
            animal.setStatusAdocao(StatusAdocao.ADOTADO);
            if (animal.getDataAdocao() == null) {
                animal.setDataAdocao(agora);
            }
            animalRepository.save(animal);
            solicitacao.setDataConclusao(agora);
        }

        if (novoStatus == StatusSolicitacaoAdocao.RECUSADA
                && statusAnterior == StatusSolicitacaoAdocao.APROVADA
                && animal.getStatusAdocao() == StatusAdocao.EM_PROCESSO) {
            animal.setStatusAdocao(StatusAdocao.DISPONIVEL);
            animalRepository.save(animal);
        }
    }

    private void registrarHistorico(
            SolicitacaoAdocao solicitacao,
            StatusSolicitacaoAdocao statusAnterior,
            StatusSolicitacaoAdocao statusNovo,
            String anotacao) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String registradoPor = null;

        if (authentication != null
                && authentication.isAuthenticated()) {

            registradoPor = authentication.getName();
        }

        SolicitacaoAdocaoHistorico historico =
                SolicitacaoAdocaoHistorico.builder()
                        .solicitacao(solicitacao)
                        .statusAnterior(statusAnterior)
                        .statusNovo(statusNovo)
                        .anotacao(anotacao)
                        .registradoPor(registradoPor)
                        .build();

        historicoRepository.save(historico);
    }

    private SolicitacaoAdocao buscarEntidade(Long id) {

        return solicitacaoAdocaoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Solicitação de adoção não encontrada."
                        ));
    }
    @Transactional(readOnly = true)
    public long contarPendentes() {
    return solicitacaoAdocaoRepository.countByStatus(StatusSolicitacaoAdocao.NOVA)
            + solicitacaoAdocaoRepository.countByStatus(StatusSolicitacaoAdocao.EM_ANALISE);
    }

    @Transactional(readOnly = true)
    public long contarNovas() {
    return solicitacaoAdocaoRepository.countByStatus(StatusSolicitacaoAdocao.NOVA);
    }

    @Transactional(readOnly = true)
    public long contarConcluidas() {
    return solicitacaoAdocaoRepository.countByStatus(StatusSolicitacaoAdocao.CONCLUIDA);
    }
}