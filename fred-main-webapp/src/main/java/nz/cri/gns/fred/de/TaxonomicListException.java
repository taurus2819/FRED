package nz.cri.gns.fred.de;

import java.util.Set;

import nz.cri.gns.fred.model.PaleontologyListEntry;

public class TaxonomicListException extends DataInputException {
	
	private static final long serialVersionUID = 20050818L;
    
    private Set<PaleontologyListEntry> taxaList;
	
	public TaxonomicListException() {
	}
	
	public TaxonomicListException(Set<PaleontologyListEntry> taxaList) {
		super("Taxonomic List", "Names that have not been previously recorded found");
		this.taxaList = taxaList;
	}
	
	public Set<PaleontologyListEntry> getTaxaList() {
		return taxaList;
	}
}
