package peboy.reader;

import sun.misc.Unsafe;

public final class DOSHeader {
    public final long address;

    public long e_lfanew;

    public DOSHeader(final long address) {
        this.address = address;
    }

    public void read(Unsafe unsafe) {
        e_lfanew = unsafe.getLong(address + 60); // e_lfanew
    }

    public long getAddress() {
        return address;
    }
}
