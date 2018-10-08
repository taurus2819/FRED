package nz.cri.gns.fred.importer;

import nz.cri.gns.auth.domain.User;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.RowProcessorFactory;
import nz.cri.gns.munginator.upload.TemplateRowProcessor;

public class FredRowProcessorFactory implements RowProcessorFactory {
    User user;
    
    public FredRowProcessorFactory(User user) {
        this.user = user;
    }
    
    @Override
    public RowProcessor createRowProcessor(String code) {
        switch(code) {
            case "MG_IMPORT_SHEET_TYPE":
            case "VERTICAL_SECTION":
            case "PALEO":
            case "DRILL_HOLE":
                return new TemplateRowProcessor(code);
            case "FRED_OUTCROP":
                return new FredOutcropRowProcessor(user);
            default:
                throw new MgException("Unknown type of spreadsheet: "+String.valueOf(code));
        }
        
    }
                
}
