/*
 * Copyright © 2016 Andrey Novikov.
 *
 * This file is part of neural_index.
 *
 * neural_index is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * neural_index is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with neural_index.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.andnovator.neural.indexing;

public class PosFreqPair {
    private int position = -1;
    private int frequency = 0;
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
