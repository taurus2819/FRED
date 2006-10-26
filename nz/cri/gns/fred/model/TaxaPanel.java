package nz.cri.gns.fred.model;

public interface TaxaPanel extends Comparable<TaxaPanel> {
    public TaxonomicGroup getTaxonomicGroup();
    public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup);
    public Integer getUserId();
    public void setUserId(Integer userId);
}
