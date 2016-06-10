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
    String defaultIndFileOnlyName = ".nindex";
    String defaultIndFileExt = ".ser";
    String defaultIndFilename = defaultIndFileOnlyName + defaultIndFileExt;


    @Test
    public void filesIndexTrainTest() throws Exception {
        FilesIndex fni = new FilesIndex();
        fni.setNetworkMinMSE(0.1);
        fni.indexDir(dirPath);
        String word = "is";
        Map<Path, PosFreqPair> filesWordPosMap = fni.wordSearch(word, true);
        if (filesWordPosMap.isEmpty()) {
            System.out.println("Word '" + word + "' not found in dir: " + dirPath);
        } else {
            System.out.println("Word '" + word + "' was found in files:");
            filesWordPosMap.forEach((k, v) -> System.out.println(k + ": Pos - " + v.getPos()+", freq - "+v.getFreq()));
        }
        new FilesIndexSerializer(dirPath + "\\" + defaultIndFilename).serialize(fni);
    }

    @Test
    public void wordSearchTest() throws Exception {
        FilesIndex fni;
        fni = new FilesIndexSerializer(dirPath + "\\" + defaultIndFilename).deserialize();
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
        fni.getFileIndexNILst().get(0).wordSearch(word, true);
        fni.getFileIndexNILst().get(1).wordSearch(word, true);
    }
}