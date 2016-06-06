package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NetworkFileSerializer;
import com.andnovator.neural.network.NeuralNetwork;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by novator on 03.05.2016.
 */
public class OneFileNeuralIndex {
    private NeuralNetwork neuroIndexNetwork;
    private int wordMaxLength = 15;
    static private final int bitsForChar = 8;
    private int inputsNum = wordMaxLength * bitsForChar;
    private int outputPosBitsNum = 16;
    private int outputFreqBitsNum = 16;
    private int outputBitsNum = outputPosBitsNum + outputFreqBitsNum + 1;
    private double networkMinMSE = defaultNetworkMinMSE;

    static private double defaultNetworkMinMSE = 1e-2;


    public OneFileNeuralIndex() {
        neuroIndexNetwork = null;
    }
    public OneFileNeuralIndex(NeuralNetwork neuroIndexNetwork, int posBitsNum, int freqBitsNum) {
        this.neuroIndexNetwork = neuroIndexNetwork;
        updateNetworkParams(posBitsNum, freqBitsNum);
    }
    private void setNINetwork(NeuralNetwork neuroIndexNetwork, int posBitsNum, int freqBitsNum) {
        this.neuroIndexNetwork = neuroIndexNetwork;
        updateNetworkParams(posBitsNum, freqBitsNum);
    }
    public void loadSerializedNetwork(String filepath) throws IOException, ClassNotFoundException {
        setNINetwork(new NetworkFileSerializer(filepath).deserializeNetwork(), outputPosBitsNum, outputFreqBitsNum);
    }
    public static OneFileNeuralIndex loadSerializedIndex(String filepath) throws IOException, ClassNotFoundException {
        OneFileNeuralIndex ni = new OneFileNeuralIndex();
        ni.loadSerializedNetwork(filepath);
        return ni;
    }

    private void createNINetwork(int maxWordLength) {
        setMaxWordLength(maxWordLength);
        neuroIndexNetwork = new NeuralNetwork(inputsNum,outputBitsNum,3,inputsNum+8);
        neuroIndexNetwork.setMinMSE(defaultNetworkMinMSE);
    }
    private void createNINetwork(int maxWordLength, double minMSE) {
        createNINetwork(maxWordLength);
        setNetworkMinMSE(minMSE);
    }
    private void updateNetworkParams(int posBitsNum, int freqBitsNum) {
        inputsNum = neuroIndexNetwork.getInputsNum();
        wordMaxLength = inputsNum / bitsForChar;
        outputPosBitsNum = posBitsNum;
        outputFreqBitsNum = freqBitsNum;
        outputBitsNum = outputPosBitsNum + outputFreqBitsNum + 1;
    }
    private void setMaxWordLength(int length) {
        wordMaxLength = length;
        inputsNum = wordMaxLength * bitsForChar;
    }

    public NeuralNetwork getNeuroIndexNetwork() { return neuroIndexNetwork; }
    void setNeuroIndexNetwork(NeuralNetwork neuroIndexNetwork, int posBitsNum, int freqBitsNum) {
        this.neuroIndexNetwork = neuroIndexNetwork;
        updateNetworkParams(posBitsNum, freqBitsNum);
    }
    public int getWordMaxLength() { return wordMaxLength; }
    public int getOutpuBitsNum() { return outputBitsNum; }
    public double getNetworkMinMSE() {
        if (neuroIndexNetwork!=null) {
            return neuroIndexNetwork.getMinMSE();
        } else {
            return networkMinMSE;
        }
    }
    public void setNetworkMinMSE(double minMSE) {
        if ((minMSE > 0) && (minMSE < 0.5)) {
            if (neuroIndexNetwork != null) {
                neuroIndexNetwork.setMinMSE(minMSE);
            }
            networkMinMSE = minMSE;
        }
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

    static List<Double> strToDoubleBitArrList(String str, int bitsNum) {
        if (bitsNum < 8*str.length()) {
            System.out.println("Bits num change to str size ("+8*str.length()+")");
        }
        byte[] barr = str.getBytes(StandardCharsets.US_ASCII);
        List<Double> resArr = new ArrayList<>();
        if (bitsNum > 8*str.length()) {
            for (int i=8*str.length(); i<bitsNum; ++i) {
                resArr.add(-1.);
            }
        }
        for (byte b : barr) {
            int val = b;
            for (int i = 0; i < 8; ++i) {
                resArr.add((val & 128) == 0 ? -1. : 1.);
                val <<= 1;
            }
        }
        return resArr;
    }
    protected List<Double> strToDoubleBits(String str) {
        return strToDoubleBitArrList(str, inputsNum);
    }

    static int bitsListToInt(List<Double> bitsArrLst) throws IllegalArgumentException {
        int res = 0;
        for (int i = 0; i < bitsArrLst.size(); i++) {
            double dBit = bitsArrLst.get(i);
            res <<= 1;
            if (isDoubleBitOne(dBit)) {
                res += 1;
            } else if (!isDoubleBitZero(dBit)) {
                throw new IllegalArgumentException("Net responce on "+i+" place != 1 by abs - get:" + bitsArrLst.get(i));
            }
        }
        return res;
    }

    // Number of bits to represent numbers in binary format for input (or output) to neural network
    public static final int DEFAULT_BITS = 5;
    // The string "00000...0" (of length DEFAULT_BITS) used for padding
    public static final String PAD_STR = new String(new char[DEFAULT_BITS]).replace('\0', '0');  // http://stackoverflow.com/a/2807731
    // pad to PAD_STR chars
    public static String padLeft(String str) {
        return PAD_STR.substring(str.length()) + str;
    }

    //FIXME: Method below is deprecated
    /**
     * @deprecated use {@link #intToDoubleBits(int number, int bitsNum)} instead.
     * Converts the number (e.g. 6) to a binary array (e.g. [0, 0, 1, 1, 0]), encoding bits as +/- 1
     * @param number the number (6)
     * @param desiredLength the (minimal) length of array (5 for [0, 0, 1, 1, 0])
     * @return the array [+, +, -, -, +]
     */
    @Deprecated
    public  static List<Double> numberToBits(int number, int desiredLength) {
        // FIXME_: actually desiredLength = DEFAULT_BITS always
        String binary = padLeft(Integer.toBinaryString(number));
        char[] bytes = binary.toCharArray();
        // TODO_: make this ----^ normal?

        List<Double> res = new ArrayList<>(bytes.length);
        for (char b : bytes) {
            res.add(b == '0' ? -1.0 : 1.0);
        }
        return res;
    }
    public static List<Double> intToDoubleBits(int number, int bitsNum) {
        int numberBitsNum = Integer.SIZE - Integer.numberOfLeadingZeros(number);
        if (bitsNum < numberBitsNum) {
            System.err.println("Input integer bits num is greater than input bits num.");
            System.err.println("Convert int->'double bits' is failure. Return value is 'null'");
            return null;
        }
        List<Double> resArr = new ArrayList<>();
        if (bitsNum > numberBitsNum) {
            for (int i=numberBitsNum; i<bitsNum; ++i) {
                resArr.add(-1.);
            }
        }
        int checkBitInt = (1 << numberBitsNum-1);
        for (int i=numberBitsNum; i>0; --i) {
            resArr.add((number & checkBitInt) == 0 ? -1. : 1.);
            number <<= 1;
        }
        return resArr;
    }

    static List<Double> posFreqToDoubleBitsArrLst(PosFreqPair pair, int posBitsNum, int freqBitsNum) {
        List<Double> resArr = new ArrayList<>();
        if (pair.getFreq() == 0) {
            resArr.add(-1.);
        } else {
            resArr.add(1.);
        }
        List<Double> posArr = intToDoubleBits(pair.getPos(), posBitsNum);
        List<Double> freqArr = intToDoubleBits(pair.getFreq(), freqBitsNum);
        if ((posArr == null) || (freqArr == null)) {
            return null;
        }
        resArr.addAll(posArr);
        resArr.addAll(freqArr);
        return resArr;
    }
    public List<Double> posFreqToDoubleBits(PosFreqPair pair) {
        return posFreqToDoubleBitsArrLst(pair, outputPosBitsNum, outputFreqBitsNum);
    }

    public static int maxMapStrLengthInLst(Map<String,PosFreqPair> stringPairMap) {
        return Collections.max(stringPairMap.keySet(), Comparator.comparingInt(String::length)).length();
    }
    public static int maxStrLengthInLst(List<String> strings) {
        return Collections.max(strings, Comparator.comparingInt(String::length)).length();
    }

    /**
     * Return Map keys as List of keys class
     * using: OneFileNeuralIndex.&lt;K, V&gt;hashMapKeysToList(...)
     * example: OneFileNeuralIndex.&lt;String, Integer&gt;hashMapKeysToList(map)
     * @param <K> Map keys class
     * @param <V> Map values class
     * @return Map keys as List
     */
    public static <K, V> List<K> hashMapKeysToList(Map<K, V> map) {
        return new ArrayList<>(map.keySet());
    }

    public static List<String> generateRandStrList(int strNumbers) {
        List<String> randomStrings = new ArrayList<>(strNumbers);
        for(int i = 0; i < strNumbers; ++i) {
            char[] word = new char[ThreadLocalRandom.current().nextInt(4)+1];
            for(int j = 0; j < word.length; j++) {
                word[j] = (char)(ThreadLocalRandom.current().nextInt(1, 65));
            }
            randomStrings.add(new String(word));
        }
        return randomStrings;
    }
    public static List<String> generateRandWordList(int wordNumbers) {
        List<String> randomWords = new ArrayList<>(wordNumbers);
        int wordsAdded = 0;
        // 'z', 'c', 't' - some random chars, needed for random not-word string generating
        char[] strChars = {'a', 'a', 'a', 'z', 'c', 't'};
        strChars[3] = 'z';
        strChars[4] = 'c';
        strChars[5] = 't';
        for (char fCh=97; fCh<=122; ++fCh) {
            Arrays.fill(strChars, (char)1);
            strChars[0] = fCh;
            for (char sCh=97; sCh<=122; ++sCh) {
                strChars[1] = sCh;
                for (char tCh = 97; tCh <= 122; tCh++) {
                    strChars[2] = tCh;
                    randomWords.add(new String(strChars));
                    ++wordsAdded;
                    if (wordsAdded>=wordNumbers) { return randomWords; }
                }
            }
        }
        return randomWords;
    }

    //FIXME: use staic constant, for example - NOT in code
    public boolean trainIndex(Map<String, PosFreqPair> itemWordsMap, List<String> allWordsList, boolean recreateNetwork) {
//        return trainIndex(itemWordsMap, allWordsList, 15);
        return trainIndex(itemWordsMap, allWordsList, maxStrLengthInLst(allWordsList), recreateNetwork);
    }
    public boolean trainIndex(Map<String, PosFreqPair> itemWordsMap, List<String> allWordsList) {
//        return trainIndex(itemWordsMap, allWordsList, 15);
        return trainIndex(itemWordsMap, allWordsList, maxStrLengthInLst(allWordsList), true);
    }

    public boolean trainIndex(Map<String, PosFreqPair> itemWordsMap, List<String> allWordsList, int maxLangWordLength, boolean recreateNetwork) {
//        createNINetwork(maxMapStrLengthInLst(itemWordsMap), 1e-4);
        if ((neuroIndexNetwork == null) || recreateNetwork) {
            createNINetwork(maxLangWordLength, networkMinMSE);
        }
        List<List<Double>> wordsToFeed = new ArrayList<>();
        List<List<Double>> trainingSample = new ArrayList<>();
        for (String word : itemWordsMap.keySet()) {
            wordsToFeed.add(strToDoubleBits(word));
            trainingSample.add(posFreqToDoubleBits(itemWordsMap.get(word)));
        }
        int allWordsNum = allWordsList.size();
        // FIXME: there should be correct constant, it is taken by words num
        final int someConstantOtherWordFeedNum = 6;
        if (itemWordsMap.size() >= allWordsList.size()) {
            for (String otherWord : generateRandWordList(someConstantOtherWordFeedNum)) {
                wordsToFeed.add(strToDoubleBits(otherWord));
                trainingSample.add(posFreqToDoubleBits(new PosFreqPair(0, 0)));
            }
        } else {
            Collections.shuffle(allWordsList);
            int wordsAdded = 0;
            for (String otherWord : allWordsList) {
                if (itemWordsMap.get(otherWord) == null) {
                    if (wordsAdded>=someConstantOtherWordFeedNum) { break; }
                    wordsToFeed.add(strToDoubleBits(otherWord));
                    trainingSample.add(posFreqToDoubleBits(new PosFreqPair(0, 0)));
                    ++wordsAdded;
                }
            }
        }
        return neuroIndexNetwork.Train(wordsToFeed, trainingSample);
    }
    public int[] wordSearch(String word){
        return wordSearch(word, false);
    }
    public int[] wordSearch(String word, boolean isResPrint){
        int[] resArr = {-1, -1};
        List<Double> resArrLst = wordSearchNetResponce(word, isResPrint);
        if (resArrLst != null) {
            // FIXME: there must not be just ".round"!
            if ( isDoubleBitOne(resArrLst.get(0)) ) {
                try {
                    resArr[0] = bitsListToInt(resArrLst.subList(1, outputPosBitsNum + 1));
                    resArr[1] = bitsListToInt(resArrLst.subList(outputPosBitsNum + 1, outputBitsNum));
                } catch (IllegalArgumentException e) {
                    return new int[]{-1, -1};
                }
            }
        }
        return resArr;
    }
    List<Double> wordSearchNetResponce(String word) {
        return wordSearchNetResponce(word, false);
    }
    List<Double> wordSearchNetResponce(String word, boolean isResPrint) {
        /*Arrays.stream( "str".getBytes(StandardCharsets.US_ASCII) )
                .map ( ch -> ch - 'a' )
                .flatMap ( ch -> numberToBits(ch).stream() )
                .collect(Collectors.toList())*/
        List<Double> searchWordBytes = strToDoubleBits(word);
        if (neuroIndexNetwork != null) {
            return neuroIndexNetwork.GetNetResponse(searchWordBytes, isResPrint);
        } else {
            return null;
        }
    }

    public static boolean isDoubleBitZero(double val) {
        return (1+val < 0.1);
    }
    public static boolean isDoubleBitOne(double val) {
        return (1-val < 0.1);
    }
    public static boolean isDoubleBitZeroOrOne(double val) {
        return isDoubleBitZero(val) || isDoubleBitOne(val);
    }
}
