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

import com.andnovator.neural.network.NetworkFileSerializer;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by novator on 06.06.2016.
 */
public class FilesIndexSerializer {
    private String filePath;

    public FilesIndexSerializer(String filePath) {
        this.filePath = filePath;
    }

    public FilesIndexSerializer(Path filePath) {
        this.filePath = filePath.normalize().toString();
    }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public void serialize(FilesIndex nindex) throws IOException {
        NetworkFileSerializer nfSer = new NetworkFileSerializer(filePath);
        try (ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            ostream.writeInt(nindex.getFilesIndexedNum());
            ostream.writeObject(nindex.getFilesPath().stream()
                    .map(Path::normalize)
                    .map(Object::toString)
                    .toArray(String[]::new));
            nfSer.seralizeNetwork(nindex.getFilesIndexNetwork(), ostream);
            for (OneFileNeuralIndex oneFileNI : nindex.getFileIndexNILst()) {
                nfSer.seralizeNetwork(oneFileNI.getNeuroIndexNetwork(), ostream);
            }
        }
    }

    public FilesIndex deserialize() throws IOException, ClassNotFoundException {
        FilesIndex filesIndex = new FilesIndex();
        NetworkFileSerializer nfSer = new NetworkFileSerializer(filePath);
        try (ObjectInputStream istream = new ObjectInputStream(new FileInputStream(filePath))) {
            filesIndex.setFilesIndexedNum(istream.readInt());
            filesIndex.setFilesPath(Arrays.stream((String[]) (istream.readObject()))
                    .map(s -> Paths.get(s))
                    .collect(Collectors.toList()));
            List<OneFileNeuralIndex> oneFileNIs = new ArrayList<>();
            filesIndex.setNINetwork(nfSer.deserializeNetwork(istream));
            int indexedFilesNum = filesIndex.getFilesIndexedNum();
            for (int ind = 0; ind < indexedFilesNum; ++ind) {
                oneFileNIs.add(OneFileNeuralIndex.loadSerializedIndex(nfSer, istream));
            }
            filesIndex.setFileIndexNILst(oneFileNIs);
        }
        return filesIndex;
    }
}
