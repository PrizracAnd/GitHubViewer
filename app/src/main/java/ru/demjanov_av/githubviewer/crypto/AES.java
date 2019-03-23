package ru.demjanov_av.githubviewer.crypto;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import demjanov.av.ru.allegedcodeproject.crypto.supports.Converters;


public class AES {
    //-----Constants begin-------------------------------
    public final static String NAME_OF_AES = "AES";
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    private Cipher cipher;

    private SecretKeySpec secretKeySpec;
    //-----Class variables end---------------------------


    //-----Other variables begin-------------------------
    private SecureRandom random = new SecureRandom();
    //-----Other variables end---------------------------



    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Begin---------------------------------------------
    public AES() {
        try {
            this.cipher = Cipher.getInstance(NAME_OF_AES);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.d(NAME_OF_AES, ": " + e.getMessage());
        }
    }

    public AES(byte[] secretKeysBytes) {
        this();
        this.setSecretKeySpec(secretKeysBytes);
    }
    //-----End-----------------------------------------------


    //////////////////////////////////////////////////////////
    ///  Getters and Setters
    /////////////////////////////////////////////////////////
    //-----Begin----------------------------------------------
    public byte[] getSecretKey() {
        return this.secretKeySpec.getEncoded();
    }

    public void setSecretKeySpec(byte[] bytes){
        this.secretKeySpec = new SecretKeySpec(bytes, NAME_OF_AES);
    }
    //-----End-------------------------------------------------


    //////////////////////////////////////////////////////////
    ///  Method encrypt
    /////////////////////////////////////////////////////////
    @Nullable
    public String encrypt (String openText){
        if(this.secretKeySpec == null){
            return null;
        }

        try {
            byte[] bytes = openText.getBytes(Converters.SYMBOL_CODE_NAME);
            this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKeySpec, this.random);
            return new String(this.cipher.doFinal(bytes), Converters.SYMBOL_CODE_NAME);

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            Log.d(NAME_OF_AES, ": " + e.getMessage());
            return null;
        }
    }


    //////////////////////////////////////////////////////////
    ///  Method decrypt
    /////////////////////////////////////////////////////////
    @Nullable
    public String decrypt (String encryptText){
        if(this.secretKeySpec == null){
            return null;
        }

        try {
            byte[] bytes = encryptText.getBytes(Converters.SYMBOL_CODE_NAME);
            this.cipher.init(Cipher.DECRYPT_MODE, this.secretKeySpec, this.random);
            return new String(this.cipher.doFinal(bytes), Converters.SYMBOL_CODE_NAME);

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            Log.d(NAME_OF_AES, ": " + e.getMessage());
            return null;
        }
    }
}
