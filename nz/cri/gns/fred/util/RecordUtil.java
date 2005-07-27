package nz.cri.gns.fred.util;

import java.util.Date;
import java.util.Iterator;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FolderDAO;
import nz.cri.gns.fred.dao.RecordDAO;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.hibernate.PalList;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.UserFolder;

/**
 *
 */
public class RecordUtil extends ModelUtil implements FREDConstants {

	private RecordDAO recordDAO;
	private FolderDAO folderDAO;


	public RecordUtil(DAOFactory factory) {
		super(factory);
		this.recordDAO = factory.getRecordDAO();
		this.folderDAO = factory.getFolderDAO();
	}
	

	public static boolean isTaxaApproved(Record record) {
		for (Iterator it = record.getPaleontology().getPalLists().iterator(); it.hasNext(); ) {
			PalList list = (PalList)it.next();
			if (!list.getTaxonomicLookup().getStatus().equals(FREDConstants.APPROVED)) 
				return false;
		}
		return true;
	}
	
	public static String getRecordName(Record record) {
		RecordDetails details = (record.getAdoption() == null) ? ((record.getPaleontology() == null) ? null : (RecordDetails)record.getPaleontology()) : record.getAdoption();
		if (details == null)
			return "Unnamed Record";
		
		String person = "";
		if (details.getPersons() != null) {
			Iterator it = details.getPersons().iterator();
			if (it.hasNext()) {
				Person p = (Person)it.next();
				person = p.getGivenName() + " " + p.getFamilyName();
			}
		}
		
		String date = "";
		if (details.getDate() != null) {
			date = FREDUtil.formatDateForOutput(details.getDate(), details.getDateRounding());
		}
		
		return ((person.length() + date.length() > 0) ? person + ((person.length() > 0 && date.length() > 0) ? ", " : "") + date : "Unnamed Record");
	}
	
	public void deleteRecord(int recordId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		Record record = recordDAO.getRecord(recordId);
		
		if (!isAllowedDeleteRecord(record, folder, user))
			throw new InsufficientPrivelegesException();
		
		recordDAO.delete(record);
		
	}

	public void submitRecord(int recordId, UserFolder folder, UserAccount user) throws StorageAccessException, InsufficientPrivelegesException {
		if (!folder.isAllowedSubmitLocalities())
			throw new InsufficientPrivelegesException();
		
		Record record = recordDAO.getRecord(recordId);
		Audit audit = record.getAudit();
		audit.setStatus(WAITING);		//TODO like sample, this was originally APPROVED - why is that?
		audit.setSubmittedById(new Integer(user.getId()));
		audit.setSubmittedDate(new Date());
		audit.setWorkingComments(null);
		audit.setFolder(null);
		recordDAO.update(audit);
	}

	
	/**
	 * @param record
	 * @param folder
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws NumberFormatException
	 */
	private boolean isAllowedDeleteRecord(Record record, UserFolder folder, UserAccount user) throws StorageAccessException {
		// TODO I made this up cos it wasn't checked before...is it right?
		Audit audit = record.getAudit();
		if (audit.getStatus().equals(APPROVED))
			return false;

		if (audit.getStatus().equals(WAITING))
			return FeatureUtil.hasMasterfileRights(user, record.getSample().getFeature(), UserFolder.FOLDER_DELETE_RIGHT, folderDAO);

		return folder.isAllowedDeleteLocalities();
	}
}
