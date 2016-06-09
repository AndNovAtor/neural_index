package com.andnovator.neural.indexing;

import com.andnovator.neural.network.NetworkFileSerializer;
import com.andnovator.utils.FileLemmatizationUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * test for Indexing of contents of one file
 * Created by novator on 10.05.2016.
 */
public class OneFileNeuralIndexTest {

    private String defaultFilePath = "neural_index.txt";
    private String defaultSerFileName = "neural_index";
    private String defaultSerFileExt = ".ser";
    private String defaultTextFileExt = ".txt";
    private String defaultSerFilePath = defaultSerFileName+defaultSerFileExt;
    private String defaultSeparator = "; ";
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd_HH.mm.ss";
    public static final SimpleDateFormat SDF_YMD_HMS = new SimpleDateFormat(DATE_FORMAT_NOW);


    private int totalExpectationsFailed = 0;

    @Before
    public void setUp() throws Exception {
        totalExpectationsFailed = 0;
    }

    @Test
    public void indexOneFileTest() throws Exception {
        String filePath = "file3.txt";
//        DocumentWords documentWords = IndexingFileLoader.loadDocument(filePath);

        Set<String> allFilesWords = new HashSet<>();
        Map<String, PosFreqPair> wordsMapOneFile = IndexingFileLoader.loadFile(filePath);
        allFilesWords.addAll(wordsMapOneFile.keySet());
        //Some static add other words
        allFilesWords.add("nebulous");      // 1
        allFilesWords.add("scare");         // 2
        allFilesWords.add("rhythm");        // 3
        allFilesWords.add("brief");         // 4
        allFilesWords.add("flash");         // 5
        allFilesWords.add("evanescent");    // 6
        allFilesWords.add("hum");           // 7
        allFilesWords.add("sloppy");        // 8
        allFilesWords.add("alcoholic");     // 9
        allFilesWords.add("jumbled");       // 10
        allFilesWords.add("tame");          // 11
        allFilesWords.add("heavenly");      // 12
        allFilesWords.add("duck");          // 13
        allFilesWords.add("makeshift");     // 14
        allFilesWords.add("intend");        // 15
        allFilesWords.add("distance");      // 16
        allFilesWords.add("remarkable");    // 17
        allFilesWords.add("thoughtless");   // 18
        allFilesWords.add("hat");           // 19
        allFilesWords.add("food");          // 20
        //

        OneFileNeuralIndex fileNIndex = new OneFileNeuralIndex();
        fileNIndex.setNetworkMinMSE(0.1);

        ArrayList<String> allWordsLst = new ArrayList<>(allFilesWords);
        Assert.assertTrue( fileNIndex.trainIndex(wordsMapOneFile, allWordsLst) );
        wordsMapOneFile.forEach( (word, posFreqPair) -> {
            System.out.println("For word: " + word);
            int[] resArr = fileNIndex.wordSearch(word, true);
            System.out.println(" pos.: " + resArr[0] + "; freq.: " + resArr[1]);
            expectEquals(posFreqPair.getPos(), resArr[0]);
            expectEquals(posFreqPair.getFreq(), resArr[1]);
        });

        new NetworkFileSerializer(defaultSerFileName+"_"+ Paths.get(filePath).getFileName() +defaultSerFileExt).seralizeNetwork(fileNIndex.getNeuroIndexNetwork());
//        new NetworkFileSerializer(defaultFilePath).saveNetwork(fileNIndex.getNeuroIndexNetwork());
//        return new Pair<>(fileNIndex.getNeuroIndexNetwork(), fileNIndex.wordSearchNetResponce(allWords.get(18)));
    }

    @Test
    public void wordSearchBySerNetworkTest() throws Exception {

        OneFileNeuralIndex fileNIndex = OneFileNeuralIndex.loadSerializedIndex(defaultSerFileName + "_2016-05-31_11.35.11" + defaultSerFileExt);
        int[] resArr;
        List<String> allWords = new ArrayList<>();
        allWords.add("nebulous");      // 1
        allWords.add("scare");         // 2
        allWords.add("file");        // 3
        allWords.add("brief");         // 4
        allWords.add("be");         // 5
        allWords.add("evanescent");    // 6
        allWords.add("hum");           // 7
        allWords.add("sloppy");        // 8
        allWords.add("alcoholic");     // 9
        allWords.add("jumbled");       // 10
        allWords.add("tame");          // 11
        allWords.add("heavenly");      // 12
        allWords.add("human");          // 13
        allWords.add("makeshift");     // 14
        allWords.add("intend");        // 15
        allWords.add("distance");      // 16
        allWords.add("remarkable");    // 17
        allWords.add("thoughtless");   // 18
        allWords.add("hat");           // 19
        allWords.add("food");          // 20
        for (String word : allWords) {
            System.out.println("For word: " + word);
            resArr = fileNIndex.wordSearch(word, true);
            System.out.println(" pos.: " + resArr[0] + "; freq.: " + resArr[1]);
        }
    }

    @Test
    public void returnNetwWithRespTest() throws Exception {
        //All words:
        List<String> allWords = new ArrayList<>();
        allWords.add("nebulous");      // 1
        allWords.add("scare");         // 2
        allWords.add("rhythm");        // 3
        allWords.add("brief");         // 4
        allWords.add("flash");         // 5
        allWords.add("evanescent");    // 6
        allWords.add("hum");           // 7
        allWords.add("sloppy");        // 8
        allWords.add("alcoholic");     // 9
        allWords.add("jumbled");       // 10
        allWords.add("tame");          // 11
        allWords.add("heavenly");      // 12
        allWords.add("duck");          // 13
        allWords.add("makeshift");     // 14
        allWords.add("intend");        // 15
        allWords.add("distance");      // 16
        allWords.add("remarkable");    // 17
        allWords.add("thoughtless");   // 18
        allWords.add("hat");           // 19
        allWords.add("food");          // 20
        //end all words
        //File:
        List<String> oneFileWords = allWords.subList(0,10);
        List<String> secFileWords = allWords.subList(8,18);
        // File words hashmap
        HashMap<String, PosFreqPair> fileWordsMap2 = new HashMap<>(secFileWords.size());
        for (int i = 0; i < secFileWords.size(); ++i) {
            fileWordsMap2.put(secFileWords.get(i), new PosFreqPair(i,1));
        }
        HashMap<String, PosFreqPair> fileWordsMap1 = new HashMap<>(oneFileWords.size());
        for (int i = 0; i < oneFileWords.size(); ++i) {
            fileWordsMap1.put(oneFileWords.get(i), new PosFreqPair(i,1));
        }
        OneFileNeuralIndex fileNIndex = new OneFileNeuralIndex();
        fileNIndex.setNetworkMinMSE(0.01);
        Assert.assertTrue( fileNIndex.trainIndex(fileWordsMap1, allWords) );
        int[] resArr;
        for (String word : allWords) {
            System.out.println("For word: " + word);
            resArr = fileNIndex.wordSearch(word, true);
            System.out.println(" pos.: " + resArr[0] + "; freq.: " + resArr[1]);
        }
//        OneFileNeuralIndex fileNIndex2 = new OneFileNeuralIndex();
//        fileNIndex2.loadSerializedNetwork(defaultSerFileName+"2"+defaultSerFileExt);
//        fileNIndex2.setNetworkMinMSE(0.005);
//        Assert.assertTrue( fileNIndex2.trainIndex(fileWordsMap2, allWords, false) );
//        int[] resArr;
//        for (String word : allWords) {
//            System.out.println("For word: " + word);
//            resArr = fileNIndex2.wordSearch(word, true);
//            System.out.println(" pos.: " + resArr[0] + "; freq.: " + resArr[1]);
//        }
        String date = SDF_YMD_HMS.format(new Date());
        new NetworkFileSerializer(defaultSerFileName+"_"+ date +defaultSerFileExt).seralizeNetwork(fileNIndex.getNeuroIndexNetwork());
//        new NetworkFileSerializer(defaultFilePath+"_"+date+defaultTextFileExt).saveNetwork(fileNIndex.getNeuroIndexNetwork());
//        return new Pair<>(fileNIndex.getNeuroIndexNetwork(), fileNIndex.wordSearchNetResponce(allWords.get(18)));
    }

    @Test
    public void simpleTest() throws Exception {
        /*System.out.println("'str' to binary: " + OneFileNeuralIndex.strToBinaryStr("str"));
        Map<String, PosFreqPair> map = new HashMap<>();
        map.put("One", new PosFreqPair(1, 1));
        map.put("Two", new PosFreqPair(2, 2));
        map.put("Three", new PosFreqPair(3, 3));
        map.put("Four", new PosFreqPair(4, 4));
        map.put("Five", new PosFreqPair(5, 5));
        Assert.assertEquals(5, OneFileNeuralIndex.maxMapStrLengthInLst(map));
        System.out.println(Math.round(26533.499999999996));*/
        //NetworkFileSerializer.convertNNTxtToSerBin(defaultFilePath,NetworkFileSerializer.DEFAULT_SEPARATOR, defaultSerFilePath);
        NetworkFileSerializer.convertNNSerBinToTxt(defaultSerFileName+"2"+defaultSerFileExt, defaultFilePath+"2", NetworkFileSerializer.DEFAULT_SEPARATOR );
    }

    @Test
    public void testScanner() throws Exception {
        try (Scanner input = new Scanner(new File("file.txt"))) {
            List<String> wordLst = new ArrayList<>(5);
            while (input.hasNext()) {
                wordLst.add(input.next());
            }
            System.out.println("Lst:");
            wordLst.forEach(System.out::println);
        } // catch (FileNotFoundException e)
        //Below - surround with catch IOException
        System.out.println(new String(Files.readAllBytes(Paths.get("file.txt")), StandardCharsets.UTF_8));
    }

    @Test
    public void testFileNormalization() throws Exception {
        FileLemmatizationUtils.fileLemmNormalization("file2.txt");
    }

    @Test
    public void isDoubleBitTest() throws Exception {
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitOne(0.99) );
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitOne(0.98) );
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitOne(0.949) );
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitOne(0.91) );
        Assert.assertTrue( !OneFileNeuralIndex.isDoubleBitOne(0.81) );
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitZero(-0.99) );
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitZero(-0.96) );
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitZero(-0.91) );
        Assert.assertTrue( !OneFileNeuralIndex.isDoubleBitZero(-0.51) );
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitZeroOrOne(0.91) );
        Assert.assertTrue( OneFileNeuralIndex.isDoubleBitZeroOrOne(-0.989) );
        Assert.assertTrue( !OneFileNeuralIndex.isDoubleBitZeroOrOne(-0.8) );

        System.out.println(OneFileNeuralIndex.isDoubleBitZeroOrOne(-0.89));
        System.out.println(OneFileNeuralIndex.isDoubleBitZeroOrOne(0.895));
        System.out.println(OneFileNeuralIndex.isDoubleBitZeroOrOne(-0.849));
        System.out.println(OneFileNeuralIndex.isDoubleBitZeroOrOne(0.9));
    }

    @Test
    public void dirLstTest() throws Exception {
        List<String> plainTxtExt = Arrays.asList(
            ".txt",
            ".log",
            ".xml",
            ".html",
            ".htm");
        Files.walk(Paths.get("C:\\Users\\novator\\Desktop")).forEach(filePath -> {
            if (Files.isDirectory(filePath)) {
                System.out.println("Dir: " + filePath);
            } else if (plainTxtExt.stream().anyMatch(ext->filePath.getFileName().toString().toLowerCase().endsWith(ext))) {
                System.out.println("--> Text File: " + filePath);
            } else if (Files.isRegularFile(filePath)) {
                System.out.println("File: " + filePath);
            } else {
                System.out.println(" Something: " + filePath);
            }
        });
    }

    void expectEquals(int expected, int actual) {
        if (expected != actual) {
            System.out.flush();
            System.err.println("ERROR: expected: " + expected + "; actual: " + actual);
            totalExpectationsFailed += 1;
        }
    }

    @After
    public void tearDown() throws Exception {
        Assert.assertEquals("Expectations failed:", 0, totalExpectationsFailed);
    }
}
