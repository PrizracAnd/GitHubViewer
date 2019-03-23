package ru.demjanov_av.githubviewer.crypto;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    //-----Constants variables begin---------------------
    public final static String HASHER = "HASHER";

    //-----Algorithms names variables begin--------------
    public final static String HASH_SPEC_MD5 = "MD5";
    public final static String HASH_SPEC_SHA_1 = "SHA-1";
    //-----Algorithms names variables end----------------
    //-----Constants variables end-----------------------


    //-----Class variables begin-------------------------
    private MessageDigest messageDigest = null;
    //-----Class variables end---------------------------

    /////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public Hasher(){
        setMessageDigestDefault();
    }

    public Hasher(String algorithm) {
        setMessageDigest(algorithm);
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    //-----Begin Methods getHash-------------------------
    public byte[] getHash(byte[] incomingBytes, String algorithm){
        setMessageDigest(algorithm);
        return getHash(incomingBytes);
    }

    public byte[] getHash(byte[] incomingBytes){
        if(this.messageDigest == null){
            setMessageDigestDefault();
        }

        return this.messageDigest.digest(incomingBytes);
    }
    //-----End Methods getHash---------------------------


    //-----Begin Methods setMessageDigest----------------
    private void setMessageDigestDefault(){
        setMessageDigest(HASH_SPEC_SHA_1);
    }

    private void setMessageDigest(String algorithm) {
        try {
            this.messageDigest = MessageDigest.getInstance(algorithm);
        }catch (NoSuchAlgorithmException e){
            Log.d(HASHER, ": " + e.getMessage());
        }
    }
    //-----End Methods setMessageDigest------------------
    //-----End-------------------------------------------
}
