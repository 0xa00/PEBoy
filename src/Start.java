import peboy.PEBoy;

import java.io.File;

public class Start {
    public static void main(String[] args) {
        final PEBoy peBoy = new PEBoy(new File(Input.INPUT));
        if (peBoy.load(Input.IS_64)) {
            peBoy.finish();
        } else {
            System.err.println("Failed to load!");
        }
    }
}
