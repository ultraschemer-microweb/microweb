package com.ultraschemer.microweb.validation;

import com.ultraschemer.microweb.error.ValidationException;
import net.sf.oval.ConstraintViolation;

import java.util.List;

public class Validator {
    public static void ensure(Object o) throws ValidationException {
        ensure(o, 400);
    }

    public static void ensure(Object o, int httpStatus) throws ValidationException {
        net.sf.oval.Validator validator = new net.sf.oval.Validator();
        List<ConstraintViolation> violations = validator.validate(o);

        if(violations.size() > 0) {
            // Obtém todas as mensagens de validação:
            StringBuilder msg = new StringBuilder();
            msg.append("Given object, of class " + o.getClass().getCanonicalName() +
                    " is invalid, with the next violations:\n");

            for(ConstraintViolation cv: violations) {
                msg.append(cv.getMessage());
                msg.append("\n");
            }

            throw new ValidationException(msg.toString(), httpStatus);
        }
    }
}