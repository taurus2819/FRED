package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.MetaCat;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SiteView;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.SiteModelUtil;

public class Feature implements Serializable, nz.cri.gns.fred.model.Feature, Cloneable {

    private static final long serialVersionUID = 20050818L;

    private Integer featureId;
    private Integer siteId;
    private String locality;
    private Integer origSystemId;
    private String origCoord;
    private Integer mapYear;
    private String coordComments;
    private String featureType;
    private String featureName;
    private String drillholeLicenceName;
    private Date startDate;
    private Date finishDate;
    private Double datumElevation;
    private Double startDepth;
    private String datumType;
    private Double finishDepth;
    private String depthUnit;
    private String startDateRounding;
    private String finishDateRounding;
    private String comments;
    private FrNumber frNumber;
    private FrNumber yardFrNumber;
    private Person person;
    private Folder masterFile;
    private Audit auditTable;
    private SiteView siteView;
    private RegistrationArea registrationArea;
    private Set<Sample> samples;
    private Set<Folder> folders;
    private Set<Relationship> relationships;
    private Set<MetaCat> metaCats;

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

    public Integer getOrigSystemId() {
        return this.origSystemId;
    }

    public void setOrigSystemId(Integer origSystemId) {
        this.origSystemId = origSystemId;
    }
    
    public String getOrigCoord() {
        return this.origCoord;
    }

    public void setOrigCoord(String origCoord) {
        this.origCoord = origCoord;
    }
    
    public Integer getMapYear() {
        return this.mapYear;
    }

    public void setMapYear(Integer mapYear) {
        this.mapYear = mapYear;
    }
    
    public String getCoordComments() {
        return this.coordComments;
    }

    public void setCoordComments(String coordComments) {
        this.coordComments = coordComments;
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
    
    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
    
    public String getDepthUnit() {
        return this.depthUnit;
    }

    public void setDepthUnit(String depthUnit) {
        this.depthUnit = depthUnit;
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

    public nz.cri.gns.fred.model.FrNumber getFrNumber() {
        return this.frNumber;
    }

    public void setFrNumber(FrNumber frNumber) {
        this.frNumber = frNumber;
    }

    public FrNumber getYardFrNumber() {
        return this.yardFrNumber;
    }

    public void setYardFrNumber(FrNumber yardFrNumber) {
        this.yardFrNumber = yardFrNumber;
    }
    
    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Folder getMasterFile() {
        return this.masterFile;
    }

    public void setMasterFile(Folder masterFile) {
        this.masterFile = masterFile;
    }

    public Audit getAudit() {
        return this.auditTable;
    }

    public void setAudit(Audit auditTable) {
        this.auditTable = auditTable;
   }

    public SiteView getSiteView() {        
        if(siteView == null)    {
            //load Site details from API. This might need more thoughts to make sure performance is ok
            siteView = SiteModelUtil.getSiteView(siteId);
        }
        return siteView;
    }
//
    public void setSiteView(SiteView siteView) {
        this.siteView = siteView;
   }
    
    public RegistrationArea getRegistrationArea() {
        return this.registrationArea;
    }

    public void setRegistrationArea(RegistrationArea registrationArea) {
        this.registrationArea = registrationArea;
    }

    public Set<Sample> getSamples() {
        return this.samples;
    }

    public void setSamples(Set<Sample> samples) {
        this.samples = samples;
    }

    public Set<Folder> getFolders() {
        return this.folders;
    }

    public void setFolders(Set<Folder> folders) {
        this.folders = folders;
    }

    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    public void setRelationships(Set<Relationship> relationships) {
        this.relationships = relationships;
    }

    @Override
    public Set<MetaCat> getMetaCats() {
        return this.metaCats;
    }

    @Override
    public void setMetaCats(Set<MetaCat> metaCats) {
        this.metaCats = new HashSet<>(metaCats);
    }

    @Override
	public Object clone() { 
    	try {
    		return super.clone();
    	} catch (CloneNotSupportedException e) {
    		//But it is!
    		return null;
    	}
    }
    
	public int compareTo(nz.cri.gns.fred.model.Feature arg0) {
		String thisName = FeatureUtil.getFeatureIdentifyingName(this);
		String thatName = FeatureUtil.getFeatureIdentifyingName(arg0);
		return thisName.compareToIgnoreCase(thatName);
	}
	
	@Override
	public String toString() {
		return FeatureUtil.getFeatureIdentifyingName(this);
	}
	
    public boolean equals(Object o) {
    	if (!(o instanceof Feature))
    		return false;
    	if (featureId == null || ((Feature)o).getFeatureId() == null)
    		return false;
    	return featureId.equals(((Feature)o).getFeatureId());
    }
	
	public int hashCode() {
		return 217 * featureId;
	}
        
        public Feature() {            
        }
        
        /**
         * A constructor to allow partial loading by hibernate
         * @param id FeatureId
         */
        public Feature(Integer id) {
            this.featureId = id;
        }
        
        /**
         * A constructor to allow partial loading by hibernate
         * @param id FeatureId
         */
        public Feature(Integer id, FrNumber frnumber) {
            this.featureId = id;
            this.frNumber = frnumber;
        }

}
