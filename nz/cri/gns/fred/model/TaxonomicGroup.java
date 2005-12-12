package nz.cri.gns.fred.model;

import java.io.Serializable;
import java.util.Set;

/**
 * @author iainm
 */
public interface TaxonomicGroup extends Serializable, Comparable<TaxonomicGroup> {

	public String getName();

	public Integer getGroupId();
	
    public Set getTaxaPanels();

    public void setTaxaPanels(Set taxaPanels);
}
