package nz.cri.gns.fred.dao;

import nz.cri.gns.dataaccess.StorageAccessException;

public interface DAOFactory {

    public FredDAO getFredDAO();

    /**
     * Closes the current session for this thread
     *
     * @throws StorageAccessException
     */
    public void closeSession() throws StorageAccessException;

}
