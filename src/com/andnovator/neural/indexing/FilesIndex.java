package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NeuralNetwork;
import com.andnovator.utils.FileLemmatizationUtils;
import edu.stanford.nlp.simple.Sentence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by novator on 16.05.2016.
 */
public class FilesIndex {
    NeuralNetwork filesIndexNN = null;
    private List<Path> filesPath = Collections.emptyList();
    List<OneFileNeuralIndex> fileIndexNILst = new ArrayList<>();
    Set<String> allFileWords = new HashSet<>();
    Set<String> allWords = new HashSet<>();
    private List<String> allWordsLst;
    private List<Set<String>> wordsByFile;

    private int wordMaxLength = 20;
    static protected final int bitsForChar = 5;
    private int inputsNum = wordMaxLength * bitsForChar;

    private int filesIndexedNum;

    private int outputBitsNum = filesIndexedNum + 1;

    static private double defaultNetworkMinMSE = 3e-2;
    private double networkMinMSE = defaultNetworkMinMSE;

    public FilesIndex() {
        filesIndexNN = null;
    }
    public FilesIndex(NeuralNetwork filesIndexNN) {
        this.filesIndexNN = filesIndexNN;
        allWords = new HashSet<>();
        allWords.add("nebulous");      // 1
        allWords.add("peregrinate");   // 2
        allWords.add("rhythm");        // 3
        allWords.add("brief");         // 4
        allWords.add("kickshaw");      // 5
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
        allWords.add("cicisbeo");      // 16
        allWords.add("remarkable");    // 17
        allWords.add("thoughtless");   // 18
        allWords.add("billow");        // 19
        allWords.add("food");          // 20
        updateNetworkParams();
    }
    void setNINetwork(NeuralNetwork neuroIndexNetwork) {
        this.filesIndexNN = neuroIndexNetwork;
        updateNetworkParams();
    }

    @Deprecated
    public void loadSerializedNI(String filepath) throws IOException, ClassNotFoundException {

    }

    private void createNINetwork() {
        filesIndexNN = new NeuralNetwork(inputsNum, outputBitsNum, 3, inputsNum + 8);
        filesIndexNN.setMinMSE(networkMinMSE);
    }
    private void createNINetwork(double minMSE) {
        filesIndexNN = new NeuralNetwork(inputsNum,outputBitsNum,3,inputsNum+8);
        filesIndexNN.setMinMSE(minMSE);
    }
    private void createNINetwork(int maxWordLength) {
        setMaxWordLength(maxWordLength);
        filesIndexNN = new NeuralNetwork(inputsNum,outputBitsNum,3,inputsNum+8);
        filesIndexNN.setMinMSE(networkMinMSE);
    }
    private void createNINetwork(int maxWordLength, double minMSE) {
        createNINetwork(maxWordLength);
        setNetworkMinMSE(minMSE);
    }
    private void updateNetworkParams() {
        inputsNum = filesIndexNN.getInputsNum();
        wordMaxLength = inputsNum / bitsForChar;
        outputBitsNum = filesIndexNN.getOutputsNum();
        filesIndexedNum = outputBitsNum - 1;
    }
    private void setMaxWordLength(int length) {
        wordMaxLength = length;
        inputsNum = wordMaxLength * bitsForChar;
    }

    NeuralNetwork getFilesIndexNetwork() { return filesIndexNN; }
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

    void setFilesIndexedNum(int filesIndexedNum) {
        this.filesIndexedNum = filesIndexedNum;
        this.outputBitsNum = filesIndexedNum + 1;
    }
    public int getFilesIndexedNum() { return filesIndexedNum; }

    public List<Path> getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(List<Path> filesPath) {
        this.filesPath = filesPath;
    }

    public List<OneFileNeuralIndex> getFileIndexNILst() { return fileIndexNILst; }
    public void setFileIndexNILst(List<OneFileNeuralIndex> fileIndexNILst) { this.fileIndexNILst = fileIndexNILst; }

    public void indexDir(String dirPathStr) throws IOException {
        indexDir(Paths.get(dirPathStr));
    }
    public void indexDir(Path dirPath) throws IOException {
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new FileNotFoundException("Input dir not exist or path incorrect. Got: "+dirPath);
        }
        Set<String> plainTxtExt = new HashSet<>();
        plainTxtExt.add(".txt");
        plainTxtExt.add(".log");
        plainTxtExt.add(".xml");
        plainTxtExt.add(".html");
        plainTxtExt.add(".htm");
        filesPath = Files.walk(dirPath)
                .filter(p -> !Files.isDirectory(p))
                // TODO: If add support files without extension, need change last filter below
                .filter(p -> p.toString().contains("."))
                .filter(p -> plainTxtExt
                        .contains(p.toString().toLowerCase().substring(p.toString().lastIndexOf('.'))))
                .collect(toList());
        setFilesIndexedNum(filesPath.size());
        for (Path filepath : filesPath) {
            allFileWords.addAll(FileLemmatizationUtils.loadFileLemms(filepath));
        }
        allWords.addAll(allFileWords);
        allWordsLst = new ArrayList<>(allWords);
        wordsByFile = new ArrayList<>(filesIndexedNum);

        setMaxWordLength(Math.max(OneFileNeuralIndex.maxStrLengthInLst(allWords), wordMaxLength));

        wordsByFile = filesPath.parallelStream().map(path -> {
            Map<String, PosFreqPair> wordsMapOneFile = null;
            try {
                wordsMapOneFile = IndexingFileLoader.loadFile(path);
            } catch (IOException e) {
                // TODO: есть какой-то готовый способ "wrap as unckecked?"
                throw new RuntimeException(e);
            }
            Set<String> wordsSet = wordsMapOneFile.keySet();
            OneFileNeuralIndex oneFileNI = new OneFileNeuralIndex();
            oneFileNI.setNetworkMinMSE(0.1);
            // Warning: trainIndex shuffle input allWordsList!!!!
            oneFileNI.trainIndex(wordsMapOneFile, allWordsLst, getWordMaxLength());
            addFileNI(oneFileNI);
            return wordsSet;
        }).collect(Collectors.toList());

        // Now train big net
        List<List<Double>> wordsToFeed = new ArrayList<>(allFileWords.size());
        List<List<Double>> trainingSample = new ArrayList<>(wordsToFeed.size());

        for (String word : allFileWords) {
            wordsToFeed.add(OneFileNeuralIndex.strToDoubleBitArrList(word, inputsNum));
            trainingSample.add(findFilesForWord(word));
        }
        Collections.shuffle(allWordsLst);
        for (String word : OneFileNeuralIndex.generateRandStrList(4)) {
            wordsToFeed.add(OneFileNeuralIndex.strToDoubleBitArrList(word, inputsNum));
            double[] dar = new double[outputBitsNum];
            Arrays.fill(dar, -1.);
            trainingSample.add(Arrays.stream(dar).boxed().collect(Collectors.toList()));
        }

        createNINetwork();
        filesIndexNN.Train(wordsToFeed, trainingSample);
    }
    private List<Double> findFilesForWord(String word) {
        List<Double> res = new ArrayList<>();
        res.add(1.);
        res.addAll(wordsByFile.stream().map(fileWords -> fileWords.contains(word) ? +1. : -1.).collect(Collectors.toList()));
        return res;
        /*return IntStream.range(0, filesIndexedNum)
                // если в файле #fileIndex содержится слово word - то +1, иначе -1
                .map( fileIndex -> wordsByFile.get(fileIndex).contains(word) ? +1 : -1)
                .mapToObj(Double::valueOf)
                .collect(toList());*/
    }

    synchronized
    public void addFileNI(OneFileNeuralIndex niNN) {
        fileIndexNILst.add(niNN);
    }


    public Map<Path, PosFreqPair> wordSearchNormal(String word){
        return wordSearchNormal(word, false);
    }
    public Map<Path, PosFreqPair> wordSearchNormal(String word, boolean isResPrint){
        Map<Path, PosFreqPair> resMap = new HashMap<>();
        List<Double> resArrLst = wordSearchNetResponce(word, isResPrint);
        if (resArrLst != null) {
            if (OneFileNeuralIndex.isDoubleBitOne(resArrLst.get(0))) {
                int niInd = 0;
                int[] ressArr;
                for (Double respDouble : resArrLst.subList(1, filesIndexedNum + 1)) {
                    if (OneFileNeuralIndex.isDoubleBitOne(respDouble)) {
                        ressArr = fileIndexNILst.get(niInd).wordSearchNormal(word, isResPrint);
                        if (ressArr[0] != -1) {
                            // Fixme: maybe not arraylist, so maybe not .get but iterator
                            resMap.put(filesPath.get(niInd), new PosFreqPair(ressArr[0], ressArr[1]));
                        }
                    }
                    ++niInd;
                }
            }
        }
        return resMap;
    }

    List<Double> wordSearchNetResponce(String word) {
        return wordSearchNetResponce(word, false);
    }
    List<Double> wordSearchNetResponce(String word, boolean isResPrint) {
        List<Double> searchWordBytes = OneFileNeuralIndex.strToDoubleBitArrList(word, inputsNum);
        if (filesIndexNN != null) {
            return filesIndexNN.GetNetResponse(searchWordBytes, isResPrint);
        } else {
            return null;
        }
    }

    public Map<Path, PosFreqPair> wordSearch(String word) { return wordSearch(word, false); }
    public Map<Path, PosFreqPair> wordSearch(String word, boolean isResPrint) {
        String normalWord = new Sentence(word).lemma(0);
        return wordSearchNormal(normalWord, isResPrint);
    }

}
