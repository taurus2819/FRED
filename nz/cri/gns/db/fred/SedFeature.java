package nz.cri.gns.db.fred;

public class SedFeature {

	private int sedFeatureId;
	private String sedFeature;
	private String abundant;

	public SedFeature() {
	}

	public void setSedFeatureId(int sedFeatureId) {
		this.sedFeatureId = sedFeatureId;
	}

	public int getSedFeatureId() {
		return sedFeatureId;
	}

	public void setSedFeature(String sedFeature) {
		this.sedFeature = sedFeature;
	}

	public String getSedFeature() {
		return sedFeature;
	}

	public void setAbundant(String abundant) {
		this.abundant = abundant;
	}

	public String getAbundant() {
		return abundant;
	}
}