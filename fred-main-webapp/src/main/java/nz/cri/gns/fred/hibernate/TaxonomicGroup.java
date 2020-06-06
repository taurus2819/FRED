package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.FrUserView;

public class TaxonomicGroup implements Serializable, nz.cri.gns.fred.model.TaxonomicGroup {

    private static final long serialVersionUID = 20050818L;

    private Integer groupId;
    private String name;
    private Set palLists;
    private Set<FrUserView> panelists;
    private Set<Taxon> taxonomicLookups;
    private nz.cri.gns.fred.model.TaxonomicGroup parent;
    private Set<nz.cri.gns.fred.model.TaxonomicGroup> children;

    public Integer getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public Set getPalLists() {
        return this.palLists;
    }

    public void setPalLists(Set palLists) {
        this.palLists = palLists;
    }

    public Set<FrUserView> getPanelists() {
        return this.panelists;
    }

    public void setPanelists(Set<FrUserView> taxaPanels) {
        this.panelists = taxaPanels;
    }

    public Set<Taxon> getTaxonomicLookups() {
        return this.taxonomicLookups;
    }

    public void setTaxonomicLookups(Set<Taxon> taxonomicLookups) {
        this.taxonomicLookups = taxonomicLookups;
    }

    @Override
    public nz.cri.gns.fred.model.TaxonomicGroup getParent() {
        return this.parent;
    }

    @Override
    public void setParent(nz.cri.gns.fred.model.TaxonomicGroup parent) {
        this.parent = parent;
    }
    
    public void setChildren(Set<nz.cri.gns.fred.model.TaxonomicGroup> children) {
        this.children = children;
    }

    public Set<nz.cri.gns.fred.model.TaxonomicGroup> getChildren() {
        return this.children;
    }
    
    public Set<nz.cri.gns.fred.model.TaxonomicGroup> getAllDescendants()    {
        Set<nz.cri.gns.fred.model.TaxonomicGroup> descendants = new HashSet<>();
        for(nz.cri.gns.fred.model.TaxonomicGroup child : this.children) {
            descendants.add(child);
            if(child.getChildren() != null) {
                descendants.addAll(child.getAllDescendants());
            }
        }
        return descendants;
    }
    
    public int compareTo(nz.cri.gns.fred.model.TaxonomicGroup arg0) {
            return groupId.compareTo(arg0.getGroupId());
    }

    @Override
    public String toString() {
            return name;
    }

    public String getUniqueIdentifier() {
            return String.valueOf(groupId);
    }

    public String getDisplayName() {
            return name;
    }

    @Override
    public int hashCode() {
            return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
            if (!(o instanceof nz.cri.gns.fred.model.TaxonomicGroup))
                    return false;
            return groupId != null && groupId.equals(((nz.cri.gns.fred.model.TaxonomicGroup)o).getGroupId());
    }
}
