package ru.demjanov_av.githubviewer.crypto;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by demjanov on 12.07.2018.
 */

public class EncryptorGOST {
    private final static String CLASS_NAME = "ENCRYPTOR_GOST";
    private final static String SYMBOL_CODE_NAME = "UTF-8";
    private long[] keys;
    private int[][] sBox;

    private SecureRandom secureRandom = new SecureRandom();


    public String encrypt(String openText){
        if(this.keys == null || this.sBox == null){
            this.keys = generateKeys();
            this.sBox = generateSbox();
        }

        GOST gost = new GOST(this.keys, this.sBox);

        try {
            List<Long> encryptList = new ArrayList<Long>();
            for (long item : strToLong(openText)) {
                gost.setDataBlock(item);
                gost.encrypt32();
                encryptList.add(gost.getDataBlock());
            }
            return longToStr(encryptList);
        }catch (Exception e){
            Log.d(CLASS_NAME, ": " + e.getMessage());
            return null;
        }
    }

    @Nullable
    public String encrypt(String openText, boolean newGOSTsParameters) {
//        if(newGOSTsParameters){
//            this.keys = generateKeys();
//            this.sBox = generateSbox();
//        }
//
//        return encrypt(openText);

        if (newGOSTsParameters || this.keys == null || this.sBox == null) {
            this.keys = generateKeys();
            this.sBox = generateSbox();
        }

        GOST gost = new GOST(this.keys, this.sBox);

        try {


            List<Long> encryptList = new ArrayList<Long>();
            for (long item : strToLong(openText)) {
                gost.setDataBlock(item);
                gost.encrypt32();
                encryptList.add(gost.getDataBlock());
            }
            return longToStr(encryptList);

        }catch (Exception e){
            Log.d(CLASS_NAME, ": " + e.getMessage());
            return null;
        }
    }

    @Nullable
    public String decrypt(String encryptText){
        try {
            if (this.keys == null || this.sBox == null) {
                throw new Exception("GOSTs parameters is not available!");
//            Log.d(CLASS_NAME, ": GOSTs parameters is not available!");
//            return null;
            }

            GOST gost = new GOST(this.keys, this.sBox);

            List<Long> encryptList = new ArrayList<Long>();
            for (long item : strToLong(encryptText)) {
                gost.setDataBlock(item);
                gost.decrypt32();
                encryptList.add(gost.getDataBlock());
            }
            return longToStr(encryptList);
        }catch (Exception e){
            Log.d(CLASS_NAME, ": " + e.getMessage());
            return null;
        }
    }



    private List<Long> strToLong(String str) throws UnsupportedEncodingException {
        List<Long> longList = new ArrayList<Long>();
        byte[] bytes = str.getBytes(SYMBOL_CODE_NAME);
        long item = 0L;

        for (int i = 0; i < bytes.length;){
            item ^= item;
            for (int j = 0; j < 8; j++){
                if(i >= bytes.length) break;
                item |= (long)bytes[i] << (j * 8);
                i++;
            }
            longList.add(item);
        }

        return longList;
    }

    @NonNull
    private String longToStr(List<Long> longList) throws UnsupportedEncodingException {
        byte[] bytes = new byte[longList.size() * 8];
        int i = 0;

        for (long item: longList){
            for (int j = 0; j < 8; j++){
                bytes[i] = (byte)(item >>> (j * 8));
                i++;
            }
        }

        i = bytes.length;
        for (int j = i - 1; j >= 0; j--){
            i = j;
            if(bytes[j] != 0){
                break;
            }
        }

        byte[] bytes1 = Arrays.copyOf(bytes, i + 1);

        return new String(bytes1, SYMBOL_CODE_NAME);
    }

    private int[][] generateSbox(){
        byte[] bytes = new byte[64];

        this.secureRandom.nextBytes(bytes);

        int[][] sBox = new int[8][16];
        int k = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 16; j++){
                sBox[i][j] = (bytes[k] & 15);
                j++;
                sBox[i][j] = (bytes[k] >>> 4);
                k++;
            }
        }

        return sBox;
    }

    private long[] generateKeys(){
        byte[] bytes = new byte[32];

        this.secureRandom.nextBytes(bytes);

        long[] keys = new long[8];
        int k = 0;
        for (int i = 0; i < keys.length; i++){
            for(int j = 0; j < 4; j++){
                keys[i] |= ((long)bytes[k] << (j * 8));
                k++;
            }
        }

        return keys;
    }
}
