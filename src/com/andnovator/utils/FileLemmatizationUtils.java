package com.andnovator.utils;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by novator on 30.05.2016.
 */
public class FileLemmatizationUtils {
    public static List<String> loadFileLemms(String filePath) throws IOException {
        return loadFileLemms(Paths.get(filePath));
    }
    public static List<String> loadFileLemms(Path filePath) throws IOException {
        String fileText = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        //fileText = fileText.replaceAll(System.lineSeparator()," ");
        Document doc = new Document(fileText);
        List<String> fileLemms = new LinkedList<>();
        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
            fileLemms.addAll(sent.lemmas().stream().filter(x -> Character.isLetter(x.charAt(0))).collect(Collectors.toList()));
        }
//        System.out.println(String.format("Lemmas for file '%s'", filePath));
//        fileLemms.stream().map(x -> x+" ").forEach(System.out::print);
//        System.out.println();
        return fileLemms;
    }

    public static List<String> loadFileLemms(String filePath, boolean useLemmatizator) throws IOException {
        return loadFileLemms(Paths.get(filePath), useLemmatizator);
    }
    public static List<String> loadFileLemms(Path filePath, boolean useLemmatizator) throws IOException {
        if (useLemmatizator) {
            return loadFileLemms(filePath);
        }
        return Arrays.asList(new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8)
                .replaceAll(System.lineSeparator(), " ")
                .replaceAll("\\p{Punct}", " ")
                .replaceAll("[\t]+", " ")
                .replaceAll("[ ]+", " ")
                .split(" "));
    }

    public static void fileLemmNormalization(String filePath) throws IOException {
        Path out = Paths.get(filePath.substring(0, filePath.lastIndexOf("."))+"_lem_normal.txt");
        List<String> fileLemms = loadFileLemms(filePath);
        Files.write(out, fileLemms);
    }
}
