package com.andnovator.neural.indexing;

import java.util.Map;

/**
 * Created by novator on 27.05.2016.
 */
@Deprecated
public class DocumentWords {
    private String filepath;
    private Map<String, PosFreqPair> lemmsPosFreqMap;

    public DocumentWords(String filepath) {
        this.filepath = filepath;
    }
    public DocumentWords(String filepath, Map<String, PosFreqPair> lemmsPosFreqMap) {
        this.filepath = filepath;
        this.lemmsPosFreqMap = lemmsPosFreqMap;
    }

    public String getFilepath() { return filepath; }
    public Map<String, PosFreqPair> getLemmsPosFreqMap() { return lemmsPosFreqMap; }
    public void setFilepath(String filepath) { this.filepath = filepath; }
    public void setLemmsPosFreqMap(Map<String, PosFreqPair> lemmsPosFreqMap) {
        this.lemmsPosFreqMap = lemmsPosFreqMap;
    }
    public DocumentWords updateLemmsMap(Map<String, PosFreqPair> lemmsPosFreqMap) {
        this.lemmsPosFreqMap = lemmsPosFreqMap;
        return this;
    }
}
