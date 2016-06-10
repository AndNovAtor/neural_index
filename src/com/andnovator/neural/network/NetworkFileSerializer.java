package com.andnovator.neural.network;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Created by novator on 21.05.2016.
 */
public class NetworkFileSerializer {
    public static final String DEFAULT_SEPARATOR = " ";

    private String fileName;
    private String separator = DEFAULT_SEPARATOR;

    public NetworkFileSerializer(String filePath) {
        fileName = filePath;
    }

    public NetworkFileSerializer(String fileName, String separator) {
        this.fileName = fileName;
        this.separator = separator;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public void saveNetwork(NeuralNetwork nn) throws IOException {
        List<double[]> netWeights = nn.exportNetworkWeightsArr();
        try (PrintWriter writer = new PrintWriter(fileName, "utf-8")) {
            writer.println("# NN config (inputs, outputs, hiddenLayersNum, neurons in every hiddenLayers, minMSE, maxTrainItNum)");
            writer.println(Arrays.asList(nn.getInputsNum(), nn.getOutputsNum(), nn.getHiddenLayersNum(), nn.getHiddenLayersSize(), nn.getMinMSE(), nn.getMaxTrainItNum())
                                 .stream()
                                 .map(Object::toString)
                                 .collect(joining(separator))
            );
            writer.printf("# [%d x %d + %d] bias neurons links weights%s", nn.getHiddenLayersNum(), nn.getHiddenLayersSize(), nn.getOutputsNum(), System.lineSeparator());
            writer.println(Arrays.stream(netWeights.get(0))
                                            .mapToObj(String::valueOf)
                                            .collect(joining(separator)));
            writer.printf("# [(%d + %d + %d) x %d] simple neurons weights%s", nn.getInputsNum(), nn.getHiddenLayersNum() - 1, nn.getOutputsNum(), nn.getHiddenLayersSize(), System.lineSeparator());
            writer.println(Arrays.stream(netWeights.get(1))
                    .mapToObj(String::valueOf)
                    .collect(joining(separator)));
        }
    }

    public NeuralNetwork loadNetwork() throws IOException, ParseException {
        AtomicInteger lineNumber = new AtomicInteger(0);
        int nextField = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String[] configLine = getNextLine(reader, lineNumber).split(separator);
            int inputsNum = Integer.valueOf(configLine[nextField++]);
            int outputsNum = Integer.valueOf(configLine[nextField++]);
            int hiddenLayersNum = Integer.valueOf(configLine[nextField++]);
            int hiddenLayersSize = Integer.valueOf(configLine[nextField++]);
            double minMSE = Double.valueOf(configLine[nextField++]);
            int maxTrainItNum = Integer.valueOf(configLine[nextField++]);

            NeuralNetwork nn = new NeuralNetwork(inputsNum, outputsNum, hiddenLayersNum, hiddenLayersSize);

            String[] biasWeightsLine = getNextLine(reader, lineNumber).split(separator);
            List<Double> biasWeights = Arrays.stream(biasWeightsLine)
                                             .map(Double::valueOf)
                                             .collect(Collectors.toList());
            String[] simpleWeightsLine = getNextLine(reader, lineNumber).split(separator);
            List<Double> simpleWeights = Arrays.stream(simpleWeightsLine)
                    .map(Double::valueOf)
                    .collect(Collectors.toList());

            nn.importNetworkWeights(biasWeights, simpleWeights);
            nn.setMinMSE(minMSE);
            nn.setMaxTrainItNum(maxTrainItNum);

            return nn;
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Incorrect number of items in line " + lineNumber.get() + ": " + e.getMessage(), lineNumber.get());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid number: " + e.getMessage() + " in line " + lineNumber.get(), lineNumber.get());
        }
    }

    /**
     * Читает из ридера следующую строку, пропуская комментарии
     * @param reader откуда читать
     * @param lineNumber переменная, в которую будет записан номер строки в которой мы работаем
     * @return следующая строка
     */
    private String getNextLine(BufferedReader reader, AtomicInteger lineNumber) throws IOException {
        String line = null;
        lineNumber.set(0);
        while (line == null || line.isEmpty() || line.startsWith("#")) {
            line = reader.readLine();
            if (line == null) {
                throw new EOFException("Unexpected end of file");
            }
            lineNumber.incrementAndGet();
        }
        return line;
    }

    public void seralizeNetwork(NeuralNetwork nn) throws IOException {
        try (ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            seralizeNetwork(nn, ostream);
        }
    }
    public void seralizeNetwork(NeuralNetwork nn, ObjectOutputStream ostream) throws IOException {
        List<double[]> netWeights = nn.exportNetworkWeightsArr();
        ostream.writeInt(nn.getInputsNum());
        ostream.writeInt(nn.getOutputsNum());
        ostream.writeInt(nn.getHiddenLayersNum());
        ostream.writeInt(nn.getHiddenLayersSize());
        ostream.writeDouble(nn.getMinMSE());
        ostream.writeInt(nn.getMaxTrainItNum());
        ostream.writeObject(netWeights.get(0));
        ostream.writeObject(netWeights.get(1));
    }
    public NeuralNetwork deserializeNetwork() throws IOException, ClassNotFoundException {
        try (ObjectInputStream istream = new ObjectInputStream(new FileInputStream(fileName))) {
            return deserializeNetwork(istream);
        }
    }
    public NeuralNetwork deserializeNetwork(ObjectInputStream istream) throws IOException, ClassNotFoundException {
        int inputsNum = istream.readInt();
        int outputsNum = istream.readInt();
        int hiddenLayersNum = istream.readInt();
        int hiddenLayersSize = istream.readInt();
        double minMSE = istream.readDouble();
        int maxTrainItNum = istream.readInt();
//        @SuppressWarnings("unchecked")
        double[] biasWeights = (double[]) istream.readObject();
//        @SuppressWarnings("unchecked")
        double[] simpleWeights = (double[]) istream.readObject();
        NeuralNetwork nn  = new NeuralNetwork(inputsNum, outputsNum, hiddenLayersNum, hiddenLayersSize);
        nn.importNetworkWeights(biasWeights, simpleWeights);
        nn.setMinMSE(minMSE);
        nn.setMaxTrainItNum(maxTrainItNum);
        return nn;
    }

    static public void convertNNTxtToSerBin(String txtFileName, String txtSeparatoer, String serFileName) throws Exception {
        NeuralNetwork nn = new NetworkFileSerializer(txtFileName, txtSeparatoer).loadNetwork();
        new NetworkFileSerializer(serFileName).seralizeNetwork(nn);
    }

    static public void convertNNSerBinToTxt(String serFileName, String txtFileName, String txtSeparatoer) throws Exception {
        NeuralNetwork nn = new NetworkFileSerializer(serFileName).deserializeNetwork();
        new NetworkFileSerializer(txtFileName, txtSeparatoer).saveNetwork(nn);
    }
}
