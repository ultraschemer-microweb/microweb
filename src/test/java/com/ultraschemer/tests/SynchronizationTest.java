package com.ultraschemer.tests;

import org.junit.After;
import org.junit.Test;
import com.ultraschemer.helpers.SynchronizationHelper;
import com.ultraschemer.microweb.domain.DistributedCriticalSection;
import com.ultraschemer.microweb.domain.Runtime;
import com.ultraschemer.microweb.domain.Sequence;
import com.ultraschemer.microweb.domain.error.CriticalSectionAcquiringFailureException;
import com.ultraschemer.microweb.domain.error.CriticalSectionExitFailureException;
import com.ultraschemer.microweb.error.StandardException;

import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class SynchronizationTest {
    @After
    public void tearDown() {
        SynchronizationHelper.clearRuntime();
        SynchronizationHelper.clearLockControl();
    }

    @Test
    public void testReadAndWrite() throws StandardException {
        // Lê um valor não atribuído:
        assertEquals("", Runtime.read("Variable"));

        // Atribui um valor qualquer:
        Runtime.write("Variable", "Any Value");

        // Lê o valor atribuído:
        assertEquals("Any Value", Runtime.read("Variable"));

        // Reatribui o valor:
        Runtime.write("Variable", "Another Value");

        // Relê o valor:
        assertEquals("Another Value", Runtime.read("Variable"));
    }

    @Test
    public void testBlockCriticalSection() {
        DistributedCriticalSection cs = new DistributedCriticalSection("TestCriticalSection");

        // Um número aleatório de 1 a 5.
        int waitSeconds = ThreadLocalRandom.current().nextInt(1, 6);
        cs.setWait(waitSeconds);

        // A expiração é um pouco mais longa que a espera:
        cs.setExpiration(waitSeconds + 10);

        Calendar endTime = null;
        Calendar waitTime = null;
        Calendar expirationTime = null;
        try {
            waitTime = Calendar.getInstance();
            expirationTime = Calendar.getInstance();
            assertTrue(cs.enterCriticalSection());

            assertFalse(cs.enterCriticalSection());

            // Obtém o momento de finalização da sessão crítica:
            endTime = Calendar.getInstance();

            waitTime.add(Calendar.SECOND, cs.getWait());
            expirationTime.add(Calendar.SECOND, cs.getExpiration());

            assertTrue(endTime.after(waitTime));
            assertFalse(endTime.after(expirationTime));

            cs.exitCriticalSection();
        } catch(StandardException se) {
            StringBuffer message = new StringBuffer();
            message.append("A seguinte exceção não foi esperada: ");
            message.append(se.getClass().toString());
            message.append(" - ");
            message.append(se.getMessage());

            fail(message.toString());
        }

        // Verifica se a seção crítica está expirando adequadamente.
        cs.setWait(waitSeconds + 10);
        cs.setExpiration(waitSeconds); // A expiração é bem menor que a espera.

        try {
            cs.enterCriticalSection();

            // Entra na seção crítica novamente:
            waitTime = Calendar.getInstance();
            expirationTime = Calendar.getInstance();
            assertTrue(cs.enterCriticalSection());

            // Tenta entrar novamente, e, dessa vez, espera até obter sucesso:
            assertTrue(cs.enterCriticalSection());

            // Obtém o momento de finalização da sessão crítica:
            endTime = Calendar.getInstance();

            waitTime.add(Calendar.SECOND, cs.getWait());
            expirationTime.add(Calendar.SECOND, cs.getExpiration());

            assertFalse(endTime.after(waitTime));
            assertFalse(expirationTime.after(endTime));

        } catch(StandardException se) {
            StringBuffer message = new StringBuffer();
            message.append("A seguinte exceção não foi esperada: ");
            message.append(se.getClass().toString());
            message.append(" - ");
            message.append(se.getMessage());

            fail(message.toString());
        }
    }

    @Test
    public void testSequenceManagement() throws StandardException {
        Sequence seq1 = new Sequence("seq1");
        Sequence seq2 = new Sequence("seq2");

        assertEquals(0, seq1.getCurrent());
        assertEquals(0, seq1.getCurrent());
        assertEquals(0, seq2.getCurrent());

        assertEquals(1, seq1.getNext());
        assertEquals(1, seq2.getNext());

        assertEquals(2, seq1.getNext());
        assertEquals(2, seq2.getNext());
        assertEquals(3, seq2.getNext());

        seq1.reset();

        assertEquals(1, seq1.getNext());
        assertEquals(4, seq2.getNext());

        seq2.reset();

        assertEquals(1, seq1.getCurrent());
        assertEquals(0, seq2.getCurrent());
    }
}
