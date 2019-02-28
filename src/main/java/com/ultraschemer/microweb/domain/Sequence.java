package com.ultraschemer.microweb.domain;

import com.ultraschemer.microweb.domain.error.*;
import com.ultraschemer.microweb.error.StandardException;

import java.math.BigInteger;

/**
 * Implementação genérica de sequência que não dependa de estruturas de sequência do banco de dados e que
 * seja compatível com outras linguagens de forma simples.
 */

/**
 * Generic implementation of infinite sized sequence - this doesn't replace the Sequence implementations on databases,
 * but is a bit more generic, and can be emulated easily in other programming languages.
 */
public class Sequence {
    /**
     * The sequence name.
     */
    private String name;

    /**
     * The internal sequence name, as a runtime variable.
     */
    private String sequenceName;

    public Sequence(String name) {
        this.name = name;
        this.sequenceName = name + "_Sequence";
    }

    /**
     * Initialize the sequence, to prepare it to number increment.
     */
    private void initializeSequence() throws UnableToWriteRuntimeException, UnableToReadRuntimeException {
        String seqVal = Runtime.read(this.sequenceName);
        if("".equals(seqVal)) {
            Runtime.write(this.sequenceName,"0");
        } else {
            try {
                new BigInteger(seqVal);
            } catch (NumberFormatException ne) {
                Runtime.write(this.sequenceName, "0");
            }
        }
    }

    /**
     * Obtém o próximo código de sequência e incrementa o contador - essa implementação é segura, com suporte a
     * seções críticas.
     *
     * @return O número de sequência a ser usado.
     */
    public long getNext() throws StandardException {
        DistributedCriticalSection criticalSection = new DistributedCriticalSection(this.sequenceName);

        // Se entrar adequadamente na seção crítica, então é possível obter o objeto de sequência:
        criticalSection.enterCriticalSection();

        initializeSequence();
        long retval = Long.parseLong(Runtime.read(this.sequenceName));
        retval ++;
        Runtime.write(this.sequenceName, Long.toString(retval));

        // Se não for possível sair da seção crítica, provavelmente outra thread obteve a seção crítica da
        // sequência e para evitar encavalamentos, não é possível retornar o valor obtido.
        criticalSection.exitCriticalSection();

        return retval;
    }

    /**
     * Obtém o código atual da sequência, sem incrementar o contador - essa implementação é segura, com suporte a
     * seções críticas.
     * @return O número de sequência a ser usado.
     */
    public long getCurrent() throws StandardException {
        DistributedCriticalSection criticalSection = new DistributedCriticalSection(this.sequenceName);

        // Se entrar adequadamente na seção crítica, então é possível obter o objeto de sequência:
        criticalSection.enterCriticalSection();

        initializeSequence();
        long retval = Long.parseLong(Runtime.read(this.sequenceName));

        // Se não for possível sair da seção crítica, provavelmente outra thread obteve a seção crítica da
        // sequência e para evitar encavalamentos, não é possível retornar o valor obtido.
        criticalSection.exitCriticalSection();

        return retval;
    }

    /**
     * Reinicia o contador de volta a zero.
     *
     */
    public void reset() throws StandardException {
        DistributedCriticalSection criticalSection = new DistributedCriticalSection(this.sequenceName);

        // Se entrar adequadamente na seção crítica, então é possível obter o objeto de sequência:
        criticalSection.enterCriticalSection();

        initializeSequence();
        Runtime.write(this.sequenceName, "0");

        // Se não for possível sair da seção crítica, provavelmente outra thread obteve a seção crítica da
        // sequência e para evitar encavalamentos, não é possível retornar o valor obtido.
        criticalSection.exitCriticalSection();
    }
}
