package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Record;

/**
 *
 */
public interface RecordDAO {

	/**
	 * @param recordId
	 * @return
	 * @throws StorageAccessException
	 */
	Record getRecord(int recordId) throws StorageAccessException;

	/**
	 * @param record
	 * @throws StorageAccessException
	 */
	void delete(Record record) throws StorageAccessException;

	/**
	 * @param audit
	 * @throws StorageAccessException
	 */
	void update(Audit audit) throws StorageAccessException;

}
