package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.AgeView;
import nz.cri.gns.fred.model.Stage;

public interface StageDAO {

	public AgeView getAgeView(int ageId) throws StorageAccessException;
	
	public <T> T saveOrUpdate(T object) throws StorageAccessException;
	
	public <T extends Comparable<? super T>> List<T> getList(String query, Class<T> clazz, Object ... parameters) throws StorageAccessException;

	public int getMaxAgeId() throws StorageAccessException;
	
	/**
	 * Locates, if one exists, a Stage entry in persistent storage that uses the given 
	 * stages (by id) and has uncertainty as specified. 
	 *@return a Stage object or null if no such object exists
	 */
	public Stage findStage(AgeView startStage, boolean startUncertain, AgeView stopStage, boolean stopUncertain) throws StorageAccessException;

	/**
	 * Creates a new, uninitialised Stage object
	 */
	public Stage createNewStage();
	
}
