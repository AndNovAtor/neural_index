package com.andnovator.neural.indexing;

import static org.junit.Assert.*;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Map;

/**
 * Created by novator on 09.06.2016.
 */
public class FilesIndexTest {
    String dirPath = "C:\\Users\\novator\\Desktop\\ind";

    @Test
    public void filesIndexTrainTest() throws Exception {
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
        new FilesIndexSerializer(dirPath+"\\index.ser").serialize(fni);
    }

    @Test
    public void bTest() throws Exception {
        FilesIndex fni = new FilesIndex();
        fni = new FilesIndexSerializer(dirPath+"\\index.ser").deserialize();
        fni.setNetworkMinMSE(0.01);
        String word = "is";
        Map<Path, PosFreqPair> filesWordPosMap = fni.wordSearch(word);
        if (filesWordPosMap.isEmpty()) {
            System.out.println("Word '" + word + "' not found in dir: " + dirPath);
        } else {
            System.out.println("Word '" + word + "' was found in files:");
            filesWordPosMap.forEach((k, v) -> System.out.println(k + ": Pos - " + v.getPos()+", freq - "+v.getFreq()));
        }
        word = "qwerty";
        filesWordPosMap = fni.wordSearch(word);
        if (filesWordPosMap.isEmpty()) {
            System.out.println("Word '" + word + "' not found in dir: " + dirPath);
        } else {
            System.out.println("Word '" + word + "' was found in files:");
            filesWordPosMap.forEach((k, v) -> System.out.println(k + ": Pos - " + v.getPos()+", freq - "+v.getFreq()));
        }
    }
}