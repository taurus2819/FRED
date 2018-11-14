package nz.cri.gns.fred.importer;

import nz.cri.gns.auth.domain.User;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.RowProcessorFactory;

public class StratigraphicRelationshipProcessorFactory implements RowProcessorFactory {

    User user;
    DAOFactory factory = null;

    public StratigraphicRelationshipProcessorFactory(User user) {
        this.user = user;
        factory = FredHibernate.get().getDAOFactory();
    }

    @Override
    public RowProcessor createRowProcessor(String code) {
        switch (code) {
            case "VERTICAL_SECTION":
            case "PALEO":
            case "DRILL_HOLE":
            case "FRED_OUTCROP":
                return new StratigraphicRelationshipRowProcessor(code, user, factory);
            default:
                throw new MgException("Unknown type of spreadsheet: " + String.valueOf(code));
        }

    }

}
