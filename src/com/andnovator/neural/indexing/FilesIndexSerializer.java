package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NetworkFileSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by novator on 06.06.2016.
 */
public class FilesIndexSerializer {
    private String filePath;

    public FilesIndexSerializer(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public void serialize(FilesIndex nindex) throws IOException {
        NetworkFileSerializer nfSer = new NetworkFileSerializer(filePath);
        try (ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            ostream.writeInt(nindex.getFilesIndexedNum());
            ostream.writeInt(nindex.getMaxFileNum());
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
            filesIndex.setMaxFileNum(istream.readInt());
            List<OneFileNeuralIndex> oneFileNIs = new ArrayList<>();
            filesIndex.setNINetwork(nfSer.deserializeNetwork(istream));
            for (int ind = 0; ind < filesIndex.getFilesIndexedNum(); ++ind) {
                oneFileNIs.add(OneFileNeuralIndex.loadSerializedIndex(nfSer));
            }
            filesIndex.setFileIndexNILst(oneFileNIs);
        }
        return filesIndex;
    }
}
