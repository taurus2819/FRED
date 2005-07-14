package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;

/** @author Hibernate CodeGenerator */
public class SentTo implements Serializable {

    /** identifier field */
    private nz.cri.gns.fred.hibernate.SentToPK comp_id;

    /** nullable persistent field */
    private Date sentDate;

    /** nullable persistent field */
    private String dateRounding;

    /** nullable persistent field */
    private Integer labId;

    /** nullable persistent field */
    private String comments;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.Sample sample;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.FossilGroup fossilGroup;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Person person;

    /** full constructor */
    public SentTo(nz.cri.gns.fred.hibernate.SentToPK comp_id, Date sentDate, String dateRounding, Integer labId, String comments, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.FossilGroup fossilGroup, nz.cri.gns.fred.hibernate.Person person) {
        this.comp_id = comp_id;
        this.sentDate = sentDate;
        this.dateRounding = dateRounding;
        this.labId = labId;
        this.comments = comments;
        this.sample = sample;
        this.fossilGroup = fossilGroup;
        this.person = person;
    }

    /** default constructor */
    public SentTo() {
    }

    /** minimal constructor */
    public SentTo(nz.cri.gns.fred.hibernate.SentToPK comp_id, nz.cri.gns.fred.hibernate.Person person) {
        this.comp_id = comp_id;
        this.person = person;
    }

    public nz.cri.gns.fred.hibernate.SentToPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.SentToPK comp_id) {
        this.comp_id = comp_id;
    }

    public Date getSentDate() {
        return this.sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getDateRounding() {
        return this.dateRounding;
    }

    public void setDateRounding(String dateRounding) {
        this.dateRounding = dateRounding;
    }

    public Integer getLabId() {
        return this.labId;
    }

    public void setLabId(Integer labId) {
        this.labId = labId;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public nz.cri.gns.fred.hibernate.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.hibernate.Sample sample) {
        this.sample = sample;
    }

    public nz.cri.gns.fred.hibernate.FossilGroup getFossilGroup() {
        return this.fossilGroup;
    }

    public void setFossilGroup(nz.cri.gns.fred.hibernate.FossilGroup fossilGroup) {
        this.fossilGroup = fossilGroup;
    }

    public nz.cri.gns.fred.hibernate.Person getPerson() {
        return this.person;
    }

    public void setPerson(nz.cri.gns.fred.hibernate.Person person) {
        this.person = person;
    }


    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof SentTo) ) return false;
        SentTo castOther = (SentTo) other;
        return castOther.comp_id.equals(comp_id);
    }
	public int hashCode() {
		return comp_id.hashCode();
	}


}
