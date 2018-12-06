package ru.demjanov_av.githubviewer.crypto;

import android.support.annotation.Nullable;
import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    //-----Constants begin-------------------------------
    public final static String NAME_OF_AES = "AES";
//    public final static int DEFAULT_KEY_SIZE = 768;
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    private Cipher cipher;

    private SecretKeySpec secretKeySpec;
//    private Key publicKey;
//    private Key privateKey;

    //-----Class variables end---------------------------


    //-----Other variables begin-------------------------
    SecureRandom random = new SecureRandom();
    //-----Other variables end---------------------------



    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public AES() {
        try {
            this.cipher = Cipher.getInstance(NAME_OF_AES);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.d(NAME_OF_AES, ": " + e.getMessage());
        }
    }

    public AES(byte[] secretKeysBytes) {
        this.setSecretKeySpec(secretKeysBytes);

        try {
            this.cipher = Cipher.getInstance(NAME_OF_AES);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.d(NAME_OF_AES, ": " + e.getMessage());
        }
    }
    //-----End----------------------


    //////////////////////////////////////////////////////////
    ///  Getters and Setters
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public byte[] getSecretKey() {
        return this.secretKeySpec.getEncoded();
    }

    public void setSecretKeySpec(byte[] bytes){
        this.secretKeySpec = new SecretKeySpec(bytes, NAME_OF_AES);
    }
    //-----End----------------------


    //////////////////////////////////////////////////////////
    ///  Method encrypt
    /////////////////////////////////////////////////////////
    @Nullable
    public byte[] encrypt (byte[] openText){
        if(this.secretKeySpec == null){
            return null;
        }

        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKeySpec, this.random);
            return this.cipher.doFinal(openText);

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Log.d(NAME_OF_AES, ": " + e.getMessage());
            return null;
        }
    }


    //////////////////////////////////////////////////////////
    ///  Method decrypt
    /////////////////////////////////////////////////////////
    @Nullable
    public byte[] decrypt (byte[] encryptText){
        if(this.secretKeySpec == null){
            return null;
        }

        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.secretKeySpec, this.random);
            return this.cipher.doFinal(encryptText);

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Log.d(NAME_OF_AES, ": " + e.getMessage());
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //FIXME!!! Вычистить main!!!
    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String s = "Hello world";
        System.out.println(s + "\n");

        Cipher cipher = Cipher.getInstance("AES");

        /* один из способов генерации ключа*/
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey key = kgen.generateKey();

        /* другой способ - передаем свой ключ */
//      SecretKeySpec key2 = new SecretKeySpec("Abc12345Bac54321".getBytes(), "AES");

        /* Зашифрование и вывод */
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(s.getBytes());
        for (byte b : bytes) System.out.print(b);

        /* Расшифрование и вывод */
        Cipher decryptCipher = Cipher.getInstance("AES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes2 = decryptCipher.doFinal(bytes);
        System.out.println("\n");
        for (byte b : bytes2) System.out.print((char)b);

    }
}
