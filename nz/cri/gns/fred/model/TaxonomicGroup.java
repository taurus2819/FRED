package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface TaxonomicGroup extends Comparable<TaxonomicGroup>, NameableAndIdentifiable {
	public String getName();
	public void setName(String name);
	public Integer getGroupId();
	public void setGroupId(Integer groupId);
    public Set<TaxaPanel> getTaxaPanels();
    public void setTaxaPanels(Set<TaxaPanel> taxaPanels);
    public Set<Taxon> getTaxonomicLookups();
    public void setTaxonomicLookups(Set<Taxon> taxonomicLookups);
}
