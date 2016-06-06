package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NeuralNetwork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by novator on 16.05.2016.
 */
public class FilesIndex {
    NeuralNetwork filesIndexNN = null;
    private List<Path> filesPath = Collections.emptyList();
    List<OneFileNeuralIndex> fileIndexNILst = new ArrayList<>();
    Set<String> allFileWords = new HashSet<>();
    Set<String> allWords;

    private int wordMaxLength = 20;
    static private final int bitsForChar = 8;
    private int inputsNum = wordMaxLength * bitsForChar;
    private int maxFileNum = 3;

    private int outputBitsNum = maxFileNum + 1;

    static private double defaultNetworkMinMSE = 1e-2;
    private double networkMinMSE = defaultNetworkMinMSE;

    public FilesIndex() {
        filesIndexNN = null;
    }
    public FilesIndex(NeuralNetwork filesIndexNN) {
        this.filesIndexNN = filesIndexNN;
        allWords = new HashSet<>();
        allWords.add("nebulous");      // 1
        allWords.add("scare");         // 2
        allWords.add("rhythm");        // 3
        allWords.add("brief");         // 4
        allWords.add("flash");         // 5
        allWords.add("evanescent");    // 6
        allWords.add("hum");           // 7
        allWords.add("sloppy");        // 8
        allWords.add("alcoholic");     // 9
        allWords.add("jumbled");       // 10
        allWords.add("tame");          // 11
        allWords.add("heavenly");      // 12
        allWords.add("duck");          // 13
        allWords.add("makeshift");     // 14
        allWords.add("intend");        // 15
        allWords.add("distance");      // 16
        allWords.add("remarkable");    // 17
        allWords.add("thoughtless");   // 18
        allWords.add("hat");           // 19
        allWords.add("food");          // 20
        updateNetworkParams();
    }
    private void setNINetwork(NeuralNetwork neuroIndexNetwork, int posBitsNum, int freqBitsNum) {
        this.filesIndexNN = neuroIndexNetwork;
        updateNetworkParams();
    }

    @Deprecated
    public void loadSerializedNI(String filepath) throws IOException, ClassNotFoundException {

    }


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

    @Deprecated
    public NeuralNetwork getFilesIndexNetwork() { return filesIndexNN; }
    void setFilesIndexNetwork(NeuralNetwork neuroIndexNetwork, int posBitsNum, int freqBitsNum) {
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

    public void indexFiles(String dirPathStr) throws IOException {
        Path dirPath = Paths.get(dirPathStr);
        Set<String> plainTxtExt = new HashSet<>();
        plainTxtExt.add(".txt");
        plainTxtExt.add(".log");
        plainTxtExt.add(".xml");
        plainTxtExt.add(".html");
        plainTxtExt.add(".htm");
        if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
            filesPath = Files.walk(dirPath)
                    .filter(p -> plainTxtExt
                            .contains(p.toString().toLowerCase().substring(p.toString().lastIndexOf('.'))))
                    .collect(Collectors.toList());
        } /*else {
            throw exception...
        }*/
        Map<String, PosFreqPair> wordsMapOneFile;
        OneFileNeuralIndex oneFileNI;
        for (Path path : filesPath) {
            wordsMapOneFile = IndexingFileLoader.loadFile(filesPath.toString());
            allFileWords.addAll(wordsMapOneFile.keySet());
            allWords.addAll(wordsMapOneFile.keySet());

        }
    }

    public void addFileNI(OneFileNeuralIndex niNN) {
        fileIndexNILst.add(niNN);
    }
}
