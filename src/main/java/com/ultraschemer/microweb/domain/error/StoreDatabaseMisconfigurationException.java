package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

/**
 * A configuração de loja, no banco de dados está em uma tabela que aceita UM ÚNICO REGISTRO.
 * Se esse registro
 */
public class StoreDatabaseMisconfigurationException extends StandardException {
    public StoreDatabaseMisconfigurationException(String message) {
        super("7f1ffbb5-5c8f-4c9c-bc3f-03d10c3fbc75", 500, message);
    }
}
