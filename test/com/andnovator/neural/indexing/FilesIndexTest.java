package com.andnovator.neural.indexing;

import static org.junit.Assert.*;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Map;

/**
 * Created by novator on 09.06.2016.
 */
public class FilesIndexTest {

    @Test
    public void filesIndexTrainTest() throws Exception {
        String dirPath = "C:\\Users\\novator\\Desktop\\ind";
        FilesIndex fni = new FilesIndex();
        fni.setNetworkMinMSE(0.1);
        fni.indexDir(dirPath);
        String word = "is";
        Map<Path, PosFreqPair> filesWordPosMap = fni.wordSearch(word);
        if (filesWordPosMap.isEmpty()) {
            System.out.println("Word '" + word + "' not found in dir: " + dirPath);
        } else {
            System.out.println("Word '" + word + "' was found in files:");
            filesWordPosMap.forEach((k, v) -> System.out.println(k + ": Pos - " + v.getPos()+", freq - "+v.getFreq()));
        }
    }
}