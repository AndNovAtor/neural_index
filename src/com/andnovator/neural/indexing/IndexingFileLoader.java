package com.andnovator.neural.indexing;

import com.andnovator.utils.FileLemmatizationUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by novator on 27.05.2016.
 */
public class IndexingFileLoader {
    public static Map<String, PosFreqPair> loadFile(String filePath) throws IOException {
        return loadFile(filePath, true);
    }
    public static Map<String, PosFreqPair> loadFile(String filePath, boolean useLemmatizator) throws IOException {
        return loadFile(Paths.get(filePath), useLemmatizator);
    }

    public static Map<String, PosFreqPair> loadFile(Path filePath) throws IOException {
        return loadFile(filePath, true);
    }
    //TODO: remove boolean useLemmatizator
    public static Map<String, PosFreqPair> loadFile(Path filePath, boolean useLemmatizator) throws IOException {
        List<String> lemms = FileLemmatizationUtils.loadFileLemms(filePath, useLemmatizator);
        Map<String, PosFreqPair> fileLemPosFreqMap = new HashMap<>();
        int pos = 0;
        for (String lemma : lemms) {
            PosFreqPair posFreqPair = fileLemPosFreqMap.get(lemma);
            if (posFreqPair == null) {
                fileLemPosFreqMap.put(lemma, new PosFreqPair(pos, 1));
            } else {
                posFreqPair.fregInc();
                fileLemPosFreqMap.put(lemma, posFreqPair);
            }
            ++pos;
        }
        return fileLemPosFreqMap;
    }

}
