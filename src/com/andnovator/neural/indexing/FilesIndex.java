package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NeuralNetwork;

/**
 * Created by novator on 16.05.2016.
 */
public class FilesIndex {
    NeuralNetwork<Double> filesIndexNN;
    private int wordMaxLength = 20;
    static private final int bitsForChar = 8;
    private int inputsNum = wordMaxLength * bitsForChar;
    private int outputFileIndBitsNum = 16;
    private int outputBitsNum = outputFileIndBitsNum + 1;
    static private double defaultNetworkMinMSE = 1e-3;

    public FilesIndex() {
        filesIndexNN = null;
    }
    public FilesIndex(NeuralNetwork<Double> filesIndexNN) {
        this.filesIndexNN = filesIndexNN;
        updateNetworkParams();
    }
    private void createNINetwork(int maxWordLength) {
        setMaxWordLength(maxWordLength);
        filesIndexNN = new NeuralNetwork<>(inputsNum,outputBitsNum,3,inputsNum+8);
        filesIndexNN.setMinMSE(defaultNetworkMinMSE);
    }
    private void createNINetwork(int maxWordLength, double minMSE) {
        createNINetwork(maxWordLength);
        setNetworkMinMSE(minMSE);
    }
    private void updateNetworkParams() {
        inputsNum = filesIndexNN.getInputsNum();
        wordMaxLength = inputsNum / bitsForChar;
        outputBitsNum = filesIndexNN.getOutputsNum();
        outputFileIndBitsNum = outputBitsNum - 1;
    }
    private void setMaxWordLength(int length) {
        if (length>0) {
            wordMaxLength = length;
            inputsNum = wordMaxLength * bitsForChar;
        }
    }

    public NeuralNetwork<Double> getfilesIndexNN() { return filesIndexNN; }
    void setfilesIndexNN(NeuralNetwork<Double> filesIndexNN, int posBitsNum, int freqBitsNum) {
        this.filesIndexNN = filesIndexNN;
        updateNetworkParams();
    }
    public int getWordMaxLength() { return wordMaxLength; }
    public int getOutpuBitsNum() { return outputBitsNum; }
    public double getNetworkMinMSE() {
        if (filesIndexNN!=null) {
            return filesIndexNN.getMinMSE();
        } else {
            return defaultNetworkMinMSE;
        }
    }
    public void setNetworkMinMSE(double minMSE) {
        if (filesIndexNN != null) {
            if ((minMSE > 0) && (minMSE < 0.5)) {
                filesIndexNN.setMinMSE(minMSE);
            }
        }
    }
}
