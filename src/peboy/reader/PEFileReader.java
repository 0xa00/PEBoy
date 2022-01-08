package peboy.reader;

import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

import static peboy.PEBoy.unsafe;

public final class PEFileReader {
    private final File targetFile;
    private byte[] fileBytes;
    private DOSHeader dosHeader;
    private NTHeader ntHeader;

    public PEFileReader(final File targetFile) {
        this.targetFile = targetFile;
    }

    public void read() throws IOException, PEHeaderException {
        byte[] bytes = this.fileBytes = Files.readAllBytes(this.targetFile.toPath());
        if (bytes[0] != 'M' || bytes[1] != 'Z') {
            throw new PEHeaderException("e_magic != IMAGE_DOS_SIGNATURE");
        }
        System.out.println(this.fileBytes[0] + " - " + this.fileBytes[1]);
        final long memDosHeader = unsafe.allocateMemory(64);
        if (memDosHeader == 0) {
            throw new OutOfMemoryError("not enough memory for dos header");
        }
        for (int i = 0; i < 64; i++) {
            unsafe.putChar(memDosHeader + i, (char) bytes[i]);
        }
        DOSHeader dosHeader = this.dosHeader = new DOSHeader(memDosHeader);
        dosHeader.read(unsafe);
        final long memNtHeader = unsafe.allocateMemory(264);
        if (memNtHeader == 0) {
            throw new OutOfMemoryError("not enough memory for nt header");
        }
        for (int i = 0; i < 264; i++) {
            unsafe.putChar(memNtHeader + i, (char) bytes[(int) (dosHeader.e_lfanew + i)]);
        }
        NTHeader ntHeader = this.ntHeader = new NTHeader(memNtHeader);
        ntHeader.read(unsafe);
        System.out.println(ntHeader.Fh_Machine);
    }

    public void recreateDosHeader() {
        for (int i = 0; i < 64; i++) {
            this.fileBytes[i] = (byte) unsafe.getChar(dosHeader.address);
        }
    }

    public void recreateNtHeader() {
        for (int i = 0; i < 264; i++) {
            this.fileBytes[(int) (dosHeader.e_lfanew + i)] = (byte) unsafe.getChar(ntHeader.address);
        }
    }

    public void close() {
        unsafe.freeMemory(dosHeader.address);
    }

    public File getTargetFile() {
        return targetFile;
    }
}
