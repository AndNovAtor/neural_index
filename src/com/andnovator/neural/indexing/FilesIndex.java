package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by novator on 16.05.2016.
 */
public class FilesIndex {
    NeuralNetwork filesIndexNN = null;
    List<OneFileNeuralIndex> fileIndexNILst = new ArrayList<>();
    private int wordMaxLength = 20;
    static private final int bitsForChar = 8;
    private int inputsNum = wordMaxLength * bitsForChar;
    private int maxFileNum = 3;
    private int outputBitsNum = maxFileNum + 1;

    static private double defaultNetworkMinMSE = 5e-3;

    private double networkMinMSE = defaultNetworkMinMSE;

    public FilesIndex() {
        filesIndexNN = null;
    }
    public FilesIndex(NeuralNetwork filesIndexNN) {
        this.filesIndexNN = filesIndexNN;
        updateNetworkParams();
    }
    private void setNINetwork(NeuralNetwork neuroIndexNetwork, int posBitsNum, int freqBitsNum) {
        this.filesIndexNN = neuroIndexNetwork;
        updateNetworkParams();
    }
    /*public void loadSerializedNetwork(String filepath) throws IOException, ClassNotFoundException {
        setNINetwork(new NetworkFileSerializer(filepath).<Double>deserializeNetwork(), outputPosBitsNum, outputFreqBitsNum);
    }*/


    private void createNINetwork(int maxWordLength) {
        setMaxWordLength(maxWordLength);
        filesIndexNN = new NeuralNetwork(inputsNum,outputBitsNum,3,inputsNum+8);
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
        maxFileNum = outputBitsNum  - 1;
    }
    private void setMaxWordLength(int length) {
        wordMaxLength = length;
        inputsNum = wordMaxLength * bitsForChar;
    }

    public NeuralNetwork getNeuroIndexNetwork() { return filesIndexNN; }
    void setNeuroIndexNetwork(NeuralNetwork neuroIndexNetwork, int posBitsNum, int freqBitsNum) {
        this.filesIndexNN = neuroIndexNetwork;
        updateNetworkParams();
    }
    public int getWordMaxLength() { return wordMaxLength; }
    public int getOutpuBitsNum() { return outputBitsNum; }
    public double getNetworkMinMSE() {
        if (filesIndexNN!=null) {
            return filesIndexNN.getMinMSE();
        } else {
            return networkMinMSE;
        }
    }
    public void setNetworkMinMSE(double minMSE) {
        if ((minMSE > 0) && (minMSE < 0.5)) {
            if (filesIndexNN != null) {
                filesIndexNN.setMinMSE(minMSE);
            }
            networkMinMSE = minMSE;
        }
    }

    public void addFileNI(OneFileNeuralIndex niNN) {
        fileIndexNILst.add(niNN);
    }
}
