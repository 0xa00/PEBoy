package peboy.reader;

import peboy.utils.Utils;
import sun.misc.Unsafe;

public final class NTHeader {
    public final long address;

    // IFM Constant (See Below)
    public int Fh_Machine;

    public NTHeader(final long address) {
        this.address = address;
    }

    public void read(Unsafe unsafe) {
        Fh_Machine = Short.toUnsignedInt(unsafe.getShort(address + 4));
    }

    public long getAddress() {
        return address;
    }

    // IFM Constants
    public static int IMAGE_FILE_MACHINE_I386 = 0x014c;
    public static int IMAGE_FILE_MACHINE_AMD64 = 0x8664;
    public static int IMAGE_FILE_MACHINE_IA64 = 0x0200;
}
