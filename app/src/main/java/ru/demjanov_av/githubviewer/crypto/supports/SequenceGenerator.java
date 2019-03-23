package ru.demjanov_av.githubviewer.crypto.supports;

import java.util.ArrayList;
import java.util.List;

public class SequenceGenerator {


    //---------------------------------------------------
    //---------------------------------------------------
    /////////////////////////////////////////////////////
    // Exception SequenceGeneratorException
    ////////////////////////////////////////////////////
    //Делаем свое исключение, чтоб можно было ругаться
    public class SequenceGeneratorException extends Exception{
        SequenceGeneratorException(String message){
            super(message);
        }
    }
    //---------------------------------------------------
    //---------------------------------------------------


    //-----Constants variables begin---------------------
    private final static int[][] BOX = {
            {2, 3},     // 0
            {1, 3},     // 1
            {1, 2},     // 2
            {0, 3},     // 3
            {0, 2},     // 4
            {0, 1},     // 5
            {3, 2},     // 6
            {3, 1},     // 7
            {2, 1},     // 8
            {3, 0},     // 9
            {2, 0},     //10    NOT USED
            {1, 0}      //11    NOT USED
    };
    //-----Constants variables end-----------------------


    //-----Class variables begin-------------------------
    private int[] incomingNumbers;
    //-----Class variables end---------------------------


    /////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public SequenceGenerator(){}

//    public SequenceGenerator(int[] incomingNumbers) throws SequenceGeneratorException {
//        setIncomingNumbers(incomingNumbers);
//    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    private void setIncomingNumbers(int[] incomingNumbers) throws SequenceGeneratorException {
        if(incomingNumbers.length < 4){
            throw new SequenceGeneratorException("Not enough data!");
        }else {
            this.incomingNumbers = incomingNumbers;
        }
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method generate
    ////////////////////////////////////////////////////
    public byte[] generate (int[] incomingNumbers) throws SequenceGeneratorException {
        List<Long> sequence;

        setIncomingNumbers(incomingNumbers);                        // проверяем полученный массив чисел (здесь м.б. исключение)

        sequence = getBasicSequence(this.incomingNumbers);          // преобразуем его в 256-битную последовательность путем
                                                                    // многократного повторения бит чисел последовательности
                                                                    // с побитовой инверсией

        sequence = shiftSequence(sequence, this.incomingNumbers);   // производим побитовый циклический сдвиг четвертей
                                                                    // последовательности в зависимости от чисел исходного массива

        return Converters.listLongToByteArray(sequence);            // преобразуем поледовательность в массив байт
    }


    /////////////////////////////////////////////////////
    // Method getBasicSequence
    ////////////////////////////////////////////////////
    private List<Long> getBasicSequence (int[] numbersArray){
        int count = numbersArray.length;
        List<Long> basicSequence = new ArrayList<Long>();
        long item = 0L;

        if(count > 4) count = 4;

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < count; j++){
                long k = numbersArray[j];

                if(((i >>> j) % 2) == 1) k = ~k;            //в зависимости от соответствующего бита числа счетчика от 0 до 15
                                                            //инвентируем значение элемента массива ('~' - побитовая инверсия)

                k &= 15;                                    //Зануляем все биты кроме первых четырех.

                item <<= 4;                                 //сдвигаем результирующую последовательность влево на 4 бита

                item |= k;                                  //заполняем первые 4 бита результирующей последовательности
            }

            if(((i + 1) % 4) == 0) basicSequence.add(item);
        }

        return basicSequence;
    }


    /////////////////////////////////////////////////////
    // Method shiftSequence
    ////////////////////////////////////////////////////
    private List<Long> shiftSequence(List<Long> longList, int[] a) {
        List<Long> sequence = new ArrayList<Long>();

        for(int i = 0; i < a.length; i++){
//            if (a[i] < 0 || a[i] > 11){
//                throw new SequenceGeneratorException("Incorrect number!");
//            }
            int k = a[i] & 2147483647;              // зануляем бит знака числа на всякий случай

            k %= BOX.length;                        // исключаем NullPointerException
            sequence.add(
                    shiftLong(
                            longList.get(i % longList.size()),
                            a[BOX[k][0]],
                            a[BOX[k][1]]
                    )
            );
        }

        return sequence;
    }


    /////////////////////////////////////////////////////
    // Method shiftLong
    ////////////////////////////////////////////////////
    private long shiftLong(long longIn, int a, int b){
        int k;
        long tmp;
        long longOut = longIn;

        // Задача данного метода - произвести циклический сдвиг части последовательности в одну из сторон.
        // Сторона сдвига определяется параметром а; количество бит сдвига рассчитывается как 2 в степени b,
        // или 2 в степени b - 5, т.к. в лонге всего 64 бита.
        if(b > 5){
            k = 1 << (b - 5);
        }else{
            k = (1 << b);
        }

        // Т.к. в java сдвиг априори НЕ циклический, сначала мы сдвигаем последовательность в противоположную сторону
        // на 64 - k бит, и таким образом сохраняем во временной переменной отбрасываемые при основном сдвиге биты,
        // чтобы затем дописать их в противоположный сдвигу край последовательности.
        if(a < 5){
            tmp = longOut << (64 - k);
            longOut >>>= k;
            longOut |= tmp;
        }else {
            tmp = longOut >>> (64 - k);
            longOut <<= k;
            longOut |= tmp;
        }

        return longOut;
    }

}
