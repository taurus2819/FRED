package nz.cri.gns.fred.hibernate;

import java.util.Set;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.FrUserView;

/** @author Hibernate CodeGenerator */
public class TaxonomicGroup implements nz.cri.gns.fred.model.TaxonomicGroup {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer groupId;

    /** persistent field */
    private String name;

    /** persistent field */
    private Set palLists;
    
    /** persistent field */
    private Set<FrUserView> panelists;

    /** persistent field */
    private Set<Taxon> taxonomicLookups;

    /** full constructor */
    public TaxonomicGroup(Integer groupId, String name, Set palLists, Set<FrUserView> panelists, Set<Taxon> taxonomicLookups) {
        this.groupId = groupId;
        this.name = name;
        this.palLists = palLists;
        this.panelists = panelists;
        this.taxonomicLookups = taxonomicLookups;
    }

    /** default constructor */
    public TaxonomicGroup() {
    }

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

	public int compareTo(nz.cri.gns.fred.model.TaxonomicGroup arg0) {
		return groupId.compareTo(arg0.getGroupId());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(groupId);
	}

	public String getDisplayName() {
		return name;
	}

	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof nz.cri.gns.fred.model.TaxonomicGroup))
			return false;
		return groupId != null && groupId.equals(((nz.cri.gns.fred.model.TaxonomicGroup)o).getGroupId());
	}
}
