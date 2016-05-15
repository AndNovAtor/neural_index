package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NeuralNetwork;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by novator on 10.05.2016.
 */
public class OneFileIndexTest {
    public static void main(String[] args) {
        OneFileIndexTest test1 = new OneFileIndexTest();
        test1.returnNetwWithRespTest();
    }

    public Pair<NeuralNetwork<Double>, ArrayList<Number>> returnNetwWithRespTest() {
        //All words:
        ArrayList<String> allWords = new ArrayList<>();
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
        ArrayList<String> allFilesWords;
        // All files words:
        allFilesWords = new ArrayList<>(oneFileWords);
        // File words hashmap
        HashMap<String, PosFreqPair> fileWordsMap = new HashMap<>(oneFileWords.size());
        for (int i = 0; i < oneFileWords.size(); ++i) {
            fileWordsMap.put(oneFileWords.get(i), new PosFreqPair(i,1));
        }
        NeuralIndex fileNIndex = new NeuralIndex();
        fileNIndex.trainIndex(fileWordsMap, allWords);
        int[] resArr;
        for (String word : allWords) {
            System.out.println("For word: " + word);
            resArr = fileNIndex.wordSearch(word, true);
            System.out.println(" pos.: " + resArr[0] + "; freq.: " + resArr[1]);
        }
        return new Pair<>(fileNIndex.getNeuroIndexNetwork(), fileNIndex.wordSearchNetResponce(allWords.get(18)));
    }
}
