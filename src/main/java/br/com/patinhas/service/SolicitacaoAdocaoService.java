package br.com.patinhas.service;

import br.com.patinhas.dto.request.SolicitacaoAdocaoRequestDTO;
import br.com.patinhas.dto.response.SolicitacaoAdocaoResponseDTO;
import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.SolicitacaoAdocao;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.entity.enums.StatusSolicitacaoAdocao;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.repository.SolicitacaoAdocaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitacaoAdocaoService {

    private final SolicitacaoAdocaoRepository solicitacaoAdocaoRepository;
    private final AnimalService animalService;

    @Transactional
    public SolicitacaoAdocaoResponseDTO cadastrar(SolicitacaoAdocaoRequestDTO dto) {
        Animal animal = animalService.buscarEntidade(dto.getAnimalId());
        validarDisponivelParaAdocao(animal);

        SolicitacaoAdocao solicitacao = SolicitacaoAdocao.builder()
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .cidade(dto.getCidade())
                .motivoAdocao(dto.getMotivoAdocao())
                .possuiOutrosAnimais(dto.getPossuiOutrosAnimais())
                .possuiEspacoAdequado(dto.getPossuiEspacoAdequado())
                .todosConcordam(dto.getTodosConcordam())
                .jaTeveAnimais(dto.getJaTeveAnimais())
                .observacoes(dto.getObservacoes())
                .status(StatusSolicitacaoAdocao.NOVA)
                .animal(animal)
                .build();

        log.info("Nova solicitação de adoção para o animal id={}", animal.getId());

        return SolicitacaoAdocaoResponseDTO.fromEntity(solicitacaoAdocaoRepository.save(solicitacao));
    }

    private void validarDisponivelParaAdocao(Animal animal) {
        if (!Boolean.TRUE.equals(animal.getAtivo())
                || animal.getStatusAdocao() != StatusAdocao.DISPONIVEL) {
            throw new BusinessException("Este animal não está disponível para adoção.");
        }
    }
}
