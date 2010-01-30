package net.ahlawat;

import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

/**
 * Core file splitting logic
 * @author Pranay Ahlawat
 */
public class Splitter {
    public static void splitFile(String inFileName, String outDirName, Config config) throws Exception {
        final long BUFER_SIZE = config.getBufferSize();
        final boolean VERBOSE = config.isVerbose();

        //create the local variables to be used in the rest of the application
        File inFile = new File(inFileName);
        long partitionSize = config.getDefaultChunkSize();
        File outDir = new File(outDirName);

        //create inital counters
        final long totalFileSize = inFile.length();

        //create the out dirs if they dont exist
        if (!outDir.exists()) {
            System.out.println("Creating directory : " + outDir.getName());
            outDir.mkdirs();
        }

        FileChannel inChannel =  new FileInputStream(inFile).getChannel();

        long currentPosition = 0;
        int ctr = 0;
        ByteBuffer buff = ByteBuffer.allocate((int)BUFER_SIZE);
        long start = System.currentTimeMillis();

        while(currentPosition < totalFileSize) {
            //get the out channel for the file - roughly is the "originalFileName.ext.n" where 'n' is the partition number
            FileChannel outChannel = getChannel(inFile, outDir, ++ctr); //init the out channel
            //the size of the nth partition
            long size = currentPosition + partitionSize < totalFileSize? partitionSize : totalFileSize - currentPosition;
            //sout
            if (VERBOSE) {
                System.out.print(String.format("Creating part %s of size %s MB", ctr, size/Config.MB_TO_BYTE));
            }
            long start2 = System.currentTimeMillis();

            //the end position of the nth partition w.r.t the entire file
            long endPosition = currentPosition + size;

            //write partition in BUFFER_SIZE chunks
            while(currentPosition < endPosition) {
                //read the chunk into the buffer
                long subSize = (currentPosition + BUFER_SIZE) < endPosition ? BUFER_SIZE : endPosition - currentPosition;
                inChannel.read(buff, currentPosition);
                //prepare for writing
                buff.flip();
                //write
                outChannel.write(buff);
                currentPosition += subSize;
                //clear the buffer - so we can write again
                buff.clear();
            }

            outChannel.close(); //close

            //print throughput for this file partition
            double delta = (double)(System.currentTimeMillis() - start2)/1000;
            if (VERBOSE) {
                System.out.println(String.format(" -> Transferred in %.2f s @ %.2f MB/s", delta,
                        (double) size/Config.MB_TO_BYTE/delta));
            }
        }

        //calculate time
        double delta =  (double)(System.currentTimeMillis() - start)/1000;

        //print out the total throughput
        System.out.println(String.format("Copied %.2f MB in %.2f s @ %.2f MB/s", (double)totalFileSize/Config.MB_TO_BYTE,
                delta, (double)totalFileSize/Config.MB_TO_BYTE/delta));

        //finally close the channel
        inChannel.close();
    }

    private static FileChannel getChannel(File inFile, File outDir, int ctr) throws FileNotFoundException {
        return new FileOutputStream(new File(outDir, (inFile.getName() + "." + ctr))).getChannel();
    }
}
