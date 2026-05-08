package br.com.patinhas.dto.response;

import br.com.patinhas.entity.Animal;
import br.com.patinhas.entity.enums.PorteAnimal;
import br.com.patinhas.entity.enums.SexoAnimal;
import br.com.patinhas.entity.enums.StatusAdocao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalResponseDTO {

    private Long id;
    private String nome;
    private Integer idade;
    private String descricao;
    private PorteAnimal porte;
    private SexoAnimal sexo;
    private StatusAdocao statusAdocao;
    private Boolean castrado;
    private Boolean vacinado;
    private String fotoUrl;
    private LocalDateTime dataCadastro;
    private Boolean ativo;

    public static AnimalResponseDTO fromEntity(Animal animal) {
        if (animal == null) {
            return null;
        }
        return AnimalResponseDTO.builder()
                .id(animal.getId())
                .nome(animal.getNome())
                .idade(animal.getIdade())
                .descricao(animal.getDescricao())
                .porte(animal.getPorte())
                .sexo(animal.getSexo())
                .statusAdocao(animal.getStatusAdocao())
                .castrado(animal.getCastrado())
                .vacinado(animal.getVacinado())
                .fotoUrl(animal.getFotoUrl())
                .dataCadastro(animal.getDataCadastro())
                .ativo(animal.getAtivo())
                .build();
    }
}
