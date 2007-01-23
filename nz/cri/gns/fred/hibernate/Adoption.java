package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.hibernate.dao.AssignedKeyed;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.RecordUtil;

/** @author Hibernate CodeGenerator */
public class Adoption implements Serializable, nz.cri.gns.fred.model.Adoption, AssignedKeyed {

	private static final long serialVersionUID = 20050818L;
	
	/** identifier field */
    private Integer recordId;

    /** nullable persistent field */
    private Date adoptionDate;

    /** nullable persistent field */
    private String dateRounding;

    /** nullable persistent field */
    private String comments;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Record record;

    /** persistent field */
    private nz.cri.gns.fred.model.Stage stage;

    /** persistent field */
    private Set<Person> adopters;

    /** full constructor */
    public Adoption(Integer recordId, Date adoptionDate, String dateRounding, String comments, nz.cri.gns.fred.hibernate.Record record, nz.cri.gns.fred.hibernate.Stage stage, Set<Person> adopters) {
        this.recordId = recordId;
        this.adoptionDate = adoptionDate;
        this.dateRounding = dateRounding;
        this.comments = comments;
        this.record = record;
        this.stage = stage;
        this.adopters = adopters;
    }

    /** default constructor */
    public Adoption() {
    }

    /** minimal constructor */
    public Adoption(Integer recordId, nz.cri.gns.fred.hibernate.Stage stage, Set<Person> adopters) {
        this.recordId = recordId;
        this.stage = stage;
        this.adopters = adopters;
    }

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Date getAdoptionDate() {
        return this.adoptionDate;
    }

    public void setAdoptionDate(Date adoptionDate) {
        this.adoptionDate = adoptionDate;
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

    public nz.cri.gns.fred.model.Record getRecord() {
        return this.record;
    }

    public void setRecord(nz.cri.gns.fred.model.Record record) {
        this.record = record;
        if (record == null)
        	this.recordId = null;
        else
        	this.recordId = record.getRecordId();
    }

    public nz.cri.gns.fred.model.Stage getStage() {
        return this.stage;
    }

    public void setStage(nz.cri.gns.fred.model.Stage stage) {
        this.stage = stage;
    }

    public Set<Person> getAdopters() {
        return this.adopters;
    }

    public void setAdopters(Set<Person> adopters) {
        this.adopters = adopters;
    }

	public Date getDate() {
		return getAdoptionDate();
	}

	public Set<Person> getPersons() {
		return getAdopters();
	}

	public void updateKey() {
		this.recordId = this.record.getRecordId();
	}

	public boolean isUnsaved() {
		return recordId == null;
	}

	public int compareTo(RecordDetails arg0) {
		if (record.getSample().equals(arg0.getRecord().getSample())) {
			Date thisDate = this.getDate();
			Date thatDate = arg0.getDate();
			if (FREDUtil.equals(thisDate, thatDate, true))
				return RecordUtil.getRecordName(this).compareTo(RecordUtil.getRecordName(arg0));
			else try {
				return thisDate.compareTo(thatDate);
			} catch (Exception e) {
				if (thisDate != null)
					return 1;
				else
					return -1;
			}
		}
		return record.getSample().compareTo(arg0.getRecord().getSample());
	}

}
