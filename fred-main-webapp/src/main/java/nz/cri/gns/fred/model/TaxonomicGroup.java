package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface TaxonomicGroup extends Comparable<TaxonomicGroup>, NameableAndIdentifiable {
    public String getName();
    public void setName(String name);
    public Integer getGroupId();
    public void setGroupId(Integer groupId);
    public Set<FrUserView> getPanelists();
    public void setPanelists(Set<FrUserView> taxaPanelists);
    public Set<Taxon> getTaxonomicLookups();
    public void setTaxonomicLookups(Set<Taxon> taxonomicLookups);
    public TaxonomicGroup getParent();
    public void setParent(TaxonomicGroup parent);
    public Set<TaxonomicGroup> getChildren();
    public void setChildren(Set<TaxonomicGroup> children);
}
