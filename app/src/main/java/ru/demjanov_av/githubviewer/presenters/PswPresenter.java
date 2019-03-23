package ru.demjanov_av.githubviewer.presenters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import ru.demjanov_av.githubviewer.crypto.Hasher;
import ru.demjanov_av.githubviewer.crypto.supports.Converters;
import ru.demjanov_av.githubviewer.crypto.supports.SequenceGenerator;
import ru.demjanov_av.githubviewer.save_load.Preferencer;
import ru.demjanov_av.githubviewer.views.PswFragment;

/**
 * Created by demjanov on 31.01.2019.
 */

public class PswPresenter {
    //-----Constants variables begin---------------------
    private final static String THIS_NAME       = "PSW_PRESENTER";
    private final static int WRONG_CODE_LIMIT   = 5;

    //-----Work code begin-------------------------------
    public final static int ENTER_CODE          = 0;
    public final static int ENTER_NEW_CODE      = 1;
    public final static int REPEAT_NEW_CODE     = 2;
    //-----Work code end---------------------------------

    //-----Gut code begin--------------------------------
    public final static int ENTER_CODE_SUCCESS  = 200;
    //-----Gut code end----------------------------------

    //-----Fail code begin-------------------------------
    public final static int WRONG_CODE          = 0;
    public final static int MISMATCH_CODE       = 1;
    public final static int RESET_CODE          = 2;
    //-----Fail code end----------------------------------
    //-----Constants variables end------------------------


    //-----Class variables begin--------------------------
    private int[] inputNumbers;
    private PswFragment pswFragment;
    private Preferencer preferencer;
    private String loadedHash = null;
    private String hash = null;
    private byte[] secretKeysBytes = new byte[0];
    //-----Class variables end----------------------------


    //-----Flags variables begin--------------------------
    private int workMode;
    private int wrongCodeCounter = 0;
    //-----Flags variables end----------------------------


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public PswPresenter(PswFragment pswFragment, Context context) {
        this.pswFragment = pswFragment;
        this.preferencer = new Preferencer(context);

        this.pswFragment.startLoad();
        loadHash();
        if(this.loadedHash == null){
            setWorkMode(ENTER_NEW_CODE);
        }else {
            setWorkMode(ENTER_CODE);
        }
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------

    public void setInputNumbers(int[] numbers) {
//        this.pswFragment.startLoad();

        switch (this.workMode) {
            case ENTER_CODE:
                this.inputNumbers = numbers;
                if (verifyCode(this.inputNumbers)){
                    this.pswFragment.setData(ENTER_CODE_SUCCESS);
                }else {
                    setWrongCodeCounter();
                }
                break;

            case ENTER_NEW_CODE:
                this.inputNumbers = numbers;
                setWorkMode(REPEAT_NEW_CODE);
                break;

            case REPEAT_NEW_CODE:
                if(isEqualsNumbers(numbers, this.inputNumbers)){
                    genHash(inputNumbers);
                    saveHash();
                    this.pswFragment.setData(ENTER_CODE_SUCCESS);
                }else {
                    setError(MISMATCH_CODE, null);
                    setWorkMode(ENTER_NEW_CODE);
                }
                break;

            default:
                this.inputNumbers = numbers;
                setWorkMode(this.workMode);
                break;
        }
    }

    private void setWorkMode(int workMode) {
        this.workMode = workMode;
        this.pswFragment.setData(workMode);
        this.pswFragment.endLoad();
    }

    private void setError(int errorCode, @Nullable String message) {
        this.pswFragment.setError(errorCode, message);
        this.pswFragment.endLoad();
    }

    private void setWrongCodeCounter() {
        this.wrongCodeCounter ++;

        if (this.wrongCodeCounter >= WRONG_CODE_LIMIT){
            this.wrongCodeCounter = 0;
            setError(RESET_CODE, null);
        }else {
            setError(WRONG_CODE, null);
        }
    }

    public byte[] getSecretKeysBytes() {
        return secretKeysBytes;
    }

    //-----End------------------------------------------


    /////////////////////////////////////////////////////
    // Method isEqualsNumbers
    ////////////////////////////////////////////////////
    private boolean isEqualsNumbers (int[] a, int[] b){
        if(a.length != b.length) return false;

        for (int i = 0; i < a.length; i++){
            if(a[i] != b[i]){
                return false;
            }
        }

        return true;
    }


    /////////////////////////////////////////////////////
    // Method resetPsw
    ////////////////////////////////////////////////////
    public void resetPsw(){
        //чистим хэш и, что за собой тянет изменяемый ключ
        this.preferencer.removeString(Preferencer.CRYPTO_PREFERENCES, Preferencer.KEY_HASH);
        this.preferencer.removeString(Preferencer.CRYPTO_PREFERENCES, Preferencer.KEY_GOST_KEYS);
        this.preferencer.removeString(Preferencer.CRYPTO_PREFERENCES, Preferencer.KEY_GOST_SBOX);

        setWorkMode(ENTER_NEW_CODE);
    }


    /////////////////////////////////////////////////////
    // Methods safe/load Hash
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------
    private void loadHash(){
        this.loadedHash = this.preferencer.loadString(Preferencer.CRYPTO_PREFERENCES, Preferencer.KEY_HASH);
    }

    private void saveHash(){
        this.preferencer.saveString(Preferencer.CRYPTO_PREFERENCES, Preferencer.KEY_HASH, this.hash);
    }
    //-----End------------------------------------------


    /////////////////////////////////////////////////////
    // Method verifyCode
    ////////////////////////////////////////////////////
    private boolean verifyCode(int[] a){

        genHash(a);

        return this.loadedHash.equals(this.hash);
    }

    /////////////////////////////////////////////////////
    // Method genHash
    ////////////////////////////////////////////////////
    private void genHash(int[] a){
        try {
            this.secretKeysBytes = (new SequenceGenerator()).generate(a);
            this.hash = new String((new Hasher()).getHash(this.secretKeysBytes), Converters.SYMBOL_CODE_NAME);
        } catch (SequenceGenerator.SequenceGeneratorException | UnsupportedEncodingException e) {
            Log.d(THIS_NAME, ": " + e.getMessage());
        }
    }


    /////////////////////////////////////////////////////
    // Method destroy
    ////////////////////////////////////////////////////
    public void destroy(){

    }
}
