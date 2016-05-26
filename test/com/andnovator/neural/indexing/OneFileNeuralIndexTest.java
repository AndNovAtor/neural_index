package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NetworkFileSerializer;
import com.andnovator.neural.network.NeuralNetwork;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by novator on 10.05.2016.
 */
public class OneFileNeuralIndexTest {

    private String defaultFilePath = "neural_index.txt";
    private String defaultSerFileName = "neural_index";
    private String defaultSerFileExt = ".ser";
    private String defaultSerFilePath = defaultSerFileName+defaultSerFileExt;
    private String defaultSeparator = "; ";

    @Test
    public void returnNetwWithRespTest() throws Exception {
        //All words:
        List<String> allWords = new ArrayList<>();
        allWords.add("nebulous");	    // 1
        allWords.add("scare");		    // 2
        allWords.add("rhythm");		    // 3
        allWords.add("brief");		    // 4
        allWords.add("flash");		    // 5
        allWords.add("evanescent");	    // 6
        allWords.add("hum");		    // 7
        allWords.add("sloppy");		    // 8
        allWords.add("alcoholic");	    // 9
        allWords.add("jumbled");	    // 10
        allWords.add("tame");		    // 11
        allWords.add("heavenly");	    // 12
        allWords.add("ducks");		    // 13
        allWords.add("makeshift");	    // 14
        allWords.add("intend");		    // 15
        allWords.add("distance");	    // 16
        allWords.add("remarkable");	    // 17
        allWords.add("thoughtless");    // 18
        allWords.add("hat");		    // 19
        allWords.add("food");		    // 20
        //end all words
        //File:
        List<String> oneFileWords = allWords.subList(0,10);
        List<String> secFileWords = allWords.subList(8,18);
        List<String> allFilesWords;
        // All files words:
        allFilesWords = new ArrayList<>(allWords.subList(0,18));
        // File words hashmap
        HashMap<String, PosFreqPair> fileWordsMap2 = new HashMap<>(secFileWords.size());
        for (int i = 0; i < secFileWords.size(); ++i) {
            fileWordsMap2.put(secFileWords.get(i), new PosFreqPair(i,1));
        }
        /*HashMap<String, PosFreqPair> fileWordsMap1 = new HashMap<>(oneFileWords.size());
        for (int i = 0; i < oneFileWords.size(); ++i) {
            fileWordsMap1.put(oneFileWords.get(i), new PosFreqPair(i,1));
        }
        OneFileNeuralIndex fileNIndex = new OneFileNeuralIndex();
        fileNIndex.setNetworkMinMSE(0.01);
        Assert.assertTrue( fileNIndex.trainIndex(fileWordsMap1, allWords) );
        int[] resArr;
        for (String word : allWords) {
            System.out.println("For word: " + word);
            resArr = fileNIndex.wordSearch(word, true);
            System.out.println(" pos.: " + resArr[0] + "; freq.: " + resArr[1]);
        }*/
        OneFileNeuralIndex fileNIndex2 = new OneFileNeuralIndex();
        fileNIndex2.loadSerializedNetwork(defaultSerFileName+"2"+defaultSerFileExt);
        fileNIndex2.setNetworkMinMSE(0.005);
        Assert.assertTrue( fileNIndex2.trainIndex(fileWordsMap2, allWords, false) );
        int[] resArr;
        for (String word : allWords) {
            System.out.println("For word: " + word);
            resArr = fileNIndex2.wordSearch(word, true);
            System.out.println(" pos.: " + resArr[0] + "; freq.: " + resArr[1]);
        }
        new NetworkFileSerializer(defaultSerFileName+"2"+defaultSerFileExt).seralizeNetwork(fileNIndex2.getNeuroIndexNetwork());
//        new NetworkFileSerializer(defaultFilePath).saveNetwork(fileNIndex.getNeuroIndexNetwork());
//        return new Pair<>(fileNIndex.getNeuroIndexNetwork(), fileNIndex.wordSearchNetResponce(allWords.get(18)));
    }

    @Test
    public void simpleTest() throws Exception {
        /*System.out.println("'str' to binary: " + OneFileNeuralIndex.strToBinaryStr("str"));
        Map<String, PosFreqPair> map = new HashMap<>();
        map.put("One", new PosFreqPair(1, 1));
        map.put("Two", new PosFreqPair(2, 2));
        map.put("Three", new PosFreqPair(3, 3));
        map.put("Four", new PosFreqPair(4, 4));
        map.put("Five", new PosFreqPair(5, 5));
        Assert.assertEquals(5, OneFileNeuralIndex.maxMapStrLengthInLst(map));
        System.out.println(Math.round(26533.499999999996));*/
        //NetworkFileSerializer.convertNNTxtToSerBin(defaultFilePath,NetworkFileSerializer.DEFAULT_SEPARATOR, defaultSerFilePath);
        NetworkFileSerializer.convertNNSerBinToTxt(defaultSerFileName+"2"+defaultSerFileExt, defaultFilePath+"2", NetworkFileSerializer.DEFAULT_SEPARATOR );
    }
}
