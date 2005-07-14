package nz.cri.gns.fred.model;

import java.io.Serializable;

/**
 * @author iainm
 */
public interface TaxaPanel extends Serializable, Comparable {
    public TaxonomicGroup getTaxonomicGroup();
    public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup);

    public Integer getUserId();
    public void setUserId(Integer userId);
}
