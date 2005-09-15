package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

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
    private Set paleontologies;

    /** full constructor */
    public LabSection(Lab lab, String name, String code, String closed, Set paleontologies) {
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
    public LabSection(Set paleontologies) {
        this.paleontologies = paleontologies;
    }

    public Integer getLabSectionId() {
        return this.labSectionId;
    }

    public void setLabSectionId(Integer labSectionId) {
        this.labSectionId = labSectionId;
    }

    public Lab getLab() {
        return this.lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClosed() {
        return this.closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public Set getPaleontologies() {
        return this.paleontologies;
    }

    public void setPaleontologies(Set paleontologies) {
        this.paleontologies = paleontologies;
    }

}
