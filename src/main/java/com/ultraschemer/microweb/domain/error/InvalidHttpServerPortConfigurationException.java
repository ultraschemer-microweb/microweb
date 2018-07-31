package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

/**
 * O sistema de Frente de Loja possui daemons que funcionam em background e que servem em portas TCP específicas,
 * com uma interface HTTP.
 */
public class InvalidHttpServerPortConfigurationException extends StandardException {
    public InvalidHttpServerPortConfigurationException(String message) {
        super("8ca77d75-32d2-4596-8cfa-3804177690e3", 500, message);
    }
}
