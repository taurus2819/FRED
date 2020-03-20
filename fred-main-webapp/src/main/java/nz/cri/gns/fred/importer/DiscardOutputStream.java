package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.io.OutputStream;

/** I am a stream that writes nothing to nowhere. */
public class DiscardOutputStream extends OutputStream {

    @Override
    public void write(int b) throws IOException {
        // Don't write anything anywhere.
    }

}
