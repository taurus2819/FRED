package nz.cri.gns.fred.util;

import java.util.List;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.Lab;


public class LabUtil extends ModelUtil {

    private final FredDAO fredDAO;

    public LabUtil(DAOFactory dao) {
        super(dao);
        this.fredDAO = dao.getFredDAO();
    }
    
    public List<Lab> getLabs()  throws  StorageAccessException{
        return fredDAO.getList("FROM Lab lab WHERE EXISTS (FROM LabSection section WHERE section.lab.name = lab.name) ", Lab.class);
    }
}
