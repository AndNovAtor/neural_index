package com.andnovator.neural.network;

import com.andnovator.neural.indexing.NeuralIndex;
import javafx.util.Pair;

import java.util.*;
//import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {

    public static void main(String[] args) {
//        testDel();
//        System.out.printf("%07d", Integer.toBinaryString(1024));
        System.out.println("'str' to binary: " + NeuralIndex.strToBinaryStr("str"));
        Map<String, Pair<Integer, Integer>> map = new HashMap<>();
        map.put("One", new Pair<>(1, 1));
        map.put("Two", new Pair<>(2, 2));
        map.put("Three", new Pair<>(3, 3));
        map.put("Four", new Pair<>(4, 4));
        map.put("Five", new Pair<>(5, 5));
        System.out.println(NeuralIndex.maxStrLengthInLst(map));
        /*ArrayList<ArrayList<Double> > DataToFeedNN = new ArrayList<>();
        ArrayList<Double> Data1 = new ArrayList<>();
        Data1.add(1.0);
        Data1.add(1.0);

        DataToFeedNN.add(Data1);

        ArrayList<Double>  Data2 = new ArrayList<>();
        Data2.add(1.0);
        Data2.add(-1.0);

        DataToFeedNN.add(Data2);

        ArrayList<Double>  Data3 = new ArrayList<>();
        Data3.add(-1.0);
        Data3.add(1.0);

        DataToFeedNN.add(Data3);

        ArrayList<Double>  Data4 = new ArrayList<>();
        Data4.add(-1.);
        Data4.add(-1.);

        //DataToFeedNN.add(Data4);

        ArrayList<Double>  Data5 = new ArrayList<>();
        Data5.add(0.);
        Data5.add(-1.0);
        //DataToFeedNN.add(Data5);

        ArrayList<ArrayList<Double> > trainingSample = new ArrayList<>();
        ArrayList<Double> ts1 = new ArrayList<>();
        ts1.add(-1.);

        ArrayList<Double> ts2 = new ArrayList<>();
        ts2.add(1.);

        ArrayList<Double> ts3 = new ArrayList<>();
        ts3.add(-1.);

        ArrayList<Double> ts4 = new ArrayList<>();
        ts4.add(-1.);

        //ArrayList<Double> ts5 = new ArrayList<>();
        //ts5.add(-1.);

        trainingSample.add(ts1);
        trainingSample.add(ts2);
        trainingSample.add(ts3);
        //trainingSample.add(ts4);
        //trainingSample.add(ts5);

        NeuralNetwork<Double> NN = new NeuralNetwork<>(2,1,5,4);
        NN.SetMinMSE(0.0001);
        NN.Train(DataToFeedNN,trainingSample);


        System.out.println();
        System.out.println("Input data: { 1, 1 }");
        NN.GetNetResponse(DataToFeedNN.get(0), true);

        System.out.println();
        System.out.println("Input data: { 1, 0 }");
        NN.GetNetResponse(DataToFeedNN.get(1), true);

        System.out.println();
        System.out.println("Input data: { 0, 1 }");
        NN.GetNetResponse(DataToFeedNN.get(2), true);

        System.out.println();
        System.out.println("Input data: { 0, 0 }");
        //NN.GetNetResponse(DataToFeedNN.get(3), true);
        NN.GetNetResponse(Data4);


        System.out.println();
        System.out.println("Input data: { test }");
        NN.GetNetResponse(Data5, true);*/
    }

    static void testForFInd() {
        ArrayList<ArrayList<Double> > DataToFeedNN = new ArrayList<>();
        ArrayList<Double> Data1 = new ArrayList<>();
        Data1.add(-1.);
        Data1.add(-1.);
        Data1.add(-1.);
        DataToFeedNN.add(Data1);
        ArrayList<Double> Data2 = new ArrayList<>();
        Data2.add(-1.);
        Data2.add(1.);
        Data2.add(-1.);
        DataToFeedNN.add(Data2);
        ArrayList<Double> Data3 = new ArrayList<>();
        Data3.add(1.);
        Data3.add(-1.);
        Data3.add(-1.);
        DataToFeedNN.add(Data3);
        ArrayList<Double> Data4 = new ArrayList<>();
        Data4.add(-1.);
        Data4.add(1.);
        Data4.add(1.);
        DataToFeedNN.add(Data4);
        ArrayList<ArrayList<Double> > trainingSample = new ArrayList<>();
        ArrayList<Double> ts1 = new ArrayList<>();
        ts1.add(0.);
        ArrayList<Double> ts2 = new ArrayList<>();
        ts2.add(0.01);
        ArrayList<Double> ts3 = new ArrayList<>();
        ts3.add(0.);
        ArrayList<Double> ts4 = new ArrayList<>();
        ts4.add(0.03);
        trainingSample.add(ts1);
        trainingSample.add(ts2);
        trainingSample.add(ts3);
        trainingSample.add(ts4);
        NeuralNetwork<Double> NN = new NeuralNetwork<>(3,1,4,6);
        NN.SetMinMSE(0.000001);
        NN.Train(DataToFeedNN,trainingSample);


        System.out.println("Trained!");
        System.out.println();
        System.out.println("Input data: { 001 }");
        NN.GetNetResponse(DataToFeedNN.get(2), true);
        System.out.println("Input data: { 010 }");
        NN.GetNetResponse(DataToFeedNN.get(1));

        ArrayList<Double> testData = new ArrayList<>();
        testData.add(-1.);
        testData.add(-1.);
        testData.add(1.);
        NN.GetNetResponse(testData, true);
    }

    // Number of bits to represent numbers in binary format for input (or output) to neural network
    static final int DEFAULT_BITS = 5;

    // The string "00000...0" (of length DEFAULT_BITS) used for padding
    static final String PAD_STR = new String(new char[DEFAULT_BITS]).replace('\0', '0');  // http://stackoverflow.com/a/2807731

    /**
     * Converts the number (e.g. 6) to a binary array (e.g. [0, 0, 1, 1, 0]), encoding bits as +/- 1
     * @param number the number (6)
     * @param desiredLength the (minimal) length of array (5 for [0, 0, 1, 1, 0])
     * @return the array [+, +, -, -, +]
     */
    static ArrayList<Double> numberToBits(int number, int desiredLength) {
        // FIXME: actually desiredLength = DEFAULT_BITS always
        String binary = padLeft(Integer.toBinaryString(number));
        char[] bytes = binary.toCharArray();
        // TODO: make this ----^ normal?

        ArrayList<Double> res = new ArrayList<>(bytes.length);
        for (char b : bytes) {
            res.add(b == '0' ? -1.0 : 1.0);
        }
        return res;
    }

    // pad to PAD_STR chars
    static String padLeft(String str) {
        return PAD_STR.substring(str.length()) + str;
    }

    static void testDel() {

        int inputNeuronNum = DEFAULT_BITS;

        List<Integer> numbers = IntStream.rangeClosed(0, 26).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);

        // Training sample: trInputDatas[i] are inputs, and trOutputDatas[i] are expected ouputs for that input
        ArrayList<ArrayList<Double>> trInputDatas = new ArrayList<>();
        ArrayList<ArrayList<Double>> trOutputDatas = new ArrayList<>();

        for (Integer number : numbers) {
            // we train to answer: "is divisible by 3?"
            Double correctAnswer = number % 3 == 0 ? +1. : -1.;
            ArrayList<Double> outputs = new ArrayList<>(1);
            outputs.add(correctAnswer);

            trInputDatas.add(numberToBits(number, DEFAULT_BITS));
            trOutputDatas.add(outputs);
        }

        NeuralNetwork<Double> NN = new NeuralNetwork<>(inputNeuronNum,1,4,inputNeuronNum*2);
        NN.SetMinMSE(0.0001);
        NN.Train(trInputDatas,trOutputDatas);

        System.out.println("Trained!");
        System.out.println();

        for (int i = 0; i < 5; ++i) {
            System.out.println("Input data: " + Arrays.toString(trInputDatas.get(i).toArray()) + " = " + numbers.get(i));
            NN.GetNetResponse(trInputDatas.get(i), true);
        }

        System.out.println("-- new values: 30 (true) and 31 (false) --");

        int newValue = 30; // a new value, that were not in the training set
        ArrayList<Double> newInput = numberToBits(newValue, DEFAULT_BITS);
        System.out.println("Input data: " + Arrays.toString(newInput.toArray()));
        NN.GetNetResponse(newInput, true);

        newValue = 31; // a new value, that were not in the training set
        newInput = numberToBits(newValue, DEFAULT_BITS);
        System.out.println("Input data: " + Arrays.toString(newInput.toArray()));
        NN.GetNetResponse(newInput, true);

    }
}
