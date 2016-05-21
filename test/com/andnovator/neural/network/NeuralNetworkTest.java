package com.andnovator.neural.network;

import com.andnovator.neural.indexing.*;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

//import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class NeuralNetworkTest {


    @Test
    public void returnNetwWithRespTest() {
        List<List<Double> > DataToFeedNN = new ArrayList<>();
        List<Double> Data1 = new ArrayList<>();
        Data1.add(1.0);
        Data1.add(1.0);

        DataToFeedNN.add(Data1);

        List<Double>  Data2 = new ArrayList<>();
        Data2.add(1.0);
        Data2.add(-1.0);

        DataToFeedNN.add(Data2);

        List<Double>  Data3 = new ArrayList<>();
        Data3.add(-1.0);
        Data3.add(1.0);

        DataToFeedNN.add(Data3);

        List<Double>  Data4 = new ArrayList<>();
        Data4.add(-1.);
        Data4.add(-1.);

        //DataToFeedNN.add(Data4);

        List<Double>  Data5 = new ArrayList<>();
        Data5.add(0.);
        Data5.add(-1.0);
        //DataToFeedNN.add(Data5);

        List<List<Double> > trainingSample = new ArrayList<>();
        List<Double> ts1 = new ArrayList<>();
        ts1.add(-1.);

        List<Double> ts2 = new ArrayList<>();
        ts2.add(1.);

        List<Double> ts3 = new ArrayList<>();
        ts3.add(-1.);

        List<Double> ts4 = new ArrayList<>();
        ts4.add(-1.);

        //List<Double> ts5 = new ArrayList<>();
        //ts5.add(-1.);

        trainingSample.add(ts1);
        trainingSample.add(ts2);
        trainingSample.add(ts3);
        //trainingSample.add(ts4);
        //trainingSample.add(ts5);

        NeuralNetwork<Double> NN = new NeuralNetwork<>(2,1,5,4);
        NN.setMinMSE(0.0001);
        NN.Train(DataToFeedNN,trainingSample);

//        System.out.println();
//        System.out.println("Input data: { 1, 1 }");
//        NN.GetNetResponse(DataToFeedNN.get(0), true);
//
//        System.out.println();
//        System.out.println("Input data: { 1, 0 }");
//        NN.GetNetResponse(DataToFeedNN.get(1), true);
//
//        System.out.println();
//        System.out.println("Input data: { 0, 1 }");
//        NN.GetNetResponse(DataToFeedNN.get(2), true);
//
//        System.out.println();
//        System.out.println("Input data: { 0, 0 }");
//        //NN.GetNetResponse(DataToFeedNN.get(3), true);
//        NN.GetNetResponse(Data4);
//
//        System.out.println();
//        System.out.println("Input data: { test }");

//        return new Pair<>(NN, NN.GetNetResponse(Data5, true));
    }

    @Test
    public void testForFInd() {
        List<List<Double> > DataToFeedNN = new ArrayList<>();
        List<Double> Data1 = new ArrayList<>();
        Data1.add(-1.);
        Data1.add(-1.);
        Data1.add(-1.);
        DataToFeedNN.add(Data1);
        List<Double> Data2 = new ArrayList<>();
        Data2.add(-1.);
        Data2.add(1.);
        Data2.add(-1.);
        DataToFeedNN.add(Data2);
        List<Double> Data3 = new ArrayList<>();
        Data3.add(1.);
        Data3.add(-1.);
        Data3.add(-1.);
        DataToFeedNN.add(Data3);
        List<Double> Data4 = new ArrayList<>();
        Data4.add(-1.);
        Data4.add(1.);
        Data4.add(1.);
        DataToFeedNN.add(Data4);
        List<List<Double> > trainingSample = new ArrayList<>();
        List<Double> ts1 = new ArrayList<>();
        ts1.add(0.);
        List<Double> ts2 = new ArrayList<>();
        ts2.add(0.01);
        List<Double> ts3 = new ArrayList<>();
        ts3.add(0.);
        List<Double> ts4 = new ArrayList<>();
        ts4.add(0.03);
        trainingSample.add(ts1);
        trainingSample.add(ts2);
        trainingSample.add(ts3);
        trainingSample.add(ts4);
        NeuralNetwork<Double> NN = new NeuralNetwork<>(3,1,4,6);
        NN.setMinMSE(0.000001);
        NN.Train(DataToFeedNN,trainingSample);


        System.out.println("Trained!");
        System.out.println();
        System.out.println("Input data: { 001 }");
        NN.GetNetResponse(DataToFeedNN.get(2), true);
        System.out.println("Input data: { 010 }");
        NN.GetNetResponse(DataToFeedNN.get(1));

        List<Double> testData = new ArrayList<>();
        testData.add(-1.);
        testData.add(-1.);
        testData.add(1.);
//        return NN.GetNetResponse(testData);
    }

    @Test
    public void testNumberToBitsAndDivisibleBy3() {

        int inputNeuronNum = OneFileNeuralIndex.DEFAULT_BITS;

        List<Integer> numbers = IntStream.rangeClosed(0, 26).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);

        // Training sample: trInputDatas[i] are inputs, and trOutputDatas[i] are expected ouputs for that input
        List<List<Double>> trInputDatas = new ArrayList<>();
        List<List<Double>> trOutputDatas = new ArrayList<>();

        for (Integer number : numbers) {
            // we train to answer: "is divisible by 3?"
            Double correctAnswer = number % 3 == 0 ? +1. : -1.;
            List<Double> outputs = new ArrayList<>(1);
            outputs.add(correctAnswer);

            trInputDatas.add(OneFileNeuralIndex.numberToBits(number, OneFileNeuralIndex.DEFAULT_BITS));
            trOutputDatas.add(outputs);
        }

        NeuralNetwork<Double> NN = new NeuralNetwork<>(inputNeuronNum,1,4,inputNeuronNum*2);
        NN.setMinMSE(0.0001);
        NN.Train(trInputDatas,trOutputDatas);

        System.out.println("Trained!");
        System.out.println();

        for (int i = 0; i < 5; ++i) {
            System.out.println("Input data: " + Arrays.toString(trInputDatas.get(i).toArray()) + " = " + numbers.get(i));
            NN.GetNetResponse(trInputDatas.get(i), true);
        }

        System.out.println("-- new values: 30 (true) and 31 (false) --");

        int newValue = 30; // a new value, that were not in the training set
        List<Double> newInput = OneFileNeuralIndex.numberToBits(newValue, OneFileNeuralIndex.DEFAULT_BITS);
        System.out.println("Input data: " + Arrays.toString(newInput.toArray()));
        List<Number> response = NN.GetNetResponse(newInput, true);
        Assert.assertEquals(1, Math.round(response.get(0).doubleValue()));

        newValue = 31; // a new value, that were not in the training set
        newInput = OneFileNeuralIndex.numberToBits(newValue, OneFileNeuralIndex.DEFAULT_BITS);
        System.out.println("Input data: " + Arrays.toString(newInput.toArray()));
        NN.GetNetResponse(newInput, true);
        Assert.assertEquals(1, Math.round(response.get(0).doubleValue()));

    }
}
