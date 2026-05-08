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

import java.util.List;

/**
 * Camada de regras de negócio para Animais.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

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
        return animalRepository.findAll().stream()
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
        return animalRepository.buscarComFiltros(nome, status)
                .stream()
                .map(AnimalResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AnimalResponseDTO cadastrar(AnimalRequestDTO dto) {
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
                .fotoUrl(dto.getFotoUrl())
                .ativo(true)
                .build();
        return AnimalResponseDTO.fromEntity(animalRepository.save(animal));
    }

    @Transactional
    public AnimalResponseDTO atualizar(Long id, AnimalRequestDTO dto) {
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
        animal.setFotoUrl(dto.getFotoUrl());
        return AnimalResponseDTO.fromEntity(animalRepository.save(animal));
    }

    @Transactional
    public void atualizarStatus(Long id, StatusAdocao novoStatus) {
        Animal animal = buscarEntidade(id);
        animal.setStatusAdocao(novoStatus);
        animalRepository.save(animal);
    }

    /**
     * Remoção lógica: marca o animal como inativo. Mantém o histórico para a ONG.
     */
    @Transactional
    public void remover(Long id) {
        log.info("Removendo (logicamente) animal id={}", id);
        Animal animal = buscarEntidade(id);
        animal.setAtivo(false);
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
