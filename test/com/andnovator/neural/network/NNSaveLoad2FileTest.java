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
    private String defaultSeparator = "; ";

    public static final double EPSILON = 1e-8;

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
        assertEquals(0.002, nn.getMinMSE(), EPSILON);
    }
}
