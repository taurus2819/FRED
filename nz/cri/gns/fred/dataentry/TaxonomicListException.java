package nz.cri.gns.fred.dataentry;

import java.util.Vector;

public class TaxonomicListException extends Exception {
	
	private Vector taxaList;
	
	public TaxonomicListException() {
	}
	
	public TaxonomicListException(Vector taxaList) {
		super("Bad taxonomic entries");
		this.taxaList = taxaList;
	}
	
	public Vector getTaxaList() {
		return taxaList;
	}
}
