package br.com.patinhas.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoHistoricoAnimal {

    CADASTRO("Cadastro"),
    RESGATE("Resgate"),
    DISPONIBILIZACAO_ADOCAO("Disponibilização para adoção"),
    TRATAMENTO("Tratamento"),
    INTERESSE_RECEBIDO("Interesse recebido"),
    INTERESSE_CANCELADO("Interesse cancelado"),
    ADOCAO_APROVADA("Adoção aprovada"),
    ADOCAO_CONCLUIDA("Adoção concluída"),
    DEVOLUCAO("Devolução"),
    ALTERACAO_STATUS("Alteração de status");

    private final String descricao;
}