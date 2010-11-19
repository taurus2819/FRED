package nz.cri.gns.fred.util;

import java.beans.IntrospectionException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.AuditEdit;
import nz.cri.gns.fred.model.ConfidentialGroup;
import nz.cri.gns.fred.model.DataOrigin;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.FrUserView;
import nz.cri.gns.fred.model.LogTable;
import nz.cri.gns.fred.model.OrgView;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.UserView;

public class AuditUtil extends ModelUtil implements FREDConstants, AuditedUtil {

	private FredDAO fredDAO;
	
	public static final String QUERY_LOG_TYPE = "query";
	public static final String DOWNLOAD_LOG_TYPE = "download";
	public static final String DETAIL_LOG_TYPE = "detail";

	public AuditUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}

	public Audit getAudit(int auditId) throws StorageAccessException {
		return fredDAO.get(auditId, nz.cri.gns.fred.hibernate.AuditTable.class);
	}
	
	public Audit saveOrUpdate(Audit audit) throws StorageAccessException {
        return fredDAO.saveOrUpdate(audit);
    }

    public void updateLapseDates(String[] auditIds, Double lapsePeriod) throws NumberFormatException, StorageAccessException {
    	for (int i = 0; i < auditIds.length; i++)
    		updateLapseDate(getAudit(Integer.parseInt(auditIds[i])), lapsePeriod);
    }
    
    public void updateLapseDate(Audit audit, Double lapsePeriod) throws StorageAccessException {
    	audit.setConfidLapseDate(getLapseDate(lapsePeriod));
    	saveOrUpdate(audit);
    }
    
    public void clearConfidentialites(String auditIds[]) throws NumberFormatException, StorageAccessException {
    	for (int i = 0; i < auditIds.length; i++)
    		clearConfidentiality(getAudit(Integer.parseInt(auditIds[i])));    	
    }
    
    public void clearConfidentiality(Audit audit) throws StorageAccessException {
    	audit.setConfidentialFlag(false);
    	audit.setConfidGroups(null);
    	audit.setConfidLapseDate(null);
    	audit.setConfidPeriod(null);
    	audit.setConfidLapseEmail(null);
    	saveOrUpdate(audit);
    }
    
    public Audit cloneAudit(Audit audit) throws IntrospectionException, StorageAccessException {
    	Audit newAudit = fredDAO.createNewAudit();
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
    			AuditEdit newAuditEdit = FredDAO.createNewAuditEdit();
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
		return fredDAO.get(dataOriginId, nz.cri.gns.fred.hibernate.DataOrigin.class);
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
			if (audit.getCreatedBy().getUserId().equals(frUser.getUserId()))
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
	
	public static Date getLapseDate(Double confidPeriod) {
		if (confidPeriod == null)
			return null;
		GregorianCalendar cal = new GregorianCalendar();
		if (confidPeriod.doubleValue() == 0.5)
			cal.add(Calendar.MONTH, 6);
		else
			cal.add(Calendar.YEAR, confidPeriod.intValue());
		return cal.getTime();
	}
	
	public void updateConfidentiality(String[] auditIds, String confidType, String confidPeriod, String confidLapseEmail, String[] confidGroupIds) throws StorageAccessException {
		for (String auditId : auditIds)
			updateConfidentiality(getAudit(Integer.parseInt(auditId)), confidType, confidPeriod, confidLapseEmail, confidGroupIds);
	}
	
	public void updateConfidentiality(Audit audit, String confidType, String confidPeriod, String confidLapseEmail, String[] confidGroupIds) throws StorageAccessException {
		if ("confid".equals(confidType)) {
			audit.setConfidentialFlag(true);
			audit.setConfidPeriod(new Double(confidPeriod));
			audit.setConfidLapseEmail(confidLapseEmail);
			if (FREDConstants.APPROVED.equals(audit.getStatus()))
				audit.setConfidLapseDate(getLapseDate(audit.getConfidPeriod()));
			else
				audit.setConfidLapseDate(null);
			audit.setConfidEmailFlag(false);
			if (confidGroupIds != null) {
				Set<ConfidentialGroup> confidGroups = new HashSet<ConfidentialGroup>();
				for (int i = 0; i < confidGroupIds.length; i++)
					confidGroups.add(getConfidentialGroup(new Integer(confidGroupIds[i])));
				audit.setConfidGroups(confidGroups);
			}
		} else {
			audit.setConfidentialFlag(false);
			audit.setConfidLapseDate(null);
			audit.setConfidPeriod(null);
			audit.setConfidEmailFlag(null);
			audit.setConfidLapseEmail(null);
			audit.setConfidGroups(null);
		}
		saveOrUpdate(audit);	
	}
	
	public List<ConfidentialGroup> getConfidentialGroups(UserAccount user) throws StorageAccessException {		
		FrUserView frUser = new UserUtil(factory).getFrUserView(new Integer(user.getId()));
		return fredDAO.getList("FROM ConfidentialGroup AS c WHERE elements(c.owners) IS NULL OR ? IN elements(c.owners)", ConfidentialGroup.class, frUser);
	}
	
	public ConfidentialGroup getConfidentialGroup(Integer groupId) throws StorageAccessException {
		return fredDAO.get(groupId, nz.cri.gns.fred.hibernate.ConfidentialGroup.class);
	}
	
	public ConfidentialGroup addConfidentialGroup(String name, UserAccount user)  throws StorageAccessException {
	    ConfidentialGroup group = fredDAO.createNewConfidentialGroup();
	    group.setName(name);
	    Set<FrUserView> owners = new HashSet<FrUserView>();
	    owners.add(new UserUtil(factory).getFrUserView(new Integer(user.getId())));
	    group.setOwners(owners);
	    fredDAO.saveOrUpdate(group);
	    return group;
	}
	
	public void deleteConfidentialGroup(int groupId, UserAccount user)  throws StorageAccessException {
		boolean isOwner = false;
		ConfidentialGroup group = getConfidentialGroup(new Integer(groupId));
		for (FrUserView owner : group.getOwners()){
			if (owner.getUserId().equals(user.getId()))
				isOwner = true;
				break;				
		}
		if (!isOwner)
			throw new IllegalStateException("Cannot delete group as not an owner");
	    fredDAO.delete(group);
	}
	
	public void addUserToConfidGroup(ConfidentialGroup group, FrUserView frUser) throws StorageAccessException {
		group.getUsers().add(frUser);
		fredDAO.saveOrUpdate(group);
	}

	public void removeUserFromConfidGroup(ConfidentialGroup group, FrUserView frUser) throws StorageAccessException {
		group.getUsers().remove(frUser);
		fredDAO.saveOrUpdate(group);
	}
	
	
	
	/**
	 * Retrieves all confidential samples with lapse date before specified one where the user has created it
	 * 
	 * @param user
	 * @param lapseDate
	 * @return
	 * @throws StorageAccessException
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Sample> getConfidentialSamples(UserAccount user, Date lapseDate) throws StorageAccessException {
		if (lapseDate == null)
			return getConfidentialSamples(user);
		
		List<Sample> samples = new ArrayList<Sample>();
		for (Sample sample : this.getConfidentialSamples(user)){	
			Date sampleLapseDate = sample.getAudit().getConfidLapseDate();
			if (sampleLapseDate != null && sampleLapseDate.before(lapseDate))
				samples.add(sample);
		}	
		return samples;
	}
	
	
	/**
	 * Retrieves all confidential samples where either the user has created it
	 * 
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Sample> getConfidentialSamples(UserAccount user) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));
		return fredDAO.getList("FROM Sample AS s WHERE s.feature.featureType <> ? AND s.audit.confidentialFlag = ? AND s.audit.createdBy = ?", Sample.class, FREDConstants.OUTCROP, true, userView);
	}
		
	
	/**
	 * Retrieves all paleontology records with lapse date before specified one where 
	 * the user has created it.
	 * 
	 * @param user
	 * @param lapseDate
	 * @return
	 * @throws StorageAccessException
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Paleontology> getConfidentialPaleontologyRecords(UserAccount user, Date lapseDate) throws StorageAccessException {
		if (lapseDate == null)
			return getConfidentialPaleontologyRecords(user);
		
		List<Paleontology> paleontologies = new ArrayList<Paleontology>();
		for (Paleontology paleontology : this.getConfidentialPaleontologyRecords(user)){
			Date paleoLapseDate = paleontology.getRecord().getAudit().getConfidLapseDate();
			if (paleoLapseDate != null && paleoLapseDate.before(lapseDate))
				paleontologies.add(paleontology);
		}	
		return paleontologies;
	}
	
	/**
	 * Retrieves all paleontology records where  the user has created it.
	 * 
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Paleontology> getConfidentialPaleontologyRecords(UserAccount user) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));
		return fredDAO.getList("FROM Paleontology AS p WHERE p.record.audit.confidentialFlag = ? AND p.record.audit.createdBy = ?", Paleontology.class, true, userView);
	}

	
	
	/**
	 * Retrieves all paleontology records in a PalList with lapse date before specified one where 
	 * the user has created it
	 * 
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Paleontology> getConfidentialPalLists(UserAccount user, Date lapseDate) throws StorageAccessException {
		if (lapseDate == null)
			return getConfidentialPalLists(user);
		
		List<Paleontology> paleontologies = new ArrayList<Paleontology>();
		for (Paleontology paleontology : this.getConfidentialPalLists(user)){
			Date paleoLapseDate = paleontology.getRecord().getPalListAudit().getConfidLapseDate();
			if (paleoLapseDate != null && paleoLapseDate.before(lapseDate))
				paleontologies.add(paleontology);
		}	
		return paleontologies;
	}
	
	/**
	 * Retrieves all paleontology records in a PalList where either the user has created it
	 * 
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Paleontology> getConfidentialPalLists(UserAccount user) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));	
		return fredDAO.getList("FROM Paleontology AS p WHERE p.record.palListAudit.confidentialFlag = ? AND p.record.palListAudit.createdBy = ?", Paleontology.class, true, userView);
	}

	
	/**
	 * Retrieves all adoption records with lapse date before specified one where 
	 * the user has created it.
	 * 
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Adoption> getConfidentialAdoptionRecords(UserAccount user, Date lapseDate) throws StorageAccessException {
		if (lapseDate == null)
			return getConfidentialAdoptionRecords(user);
		
		List<Adoption> adoptions = new ArrayList<Adoption>();
		for (Adoption adoption : this.getConfidentialAdoptionRecords(user)){
			Date adoptionLapseDate = adoption.getRecord().getAudit().getConfidLapseDate();
			if (adoptionLapseDate != null && adoptionLapseDate.before(lapseDate))
				adoptions.add(adoption);
		}	
		return adoptions;
	}
	
	/**
	 * Retrieves all adoption records where the user has created it.
	 * 
	 * @param user
	 * @return
	 * @throws StorageAccessException
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Adoption> getConfidentialAdoptionRecords(UserAccount user) throws StorageAccessException {
		UserView userView = new UserUtil(factory).getUserView(new Integer(user.getId()));
		return fredDAO.getList("FROM Adoption AS a WHERE a.record.audit.confidentialFlag = ? AND a.record.audit.createdBy = ?", Adoption.class, true, userView);
	}
	
	
	
	/**
	 * Generates a string describing the users/groups that have access 
	 * to the data for the audit.
	 * 
	 * @param audit
	 * @return
	 */
	public String getConfidAccessListDescription(Audit audit) {
		if (!audit.getConfidentialFlag())
			return null;
		StringBuffer sb = new StringBuffer();		
		UserView createdByUser = audit.getCreatedBy();
		if (createdByUser != null)
			sb.append(createdByUser.getFullName());
		
		for (ConfidentialGroup confidGroup : audit.getConfidGroups())
			sb.append(" and ").append(confidGroup.getName());
		return sb.toString();
	}
	
	public void addLogEntry(String type, UserAccount user, Integer localityCount) throws StorageAccessException {
		LogTable log = fredDAO.createNewLog();
		log.setLogDate(new Date());
		log.setLogType(type);
		if (user != null)
			log.setUserId(new Integer(user.getId()));
		log.setLocalityCount(localityCount);
		fredDAO.saveOrUpdate(log);
	}
	
}
