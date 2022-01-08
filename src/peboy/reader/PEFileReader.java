package peboy.reader;

import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

import static peboy.PEBoy.unsafe;

public final class PEFileReader {
    private final File targetFile;
    private final boolean is64;
    private byte[] fileBytes;

    /**
     * @param is64 If we should use 64 structures instead of 32 (from Win32 defs)
     */
    public PEFileReader(final File targetFile, final boolean is64) {
        this.targetFile = targetFile;
        this.is64 = is64; // use 64 structures?
    }

    public void read() throws IOException, PEHeaderException {
        byte[] bytes = this.fileBytes = Files.readAllBytes(this.targetFile.toPath());
        if (bytes[0] != 'M' || bytes[1] != 'Z') {
            throw new PEHeaderException("e_magic != IMAGE_DOS_SIGNATURE");
        }

        this.createDosHeader();
        this.createNtHeader();
    }

    public void close() {
        if (dosHeader != null && dosHeader.address != 0)
            unsafe.freeMemory(dosHeader.address);
        if (ntHeader != null && ntHeader.address != 0)
            unsafe.freeMemory(ntHeader.address);
        if (debugDirectory != null && debugDirectory.address != 0)
            unsafe.freeMemory(debugDirectory.address);
    }

    public File getTargetFile() {
        return targetFile;
    }

    // ---------------------------------------------------------------
    private DOSHeader dosHeader;
    public void createDosHeader() {
        final long memDosHeader = unsafe.allocateMemory(64);
        if (memDosHeader == 0) {
            throw new OutOfMemoryError("not enough memory for dos header");
        }
        for (int i = 0; i < 64; i++) {
            unsafe.putChar(memDosHeader + i, (char) fileBytes[i]);
        }
        DOSHeader dosHeader = this.dosHeader = new DOSHeader(memDosHeader);
        dosHeader.read(unsafe);
    }

    public void updateDosHeader() {
        for (int i = 0; i < 64; i++) {
            this.fileBytes[i] = (byte) unsafe.getChar(dosHeader.address);
        }
    }

    // ---------------------------------------------------------------
    public NTHeader ntHeader;
    public ImportAddressTable iat;
    public DebugDirectory debugDirectory;
    public void createNtHeader() {
        final int size = estimateNtHeaderSize();
        final long memNtHeader = unsafe.allocateMemory(size);
        if (memNtHeader == 0) {
            throw new OutOfMemoryError("not enough memory for nt header");
        }
        for (int i = 0; i < 264; i++) {
            unsafe.putChar(memNtHeader + i, (char) fileBytes[(int) (dosHeader.e_lfanew + i)]);
        }
        NTHeader ntHeader = this.ntHeader = new NTHeader(memNtHeader, size);
        ntHeader.read(unsafe, (iat = new ImportAddressTable()), (debugDirectory = new DebugDirectory()));

        debugDirectory.resolveVA(unsafe, fileBytes);
    }

    public void updateNtHeader() {
        for (int i = 0; i < ntHeader.getSize(); i++) {
            this.fileBytes[(int) (dosHeader.e_lfanew + i)] = (byte) unsafe.getChar(ntHeader.address);
        }
    }

    public int estimateNtHeaderSize() {
        return is64 ? 264 : 248;
    }
}
