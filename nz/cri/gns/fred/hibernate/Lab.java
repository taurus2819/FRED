package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.SentTo;

/** @author Hibernate CodeGenerator */
public class Lab implements Serializable, nz.cri.gns.fred.model.Lab {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer labId;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String address;

    /** nullable persistent field */
    private String country;

    /** persistent field */
    private Set<LabSection> sections;
    
    /** persistent field */
    private Set<SentTo> sentTos;

    /** full constructor */
    public Lab(Integer labId, String name, String address, String country, Set<LabSection> sections, Set<SentTo> sentTos) {
        this.labId = labId;
        this.name = name;
        this.address = address;
        this.country = country;
        this.sections = sections;
        this.sentTos = sentTos;
    }

    /** default constructor */
    public Lab() {
    }

    /** minimal constructor */
    public Lab(Set<LabSection> sections) {
        this.sections = sections;
    }

   public Integer getLabId() {
        return this.labId;
    }

    public void setLabId(Integer labId) {
        this.labId = labId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<LabSection> getSections() {
        return this.sections;
    }

    public void setSections(Set<LabSection> sections) {
        this.sections = sections;
    }

	public Set<SentTo> getSentTos() {
		return sentTos;
	}
	
	public void setSentTos(Set<SentTo> sentTos) {
		this.sentTos = sentTos;
	}

	public int compareTo(nz.cri.gns.fred.model.Lab arg0) {
		return name.compareTo(arg0.getName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(labId);
	}

	public String getDisplayName() {
		return name;
	}

	public boolean equals(Object o) {
		return o instanceof Lab && ((Lab)o).labId.equals(labId);
	}
	
	public int hashCode() {
		return 825 * labId;
	}
}
