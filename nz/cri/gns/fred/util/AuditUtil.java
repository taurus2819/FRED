package nz.cri.gns.fred.util;

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nz.cri.gns.fred.dao.AuditDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;

/**
 *
 */
public class AuditUtil extends ModelUtil implements FREDConstants, AuditedUtil {

	private AuditDAO auditDAO;

	public AuditUtil(DAOFactory factory) {
		super(factory);
		this.auditDAO = factory.getAuditDAO();
	}

	public Audit save(Audit audit) throws StorageAccessException {
        return auditDAO.save(audit);
    }

    public Audit update(Audit audit) throws StorageAccessException {
        return auditDAO.update(audit);
    }
    
    public Audit cloneAudit(Audit audit) throws IntrospectionException, StorageAccessException {
    	Audit newAudit = auditDAO.createNewAudit();
   		FREDUtil.beanCopy(audit, newAudit, new FREDUtil.ExcludeByName(FREDUtil.toVector(new String[] {"auditId", "features", "samples", "records", "auditEdits"}))    		);
   	
   		/**
   		 * @TODO This bit doesn't work - get Iain to check (as copied from his FeatureUtil.cloneSamples();
   		 */
   		/* 
    	//Copy the audit edits
    	Set<AuditEdit> auditEdits = audit.getAuditEdits();
    	if (auditEdits != null && auditEdits.size() > 0) {
    		Set<AuditEdit> newAuditEdits = new HashSet<AuditEdit>();
    		for (AuditEdit auditEdit : auditEdits) {
    			AuditEdit newAuditEdit = auditDAO.createNewAuditEdit();
    			FREDUtil.beanCopy(auditEdit, newAuditEdit, new FREDUtil.ExcludeByName(FREDUtil.toVector(new String[] {"auditEditId", "audit"})));
    			newAuditEdit.setAudit(newAudit);
    			newAuditEdits.add(newAuditEdit);
    		}
    		newAudit.setAuditEdits(newAuditEdits);
    	}
    	*/
    	return newAudit;
    }
    
}
