package ru.demjanov_av.githubviewer.crypto.supports;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by demjanov on 05.02.2019.
 */

public class Converters {
    //-----Constants variables begin---------------------
    public final static String   SYMBOL_CODE_NAME    = "UTF-8";
    private final static int     CODE_PLUS           = 29;
    private final static int     CODE_MINUS          = 30;
    private final static int     CODE_DOUBLE_MINUS   = 31;
    private final static int     CONST_PLUS          = 32;
    private final static int     CONST_MINUS         = 65;
    private final static int     CONST_POROG         = 187;
    private final static int     SYMBOL_MINUS        = 128;
    private final static int     HI_FRONTIER         = 126;
    private final static int     LOW_FRONTIER        = 32;

    //-----Constants variables end-----------------------


    /////////////////////////////////////////////////////
    // Method listLongToByteArray
    ////////////////////////////////////////////////////
    public static byte[] listLongToByteArray(List<Long> longList) {
        byte[] bytes = new byte[8 * longList.size()];
        int i = 0;

        for (long longIn: longList) {
            for (int j = 0; j < 8; j++) {
                bytes[i] = (byte) (longIn >>> (j * 8));
                i++;
            }
        }

        return bytes;
    }


    /////////////////////////////////////////////////////
    // Method byteArrayToListLong
    ////////////////////////////////////////////////////
    public static List<Long> byteArrayToListLong(byte[] bytes){
        List<Long> longList = new ArrayList<Long>();
        long item = 0L;

        for (int i = 0; i < bytes.length;){
            item ^= item;
            for (int j = 0; j < 8; j++){
                if(i >= bytes.length) break;
                item |= (((long)bytes[i] & 255) << (j * 8));
                i++;
            }
            longList.add(item);
        }

        return longList;
    }


    /////////////////////////////////////////////////////
    // Method longArrayToByteArray
    ////////////////////////////////////////////////////
    public static byte[] longArrayToByteArray(@NotNull long[] longs) {
        byte[] bytes = new byte[8 * longs.length];
        int k = 0;

        for (long aLong : longs) {
            for (int j = 0; j < 8; j++) {
                bytes[k] = (byte) (aLong >>> (j * 8));
                k++;
            }
        }

        return bytes;
    }


    /////////////////////////////////////////////////////
    // Method byteArrayToLongArray
    ////////////////////////////////////////////////////
    public static long[] byteArrayToLongArray(byte[] bytes){
        List<Long> longList = byteArrayToListLong(bytes);
        long[] longs = new long[longList.size()];

        for (int i = 0; i < longList.size(); i++){
            longs[i] = longList.get(i);
        }

        return longs;
    }


    /////////////////////////////////////////////////////
    // Method strToListLong
    ////////////////////////////////////////////////////
    public static List<Long> strToListLong(@NotNull String str) throws UnsupportedEncodingException {
        byte[] bytes = str.getBytes(SYMBOL_CODE_NAME);

        return byteArrayToListLong(decodeUTF(bytes));
    }


    /////////////////////////////////////////////////////
    // Method listLongToStr
    ////////////////////////////////////////////////////
    @NonNull
    public static String listLongToStr(@NonNull List<Long> longList) throws UnsupportedEncodingException {
        byte[] bytes = listLongToByteArray(longList);

        return new String(encodeUTF(bytes), SYMBOL_CODE_NAME);
    }


    /////////////////////////////////////////////////////
    // Method bytesToIntSBOX
    ////////////////////////////////////////////////////
    public static int[][] bytesToIntSBOX (byte[] sBoxBytes){
        int[][] sBox;

        if(sBoxBytes.length < 64){
            sBox = new int[0][0];
        }else {
            sBox = new int[8][16];
            int k = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 16; j++) {
                    sBox[i][j] = (sBoxBytes[k] & 15);
                    j++;
                    sBox[i][j] = (sBoxBytes[k] >>> 4);
                    k++;
                }
            }
        }
        return sBox;
    }


    // 29-30    65, 32

    /////////////////////////////////////////////////////
    // Method encodeUTF
    ////////////////////////////////////////////////////
    @Contract(pure = true) @NotNull
    private static byte[] encodeUTF(@NotNull byte[] bytes){
        byte[] bytes1 = new byte[bytes.length * 2];
        int k = 0;

        for (byte aByte : trimLeftByteArray(bytes)) {
            if (aByte < 0) {                                    // обрабатываем числа меньше 0
                int a = (aByte * -1) + SYMBOL_MINUS;
                if (a > CONST_POROG) {
                    bytes1[k] = CODE_DOUBLE_MINUS;
                    k++;
                    bytes1[k] = (byte) (a - (CONST_MINUS * 2));
                } else {
                    bytes1[k] = CODE_MINUS;
                    k++;
                    bytes1[k] = (byte) (a - CONST_MINUS);
                }
            } else {                                            // обрабатываем числа больше 0

                if (aByte < LOW_FRONTIER) {                     // если меньше нижней границы
                                                                // диапазона допустимых байт
                    bytes1[k] = CODE_PLUS;
                    k++;
                    bytes1[k] = (byte) (aByte + CONST_PLUS);

                } else if (aByte > HI_FRONTIER) {               // если больше верхней границы
                                                                // диапазона допустимых байт (127)
                    bytes1[k] = CODE_MINUS;
                    k++;
                    bytes1[k] = (byte) (aByte - CONST_MINUS);

                } else {                                        // если в границах
                                                                // диапазона допустимых байт
                    bytes1[k] = aByte;
                }
            }
            k++;
        }

        return trimLeftByteArray(bytes1);
    }


    /////////////////////////////////////////////////////
    // Method decodeUTF
    ////////////////////////////////////////////////////
    @Contract(pure = true) @NotNull
    private static byte[] decodeUTF(@NotNull byte[] bytes){
        byte[] bytes1 = new byte[bytes.length];
        int k = 0;

        for (int i = 0; i < bytes.length; i++){
            if(bytes[i] < LOW_FRONTIER){                        // если это код операции
                int a = bytes[i];
                i++;
                int b = bytes[i];

                switch (a){
                    case CODE_PLUS:
                        bytes1[k] = (byte)(b - CONST_PLUS);
                        break;
                    case CODE_DOUBLE_MINUS:                     // в этой ветке break отсутствует умышленно
                        b += CONST_MINUS;
                    case CODE_MINUS:
                        b += CONST_MINUS;
                        if(b >= SYMBOL_MINUS){
                            b = (b - SYMBOL_MINUS) * -1;
                        }
                        bytes1[k] = (byte)b;
                        break;
                    default:
                         break;
                }
            }else {                                             // если просто символ
                bytes1[k] = bytes[i];
            }

            k++;
        }

        return trimLeftByteArray(bytes1);
    }


    /////////////////////////////////////////////////////
    // Supports Methods
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @NonNull
    private static byte[] trimLeftByteArray(byte[] bytes){
        int i = bytes.length;

        for (int j = i - 1; j >= 0; j--){
            i = j;
            if(bytes[j] != 0){
                break;
            }
        }

        return Arrays.copyOf(bytes, i + 1);
    }
    //-----End-------------------------------------------
}
