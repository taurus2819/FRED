package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;

import nz.cri.gns.fred.util.FREDUtil;


/** @author Hibernate CodeGenerator */
public class SentTo implements nz.cri.gns.fred.model.SentTo, Serializable {

    private static final long serialVersionUID = 20050818L;

	/** nullable persistent field */
    private Date sentDate;

    /** nullable persistent field */
    private String dateRounding;

    /** nullable persistent field */
    private Integer labId;

    /** nullable persistent field */
    private String comments;

    /** persistent field */
    private nz.cri.gns.fred.model.FossilGroup fossilGroup;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Person person;

    /** full constructor */
    public SentTo(Date sentDate, String dateRounding, Integer labId, String comments, nz.cri.gns.fred.hibernate.FossilGroup fossilGroup, nz.cri.gns.fred.model.Person person) {
        this.sentDate = sentDate;
        this.dateRounding = dateRounding;
        this.labId = labId;
        this.comments = comments;
        this.fossilGroup = fossilGroup;
        this.person = person;
    }

    /** default constructor */
    public SentTo() {
    }

    /** minimal constructor */
    public SentTo(nz.cri.gns.fred.model.FossilGroup fossilGroup) {
        this.fossilGroup = fossilGroup;
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

    public nz.cri.gns.fred.model.FossilGroup getFossilGroup() {
        return this.fossilGroup;
    }

    public void setFossilGroup(nz.cri.gns.fred.model.FossilGroup fossilGroup) {
        this.fossilGroup = fossilGroup;
    }

    public nz.cri.gns.fred.model.Person getPerson() {
        return this.person;
    }

    public void setPerson(nz.cri.gns.fred.model.Person person) {
        this.person = person;
    }

    public String toString() {
        return super.toString() + "{" + getFossilGroup() + "}";
    }

	public String getDisplayName() {
		return (person == null) ? "" : person.getDisplayName();
	}

	public Object clone() { 
    	try {
    		SentTo sento = (SentTo) super.clone();
    		return sento;
    	} catch (CloneNotSupportedException e) {
    		//But it is!
    		return null;
    	}
    }
	
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof SentTo))
			return false;
		SentTo sentTo = (SentTo)o;
		return FREDUtil.equals(sentDate, sentTo.getSentDate(), true)
			&& FREDUtil.equals(person, sentTo.getPerson(), true)
			&& FREDUtil.equals(labId, sentTo.getLabId(), true)
			&& FREDUtil.equals(fossilGroup, sentTo.getFossilGroup(), true)
			&& FREDUtil.equals(dateRounding, sentTo.getDateRounding(), true)
			&& FREDUtil.equals(comments, sentTo.getComments(), true)
			;
	}
	
	public int hashCode() {
		return fossilGroup.hashCode();
	}
}
