package peboy.reader;

import sun.misc.Unsafe;

public class DebugDirectory extends DataDirectory {
    public long address;

    public void resolveVA(Unsafe unsafe, byte[] fileBytes) {
        if (this.Size % 28 != 0)
            throw new RuntimeException("invalid debug directory size: " + this.Size);
        if (this.Size != 28)
            System.out.println("Multiple debug directories found: " + this.Size / 28);
        if (this.Size < 0 || this.Size > 28 * 5)
            throw new RuntimeException("stopped because memory is out of bounds for debug directory");
        long address = this.address = unsafe.allocateMemory(this.Size);
        if (address == 0)
            throw new OutOfMemoryError("debug directory memory");
        for (int i = 0; i < Size; i++) {
            unsafe.putChar(address + i, (char) fileBytes[(int) (this.VirtualAddress + i)]);
        }
    //    System.out.println(unsafe.getShort(address + 20));
    }
}
