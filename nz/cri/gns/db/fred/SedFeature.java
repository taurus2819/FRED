package nz.cri.gns.db.fred;

public class SedFeature {

	private String sedFeature;
	private Integer sedFeatureId;
	private String feat;
	private String abundant;

	public SedFeature(String sedFeature) {
		this.sedFeature = sedFeature;
	}

	public String toString() {
		return sedFeature;
	}

	public void setSedFeature(String sedFeature) {
		this.sedFeature = sedFeature;
	}

	public String getSedFeature() {
		return sedFeature;
	}
	
	public void setSedFeatureId(Integer sedFeatureId) {
		this.sedFeatureId = sedFeatureId;
	}

	public Integer getSedFeatureId() {
		return sedFeatureId;
	}

	public void setFeat(String feat) {
		this.feat = feat;
	}

	public String getFeat() {
		return feat;
	}

	public void setAbundant(String abundant) {
		this.abundant = abundant;
	}

	public String getAbundant() {
		return abundant;
	}
}