package peboy.reader;

import sun.misc.Unsafe;

public final class NTHeader {
    public final long address;
    private final int size;

    // IFM Constant (See Below)
    public int Fh_Machine;

    public NTHeader(final long address, int size) {
        this.address = address;
        this.size = size;

        //noinspection ResultOfMethodCallIgnored
        is64();
    }

    public void read(Unsafe unsafe, ImportAddressTable iat, DebugDirectory debugDirectory) {
        Fh_Machine = Short.toUnsignedInt(unsafe.getShort(address + 4));

        iat.setAddress(address + (is64() ? 232 : 216));
        iat.read(unsafe);

        debugDirectory.read(address + (is64() ? 184 : 168), unsafe);
    }

    public long getAddress() {
        return address;
    }

    public int getSize() {
        return size;
    }

    public boolean is64() {
        if (size == 248)
            return false;
        else if (size == 264)
            return true;
        else
            throw new RuntimeException("invalid nt header size");
    }

    // IFM Constants
    public static int IMAGE_FILE_MACHINE_I386 = 0x014c;
    public static int IMAGE_FILE_MACHINE_AMD64 = 0x8664;
    public static int IMAGE_FILE_MACHINE_IA64 = 0x0200;
}
