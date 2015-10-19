package com.sun_post.crypto;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int PBKDF2_ITERATIONS = 1000;
    public static final int SALT_BYTE_SIZE = 20;
    public static final int AES_KEY_SIZE = 256;

    public static final int ITERATION_INDEX = 0;
    public static final int SALT_INDEX = 1;
    public static final int IV_INDEX = 2;
    public static final int CIPHER_TEXT_INDEX = 3;

    /**
     * Given the plain text and master password, encrypt the plain text.
     * 
     * @param plainText
     *            plain text to be encrypted
     * @param masterPassword
     *            user master password
     * @return cipher text and its parameters
     * @throws Exception
     */
    public static String encrypt(String plainText, String masterPassword) throws Exception {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        // Derive the key
        SecretKeySpec secret = new SecretKeySpec(pbkdf2(masterPassword.toCharArray(), salt, PBKDF2_ITERATIONS,
                AES_KEY_SIZE), "AES");

        // encrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(ivBytes) + ":" + toHex(encryptedTextBytes);
    }

    /**
     * Given the cipher text with its parameters and master password, decrypt
     * the cipher text.
     * 
     * @param encryptedOutput
     *            the cipher text along with other parameters
     * @param masterPassword
     *            user master password
     * @return plain text
     * @throws Exception
     */
    public static String decrypt(String encryptedOutput, String masterPassword) throws Exception {
        // Decode its parameters
        String[] params = encryptedOutput.split(":");
        int iterations = Integer.parseInt(params[ITERATION_INDEX]);
        byte[] salt = fromHex(params[SALT_INDEX]);
        byte[] iv = fromHex(params[IV_INDEX]);
        byte[] encryptedTextBytes = fromHex(params[CIPHER_TEXT_INDEX]);

        // Derive the key
        SecretKeySpec secret = new SecretKeySpec(pbkdf2(masterPassword.toCharArray(), salt, iterations, AES_KEY_SIZE),
                "AES");

        // Decrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);

        return new String(decryptedTextBytes, "UTF-8");
    }

    /**
     * Computes the PBKDF2 hash of a password.
     * 
     * @param password
     *            the password to hash.
     * @param salt
     *            the salt
     * @param iterations
     *            the iteration count (slowness factor)
     * @param keyLength
     *            the to-be-derived key length.
     * @return the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     * 
     * @param hex
     *            the hex string
     * @return the hex string decoded into a byte array
     */
    private static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    /**
     * Converts a byte array into a hexadecimal string.
     * 
     * @param array
     *            the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

    public static void main(String[] args) throws Exception {
        String masterPassword = "test";
        String encryptedText = AES.encrypt("Hello", masterPassword);
        System.out.println("Encrypted string:" + encryptedText);
        System.out.println("Decrypted string:" + AES.decrypt(encryptedText, masterPassword));
    }
}