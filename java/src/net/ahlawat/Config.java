package net.ahlawat;

/**
 * The configuration used for splitting and integration
 * @author Pranay Ahlawat
 */
public class Config {
    public static final long MB_TO_BYTE = 1024*1024;
    public static final long KB_TO_BYTE = 1024;

    private long bufferSize = 128 * KB_TO_BYTE; //default 128K buffer
    private long defaultChunkSize = 100 * MB_TO_BYTE; //default file chunk - 100MB
    private boolean verbose = false;

    public long getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(long bufferSize) {
        this.bufferSize = bufferSize;
    }

    public long getDefaultChunkSize() {
        return defaultChunkSize;
    }

    public void setDefaultChunkSize(long defaultChunkSize) {
        this.defaultChunkSize = defaultChunkSize;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
