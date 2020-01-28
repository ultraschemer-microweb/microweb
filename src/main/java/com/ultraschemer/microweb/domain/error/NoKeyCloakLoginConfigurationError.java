package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class NoKeyCloakLoginConfigurationError extends StandardException {
    public NoKeyCloakLoginConfigurationError(String message) {
        super("45960a7e-874f-4fe8-a944-d62878cfe19c", 500, message);
    }

    public NoKeyCloakLoginConfigurationError(String message, Throwable cause) {
        super("45960a7e-874f-4fe8-a944-d62878cfe19c", 500, message, cause);
    }
}
