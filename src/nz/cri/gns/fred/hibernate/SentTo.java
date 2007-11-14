package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;

import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.Lab;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FREDUtil;


/** @author Hibernate CodeGenerator */
public class SentTo implements nz.cri.gns.fred.model.SentTo, Serializable {

    private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer sentToId;
    
	/** nullable persistent field */
    private Date sentDate;

    /** nullable persistent field */
    private String dateRounding;

    /** nullable persistent field */
    private String comments;

    /** persistent field */
    private Sample sample;
    
    /** nullable persistent field */
    private FossilGroup fossilGroup;

    /** nullable persistent field */
    private Person person;
    
    /** nullable persistent field */
    private Lab lab;
    
    /** full constructor */
    public SentTo(Date sentDate, String dateRounding, String comments, Sample sample, FossilGroup fossilGroup, Person person, Lab lab) {
        this.sentDate = sentDate;
        this.dateRounding = dateRounding;
        this.comments = comments;
        this.sample = sample;
        this.fossilGroup = fossilGroup;
        this.person = person;
        this.setLab(lab);
    }

    /** default constructor */
    public SentTo() {
    }

    /** minimal constructor */
    public SentTo(nz.cri.gns.fred.model.Sample sample) {
    	this.sample = sample;
    }

    public Integer getSentToId() {
        return this.sentToId;
    }

    public void setSentToId(Integer sentToId) {
        this.sentToId = sentToId;
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

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Sample getSample() {
        return this.sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }
    
    public FossilGroup getFossilGroup() {
        return this.fossilGroup;
    }

    public void setFossilGroup(FossilGroup fossilGroup) {
        this.fossilGroup = fossilGroup;
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

	public Lab getLab() {
		return lab;
	}
	
    public void setLab(Lab lab) {
		this.lab = lab;
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
			&& FREDUtil.equals(lab, sentTo.getLab(), true)
			&& FREDUtil.equals(fossilGroup, sentTo.getFossilGroup(), true)
			&& FREDUtil.equals(dateRounding, sentTo.getDateRounding(), true)
			&& FREDUtil.equals(comments, sentTo.getComments(), true)
			;
	}
	
	public int hashCode() {
		return (fossilGroup == null) ? 0 : fossilGroup.hashCode();
	}
}
