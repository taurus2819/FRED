package nz.cri.gns.db.fred.data;

public class SampleHeader {

	private int sampleID;
	private String sampleName;
	private String drillholeDepth;

	public SampleHeader(int sampleID, String sampleName, String drillholeDepth) {
		this.sampleID = sampleID;
		this.sampleName = sampleName;
		this.drillholeDepth = drillholeDepth;
	}

	public void setSampleID(int sampleID) {
		this.sampleID = sampleID;
	}

	public int getSampleID() {
		return sampleID;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setDrillholeDepth(String drillholeDepth) {
		this.drillholeDepth = drillholeDepth;
	}

	public String getDrillholeDepth() {
		return drillholeDepth;
	}

}
