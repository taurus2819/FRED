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
   		System.out.println("Cloned audit comparison. Old ID = " + audit.getAuditId() + ", new ID = " + newAudit.getAuditId());
   		System.out.println("Cloned audit comparison. Old Created_By_ID = " + audit.getCreatedById() + ", new Created_By_ID = " + newAudit.getCreatedById());
   		
    	//Copy the audit edits
    	Set<AuditEdit> auditEdits = audit.getAuditEdits();
    	if (auditEdits != null && auditEdits.size() > 0) {
    		Set<AuditEdit> newAuditEdits = new HashSet<AuditEdit>();
    		for (AuditEdit auditEdit : auditEdits) {
    			AuditEdit newAuditEdit = auditDAO.createNewAuditEdit();
    			FREDUtil.beanCopy(auditEdit, newAuditEdit, new FREDUtil.ExcludeByName(FREDUtil.toVector(new String[] {"auditEditId", "audit"})));
    			newAuditEdit.setAudit(newAudit);
    			newAuditEdits.add(newAuditEdit);
    			System.out.println("AuditEdit comparison. AE.Audit_ID: " + auditEdit.getAudit().getAuditId() + ", new AE.Audit_ID = " + ((newAuditEdit.getAudit() != null) ? newAuditEdit.getAudit().getAuditId() : -1));
    			System.out.println("AuditEdit comparison. AE.Comments: " + auditEdit.getComments() + ", new AE.Comments = " + newAuditEdit.getComments());
    			
    			//auditDAO.save(newAuditEdit);
    		}
    		//newAudit.setAuditEdits(newAuditEdits);
    	}
    	
    	System.out.println("Cloned audit comparison. AuditEdit count = " + ((audit.getAuditEdits() != null) ? audit.getAuditEdits().size() : 0) + ", new AuditEdit count = " + ((newAudit.getAuditEdits() != null) ? newAudit.getAuditEdits().size() : 0));
    	
    	return newAudit;
    }
    
}
