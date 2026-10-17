package br.com.patinhas.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoMovimentacaoEstoque {

    ENTRADA("Entrada"),
    SAIDA("Saída");

    private final String descricao;
}
