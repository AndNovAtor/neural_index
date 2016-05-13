package com.andnovator.neural.indexing;

public class PosFreqPair {
    private int position;
    private int frequency;
    public PosFreqPair(int position, int frequency) {
        this.position = position;
        this.frequency = frequency;
    }
    public int getPos() { return position; }
    public void setPos(int pos) {position = pos; }
    public int getFreq() { return frequency; }
    public void setFreq(int freq) {frequency = freq;}
    public void posInc() {++position;}
    public void fregInc() {++frequency;}
    public void posAdd(int val) {position+=val;}
    public void freqAdd(int val) {frequency+=val;}
}
