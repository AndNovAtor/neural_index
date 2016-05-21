package com.andnovator.neural.network;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

/**
 * Created by novator on 21.05.2016.
 */
public class NetworkFileSerializer {
    private String fileName;
    private String separator = " ";

    public NetworkFileSerializer(String filePath) {
        fileName = filePath;
    }

    public NetworkFileSerializer(String fileName, String separator) {
        this.fileName = fileName;
        this.separator = separator;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public <T> void saveNetwork(NeuralNetwork<T> nn) throws IOException {
        List<List<Double>> netWeights = nn.exportNetworkWeights();
        try (PrintWriter writer = new PrintWriter(fileName, "utf-8")) {
            writer.println("# NN config (inputs, outputs, hiddenLayersNum, neurons in every hiddenLayers, minMSE, maxTrainItNum)");
            writer.println(Arrays.asList(nn.getInputsNum(), nn.getOutputsNum(), nn.getHiddenLayersNum(), nn.getHiddenLayersSize(), nn.getMinMSE(), nn.getMaxTrainItNum())
                                 .stream()
                                 .map(Object::toString)
                                 .collect(joining(separator))
            );
            writer.printf("# [%d x %d + %d] bias neurons links weights%s", nn.getHiddenLayersNum(), nn.getHiddenLayersSize(), nn.getOutputsNum(), System.lineSeparator());
            writer.println(netWeights.get(0).stream()
                                            .map(Object::toString)
                                            .collect(joining(separator)));
            writer.printf("# [(%d + %d + %d) x %d] simple neurons weights%s", nn.getInputsNum(), nn.getHiddenLayersNum() - 1, nn.getOutputsNum(), nn.getHiddenLayersSize(), System.lineSeparator());
            writer.println(netWeights.get(1).stream()
                    .map(Object::toString)
                    .collect(joining(separator)));
        }
    }

    public <T> NeuralNetwork<T> loadNetwork() throws IOException, ParseException {
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

            NeuralNetwork<T> nn = new NeuralNetwork<T>(inputsNum, outputsNum, hiddenLayersNum, hiddenLayersSize);

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
}
