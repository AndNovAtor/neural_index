package com.andnovator.neural.network;

import com.andnovator.neural.indexing.OneFileIndexTest;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by novator on 13.05.2016.
 */
public class NNSaveLoad2FileTest {
    public static void main(String[] args) {
//        Pair<NeuralNetwork<Double>, ArrayList<Number>> netwTestResPair = new NeuralNetworkTest().returnNetwWithRespTest();
        Pair<NeuralNetwork<Double>, ArrayList<Number>> netwTestResPair = new OneFileIndexTest().returnNetwWithRespTest();
        NeuralNetwork<Double> nn = netwTestResPair.getKey();
        ArrayList<Number> respArr = netwTestResPair.getValue();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("neural_index.txt"), "utf-8"))) {
            String separator = " ";
            writer.write(IntStream
                    .of(nn.getInputsNum(), nn.getOutputsNum(), nn.getHiddenLayersNum(), nn.getHiddenLayersSize())
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining(separator)));
            writer.write(System.lineSeparator());
            writer.write(respArr.stream().map(Object::toString).collect(Collectors.joining(separator)));
        } catch (IOException ex) {
        // report
        }
    }
}
