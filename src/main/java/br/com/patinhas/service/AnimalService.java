package br.com.patinhas.service;

import br.com.patinhas.dto.request.AnimalRequestDTO;
import br.com.patinhas.dto.response.AnimalResponseDTO;
import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Camada de regras de negócio para Animais.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final ImageStorageService imageStorageService;

    @Transactional(readOnly = true)
    public List<AnimalResponseDTO> listarDestaque() {
        return animalRepository.findAllByAtivoTrueAndDestaqueTrueOrderByDataCadastroDesc()
                .stream()
                .limit(6)
                .map(AnimalResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDTO> listarDisponiveis() {
        return animalRepository
                .findAllByStatusAdocaoAndAtivoTrueOrderByDataCadastroDesc(StatusAdocao.DISPONIVEL)
                .stream()
                .map(AnimalResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDTO> listarTodosAtivos() {
        return animalRepository.findAllByAtivoTrueOrderByDataCadastroDesc()
                .stream()
                .map(AnimalResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDTO> listarAdmin() {
        return animalRepository.findAllByOrderByAtivoDescDataCadastroDesc().stream()
                .map(AnimalResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnimalResponseDTO buscarPorId(Long id) {
        return AnimalResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Animal buscarEntidade(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDTO> filtrar(String nome, StatusAdocao status) {
        String nomePattern = (nome == null || nome.isBlank())
                ? null
                : "%" + nome.toLowerCase() + "%";
        return animalRepository.buscarComFiltros(nomePattern, status)
                .stream()
                .map(AnimalResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AnimalResponseDTO cadastrar(AnimalRequestDTO dto) {
        return cadastrar(dto, null);
    }

    @Transactional
    public AnimalResponseDTO cadastrar(AnimalRequestDTO dto, MultipartFile imagem) {
        log.info("Cadastrando novo animal: {}", dto.getNome());
        Animal animal = Animal.builder()
                .nome(dto.getNome())
                .idade(dto.getIdade())
                .descricao(dto.getDescricao())
                .porte(dto.getPorte())
                .sexo(dto.getSexo())
                .statusAdocao(dto.getStatusAdocao() == null ? StatusAdocao.DISPONIVEL : dto.getStatusAdocao())
                .castrado(Boolean.TRUE.equals(dto.getCastrado()))
                .vacinado(Boolean.TRUE.equals(dto.getVacinado()))
                .destaque(Boolean.TRUE.equals(dto.getDestaque()))
                .ativo(true)
                .build();
        animal.setFotoUrl(imageStorageService.salvar(imagem, "animais"));
        return AnimalResponseDTO.fromEntity(animalRepository.save(animal));
    }

    @Transactional
    public AnimalResponseDTO atualizar(Long id, AnimalRequestDTO dto) {
        return atualizar(id, dto, null, false);
    }

    @Transactional
    public AnimalResponseDTO atualizar(Long id, AnimalRequestDTO dto, MultipartFile imagem, boolean removerImagem) {
        log.info("Atualizando animal id={}", id);
        Animal animal = buscarEntidade(id);
        animal.setNome(dto.getNome());
        animal.setIdade(dto.getIdade());
        animal.setDescricao(dto.getDescricao());
        animal.setPorte(dto.getPorte());
        animal.setSexo(dto.getSexo());
        if (dto.getStatusAdocao() != null) {
            animal.setStatusAdocao(dto.getStatusAdocao());
        }
        animal.setCastrado(Boolean.TRUE.equals(dto.getCastrado()));
        animal.setVacinado(Boolean.TRUE.equals(dto.getVacinado()));
        animal.setDestaque(Boolean.TRUE.equals(dto.getDestaque()));
        if (dto.getAtivo() != null) {
            animal.setAtivo(dto.getAtivo());
        }
        atualizarImagem(animal, imagem, removerImagem);
        return AnimalResponseDTO.fromEntity(animalRepository.save(animal));
    }

    private void atualizarImagem(Animal animal, MultipartFile imagem, boolean removerImagem) {
        if (removerImagem) {
            imageStorageService.remover(animal.getFotoUrl());
            animal.setFotoUrl(null);
            return;
        }
        if (imagem != null && !imagem.isEmpty()) {
            String novoCaminho = imageStorageService.salvar(imagem, "animais");
            imageStorageService.substituir(animal.getFotoUrl(), novoCaminho);
            animal.setFotoUrl(novoCaminho);
        }
    }

    @Transactional
    public void atualizarStatus(Long id, StatusAdocao novoStatus) {
        Animal animal = buscarEntidade(id);
        animal.setStatusAdocao(novoStatus);
        animalRepository.save(animal);
    }

    /**
     * Desativa o cadastro (remoção lógica). O registro permanece no histórico da listagem admin.
     */
    @Transactional
    public void desativar(Long id) {
        log.info("Desativando animal id={}", id);
        Animal animal = buscarEntidade(id);
        animal.setAtivo(false);
        animal.setDestaque(false);
        animalRepository.save(animal);
    }

    @Transactional(readOnly = true)
    public long contarDisponiveis() {
        return animalRepository.countByStatusAdocaoAndAtivoTrue(StatusAdocao.DISPONIVEL);
    }

    @Transactional(readOnly = true)
    public long contarAtivos() {
        return animalRepository.countByAtivoTrue();
    }
}
