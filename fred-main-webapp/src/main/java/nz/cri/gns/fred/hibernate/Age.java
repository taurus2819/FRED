package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Stage;

public class Age implements Serializable, nz.cri.gns.fred.model.Age {

    private static final long serialVersionUID = 20050818L;

    private Integer ageId;
    private String name;
    private String code;
    private String period;
    private Double baseAge;
    private Double topAge;
    private String comments;
    private Boolean obsoleteFlag;
    private Boolean duplicateFlag;
    private Set<Stage> stagesByAgeLowerId;
    private Set<Stage> stagesByAgeUpperId;

    public void setAgeId(Integer siteId) {
		this.ageId = siteId;
	}

	public Integer getAgeId() {
		return ageId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPeriod() {
		return period;
	}

	public void setBaseAge(Double baseAge) {
		this.baseAge = baseAge;
	}

	public Double getBaseAge() {
		return baseAge;
	}

	public void setTopAge(Double topAge) {
		this.topAge = topAge;
	}

	public Double getTopAge() {
		return topAge;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setObsoleteFlag(Boolean obsoleteFlag) {
		this.obsoleteFlag = obsoleteFlag;
	}

	public Boolean getObsoleteFlag() {
		return obsoleteFlag;
	}

	public void setDuplicateFlag(Boolean duplicateFlag) {
		this.duplicateFlag = duplicateFlag;
	}

	public Boolean getDuplicateFlag() {
		return duplicateFlag;
	}

	public void setStagesByAgeLowerId(Set<Stage> stagesByAgeLowerId) {
		this.stagesByAgeLowerId = stagesByAgeLowerId;
	}

	public Set<Stage> getStagesByAgeLowerId() {
		return stagesByAgeLowerId;
	}

	public void setStagesByAgeUpperId(Set<Stage> stagesByAgeUpperId) {
		this.stagesByAgeUpperId = stagesByAgeUpperId;
	}

	public Set<Stage> getStagesByAgeUpperId() {
		return stagesByAgeUpperId;
	}

	public int compareTo(nz.cri.gns.fred.model.Age arg0) {
		if (!baseAge.equals(arg0.getBaseAge()))
			return topAge.compareTo(arg0.getTopAge());
		return baseAge.compareTo(arg0.getBaseAge());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(ageId);
	}

	public String getDisplayName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Age && ((Age)o).getAgeId().equals(ageId);
	}
	
	@Override
	public String toString() {
		return name + " [" + code + "; " + baseAge + "-" + topAge + "]";
	}
	
}