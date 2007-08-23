package nz.cri.gns.fred.model;

import java.util.Set;

public class UnsavedListEntry implements PaleontologyListEntry {

	private String comments;
	private Integer specimenCount;
	private String specimenCoords;
	private String taxonomicName;
	private TaxonomicGroup taxonomicGroup;
	private Paleontology paleontology;
	private Taxon taxon;
	private Set<PalListMeta> palListMetas;
	
	public Integer getPalListId() {
		return null;
	}

	public void setPalListId(Integer palListId) {
		throw new IllegalStateException("Not savable");
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Paleontology getPaleontology() {
		return paleontology;
	}

	public void setPaleontology(Paleontology paleontology) {
		this.paleontology = paleontology;
	}

	public String getSpecimenCoords() {
		return specimenCoords;
	}

	public void setSpecimenCoords(String specimenCoords) {
		this.specimenCoords = specimenCoords;
	}

	public Integer getSpecimenCount() {
		return specimenCount;
	}

	public void setSpecimenCount(Integer specimenCount) {
		this.specimenCount = specimenCount;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public TaxonomicGroup getTaxonomicGroup() {
		return taxonomicGroup;
	}

	public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup) {
		this.taxonomicGroup = taxonomicGroup;
	}

	public String getTaxonomicName() {
		return taxonomicName;
	}

	public void setTaxonomicName(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}

	public Set<PalListMeta> getPalListMetas() {
		return palListMetas;
	}

	public void setPalListMetas(Set<PalListMeta> palListMetas) {
		this.palListMetas = palListMetas;
	}

	public int compareTo(PaleontologyListEntry arg0) {
		if (taxonomicName == null)
			return 1;
		if (arg0.getTaxonomicName() == null)
			return -1;
		return taxonomicName.compareTo(arg0.getTaxonomicName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(getPalListId());
	}

	public String getDisplayName() {
		return taxonomicName;
	}
}