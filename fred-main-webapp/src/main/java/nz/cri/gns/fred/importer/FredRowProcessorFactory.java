package nz.cri.gns.fred.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.RowProcessorFactory;
import nz.cri.gns.munginator.upload.TemplateRowProcessor;

public class FredRowProcessorFactory implements RowProcessorFactory {

    User user;
    DAOFactory factory = null;
    Map<Integer, Record>  paleoMatrix;
    
    public FredRowProcessorFactory(User user, DAOFactory factory) {
        this.user = user;
        this.factory = factory;
    }
    
    private static List<RowProcessor> list(RowProcessor one, RowProcessor two) {
        List<RowProcessor> result = new ArrayList<>();
        result.add(one);
        if(null!=two) {
            result.add(two);
        }
        return result;
    }

    @Override
    public List<RowProcessor> createRowProcessors(String code) {
        switch (code) {
            case "MG_IMPORT_SHEET_TYPE":
                return list(new TemplateRowProcessor(code), null);
            case "VERTICAL_SECTION":
            case "DRILL_HOLE":
            case "FRED_OUTCROP":
                return list(new FredRowProcessor(user, factory, code), new StratigraphicRelationshipRowProcessor(code, user, factory));
            case "PALEO":
                return list(new PaleoRowProcessor(user, factory, code, getPaleoRowProcessorMatrix()), null);
            default:
                throw new MgException("Unknown type of spreadsheet: " + String.valueOf(code));
        }

    }
    
    public Map<Integer, Record>  getPaleoRowProcessorMatrix() {
        if (null==paleoMatrix) {
            paleoMatrix = new HashMap<Integer, Record> ();
        }
        return paleoMatrix;
    }

}
