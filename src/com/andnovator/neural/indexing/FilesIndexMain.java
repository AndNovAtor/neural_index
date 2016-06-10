package com.andnovator.neural.indexing;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by NovAtor on 10.06.2016.
 */

public class FilesIndexMain {
    private static final Logger slf4jLogger = LoggerFactory.getLogger(FilesIndexMain.class);

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            System.err.println("No args");
            return;
        } else if (args.length == 1) {
            System.err.println("Not all args");
            return;
        }
        Path dirPath = Paths.get(args[0]).normalize();
        String[] comLineArgs = Arrays.copyOfRange(args, 1, args.length);

        Option optionFname = new Option("ifn", "ind_fname", true, "Filename for storage index");
        optionFname.setArgs(1);
        optionFname.setOptionalArg(false);
        Option optionTime = new Option("t", "time", false, "Indexing directory");
        optionTime.setOptionalArg(false);
        Options opts = new Options();
        opts.addOption(optionFname);
        opts.addOption(optionTime);
        CommandLineParser cmdLinePosixParser = new DefaultParser();// создаем Posix парсер
        CommandLine commandLine;

        try {
            commandLine = cmdLinePosixParser.parse(opts, comLineArgs);// парсим командную строку
        } catch (ParseException e) {
            System.out.println("Args was not parsed");
            return;
        }

        String indFileName = "neural_index";
        boolean printDuration = commandLine.hasOption("t");
        if (commandLine.hasOption("ifn")) { // проверяем, передавали ли нам команду l, сравнение будет идти с первым представлением опции, в нашем случаее это было однобуквенное представление l
            indFileName = commandLine.getOptionValues("ifn")[0];// если такая опция есть, то получаем переданные ей аргументы
        }
//        System.out.println(dirPath);
//        System.out.println(indFileName);
//        System.out.println(printDuration);
//        System.out.println(Paths.get(dirPath));
        slf4jLogger.info("Start indexing with NN dir: '" + dirPath.toString() + "'");
        double startMills = System.currentTimeMillis();
        FilesIndex fni = new FilesIndex();
        fni.setNetworkMinMSE(0.1);
        fni.indexDir(dirPath);
        double endTraining = System.currentTimeMillis();
        new FilesIndexSerializer(Paths.get(dirPath.toString(), indFileName + ".ser")).serialize(fni);
        double endIndexing = System.currentTimeMillis();
        double indexingTrainigDuration = endTraining - startMills;
        double indexingAllDuration = endTraining - startMills;
//        System.out.println("Indexing dir success - for dir: '"+dirPath.toString()+"'");
        slf4jLogger.info("Indexing dir success - for dir: '" + dirPath.toString() + "'");
        if (printDuration) {
            slf4jLogger.info("Train NI network duration, ms: '" + indexingTrainigDuration);
            slf4jLogger.info("All NI work duration, ms: '" + indexingAllDuration);
            slf4jLogger.info("Train NI network duration, m: '" + indexingTrainigDuration / 1000 / 60);
            slf4jLogger.info("All NI work duration, m: '" + indexingAllDuration / 1000 / 60);
        }
    }
}
