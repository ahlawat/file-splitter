package net.ahlawat;

import java.util.List;
import java.util.Arrays;

/**
 * Main driver class
 * @author Pranay Ahlawat
 */
public class Main {
    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);

        if (args.length < 1) {
            printHelp();
            System.exit(1);
        }
    }

    private static void printHelp() {
        StringBuffer buff = new StringBuffer();
        buff.append("Usage:\n\tjava -jar file-splitter.jar [-b bufferSize in KB] " +
                "[-s maxPartitionSize in MB] fileName.ext\n");

        buff.append("Arguments:\n\t1. b - buffer size in KB, a default value of 128\n");
        buff.append("\t2. s - max size of each partition in MB - a default size of 100");

        System.out.println(buff);
    }
}
