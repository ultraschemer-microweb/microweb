package com.ultraschemer.microweb.validation;

import com.ultraschemer.microweb.domain.bean.Message;
import com.ultraschemer.microweb.error.ValidationException;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.exception.ConstraintsViolatedException;

import java.util.List;

public class Validator {
    public static void ensure(Object o) throws ValidationException {
        net.sf.oval.Validator validator = new net.sf.oval.Validator();
        List<ConstraintViolation> violations = validator.validate(o);

        if(violations.size() > 0) {
            // Obtém todas as mensagens de validação:
            StringBuilder msg = new StringBuilder();
            msg.append("Objeto passado, de classe " + o.getClass().getCanonicalName() +
                    " é inválido, com as seguintes violações:\n");

            for(ConstraintViolation cv: violations) {
                msg.append(cv.getMessage());
                msg.append("\n");
            }

            throw new ValidationException(msg.toString());
        }
    }

    public static Message formatStandardMessage(ConstraintsViolatedException exception) {
        ConstraintViolation [] violations = exception.getConstraintViolations();

        // Obtém todas as mensagens de validação:
        StringBuilder msg = new StringBuilder();
        msg.append("Erro de validação encontrado, com as seguintes violações:\n");

        for(ConstraintViolation cv: violations) {
            msg.append(cv.getMessage());
            msg.append("\n");
        }

        return new ValidationException(msg.toString()).bean();
    }
}