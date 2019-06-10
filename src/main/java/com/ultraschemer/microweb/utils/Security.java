package com.ultraschemer.microweb.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class with the routines to secure password storage and validation. Other security routines can be put here.
 */
public class Security {
    /**
     * The default number of cycles to hash a password.
     */
    public static final int PBKDF2_CYCLES = 32768;
    public static final int PBKDF2_SHA512_LENGTH = 512;

    public static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength )
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
        PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
        SecretKey key = skf.generateSecret( spec );
        byte[] res = key.getEncoded( );

        return res;
    }

    /**
     * Secure a password with a random salt
     *
     * @param password The password text to be hashed.
     * @return The hashed password in the format: [Salt, bytes in Base64]:[Password hash, bytes in Base64]
     */
    public static String hashade(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte salt[] = new byte[32];
        new Random().nextBytes(salt);

        byte hash[] = hashPassword(password.toCharArray(), salt, PBKDF2_CYCLES, PBKDF2_SHA512_LENGTH);
        String base64Salt = Base64.encodeBase64String(salt);
        String base64Hash = Base64.encodeBase64String(hash);

        return base64Salt + ":" + base64Hash;
    }

    /**
     * Secure a password using a given salt.
     *
     * @param password The password text to be hashed.
     * @param salt The salt, bytes in Base 64.
     * @return The hashed password, in the format: [Salt, bytes in Base64]:[Password hash, bytes in Base64]
     */
    public static String hashade(String password, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte bsalt[] = Base64.decodeBase64(salt.getBytes());

        byte hash[] = hashPassword(password.toCharArray(), bsalt, PBKDF2_CYCLES, PBKDF2_SHA512_LENGTH);
        String base64Salt = Base64.encodeBase64String(bsalt);
        String base64Hash = Base64.encodeBase64String(hash);

        return base64Salt + ":" + base64Hash;
    }

    /**
     * Validate a password against a hashed one.
     *
     * @param plainPassword The password to be validated.
     * @param hashedPassword The hashed password in the format: [Salt, bytes in Base64]:[Password hash, bytes in Base64]
     * @return True, if both passwords are equal. False, otherwise.
     */
    public static boolean validate(String plainPassword, String hashedPassword) {
        try {
            String hashParts[] = hashedPassword.split(":");
            String hashToCompare = hashade(plainPassword, hashParts[0]);

            return hashToCompare.equals(hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    public static String randomToken() {
        final String characterTable = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#";
        int size = ThreadLocalRandom.current().nextInt(5, 65);
        StringBuffer tokenBuffer = new StringBuffer();
        for(int i=0; i<size; i++) {
            int pos = ThreadLocalRandom.current().nextInt(0, 85);
            tokenBuffer.append(characterTable.charAt(pos));
        }
        return tokenBuffer.toString();
    }
}
