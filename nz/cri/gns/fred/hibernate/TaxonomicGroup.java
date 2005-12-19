package nz.cri.gns.fred.hibernate;

import java.util.Set;
import nz.cri.gns.fred.model.TaxaPanel;
import nz.cri.gns.fred.model.Taxon;


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
    private Set<TaxaPanel> taxaPanels;

    /** persistent field */
    private Set<Taxon> taxonomicLookups;

    /** full constructor */
    public TaxonomicGroup(Integer groupId, String name, Set palLists, Set<TaxaPanel> taxaPanels, Set<Taxon> taxonomicLookups) {
        this.groupId = groupId;
        this.name = name;
        this.palLists = palLists;
        this.taxaPanels = taxaPanels;
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

    public Set<TaxaPanel> getTaxaPanels() {
        return this.taxaPanels;
    }

    public void setTaxaPanels(Set<TaxaPanel> taxaPanels) {
        this.taxaPanels = taxaPanels;
    }

    public Set<Taxon> getTaxonomicLookups() {
        return this.taxonomicLookups;
    }

    public void setTaxonomicLookups(Set<Taxon> taxonomicLookups) {
        this.taxonomicLookups = taxonomicLookups;
    }

	public int compareTo(nz.cri.gns.fred.model.TaxonomicGroup arg0) {
		return name.compareTo(arg0.getName());
	}

}
