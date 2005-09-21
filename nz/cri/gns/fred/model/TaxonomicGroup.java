package nz.cri.gns.fred.model;

import java.io.Serializable;

/**
 * @author iainm
 */
public interface TaxonomicGroup extends Serializable, Comparable<TaxonomicGroup> {

	public String getName();

	public Integer getGroupId();
}
