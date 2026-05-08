package br.com.patinhas.entity.enums;

/**
 * Representa o porte físico do animal cadastrado na ONG.
 */
public enum PorteAnimal {

    PEQUENO("Pequeno"),
    MEDIO("Médio"),
    GRANDE("Grande");

    private final String descricao;

    PorteAnimal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
