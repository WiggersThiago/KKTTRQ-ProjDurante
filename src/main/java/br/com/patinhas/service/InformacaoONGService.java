package br.com.patinhas.service;

import br.com.patinhas.dto.request.InformacaoONGRequestDTO;
import br.com.patinhas.dto.response.InformacaoONGResponseDTO;
import br.com.patinhas.entity.InformacaoONG;
import br.com.patinhas.exception.ResourceNotFoundException;
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

    private static final String NOME_ONG_PADRAO = "ONG Patinhas";
    private static final String QUEM_SOMOS_PADRAO = "Em breve traremos mais informações sobre nossa ONG.";

    private final InformacaoONGRepository informacaoONGRepository;

    @Transactional(readOnly = true)
    public InformacaoONGResponseDTO obter() {
        return InformacaoONGResponseDTO.fromEntity(buscarOuCriarPadrao());
    }

    @Transactional(readOnly = true)
    public InformacaoONG obterEntidade() {
        return buscarOuCriarPadrao();
    }

    @Transactional
    public InformacaoONGResponseDTO atualizar(InformacaoONGRequestDTO dto) {
        InformacaoONG info = dto.getId() != null
                ? informacaoONGRepository.findById(dto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Informação da ONG", dto.getId()))
                : buscarOuCriarPadrao();

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

    private InformacaoONG buscarOuCriarPadrao() {
        return informacaoONGRepository.findFirstByOrderByIdAsc()
                .orElseGet(this::criarEntidadePadrao);
    }

    private InformacaoONG criarEntidadePadrao() {
        return InformacaoONG.builder()
                .nomeONG(NOME_ONG_PADRAO)
                .quemSomos(QUEM_SOMOS_PADRAO)
                .build();
    }
}
