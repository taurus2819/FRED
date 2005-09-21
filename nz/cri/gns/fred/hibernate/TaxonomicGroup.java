package nz.cri.gns.fred.hibernate;

import java.util.Set;


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
    private Set taxaPanels;

    /** persistent field */
    private Set taxonomicLookups;

    /** full constructor */
    public TaxonomicGroup(Integer groupId, String name, Set palLists, Set taxaPanels, Set taxonomicLookups) {
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

    public Set getTaxaPanels() {
        return this.taxaPanels;
    }

    public void setTaxaPanels(Set taxaPanels) {
        this.taxaPanels = taxaPanels;
    }

    public Set getTaxonomicLookups() {
        return this.taxonomicLookups;
    }

    public void setTaxonomicLookups(Set taxonomicLookups) {
        this.taxonomicLookups = taxonomicLookups;
    }

	public int compareTo(nz.cri.gns.fred.model.TaxonomicGroup arg0) {
		return name.compareTo(arg0.getName());
	}

}
