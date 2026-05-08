package br.com.patinhas.service;

import br.com.patinhas.dto.request.InformacaoONGRequestDTO;
import br.com.patinhas.dto.response.InformacaoONGResponseDTO;
import br.com.patinhas.entity.InformacaoONG;
import br.com.patinhas.repository.InformacaoONGRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pelos dados institucionais da ONG.
 *
 * Como existe apenas um registro lógico, o serviço sempre opera sobre o
 * primeiro (e único) registro existente, criando-o se ainda não existir.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InformacaoONGService {

    private final InformacaoONGRepository informacaoONGRepository;

    @Transactional(readOnly = true)
    public InformacaoONGResponseDTO obter() {
        return informacaoONGRepository.findFirstByOrderByIdAsc()
                .map(InformacaoONGResponseDTO::fromEntity)
                .orElse(InformacaoONGResponseDTO.builder()
                        .nomeONG("ONG Patinhas")
                        .quemSomos("Em breve traremos mais informações sobre nossa ONG.")
                        .build());
    }

    @Transactional(readOnly = true)
    public InformacaoONG obterEntidade() {
        return informacaoONGRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> InformacaoONG.builder()
                        .nomeONG("ONG Patinhas")
                        .build());
    }

    @Transactional
    public InformacaoONGResponseDTO atualizar(InformacaoONGRequestDTO dto) {
        InformacaoONG info = informacaoONGRepository.findFirstByOrderByIdAsc()
                .orElseGet(InformacaoONG::new);

        info.setNomeONG(dto.getNomeONG());
        info.setQuemSomos(dto.getQuemSomos());
        info.setProposito(dto.getProposito());
        info.setPixDoacao(dto.getPixDoacao());
        info.setEnderecoDoacao(dto.getEnderecoDoacao());
        info.setTelefoneContato(dto.getTelefoneContato());
        info.setEmailContato(dto.getEmailContato());
        info.setInstagram(dto.getInstagram());
        info.setFacebook(dto.getFacebook());

        log.info("Atualizando informações institucionais da ONG: {}", dto.getNomeONG());
        return InformacaoONGResponseDTO.fromEntity(informacaoONGRepository.save(info));
    }
}
