package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;
import nz.cri.gns.fred.model.Paleontology;

import nz.cri.gns.fred.model.Lab;

/** @author Hibernate CodeGenerator */
public class LabSection implements Serializable, nz.cri.gns.fred.model.LabSection {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer labSectionId;

    /** nullable persistent field */
    private Lab lab;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String code;

    /** nullable persistent field */
    private String closed;

    /** persistent field */
    private Set<Paleontology> paleontologies;

    /** full constructor */
    public LabSection(Lab lab, String name, String code, String closed, Set<Paleontology> paleontologies) {
        this.lab = lab;
        this.name = name;
        this.code = code;
        this.closed = closed;
        this.paleontologies = paleontologies;
    }

    /** default constructor */
    public LabSection() {
    }

    /** minimal constructor */
    public LabSection(Set<Paleontology> paleontologies) {
        this.paleontologies = paleontologies;
    }

    public final Integer getLabSectionId() {
        return this.labSectionId;
    }

    public void setLabSectionId(Integer labSectionId) {
        this.labSectionId = labSectionId;
    }

    public final Lab getLab() {
        return this.lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public final String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public final String getClosed() {
        return this.closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public final Set<Paleontology> getPaleontologies() {
        return this.paleontologies;
    }

    public void setPaleontologies(Set<Paleontology> paleontologies) {
        this.paleontologies = paleontologies;
    }

    @Override
	public String toString() {
    	return lab.getName() + " " + code;
    }
    
	public int compareTo(nz.cri.gns.fred.model.LabSection arg0) {
		return (lab.getName() + code).compareTo(arg0.getLab().getName() + arg0.getCode());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(labSectionId);
	}

	public String getDisplayName() {
		return lab.getName() + " " + code;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof LabSection && ((LabSection)o).labSectionId.equals(labSectionId);
	}
	
	@Override
	public int hashCode() {
		return 735 * labSectionId;
	}
}
