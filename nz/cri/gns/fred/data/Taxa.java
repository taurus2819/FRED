package nz.cri.gns.fred.data;

public class Taxa {

	private String taxonomicName;
	private Integer taxaID;
	private String author;
	private Integer specimenCount;
	private String specimenCoords;
	private String comments;
	private Integer groupID;
	private String groupName;

	public Taxa() {
	}

	public Taxa(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}

	public String toString() {
		return this.taxonomicName;
	}

	public void setTaxonomicName(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}

	public String getTaxonomicName() {
		return taxonomicName;
	}

	public void setTaxaID(Integer taxaID) {
		this.taxaID = taxaID;
	}

	public Integer getTaxaID() {
		return taxaID;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setSpecimenCount(Integer specimenCount) {
		this.specimenCount = specimenCount;
	}

	public Integer getSpecimenCount() {
		return specimenCount;
	}

	public void setSpecimenCoords(String specimenCoords) {
		this.specimenCoords = specimenCoords;
	}

	public String getSpecimenCoords() {
		return specimenCoords;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	public Integer getGroupID() {
		return groupID;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}
}
