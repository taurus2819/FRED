package nz.cri.gns.fred.data;

import java.util.Vector;

public class SampleHeader {

	private int sampleID;
	private String sampleName;
	private String drillholeDepth;
	private Vector workingRecords;

	public SampleHeader(int sampleID, String sampleName, String drillholeDepth, Vector workingRecords) {
		this.sampleID = sampleID;
		this.sampleName = sampleName;
		this.drillholeDepth = drillholeDepth;
		this.workingRecords = workingRecords;
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

	public void setWorkingRecords(Vector workingRecords) {
		this.workingRecords = workingRecords;
	}

	public Vector getWorkingRecords() {
		return workingRecords;
	}

	public int getWorkingRecordCount() {
		return workingRecords.size();
	}
}
