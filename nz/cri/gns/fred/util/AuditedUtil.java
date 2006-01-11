package nz.cri.gns.fred.util;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.Audit;

public interface AuditedUtil {

    public Audit update(Audit audit) throws StorageAccessException;
    public Audit save(Audit audit) throws StorageAccessException;

}
