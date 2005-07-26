package nz.cri.gns.fred.util;

import java.util.Iterator;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.RecordDAO;
import nz.cri.gns.fred.hibernate.PalList;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Record;

/**
 *
 */
public class RecordUtil extends ModelUtil {

	private RecordDAO recordDAO;


	public RecordUtil(DAOFactory factory) {
		super(factory);
		this.recordDAO = factory.getRecordDAO();
	}
	

	public static boolean isTaxaApproved(Record record) {
		for (Iterator it = record.getPaleontology().getPalLists().iterator(); it.hasNext(); ) {
			PalList list = (PalList)it.next();
			if (!list.getTaxonomicLookup().getStatus().equals(FREDConstants.APPROVED)) 
				return false;
		}
		return true;
	}
}
