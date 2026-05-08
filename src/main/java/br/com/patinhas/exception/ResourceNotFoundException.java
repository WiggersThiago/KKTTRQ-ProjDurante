package br.com.patinhas.exception;

/**
 * Lançada quando um recurso solicitado (animal, evento, denúncia, etc.) não é encontrado.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String recurso, Object id) {
        super("%s não encontrado(a) com identificador: %s".formatted(recurso, id));
    }
}
