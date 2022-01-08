package peboy;

import peboy.reader.PEFileReader;
import peboy.reader.PEHeaderException;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class PEBoy {
    private final File targetFile, outputFile;
    private PEFileReader peFileReader;

    public PEBoy(final File targetFile) {
        this.targetFile = targetFile;
        String absolutePath = targetFile.getAbsolutePath();
        if (absolutePath.endsWith("\\") || absolutePath.endsWith("/")) {
            absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
        }
        this.outputFile = new File(absolutePath + "_obscured.exe");
        if (this.outputFile.exists()) {
            this.outputFile.delete();
        }
    }

    public PEFileReader getPeFileReader() {
        return peFileReader;
    }

    public boolean load(boolean is64) {
        this.peFileReader = new PEFileReader(this.targetFile, is64);
        try {
            this.peFileReader.read();
        } catch (IOException | PEHeaderException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void finish() {
        this.peFileReader.close();
    }

    public static Unsafe unsafe;

    static {
        Field theUnsafe = null;
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
