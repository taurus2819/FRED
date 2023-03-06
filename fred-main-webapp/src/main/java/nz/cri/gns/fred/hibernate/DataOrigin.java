package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;

/** @author Hibernate CodeGenerator */
public class DataOrigin implements nz.cri.gns.fred.model.DataOrigin, Serializable {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private Integer originId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private Set<Audit> audits;

    /** full constructor */
    public DataOrigin(Integer originId, String name, String description, Set<Audit> audits) {
        this.originId = originId;
        this.name = name;
        this.description = description;
        this.audits = audits;
    }

    /** default constructor */
    public DataOrigin() {
    }

    /** minimal constructor */
    public DataOrigin(Integer originId, String name, Set<Audit> audits) {
        this.originId = originId;
        this.name = name;
        this.audits = audits;
    }

    public Integer getOriginId() {
        return this.originId;
    }

    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Audit> getAudits() {
        return this.audits;
    }

    public void setAudits(Set<Audit> audits) {
        this.audits = audits;
    }

    @Override
	public String toString() {
        return name;
    }

	@Override
	public boolean equals(Object o) {
		return o instanceof DataOrigin && ((DataOrigin)o).getOriginId().equals(originId);
	}
	
	@Override
	public int hashCode() {
		return 527 * originId;
	}
}
