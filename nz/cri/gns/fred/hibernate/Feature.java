package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class Feature implements Serializable, nz.cri.gns.fred.model.Feature, Cloneable, Comparable {

    /** identifier field */
    private Integer featureId;

    /** nullable persistent field */
    private Integer siteId;

    /** nullable persistent field */
    private String locality;

    /** nullable persistent field */
    private String comments;

    /** persistent field */
    private String featureType;

    /** nullable persistent field */
    private String featureName;

    /** nullable persistent field */
    private String drillholeLicenceName;

    /** nullable persistent field */
    private Date startDate;

    /** nullable persistent field */
    private Date finishDate;

    /** nullable persistent field */
    private Double datumElevation;

    /** nullable persistent field */
    private Double startDepth;

    /** nullable persistent field */
    private String datumType;

    /** nullable persistent field */
    private Double finishDepth;

    /** nullable persistent field */
    private String startDateRounding;

    /** nullable persistent field */
    private String finishDateRounding;

    /** persistent field */
    private nz.cri.gns.fred.model.Person person;

    /** persistent field */
    private nz.cri.gns.fred.model.Folder masterFile;

    /** persistent field */
    private nz.cri.gns.fred.model.Audit auditTable;

    /** persistent field */
    private nz.cri.gns.fred.model.RegistrationArea registrationArea;

    /** persistent field */
    private Set samples;

    /** persistent field */
    private Set folders;

    /** persistent field */
    private Set relationships;

    /** persistent field */
    private Set featureMetas;

    /** full constructor */
    public Feature(Integer siteId, String locality, String comments, String featureType, String featureName, String drillholeLicenceName, Date startDate, Date finishDate, Double datumElevation, Double startDepth, String datumType, Double finishDepth, String startDateRounding, String finishDateRounding, nz.cri.gns.fred.hibernate.Person person, nz.cri.gns.fred.hibernate.Folder masterFile, nz.cri.gns.fred.hibernate.AuditTable auditTable, nz.cri.gns.fred.hibernate.RegistrationArea registrationArea, Set samples, Set folders, Set relationships, Set featureMetas) {
        this.siteId = siteId;
        this.locality = locality;
        this.comments = comments;
        this.featureType = featureType;
        this.featureName = featureName;
        this.drillholeLicenceName = drillholeLicenceName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.datumElevation = datumElevation;
        this.startDepth = startDepth;
        this.datumType = datumType;
        this.finishDepth = finishDepth;
        this.startDateRounding = startDateRounding;
        this.finishDateRounding = finishDateRounding;
        this.person = person;
        this.masterFile = masterFile;
        this.auditTable = auditTable;
        this.registrationArea = registrationArea;
        this.samples = samples;
        this.folders = folders;
        this.relationships = relationships;
        this.featureMetas = featureMetas;
    }

    /** default constructor */
    public Feature() {
    }

    /** minimal constructor */
    public Feature(String featureType, nz.cri.gns.fred.hibernate.Person person, nz.cri.gns.fred.hibernate.Folder masterFile, nz.cri.gns.fred.hibernate.AuditTable auditTable, nz.cri.gns.fred.hibernate.RegistrationArea registrationArea, Set samples, Set folders, Set relationships, Set featureMetas) {
        this.featureType = featureType;
        this.person = person;
        this.masterFile = masterFile;
        this.auditTable = auditTable;
        this.registrationArea = registrationArea;
        this.samples = samples;
        this.folders = folders;
        this.relationships = relationships;
        this.featureMetas = featureMetas;
    }

    public Integer getFeatureId() {
        return this.featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public Integer getSiteId() {
        return this.siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getLocality() {
        return this.locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFeatureType() {
        return this.featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getFeatureName() {
        return this.featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getDrillholeLicenceName() {
        return this.drillholeLicenceName;
    }

    public void setDrillholeLicenceName(String drillholeLicenceName) {
        this.drillholeLicenceName = drillholeLicenceName;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return this.finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Double getDatumElevation() {
        return this.datumElevation;
    }

    public void setDatumElevation(Double datumElevation) {
        this.datumElevation = datumElevation;
    }

    public Double getStartDepth() {
        return this.startDepth;
    }

    public void setStartDepth(Double startDepth) {
        this.startDepth = startDepth;
    }

    public String getDatumType() {
        return this.datumType;
    }

    public void setDatumType(String datumType) {
        this.datumType = datumType;
    }

    public Double getFinishDepth() {
        return this.finishDepth;
    }

    public void setFinishDepth(Double finishDepth) {
        this.finishDepth = finishDepth;
    }

    public String getStartDateRounding() {
        return this.startDateRounding;
    }

    public void setStartDateRounding(String startDateRounding) {
        this.startDateRounding = startDateRounding;
    }

    public String getFinishDateRounding() {
        return this.finishDateRounding;
    }

    public void setFinishDateRounding(String finishDateRounding) {
        this.finishDateRounding = finishDateRounding;
    }

    public nz.cri.gns.fred.model.Person getPerson() {
        return this.person;
    }

    public void setPerson(nz.cri.gns.fred.model.Person person) {
        this.person = person;
    }

    public nz.cri.gns.fred.model.Folder getMasterFile() {
        return this.masterFile;
    }

    public void setMasterFile(nz.cri.gns.fred.model.Folder masterFile) {
        this.masterFile = masterFile;
    }

    public nz.cri.gns.fred.model.Audit getAudit() {
        return this.auditTable;
    }

    public void setAudit(nz.cri.gns.fred.model.Audit audit) {
        if (this.auditTable != null)
            this.auditTable.getFeatures().remove(this);
        this.auditTable = audit;
        //This can't be right!!!!???
        try {
            System.out.println("========");
            if (audit.getFeatures() == null) {
                audit.setFeatures(new HashSet());
            }
            audit.getFeatures().add(this);
            System.out.println("========Audit now has " + audit.getFeatures().size() + " features");
        } catch (Exception e) {
        }
   }

    public nz.cri.gns.fred.model.RegistrationArea getRegistrationArea() {
        return this.registrationArea;
    }

    public void setRegistrationArea(nz.cri.gns.fred.model.RegistrationArea registrationArea) {
        this.registrationArea = registrationArea;
    }

    public Set getSamples() {
        return this.samples;
    }

    public void setSamples(Set samples) {
        this.samples = samples;
    }

    public Set getFolders() {
        return this.folders;
    }

    public void setFolders(Set folders) {
        this.folders = folders;
    }

    public Set getRelationships() {
        return this.relationships;
    }

    public void setRelationships(Set relationships) {
        this.relationships = relationships;
    }

    public Set getFeatureMetas() {
        return this.featureMetas;
    }

    public void setFeatureMetas(Set featureMetas) {
        this.featureMetas = featureMetas;
    }

    public Object clone() { 
    	try {
    		return super.clone();
    	} catch (CloneNotSupportedException e) {
    		//But it is!
    		return null;
    	}
    }
    
    public boolean equals(Object o) {
    	if (!(o instanceof Feature))
    		return false;
    	if (featureId == null || ((Feature)o).featureId == null)
    		return false;
    	return featureId.equals(((Feature)o).featureId);
    }

	public int compareTo(Object arg0) {
		String thisName = (getFeatureName() == null) ? "Unnamed Locality" : featureName;
		String thatName = ((Feature)arg0).getFeatureName();
		if (thatName == null) 
			thatName = "Unnamed Locality";
		return thisName.compareTo(thatName);
	}
}
