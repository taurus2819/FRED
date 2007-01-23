package nz.cri.gns.fred.util;

import java.beans.IntrospectionException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.AuditDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.DataOrigin;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.OrgView;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.UserView;

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
   		FREDUtil.beanCopy(audit, newAudit, new FREDUtil.ExcludeByName(FREDUtil.toVector(new String[] {"auditId", "features", "samples", "records", "recordByPalListAuditIds", "confidGroups", "auditEdits", "confidentialFlag", "confidPeriod",  "confidLapseDate",  "confidEmailFlag", "confidLapseEmail"})));
   	
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
    
    public static String getAuditBacklogStatus(Audit audit) {
    	Date auditDate = audit.getCreatedDate();
		if (audit.getStatus().equals(FREDConstants.APPROVED)) {
			if (audit.getCuratorComments() != null && audit.getCuratorComments().indexOf("backlog") > 0)
				return FREDConstants.BACKLOG_COMPLETE;
			try {
				if (auditDate != null) {
					Date oct05 = new SimpleDateFormat("dd/MM/yyyy").parse("01/10/2005");
					if (auditDate.after(oct05))
						return FREDConstants.BACKLOG_NEW;
				}
			} catch (ParseException e) {
				//shouldn't happen
			}
		}
		for (Iterator j = audit.getAuditEdits().iterator(); j.hasNext();) {
			AuditEdit edit = (AuditEdit) j.next();
			if (edit.getComments() != null && edit.getComments().indexOf("backlog") > 0)
				return FREDConstants.BACKLOG_PROCESSING;
		}
		return FREDConstants.BACKLOG_NOT_STARTED;
    }
    
	public DataOrigin getDataOrigin(Integer dataOriginId) throws StorageAccessException {
		return auditDAO.getDataOrigin(dataOriginId);
	}
    
	public static String getStatusHTMLOutputStyle(String status, String[] extraStyles) {
		StringBuffer style = new StringBuffer("style=\"");
		for (int i = 0; i < extraStyles.length; i++)
			style.append(extraStyles[i]).append("; ");
		if (status.equals(FREDConstants.WORKING))
			return style.append("color: #00FF00\"").toString();
		if (status.equals(FREDConstants.WAITING))
			return style.append("color: #FF9900\"").toString();
		if (status.equals(FREDConstants.REJECTED))
			return style.append("color: #FF0000\"").toString();
		return style.append("\"").toString();
	}
	
	public static List<AuditEdit> getOrderedAuditEdits(Audit audit) {
		List<AuditEdit> edits = new Vector<AuditEdit>(audit.getAuditEdits());
		Collections.sort(edits);
		return edits;
	}
	
	public boolean isAllowedReadApproved(Audit audit, UserAccount user) throws NumberFormatException, StorageAccessException {
		if (user == null || audit == null)
			return false;
		if (!FREDConstants.APPROVED.equals(audit.getStatus()))
			return false;
		if (audit.getConfidentialFlag()) {
			FrUserView frUser = new UserUtil(factory).getFrUserView(Integer.parseInt(user.getId()));
			OrgView userOrg = frUser.getOrgView();
			if (audit.getSubmittedBy().getUserId().equals(frUser.getUserId()))
				return true;
			for (ConfidentialGroup confidGroup : audit.getConfidGroups()) {
				if (confidGroup.getOrgView() != null) {
					if (confidGroup.getOrgView().equals(userOrg))
						return true;
				} else {
					for (FrUserView confidUser : confidGroup.getUsers()) {
						if (confidUser.equals(frUser))
							return true;
					}
				}
			}
			return false;
		} else
			return true;
	}
	
	public List<ConfidentialGroup> getConfidentialGroups(UserAccount user) throws StorageAccessException {
		FrUserView frUser = new UserUtil(factory).getFrUserView(new Integer(user.getId()));
		return auditDAO.getList("FROM ConfidentialGroup AS c WHERE c.owner IS NULL OR c.owner = ?", ConfidentialGroup.class, frUser);
	}
	
	public ConfidentialGroup getConfidentialGroup(Integer groupId) throws StorageAccessException {
		return auditDAO.getConfidentialGroup(groupId);
	}
	
	public ConfidentialGroup addConfidentialGroup(String name, UserAccount user)  throws StorageAccessException {
	    ConfidentialGroup group = auditDAO.createNewConfidentialGroup();
	    group.setName(name);
	    group.setOwner(new UserUtil(factory).getFrUserView(new Integer(user.getId())));
	    auditDAO.save(group);
	    return group;
	}
	
	public void deleteConfidentialGroup(int groupId, UserAccount user)  throws StorageAccessException {
		ConfidentialGroup group = auditDAO.getConfidentialGroup(new Integer(groupId));
		if (!String.valueOf(group.getOwner().getUserId()).equals(user.getId()))
			throw new IllegalStateException("Cannot delete group as not owner");
	    auditDAO.delete(group);
	}
	
	public void addUserToConfidGroup(ConfidentialGroup group, FrUserView frUser) throws StorageAccessException {
		group.getUsers().add(frUser);
		auditDAO.save(group);
	}

	public void removeUserFromConfidGroup(ConfidentialGroup group, FrUserView frUser) throws StorageAccessException {
		group.getUsers().remove(frUser);
		auditDAO.save(group);
	}
	
	public List<Sample> getConfidentialSamples(UserAccount user) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));
		return auditDAO.getList("FROM Sample AS s WHERE s.feature.featureType <> ? AND s.audit.confidentialFlag = ? AND s.audit.submittedBy = ?", Sample.class, FREDConstants.OUTCROP, true, userView);
	}
	
	public List<Sample> getConfidentialSamples(UserAccount user, Date lapseDate) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));
		return auditDAO.getList("FROM Sample AS s WHERE s.feature.featureType <> ? AND s.audit.confidentialFlag = ? AND s.audit.submittedBy = ? AND s.audit.confidLapseDate <= ?", Sample.class, FREDConstants.OUTCROP, true, userView, lapseDate);
	}
	
	public List<Paleontology> getConfidentialPaleontologyRecords(UserAccount user) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));
		return auditDAO.getList("FROM Paleontology AS p WHERE p.record.audit.confidentialFlag = ? AND p.record.audit.submittedBy = ?", Paleontology.class, true, userView);
	}
	
	public List<Paleontology> getConfidentialPalLists(UserAccount user) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));
		return auditDAO.getList("FROM Paleontology AS p WHERE p.record.palListAudit.confidentialFlag = ? AND p.record.palListAudit.submittedBy = ?", Paleontology.class, true, userView);
	}
	
	public List<Adoption> getConfidentialAdoptionRecords(UserAccount user) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));
		return auditDAO.getList("FROM Adoption AS a WHERE a.record.audit.confidentialFlag = ? AND a.record.audit.submittedBy = ?", Adoption.class, true, userView);
	}
	
}
