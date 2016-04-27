package com.andnovator.NeuralNetwork;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<ArrayList<Double> > DataToFeedNN = new ArrayList<>();
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
        NN.GetNetResponse(DataToFeedNN.get(0));

        System.out.println();
        System.out.println("Input data: { 1, 0 }");
        NN.GetNetResponse(DataToFeedNN.get(1));

        System.out.println();
        System.out.println("Input data: { 0, 1 }");
        NN.GetNetResponse(DataToFeedNN.get(2));

        System.out.println();
        System.out.println("Input data: { 0, 0 }");
        //NN.GetNetResponse(DataToFeedNN.get(3));
        NN.GetNetResponse(Data4);


        System.out.println();
        System.out.println("Input data: { test }");
        NN.GetNetResponse(Data5);
    }
}
