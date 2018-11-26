package nz.cri.gns.fred.importer;

import java.util.HashMap;
import java.util.Map;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.munginator.MgException;

/** I store the columns of the Paleo spreadsheet. */
public class PaleoRowProcessorMatrix {
    Map<Integer, Paleontology> paleos;

    public PaleoRowProcessorMatrix() {
        paleos = new HashMap<>();
    }
    
    public void put(int index, Paleontology p) {
        if (paleos.containsKey(index)) {
            throw new ArrayStoreException("Index "+index+" is already occupied.");
        }
        paleos.put(index, p);
    }
    
    public Paleontology get(int index) {
        return paleos.get(index);
    }
    
    public void close() {
        // TODO: save everything.
        throw new MgException("TODO");
    }
}
