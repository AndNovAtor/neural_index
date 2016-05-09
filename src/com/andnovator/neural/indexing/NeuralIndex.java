package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NeuralNetwork;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by novator on 03.05.2016.
 */
public class NeuralIndex {
    private NeuralNetwork<Double> neuroIndexNetwork;
    private int wordMaxLength = 10;
    private int outputNumberBytesNum = 2;
    private int outputNumberBitsNum = outputNumberBytesNum * 8;
    private int outputBitsNum = outputNumberBitsNum*2 + 1;


    public NeuralIndex() {
        neuroIndexNetwork = null;
    }

    public NeuralIndex(NeuralNetwork<Double> neuroIndexNetwork) {
        this.neuroIndexNetwork = neuroIndexNetwork;
        wordMaxLength = neuroIndexNetwork.getInputsNum();
    }

    public NeuralNetwork<Double> getNeuroIndexNetwork() { return neuroIndexNetwork; }

    public void setNeuroIndexNetwork(NeuralNetwork<Double> neuroIndexNetwork) {
        this.neuroIndexNetwork = neuroIndexNetwork;
    }

    public static String strToBinaryStr(String str) {
        byte[] barr = str.getBytes();
        StringBuilder binarySB = new StringBuilder();
        for (byte b : barr) {
            int val = b;

            for (int i = 0; i < 8; ++i) {
                binarySB.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binarySB.append(' ');
        }
        return binarySB.toString();
    }

    public static ArrayList<Byte> strToBitList(String str) {
        byte[] barr = str.getBytes();
        ArrayList<Byte> res = new ArrayList<>();
        for (byte b : barr) {
            int val = b;
            for (int i = 0; i < 8; ++i) {
                res.add((val & 128) == 0 ? (byte) 0 : (byte) 1);
                val <<= 1;
            }
        }
        return res;
    }

    static ArrayList<Double> strToDoubleBitList(String str) {
        byte[] barr = str.getBytes(/*StandardCharsets.US_ASCII*/);
        ArrayList<Double> res = new ArrayList<>();
        for (byte b : barr) {
            int val = b;
            for (int i = 0; i < 8; ++i) {
                res.add((val & 128) == 0 ? -1. : 1.);
                val <<= 1;
            }
        }
        return res;
    }

    static int bitsArraylstToInt(List<Number> bitsArrLst) throws IllegalArgumentException {
        int res = 0;
        for (int i = 0; i < bitsArrLst.size(); i++) {
            double dBit = Math.round((double)bitsArrLst.get(i));
            res <<= 1;
            if (dBit == 1) {
                res += 1;
            } else if (dBit != -1) {
                throw new IllegalArgumentException("Net responce on "+i+" place != 1 by abs");
            }
        }
        return res;
    }

    public static int maxStrLengthInLst(Map<String,Pair<Integer,Integer>> stringPairMap) {
        return (Collections.max(stringPairMap.keySet(), (s1, s2) -> Math.max(s1.length(), s2.length()))).length();
    }

    public boolean trainIndex(Map<String,Pair<Integer,Integer>> itemWordsList, List<String> allWordsList) {
        int maxLength = maxStrLengthInLst(itemWordsList);
        neuroIndexNetwork = new NeuralNetwork<>(maxLength,1,5,4);
        return true;
    }

    public double[] wordSearch(String word){

        /*Arrays.stream( "str".getBytes(StandardCharsets.US_ASCII) )
                .map ( ch -> ch - 'a' )
                .flatMap ( ch -> numberToBits(ch).stream() )
                .collect(Collectors.toList())*/

        ArrayList<Double> searchWordBytes = strToDoubleBitList(word);
        double[] resArr = {-1.,-1.};
        if (neuroIndexNetwork != null) {
            ArrayList<Number> resArrLst =  neuroIndexNetwork.GetNetResponse(searchWordBytes);
            int resNumeCodBits = (resArrLst.size()-1)/2;
            if ((Math.round((double)(resArrLst.get(0))) == 1) && (resNumeCodBits>=16)) {
                for (int j = 0; j < 2; j++) {
                    resArr[j] = bitsArraylstToInt(resArrLst.subList(resNumeCodBits*j, resNumeCodBits*(j+1)));
                }
            }
        }
        return resArr;
    }
}

//class WordInFilePosStruct {
//    int position;
//    int frequency;
//    public WordInFilePosStruct(int position, int frequency) {
//        this.position = position;
//        this.frequency = frequency;
//    }
//    public int getPosition() { return position; }
//    public void setPosition(int position) {
//        this.position = position;
//    }
//    public int getFrequency() { return frequency; }
//    public void setFrequency(int frequency) {
//        this.frequency = frequency;
//    }
//}
