package com.andnovator.neural.network;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by novator on 13.05.2016.
 * <p>
 * Tests for saving/load net using {@link NetworkFileSerializer}
 */
public class NNSaveLoad2FileTest {

    private String defaultFilePath = "neural_network.txt";
    private String defaultSerFilePath = "neural_network_ser";
    private String defaultSeparator = "; ";

    @Test
    public void testSave() throws Exception {
        NeuralNetwork<Double> nn = new NeuralNetwork<>(2, 3, 5, 6);
        nn.setMinMSE(0.002);
        nn.setMaxTrainItNum(30_000);
        new NetworkFileSerializer(defaultFilePath, defaultSeparator).saveNetwork(nn);
    }

    @Test
    public void testLoad() throws Exception {
        NeuralNetwork<Double> nn = new NetworkFileSerializer(defaultFilePath, defaultSeparator).loadNetwork();
        assertEquals(2, nn.getInputsNum());
        assertEquals(3, nn.getOutputsNum());
        assertEquals(5, nn.getHiddenLayersNum());
        assertEquals(6, nn.getHiddenLayersSize());
        assertEquals(0.002, nn.getMinMSE(), NeuralNetworkTest.EPSILON);
    }

    @Test
    public void testSerialize() throws Exception {
        NeuralNetwork<Double> nn = new NeuralNetwork<>(3, 2, 4, 6);
        nn.setMinMSE(0.01);
        nn.setMaxTrainItNum(40_000);
        new NetworkFileSerializer(defaultSerFilePath).seralizeNetwork(nn);
    }

    @Test
    public void testDeserialize() throws Exception {
        NeuralNetwork<Double> nn = new NetworkFileSerializer(defaultSerFilePath).deserializeNetwork();
        assertEquals(3, nn.getInputsNum());
        assertEquals(2, nn.getOutputsNum());
        assertEquals(4, nn.getHiddenLayersNum());
        assertEquals(6, nn.getHiddenLayersSize());
        assertEquals(0.01, nn.getMinMSE(), NeuralNetworkTest.EPSILON);
        assertEquals(40_000, nn.getMaxTrainItNum());
    }
}
