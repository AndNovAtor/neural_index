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

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by NovAtor on 10.06.2016.
 */

public class FilesIndexMain {
    private static Logger slf4jLogger = null;

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            System.err.println("No args");
            return;
        } else if (args.length == 1) {
            System.err.println("Not all args");
            return;
        }
        Path dirPath = Paths.get(args[0]).normalize(); // Get path for dir - first program arg (needed always)
        String[] comLineArgs = Arrays.copyOfRange(args, 1, args.length);

        Option optionIndex = new Option("i", "index", false, "Indexing directory");
        optionIndex.setOptionalArg(false);
        Option optionIndFname = new Option("ifn", "ind_fname", true, "Filename for storage index");
        optionIndFname.setArgs(1);
        optionIndFname.setOptionalArg(false);
        Option optionTime = new Option("t", "time", false, "Print time for indexing directory");
        optionTime.setOptionalArg(false);
        Option optionWSearch = new Option("s", "search", true, "Word for searching in dir by index");
        optionIndFname.setArgs(1);
        optionIndFname.setOptionalArg(false);
        Options opts = new Options();
        opts.addOption(optionIndex);
        opts.addOption(optionIndFname);
        opts.addOption(optionTime);
        opts.addOption(optionWSearch);
        CommandLineParser cmdLinePosixParser = new DefaultParser();
        CommandLine commandLine;

        try {
            commandLine = cmdLinePosixParser.parse(opts, comLineArgs);
        } catch (ParseException e) {
            System.out.println("Args was not parsed");
            return;
        }

        String indFileName = ".neural_index";
        boolean printDuration = commandLine.hasOption('t');
        if (commandLine.hasOption("ifn")) { // Check - param arg exists?
            indFileName = commandLine.getOptionValues("ifn")[0];// If yes - get it args/arg
        }

        boolean needIndex = commandLine.hasOption('i');
        boolean needSearch = commandLine.hasOption("s");
        if (needIndex) {
            slf4jLogger = LoggerFactory.getLogger(FilesIndexMain.class);
            index(dirPath, indFileName, printDuration);
        }
        if (needSearch) {
            if (slf4jLogger == null) {
                slf4jLogger = LoggerFactory.getLogger(FilesIndexMain.class);
            }
            try {
                for (String word : commandLine.getOptionValues('s')) {
                    word_search(dirPath, indFileName, word);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Error when deserialize index network with message:");
                System.err.println(e.getMessage());
                System.err.println("Exit...");
            }

        } else if (!(needIndex || needSearch)) {
            System.out.println("No args for index dir or search word in dir by index");
        }
    }

    private static void index(Path dirPath, String indFileName, boolean printDuration) throws IOException {
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

    private static void word_search(Path dirPath, String indFileName, String word) throws IOException, ClassNotFoundException {
        FilesIndex fni = new FilesIndexSerializer(Paths.get(dirPath.toString(), indFileName + ".ser")).deserialize();
        Map<Path, PosFreqPair> filesWordPosMap = fni.wordSearch(word);
        if (filesWordPosMap.isEmpty()) {
            System.out.println("Word '" + word + "' not found in dir: " + dirPath);
        } else {
            System.out.println("Word '" + word + "' was found in files:");
            filesWordPosMap.forEach((k, v) -> System.out.println(k + ": Pos - " + v.getPos() + ", freq - " + v.getFreq()));
        }
    }
}
