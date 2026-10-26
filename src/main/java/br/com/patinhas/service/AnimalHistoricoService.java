package br.com.patinhas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.patinhas.entity.enums.SituacaoAnimal;
import br.com.patinhas.repository.AnimalRepository;
import br.com.patinhas.entity.enums.StatusAdocao;
import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.AnimalHistorico;
import br.com.patinhas.entity.enums.TipoHistoricoAnimal;
import br.com.patinhas.repository.AnimalHistoricoRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AnimalHistoricoService {

    private final AnimalRepository animalRepository;
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
    @Transactional
public AnimalHistorico registrarAcontecimentoManual(
        Animal animal,
        TipoHistoricoAnimal tipo,
        LocalDate data,
        String observacao) {

    if (tipo != TipoHistoricoAnimal.RESGATE
            && tipo != TipoHistoricoAnimal.TRATAMENTO
            && tipo != TipoHistoricoAnimal.DEVOLUCAO) {
        throw new IllegalArgumentException(
                "Tipo de acontecimento inválido.");
    }

    String texto = observacao == null ? "" : observacao.trim();

    if (texto.isBlank() || texto.length() > 500) {
        throw new IllegalArgumentException(
                "A observação deve ter entre 1 e 500 caracteres.");
    }

    if (data == null) {
        throw new IllegalArgumentException(
                "Informe a data do acontecimento.");
    }

    LocalDateTime dataHora = data.atStartOfDay();

    if (tipo == TipoHistoricoAnimal.TRATAMENTO) {
        animal.setSituacaoAnimal(SituacaoAnimal.EM_TRATAMENTO);
        animalRepository.save(animal);
    }

    if (tipo == TipoHistoricoAnimal.DEVOLUCAO) {
        animal.setStatusAdocao(StatusAdocao.DISPONIVEL);
        animal.setDataAdocao(null);
        animal.setDataDisponivel(dataHora);
        animal.setSituacaoAnimal(SituacaoAnimal.NORMAL);
        animal.setAtivo(true);

        animalRepository.save(animal);
    }

    return registrar(
            animal,
            tipo,
            dataHora,
            texto,
            false
    );
}
}