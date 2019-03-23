package ru.demjanov_av.githubviewer.crypto;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import demjanov.av.ru.allegedcodeproject.crypto.supports.Converters;
import demjanov.av.ru.allegedcodeproject.save_load.Preferencer;


/**
 * Created by demjanov on 12.07.2018.
 */

public class EncryptorGOST {
    //-----Constants variables begin-------------------------
    private final static String CLASS_NAME      = "ENCRYPTOR_GOST";

    private final static long C232              = 4294967296L;

    //-----Step counters variables begin---------------------
    private final static long C1                = 1010101L;
    private final static long C2                = 1010104L;
    //-----Step counters variables end-----------------------
    //-----Constants variables end---------------------------


    //-----Class variables begin-----------------------------
    private long[] keys;
    private int[][] sBox;
    private byte[] sBoxBytes;
    private boolean isGeneratedKey = false;

    private Preferencer preferencer;

    private SecureRandom secureRandom = new SecureRandom();
    //-----Class variables end--------------------------------


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Begin----------------------------------------------

    public EncryptorGOST(Preferencer preferencer) {
        this.preferencer = preferencer;

        if(!loadGOSTsParameters()){
            generateSbox();
            generateKeys();
            saveGOSTsParameters();
            this.isGeneratedKey = true;
        }
    }
    //-----End------------------------------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------------
    public boolean isGeneratedKey() {
        return isGeneratedKey;
    }
    //-----End------------------------------------------------


    //////////////////////////////////////////////////////////
    ///  Method encryptGamma
    /////////////////////////////////////////////////////////
    @Nullable
    public String encryptGamma(String openText){
        GOST gost = new GOST(this.keys, this.sBox);
        long synchronizedPost = this.secureRandom.nextLong();
        List<Long> encryptList = new ArrayList<Long>();

        encryptList.add(synchronizedPost);                                      // добавляем sp к
                                                                                // шифрованному тексту
        try {

            for (long item : Converters.strToListLong(openText)){
                long nL = ((synchronizedPost & (C232 - 1)) + C1) & (C232 - 1);  // выполняем приращение sp
                long nH = ((synchronizedPost >>> 32) + C2) & (C232 - 1);        // --||--
                synchronizedPost = (nH << 32) | nL;                             // --||--

                gost.setDataBlock(synchronizedPost);                            // передаем значение счетчика на шифрование

                encryptList.add(gost.getEncryptDataBlock() ^ item);             // получаем гамму и
                                                                                // склыдываем ее по модулю 2
                                                                                // с шифруемым блоком
            }

            return Converters.listLongToStr(encryptList);
        } catch (UnsupportedEncodingException e) {
            Log.d(CLASS_NAME, ": " + e.getMessage());
            return null;
        }
    }


    //////////////////////////////////////////////////////////
    ///  Method decryptGamma
    /////////////////////////////////////////////////////////
    @Nullable
    public String decryptGamma(String encryptText) {
        GOST gost = new GOST(this.keys, this.sBox);
        List<Long> openList = new ArrayList<Long>();

        try {
            List<Long> encryptList = Converters.strToListLong(encryptText);
            long synchronizedPost = encryptList.get(0);                         // вычленяем синхропосылку

            encryptList.remove(0);                                        // удаляем синхропосылку
                                                                                // из текста (чтоб не мешалась)
            for(long item : encryptList){
                long nL = ((synchronizedPost & (C232 - 1)) + C1) & (C232 - 1);  // выполняем приращение sp
                long nH = ((synchronizedPost >>> 32) + C2) & (C232 - 1);        // --||--
                synchronizedPost = (nH << 32) | nL;                             // --||--

                gost.setDataBlock(synchronizedPost);                            // передаем значение счетчика на шифрование

                openList.add(gost.getEncryptDataBlock() ^ item);                // получаем гамму и
                                                                                // склыдываем ее по модулю 2
                                                                                // с зашифрованным блоком
            }

            return Converters.listLongToStr(openList);
        } catch (UnsupportedEncodingException e) {
            Log.d(CLASS_NAME, ": " + e.getMessage());
            return null;
        }
    }


    //////////////////////////////////////////////////////////
    ///  Methods encrypt
    /////////////////////////////////////////////////////////
    //-----Begin----------------------------------------------
    @Nullable
    public String encrypt(String openText){
        GOST gost = new GOST(this.keys, this.sBox);

        try {
            List<Long> encryptList = new ArrayList<Long>();
            for (long item : Converters.strToListLong(openText)) {
                gost.setDataBlock(item);
                gost.encrypt32();
                encryptList.add(gost.getEncryptDataBlock());
            }
            return Converters.listLongToStr(encryptList);
        }catch (Exception e){
            Log.d(CLASS_NAME, ": " + e.getMessage());
            return null;
        }
    }


    @Deprecated
    @Nullable
    public String encrypt(String openText, boolean newGOSTsParameters) {

        if (newGOSTsParameters) {
            generateKeys();
            generateSbox();
        }

        GOST gost = new GOST(this.keys, this.sBox);

        try {


            List<Long> encryptList = new ArrayList<Long>();
            for (long item : Converters.strToListLong(openText)) {
                gost.setDataBlock(item);
                encryptList.add(gost.getEncryptDataBlock());
            }
            return Converters.listLongToStr(encryptList);

        }catch (Exception e){
            Log.d(CLASS_NAME, ": " + e.getMessage());
            return null;
        }
    }
    //-----End------------------------------------------------


    //////////////////////////////////////////////////////////
    ///  Method decrypt
    /////////////////////////////////////////////////////////
    @Nullable
    public String decrypt(String encryptText){
        try {
            if (this.keys == null || this.sBox == null) {
                throw new Exception("GOSTs parameters is not available!");
            }

            GOST gost = new GOST(this.keys, this.sBox);

            List<Long> encryptList = new ArrayList<Long>();
            for (long item : Converters.strToListLong(encryptText)) {
                gost.setDataBlock(item);
                encryptList.add(gost.getEncryptDataBlock());
            }
            return Converters.listLongToStr(encryptList);
        }catch (Exception e){
            Log.d(CLASS_NAME, ": " + e.getMessage());
            return null;
        }
    }


    //////////////////////////////////////////////////////////
    ///  Methods save/load GOSTsParameters
    /////////////////////////////////////////////////////////
    //-----Begin----------------------------------------------
    private boolean loadGOSTsParameters(){
        String sBoxStr = this.preferencer.loadStringWithDecrypt(Preferencer.CRYPTO_PREFERENCES, Preferencer.KEY_GOST_SBOX);
        String keysStr = this.preferencer.loadStringWithDecrypt(Preferencer.CRYPTO_PREFERENCES, Preferencer.KEY_GOST_KEYS);

        boolean l = sBoxStr != null && keysStr != null;

        if(l){
            byte[] keysBytes;
            try {
                this.sBoxBytes = sBoxStr.getBytes(Converters.SYMBOL_CODE_NAME);
                this.keys = Converters.byteArrayToLongArray(keysStr.getBytes(Converters.SYMBOL_CODE_NAME));
            } catch (UnsupportedEncodingException e) {
                Log.d(CLASS_NAME, ": " + e.getMessage());
                return false;
            }
        }

        return l;
    }


    private void saveGOSTsParameters(){
        try {
            this.preferencer.saveStringWithEncrypt(
                    Preferencer.CRYPTO_PREFERENCES,
                    Preferencer.KEY_GOST_SBOX,
                    new String(this.sBoxBytes, Converters.SYMBOL_CODE_NAME)
            );

            this.preferencer.saveStringWithEncrypt(
                    Preferencer.CRYPTO_PREFERENCES,
                    Preferencer.KEY_GOST_KEYS,
                    new String(
                            Converters.longArrayToByteArray(this.keys),
                            Converters.SYMBOL_CODE_NAME
                    )
            );
        } catch (UnsupportedEncodingException e) {
            Log.d(CLASS_NAME, ": " + e.getMessage());
        }
    }
    //-----End------------------------------------------------


    //////////////////////////////////////////////////////////
    ///  Methods generate
    /////////////////////////////////////////////////////////
    //-----Begin----------------------------------------------
    private void generateSbox(){
        this.sBoxBytes = new byte[64];
        this.secureRandom.nextBytes(this.sBoxBytes);
        this.sBox = Converters.bytesToIntSBOX(this.sBoxBytes);
    }

    private void generateKeys(){
        byte[] bytes = new byte[32];

        this.secureRandom.nextBytes(bytes);

        this.keys = new long[8];
        int k = 0;
        for (int i = 0; i < this.keys.length; i++){
            for(int j = 0; j < 4; j++){
                this.keys[i] |= ((long)bytes[k] << (j * 8));
                k++;
            }
        }
    }
    //-----End------------------------------------------------
}
