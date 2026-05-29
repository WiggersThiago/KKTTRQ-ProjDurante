package br.com.patinhas.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class WhatsappUtil {

    private WhatsappUtil() {
    }

    public static String formatarNumero(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return "";
        }
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            return "";
        }
        if (digitos.length() >= 10 && digitos.length() <= 11 && !digitos.startsWith("55")) {
            digitos = "55" + digitos;
        }
        return digitos;
    }

    public static String montarLink(String telefone, String mensagem) {
        String numero = formatarNumero(telefone);
        if (numero.isEmpty()) {
            return "";
        }
        String texto = mensagem == null ? "" : mensagem;
        return "https://wa.me/" + numero + "?text=" + URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }
}
