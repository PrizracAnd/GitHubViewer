package ru.demjanov_av.githubviewer.save_load;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.demjanov_av.githubviewer.crypto.AES;
import ru.demjanov_av.githubviewer.crypto.supports.Converters;


/**
 * Created by demjanov on 05.02.2019.
 */

public class Preferencer {
    //-----Constants variables begin---------------------
    private final static String PREFERENCER         = "Preferencer";

    //-----FileNames variables begin---------------------
    public final static String CRYPTO_PREFERENCES   = "cryptoSettings";
    public final static String APP_PREFERENCES      = "cryptoSettings";
    //-----FileNames variables end-----------------------

    //-----Keys variables begin--------------------------
    public final static String KEY_HASH             = "Hash_code";
    public final static String KEY_GOST_SBOX        = "GOST_sBox";
    public final static String KEY_GOST_KEYS        = "GOST_keys";
    //-----Keys variables end----------------------------
    //-----Constants variables end-----------------------


    //-----Class variables begin-------------------------
    private Context context;
    private AES aes = null;
    //-----Class variables end---------------------------


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public Preferencer(Context context) {
        this.context = context;
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------

    public void setAes(AES aes) {
        this.aes = aes;
    }

    //-----End------------------------------------------


    /////////////////////////////////////////////////////
    // Method saveStringWithEncrypt
    ////////////////////////////////////////////////////
    public void saveStringWithEncrypt(String prefFileName, String saveKey, String inputString){
        String encryptString = null;

        if(this.aes != null) {
            encryptString = aes.encrypt(inputString);
        }

        if(encryptString != null){
            saveString(prefFileName, saveKey, encryptString);
        }
    }


    /////////////////////////////////////////////////////
    // Method saveString
    ////////////////////////////////////////////////////
    public void saveString(String prefFileName, String saveKey, String inputString){
        SharedPreferences sp = this.context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(saveKey, inputString);
        editor.apply();
    }


    /////////////////////////////////////////////////////
    // Method loadStringWithDecrypt
    ////////////////////////////////////////////////////
    @Nullable
    public String loadStringWithDecrypt(String prefFileName, String loadKey){
        String str = loadString(prefFileName, loadKey);

        if(this.aes == null){
            str = null;
        }

        if(str != null) {
           str = aes.decrypt(str);
        }

        return str;
    }


    /////////////////////////////////////////////////////
    // Method loadString
    ////////////////////////////////////////////////////
    @Nullable
    public String loadString(String prefFileName, String loadKey){
        SharedPreferences sp = this.context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        if(sp.contains(loadKey)) {
            return sp.getString(loadKey, null);
        }else {
            return null;
        }
    }



    /////////////////////////////////////////////////////
    // Method removeString
    ////////////////////////////////////////////////////
    public void removeString(String prefFileName, String saveKey){
        SharedPreferences sp = this.context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(saveKey);
        editor.apply();
    }


    /////////////////////////////////////////////////////
    // Method saveBytes
    ////////////////////////////////////////////////////
    public void saveBytes(String prefFileName, String saveKey, byte[] bytes){
        SharedPreferences sp = this.context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        int i = 0;
        for(long item: Converters.byteArrayToListLong(bytes)){
            editor.putLong(saveKey + i, item);
            i++;
        }
        editor.apply();
    }


    /////////////////////////////////////////////////////
    // Method loadBytes
    ////////////////////////////////////////////////////
    @Nullable
    public byte[] loadBytes(String prefFileName, String loadKey){
        SharedPreferences sp = this.context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);

        List<Long> longList = new ArrayList<Long>();
        int i = 0;
        String innerLoadKey = loadKey + i;

        while (sp.contains(innerLoadKey)){
            longList.add(sp.getLong(innerLoadKey, 0L));
            i++;
            innerLoadKey = loadKey + i;
        }

        if(longList.size() > 0) {
            return Converters.trimLeftByteArray(Converters.listLongToByteArray(longList));
        }else {
            return null;
        }
    }


    /////////////////////////////////////////////////////
    // Method removeBytes
    ////////////////////////////////////////////////////
    public void removeBytes(String prefFileName, String saveKey){
        SharedPreferences sp = this.context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        int i = 0;
        String innerSaveKey = saveKey + i;

        while (sp.contains(innerSaveKey)){
            editor.remove(innerSaveKey);
            i++;
            innerSaveKey = saveKey + i;
        }
        editor.apply();
    }


    /////////////////////////////////////////////////////
    // Method saveBytesWithEncrypt
    ////////////////////////////////////////////////////
    public void saveBytesWithEncrypt(String prefFileName, String saveKey, byte[] bytes){
        byte[] encryptBytes = null;

        if(this.aes != null) {
            encryptBytes = aes.encrypt(bytes);
        }

        if(encryptBytes != null){
            saveBytes(prefFileName, saveKey, encryptBytes);
        }
    }


    /////////////////////////////////////////////////////
    // Method loadBytesWithDecrypt
    ////////////////////////////////////////////////////
    @Nullable
    public byte[] loadBytesWithDecrypt(String prefFileName, String loadKey){
        byte[] bytes = loadBytes(prefFileName, loadKey);

        if(this.aes == null || bytes == null){
            return null;
        }else {
            return aes.decrypt(bytes);
        }
    }
}
