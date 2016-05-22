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
    private String defaultSerFilePath = "neural_index.ser";
    private String defaultSeparator = "; ";

    @Test
    public void returnNetwWithRespTest() throws Exception {
        //All words:
        List<String> allWords = new ArrayList<>();
        allWords.add("nebulous");
        allWords.add("scare");
        allWords.add("rhythm");
        allWords.add("brief");
        allWords.add("flash");
        allWords.add("evanescent");
        allWords.add("hum");
        allWords.add("sloppy");
        allWords.add("alcoholic");
        allWords.add("jumbled");
        allWords.add("tame");
        allWords.add("heavenly");
        allWords.add("ducks");
        allWords.add("makeshift");
        allWords.add("intend");
        allWords.add("distance");
        allWords.add("remarkable");
        allWords.add("thoughtless");
        allWords.add("hat");
        allWords.add("food");
        //end all words
        //File:
        List<String> oneFileWords = allWords.subList(0,10);
        List<String> allFilesWords;
        // All files words:
        allFilesWords = new ArrayList<>(oneFileWords);
        // File words hashmap
        HashMap<String, PosFreqPair> fileWordsMap = new HashMap<>(oneFileWords.size());
        for (int i = 0; i < oneFileWords.size(); ++i) {
            fileWordsMap.put(oneFileWords.get(i), new PosFreqPair(i,1));
        }
        OneFileNeuralIndex fileNIndex = new OneFileNeuralIndex();
        fileNIndex.setNetworkMinMSE(0.01);
        Assert.assertTrue( fileNIndex.trainIndex(fileWordsMap, allWords) );
        int[] resArr;
        for (String word : allWords) {
            System.out.println("For word: " + word);
            resArr = fileNIndex.wordSearch(word, true);
            System.out.println(" pos.: " + resArr[0] + "; freq.: " + resArr[1]);
        }
        new NetworkFileSerializer(defaultSerFilePath).seralizeNetwork(fileNIndex.getNeuroIndexNetwork());
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
        NetworkFileSerializer.convertNNTxtToSerBin(defaultFilePath,NetworkFileSerializer.DEFAULT_SEPARATOR, defaultSerFilePath);
    }
}
