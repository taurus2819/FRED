package nz.cri.gns.fred.dataentry;

import java.util.Vector;

public class TaxonomicListException extends Exception {
	
	private Vector taxaGroups;
	
	public TaxonomicListException() {
	}
	
	public TaxonomicListException(Vector taxaGroups) {
		super("New taxonomic entries");
		this.taxaGroups = taxaGroups;
	}
	
	public Vector getTaxaGroups() {
		return taxaGroups;
	}
}
