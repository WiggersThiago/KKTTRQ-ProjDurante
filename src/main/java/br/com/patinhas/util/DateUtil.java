package br.com.patinhas.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitários comuns de formatação de datas.
 */
public final class DateUtil {

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateUtil() {
        // utilidade
    }

    public static String formatar(LocalDate data) {
        return data == null ? "" : data.format(FORMATO_DATA);
    }

    public static String formatar(LocalDateTime dataHora) {
        return dataHora == null ? "" : dataHora.format(FORMATO_DATA_HORA);
    }
}
