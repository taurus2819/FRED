package nz.cri.gns.db.fred;

import java.util.Date;

public class Feature {

	private int featureID;
	private int siteID;
	private int masterfileID;
	private String locality;
	private String comments;
	private int auditID;
	private int regAreaID;
	private int securityClassID;
	private String featureType;
	private String featureName;
	private String drillholeLicenceName;
	private Date startDate;
	private String startDateRounding;
	private Date finishDate;
	private String finishDateRounding;
	private int personID;
	private String datumType;
	private double datumElevation;
	private double startDepth;
	private double finishDepth;
	
	public Feature() {
	}

	public void setFeatureID(int featureID) {
		this.featureID = featureID;
	}

	public int getFeatureID() {
		return featureID;
	}

	public void setSiteID(int siteID) {
		this.siteID = siteID;
	}

	public int getSiteID() {
		return siteID;
	}

	public void setMasterfileID(int masterfileID) {
		this.masterfileID = masterfileID;
	}

	public int getMasterfileID() {
		return masterfileID;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLocality() {
		return locality;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public int getAuditID() {
		return auditID;
	}

	public void setRegAreaID(int regAreaID) {
		this.regAreaID = regAreaID;
	}

	public int getRegAreaID() {
		return regAreaID;
	}

	public void setSecurityClassID(int securityClassID) {
		this.securityClassID = securityClassID;
	}

	public int getSecurityClassID() {
		return securityClassID;
	}

	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}

	public String getFeatureType() {
		return featureType;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setDrillholeLicenceName(String drillholeLicenceName) {
		this.drillholeLicenceName = drillholeLicenceName;
	}

	public String getDrillholeLicenceName() {
		return drillholeLicenceName;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDateRounding(String startDateRounding) {
		this.startDateRounding = startDateRounding;
	}

	public String getStartDateRounding() {
		return startDateRounding;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDateRounding(String finishDateRounding) {
		this.finishDateRounding = finishDateRounding;
	}

	public String getFinishDateRounding() {
		return finishDateRounding;
	}

	public void setPersonID(int personID) {
		this.personID = personID;
	}

	public int getPersonID() {
		return personID;
	}

	public void setDatumType(String datumType) {
		this.datumType = datumType;
	}

	public String getDatumType() {
		return datumType;
	}

	public void setDatumElevation(double datumElevation) {
		this.datumElevation = datumElevation;
	}

	public double getDatumElevation() {
		return datumElevation;
	}

	public void setStartDepth(double startDepth) {
		this.startDepth = startDepth;
	}

	public double getStartDepth() {
		return startDepth;
	}

	public void setFinishDepth(double finishDepth) {
		this.finishDepth = finishDepth;
	}

	public double getFinishDepth() {
		return finishDepth;
	}
	
	
}
