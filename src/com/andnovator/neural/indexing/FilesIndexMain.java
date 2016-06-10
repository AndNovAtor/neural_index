package com.andnovator.neural.indexing;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by NovAtor on 10.06.2016.
 */

public class FilesIndexMain {
    static final String SYSTEM_PATH_SLASH = "\\";

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            System.out.println("No args");
            return;
        } else if (args.length == 1) {
            System.out.println("Not all args");
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
        boolean printTime = commandLine.hasOption("t");
        if (commandLine.hasOption("ifn")) { // проверяем, передавали ли нам команду l, сравнение будет идти с первым представлением опции, в нашем случаее это было однобуквенное представление l
            indFileName = commandLine.getOptionValues("ifn")[0];// если такая опция есть, то получаем переданные ей аргументы
        }
//        System.out.println(dirPath);
//        System.out.println(indFileName);
//        System.out.println(printTime);
//        System.out.println(Paths.get(dirPath));
        FilesIndex fni = new FilesIndex();
        fni.setNetworkMinMSE(0.1);
        fni.indexDir(dirPath);
        new FilesIndexSerializer(Paths.get(dirPath.toString(), indFileName + ".ser")).serialize(fni);
    }
}
