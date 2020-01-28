package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class InvalidLinkConditionInQueryExtensionException extends StandardException {
    public InvalidLinkConditionInQueryExtensionException(String message) {
        super("51356ae8-2577-4241-a945-42105a9c187b", 500, message);
    }
}
