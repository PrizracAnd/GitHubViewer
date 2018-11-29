package ru.demjanov_av.githubviewer.crypto;

import android.support.annotation.Nullable;
import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.security.*;

public class RSA {
    //-----Constants begin-------------------------------
    public final static String NAME_OF_RSA = "RSA";
    public final static int DEFAULT_KEY_SIZE = 768;
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    private Cipher cipher;

    private Key publicKey;
    private Key privateKey;

    //-----Class variables end---------------------------


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------
    public RSA( ) {
        try {
            this.cipher = Cipher.getInstance(NAME_OF_RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
        }
    }

    public RSA(Key publicKey, Key privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;

        try {
            this.cipher = Cipher.getInstance(NAME_OF_RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
        }
    }
    //-----Constructors end-------


    //////////////////////////////////////////////////////////
    ///  Methods generateKeys
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    void generateKeys(){
        KeyPairGenerator pairGenerator = null;
        try {
            pairGenerator = KeyPairGenerator.getInstance(NAME_OF_RSA);
        } catch (NoSuchAlgorithmException e) {
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
        }

        SecureRandom random = new SecureRandom();
        try {
            if(pairGenerator != null) {
                pairGenerator.initialize(DEFAULT_KEY_SIZE, random);
                KeyPair keyPair = pairGenerator.generateKeyPair();
                this.publicKey = keyPair.getPublic();
                this.privateKey = keyPair.getPrivate();

            }else {
                Log.d(NAME_OF_RSA, ": " + "No generated keys!!!");
            }
        }catch (NullPointerException | InvalidParameterException e){
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
        }
    }

    void generateKeys(int keySize){
        KeyPairGenerator pairGenerator = null;
        try {
            pairGenerator = KeyPairGenerator.getInstance(NAME_OF_RSA);
        } catch (NoSuchAlgorithmException e) {
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
        }

        SecureRandom random = new SecureRandom();
        try {
            if(pairGenerator != null) {
                pairGenerator.initialize(keySize, random);
                KeyPair keyPair = pairGenerator.generateKeyPair();
                this.publicKey = keyPair.getPublic();
                this.privateKey = keyPair.getPrivate();
            }else {
                Log.d(NAME_OF_RSA, ": " + "No generated keys!!!");
            }
        }catch (NullPointerException | InvalidParameterException e){
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
        }
    }
    //-----End----------------------


    //////////////////////////////////////////////////////////
    ///  Methods getKeys and setKeys
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    @Nullable
    public Key[] getKeys(){
        Key[] keys = null;
        if(this.publicKey != null && this.privateKey != null) {
            keys = new Key[2];
            keys[0] = this.publicKey;
            keys[1] = this.privateKey;
        }
        return keys;
    }

    public void setKeys(Key publicKey, Key privateKey ){
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Nullable
    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }

    @Nullable
    public Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }
    //-----End----------------------


    //////////////////////////////////////////////////////////
    ///  Method encrypt
    /////////////////////////////////////////////////////////
    public byte[] encrypt (byte[] openText, Key key){
        byte[] bytes = new byte[0];

        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, key);
            bytes = this.cipher.doFinal(openText);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
        }

        return bytes;
    }


    //////////////////////////////////////////////////////////
    ///  Method decrypt
    /////////////////////////////////////////////////////////
    public byte[] decrypt (byte[] encryptText, Key key){
        byte[] bytes = new byte[0];

        try {
            this.cipher.init(Cipher.DECRYPT_MODE, key);
            bytes = this.cipher.doFinal(encryptText);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
        }

        return bytes;
    }


    //////////////////////////////////////////////////////////
    ///  Methods keys conversion
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    @Nullable
    public byte[] convertKeyToByte (Key key){
        return key.getEncoded();
    }

    public Key convertByteToKey (byte[] bytes){
        Key key;

        try {

            key = new SecretKeySpec(bytes, NAME_OF_RSA);
            return key;
        }catch (IllegalArgumentException e) {
            Log.d(NAME_OF_RSA, ": " + e.getMessage());
            return null;
        }
    }
    //-----End----------------------

}