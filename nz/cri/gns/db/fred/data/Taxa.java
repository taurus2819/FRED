package nz.cri.gns.db.fred.data;

public class Taxa {

	private String taxonomicName;
	private Integer taxaId;
	private String author;
	private Integer specimenCount;
	private String specimenCoords;
	private String comments;

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

	public void setTaxaId(Integer taxaId) {
		this.taxaId = taxaId;
	}

	public Integer getTaxaId() {
		return taxaId;
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
}
