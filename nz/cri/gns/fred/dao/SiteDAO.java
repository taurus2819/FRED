package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.DatumMethod;
import nz.cri.gns.fred.model.SiteView;

public interface SiteDAO {

	public SiteView getSiteView(int siteId) throws StorageAccessException;
	
	public DatumMethod getSiteDatumMethod(int methodId) throws StorageAccessException;
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;
	
}
