package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

/**
 * Essa exceção é lançada caso
 */
public class CriticalSectionAcquiringFailureException extends StandardException {
    public CriticalSectionAcquiringFailureException(String message) {
        super("a6734690-9146-4ec3-af1c-bf6a9482e2b2", 500, message);
    }
}
