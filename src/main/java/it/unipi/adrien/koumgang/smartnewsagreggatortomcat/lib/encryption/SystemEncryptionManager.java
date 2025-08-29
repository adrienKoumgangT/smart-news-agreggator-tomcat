package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class SystemEncryptionManager {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    // Generate a new AES key
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }

    // Encrypt data
    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    // Decrypt data
    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    // URL encode a string
    public static String urlEncode(String data) throws UnsupportedEncodingException {
        return URLEncoder.encode(data, StandardCharsets.UTF_8);
    }

    // URL decode a string
    public static String urlDecode(String data) throws UnsupportedEncodingException {
        return URLDecoder.decode(data, StandardCharsets.UTF_8);
    }

    // Convert SecretKey to String
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Convert String to SecretKey
    public static SecretKey stringToKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }


    public static void main(String[] args) {
        // test01();
        test02();
    }

    public static void test01() {
        try {
            // Generate a new AES key
            SecretKey key = SystemEncryptionManager.generateKey();
            String keyString = SystemEncryptionManager.keyToString(key);

            // Print the key for reference
            System.out.println("Generated Key: " + keyString);


            String originalData = "Hello, World!";
            System.out.println("Original Data: " + originalData);

            // Encrypt data
            String encryptedData = SystemEncryptionManager.encrypt(originalData, key);
            System.out.println("Encrypted Data: " + encryptedData);

            // Decrypt data
            String decryptedData = SystemEncryptionManager.decrypt(encryptedData, key);
            System.out.println("Decrypted Data: " + decryptedData);

            // Converting string key back to SecretKey
            SecretKey convertedKey = SystemEncryptionManager.stringToKey(keyString);
            String reDecryptedData = SystemEncryptionManager.decrypt(encryptedData, convertedKey);
            System.out.println("Re-Decrypted Data: " + reDecryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test02() {
        try {
            // Generate a new AES key
            SecretKey key = SystemEncryptionManager.generateKey();
            String keyString = SystemEncryptionManager.keyToString(key);

            // Print the key for reference
            System.out.println("Generated Key: " + keyString);

            String originalData = "Hello, World!";
            System.out.println("Original Data: " + originalData);

            // Encrypt data
            String encryptedData = SystemEncryptionManager.encrypt(originalData, key);
            System.out.println("Encrypted Data: " + encryptedData);

            // URL encode the encrypted data
            String urlEncodedEncryptedData = SystemEncryptionManager.urlEncode(encryptedData);
            System.out.println("URL Encoded Encrypted Data: " + urlEncodedEncryptedData);

            // URL decode the encrypted data
            String urlDecodedEncryptedData = SystemEncryptionManager.urlDecode(urlEncodedEncryptedData);
            System.out.println("URL Decoded Encrypted Data: " + urlDecodedEncryptedData);

            // Decrypt data
            String decryptedData = SystemEncryptionManager.decrypt(urlDecodedEncryptedData, key);
            System.out.println("Decrypted Data: " + decryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
