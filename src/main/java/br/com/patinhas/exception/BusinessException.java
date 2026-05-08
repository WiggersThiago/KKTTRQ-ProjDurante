package br.com.patinhas.exception;

/**
 * Lançada quando uma regra de negócio do sistema é violada.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
