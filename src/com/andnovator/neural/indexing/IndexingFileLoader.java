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
