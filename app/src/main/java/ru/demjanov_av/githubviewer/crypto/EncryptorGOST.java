package ru.demjanov_av.githubviewer.crypto;


import java.security.SecureRandom;

/**
 * Created by demjanov on 12.07.2018.
 */

public class EncryptorGOST {

    private SecureRandom secureRandom = new SecureRandom();



    private int[][] generateSbox(){
        byte[] bytes = new byte[64];

        this.secureRandom.nextBytes(bytes);

        int[][] sBox = new int[8][16];
        int k = 0;
        for(int i = 0; i < sBox.length; i++){
            for(int j = 0; j < sBox[i].length; j++){
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
