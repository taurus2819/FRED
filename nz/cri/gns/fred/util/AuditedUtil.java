package nz.cri.gns.fred.util;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Audit;

public interface AuditedUtil {

    public Audit saveOrUpdate(Audit audit) throws StorageAccessException;

}
