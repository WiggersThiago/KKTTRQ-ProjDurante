package br.com.patinhas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.AnimalHistorico;
import br.com.patinhas.entity.enums.TipoHistoricoAnimal;
import br.com.patinhas.repository.AnimalHistoricoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnimalHistoricoService {

    private final AnimalHistoricoRepository animalHistoricoRepository;

    @Transactional
    public AnimalHistorico registrar(
            Animal animal,
            TipoHistoricoAnimal tipo,
            String texto,
            boolean automatico) {

        AnimalHistorico historico = AnimalHistorico.builder()
                .animal(animal)
                .tipo(tipo)
                .data(LocalDateTime.now())
                .texto(texto)
                .automatico(automatico)
                .build();

        return animalHistoricoRepository.save(historico);
    }

    @Transactional
    public AnimalHistorico registrar(
            Animal animal,
            TipoHistoricoAnimal tipo,
            LocalDateTime data,
            String texto,
            boolean automatico) {

        AnimalHistorico historico = AnimalHistorico.builder()
                .animal(animal)
                .tipo(tipo)
                .data(data)
                .texto(texto)
                .automatico(automatico)
                .build();

        return animalHistoricoRepository.save(historico);
            }
    @Transactional
    public void garantirHistoricoCadastro(Animal animal) {

            boolean jaExiste = animalHistoricoRepository.existsByAnimalIdAndTipo(
                    animal.getId(),
                    TipoHistoricoAnimal.CADASTRO
            );

            if (jaExiste) {
                return;
            }

            registrar(
                    animal,
                    TipoHistoricoAnimal.CADASTRO,
                    animal.getDataCadastro(),
                    "Animal cadastrado no sistema",
                    true
            );
    }
    @Transactional(readOnly = true)
    public List<AnimalHistorico> listarPorAnimal(Long animalId) {
        return animalHistoricoRepository.findByAnimalIdOrderByDataAsc(animalId);
    }
}