package nz.cri.gns.fred.data;

import java.util.Vector;

public class TaxaGroup {

	private String groupName;
	private Integer groupID;
	private Vector taxaList;

	public TaxaGroup() {
	}

	public TaxaGroup(String groupName) {
		this.groupName = groupName;
	}

	public String toString() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	public Integer getGroupID() {
		return groupID;
	}

	public void setTaxaList(Vector taxaList) {
		this.taxaList = taxaList;
	}

	public Vector getTaxaList() {
		return taxaList;
	}
}