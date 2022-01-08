package peboy.reader;

import sun.misc.Unsafe;

import java.util.Objects;

public class ImportAddressTable extends DataDirectory {
    public long addressNtHeader;

    public void read(Unsafe unsafe) {
        this.read(this.addressNtHeader, unsafe);
    }

    public void setAddress(long address) {
        this.addressNtHeader = address;
    }

    public long getAddress() {
        return addressNtHeader;
    }
}
