package net.ahlawat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

/**
 * The part of the application that does the integration
 * @author Pranay Ahlawat
 */
public class Integrator {
    public static void integrate(String dirName, String fileName, String outFileName, Config config) throws IOException {
        final long BUFER_SIZE = config.getBufferSize();
        final boolean VERBOSE = config.isVerbose();
        
        //create core variables
        File dir = new File(dirName);
        String baseFileName = fileName;
        File outFile = new File(outFileName == null? baseFileName:outFileName);

        //create the out channel - to which the data will be written
        FileChannel outChannel = new FileOutputStream(outFile).getChannel();

        //core buffer
        ByteBuffer buff = ByteBuffer.allocate((int)BUFER_SIZE);

        int ctr = 0;
        long start = System.currentTimeMillis();
        while(true) {
            //some profiling
            long start2 = System.currentTimeMillis();
            //create the file and test to see if it's there
            File file = new File(dir, String.format("%s.%s", baseFileName, ++ctr));
            if (!file.exists()) { //no the file 'n' does not exist - integration complete
                break;
            }

            System.out.print(String.format("Integrating %s", file.getName()));

            //creat the in channel for the partitioned file 'n'
            FileChannel inChannel = new FileInputStream(file).getChannel();

            long currentPosition = 0;
            long fileSize = file.length();

            //read the file in chunks of BUFFER_SIZE
            while(currentPosition < fileSize) {
                long chunkSize = (currentPosition + BUFER_SIZE) < fileSize? BUFER_SIZE : fileSize - currentPosition;
                inChannel.read(buff, currentPosition);
                currentPosition += chunkSize;
                buff.flip(); //flip the buffer we are ready to write
                outChannel.write(buff);
                buff.clear(); //clear
            }

            //close/flush the information
            inChannel.close();

            //print profiling inforamtion
            double delta = (double) (System.currentTimeMillis() - start2)/1000;
            if (VERBOSE) {
                System.out.println(String.format(" -> Integration complete in %.2f s @ %.2f MB/s",
                        delta, file.length()/Config.MB_TO_BYTE/delta));
            }
        }

        outChannel.close();
        double delta = (double) (System.currentTimeMillis() - start)/1000;
        System.out.println(String.format("Integration complete - created file %s in %.2f @ %.2f MB/s",
                outFile.getName(), delta, outFile.length()/Config.MB_TO_BYTE/delta));
    }
}
