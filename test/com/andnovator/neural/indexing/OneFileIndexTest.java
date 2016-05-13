package com.andnovator.neural.indexing;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by novator on 10.05.2016.
 */
public class OneFileIndexTest {
    public static void main(String[] args) {
        ArrayList<String> oneFileWords = new ArrayList<>();
        ArrayList<String> allFilesWords;
        ArrayList<String> allWords = new ArrayList<>();
        //File:
        oneFileWords.add("nebulous");
        oneFileWords.add("scare");
        oneFileWords.add("rhythm");
        oneFileWords.add("brief");
        oneFileWords.add("flash");
        oneFileWords.add("evanescent");
        oneFileWords.add("hum");
        oneFileWords.add("sloppy");
        oneFileWords.add("alcoholic");
        oneFileWords.add("jumbled");
        //end file
        // All files words:
        allFilesWords = new ArrayList<>(oneFileWords);
        //All words:
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
        // File words hashmap
        HashMap<String, PosFreqPair> fileWordsMap = new HashMap<>(oneFileWords.size());
        for (int i = 0; i < oneFileWords.size(); ++i) {
            fileWordsMap.put(oneFileWords.get(i), new PosFreqPair(i,1));
        }
        NeuralIndex fileNIndex = new NeuralIndex();
        fileNIndex.trainIndex(fileWordsMap, allWords);
        int[] resArr = fileNIndex.wordSearch(oneFileWords.get(1));
        int a = resArr[0];
    }
}
