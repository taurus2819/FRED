package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Stage;

public class AgeView implements Serializable, nz.cri.gns.fred.model.AgeView {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer ageId;

    /** nullable persistent field */
    private String ageName;
    
    /** nullable persistent field */
    private String ageAbbrev;

    /** nullable persistent field */
    private Double ageStop;

    /** nullable persistent field */
    private Double ageStart;
    
    /** persistent field */
    private Set<Stage> stagesByStageLowerId;
    
    /** persistent field */
    private Set<Stage> stagesByStageUpperId;

    /** full constructor */
    public AgeView(Integer ageId, String ageName, String ageAbbrev, Double ageStop, Double ageStart, Set<Stage> stagesByStageLowerId, Set<Stage> stagesByStageUpperId) {
    	this.ageId = ageId;
    	this.ageName = ageName;
    	this.ageStop = ageStop;
    	this.ageStart = ageStart;
    	this.ageAbbrev = ageAbbrev;
    	this.stagesByStageLowerId = stagesByStageLowerId;
    	this.stagesByStageUpperId = stagesByStageUpperId;
    }

    /** default constructor */
    public AgeView() {
    }

	public void setAgeId(Integer siteId) {
		this.ageId = siteId;
	}

	public Integer getAgeId() {
		return ageId;
	}

	public void setAgeName(String siteName) {
		this.ageName = siteName;
	}

	public String getAgeName() {
		return ageName;
	}
	
	public void setAgeAbbrev(String countryCode) {
		this.ageAbbrev = countryCode;
	}

	public String getAgeAbbrev() {
		return ageAbbrev;
	}

	public void setAgeStop(Double latitude) {
		this.ageStop = latitude;
	}

	public Double getAgeStop() {
		return ageStop;
	}

	public void setAgeStart(Double longitude) {
		this.ageStart = longitude;
	}

	public Double getAgeStart() {
		return ageStart;
	}

	public void setStagesByStageLowerId(Set<Stage> stagesByStageLowerId) {
		this.stagesByStageLowerId = stagesByStageLowerId;
	}

	public Set<Stage> getStagesByStageLowerId() {
		return stagesByStageLowerId;
	}

	public void setStagesByStageUpperId(Set<Stage> stagesByStageUpperId) {
		this.stagesByStageUpperId = stagesByStageUpperId;
	}

	public Set<Stage> getStagesByStageUpperId() {
		return stagesByStageUpperId;
	}

	public int compareTo(nz.cri.gns.fred.model.AgeView arg0) {
		if (!ageStop.equals(arg0.getAgeStop()))
			return ageStop.compareTo(arg0.getAgeStop());
		return ageStart.compareTo(arg0.getAgeStart());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(ageId);
	}

	public String getDisplayName() {
		return ageName;
	}
	
	public boolean equals(Object o) {
		return o instanceof AgeView && ((AgeView)o).ageId.equals(ageId);
	}
	
	public String toString() {
		return ageName + " [" + ageAbbrev + "; " + ageStart + "-" + ageStop + "]";
	}
	
	public int hashCode() {
		return (int)Math.floor(ageName.hashCode() + 765 * ageStart + 12 * ageStop);
	}
}
