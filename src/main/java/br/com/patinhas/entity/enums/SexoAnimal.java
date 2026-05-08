package br.com.patinhas.entity.enums;

/**
 * Representa o sexo do animal.
 */
public enum SexoAnimal {

    MACHO("Macho"),
    FEMEA("Fêmea");

    private final String descricao;

    SexoAnimal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
