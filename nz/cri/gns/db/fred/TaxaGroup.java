package nz.cri.gns.db.fred;

import java.util.Vector;

public class TaxaGroup {

	private String groupName;
	private Integer groupId;
	private Vector taxaList;

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
	
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setTaxaList(Vector taxaList) {
		this.taxaList = taxaList;
	}

	public Vector getTaxaList() {
		return taxaList;
	}
}