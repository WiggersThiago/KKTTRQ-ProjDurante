package br.com.patinhas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.patinhas.entity.Especie;
import br.com.patinhas.repository.EspecieRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspecieService {

    private final EspecieRepository especieRepository;
    
    /*comboboxx que a beca sugeriu*/
    @Transactional
    public Especie buscarOuCriar(String nome)
     {
        String nomeTratado = nome.trim();

        return especieRepository.findByNomeIgnoreCase(nomeTratado)
                .orElseGet(() -> especieRepository.save(
                        Especie.builder()
                                .nome(nomeTratado)
                                .build()
                ));
    }

    @Transactional(readOnly = true)
    public List<Especie> listarTodas() {
        return especieRepository.findAllByOrderByNomeAsc();
    }
}