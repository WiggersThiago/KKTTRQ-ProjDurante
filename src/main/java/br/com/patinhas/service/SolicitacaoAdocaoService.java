package br.com.patinhas.service;

import br.com.patinhas.dto.request.SolicitacaoAdocaoRequestDTO;
import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.SolicitacaoAdocao;
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
    public SolicitacaoAdocao cadastrar(SolicitacaoAdocaoRequestDTO dto) {
        Animal animal = animalService.buscarEntidade(dto.getAnimalId());

        if (!animal.getAtivo()) {
            throw new IllegalArgumentException("O animal selecionado não está disponível.");
        }

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
                .animal(animal)
                .build();

        log.info("Nova solicitação de adoção para o animal id={}", animal.getId());

        return solicitacaoAdocaoRepository.save(solicitacao);
    }
}
