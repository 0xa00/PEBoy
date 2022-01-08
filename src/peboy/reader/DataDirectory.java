package peboy.reader;

import sun.misc.Unsafe;

import java.util.Objects;

public abstract class DataDirectory {
    public long VirtualAddress,
            Size;

    public void read(long address, Unsafe unsafe) {
        VirtualAddress = unsafe.getLong(address);
        Size = unsafe.getLong(address + 4);

        if (!Objects.equals(Long.toUnsignedString(VirtualAddress), Long.toString(VirtualAddress)) ||
                !Objects.equals(Long.toUnsignedString(Size), Long.toString(Size)))
            throw new RuntimeException("VA or size is too big, this is not currently supported");
    }

    @Override
    public String toString() {
        return "DataDirectory{" +
                "VirtualAddress=" + VirtualAddress +
                ", Size=" + Size +
                '}';
    }
}
