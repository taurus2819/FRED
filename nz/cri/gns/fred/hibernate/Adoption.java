package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
/** @author Hibernate CodeGenerator */
public class Adoption implements Serializable, nz.cri.gns.fred.model.Adoption {

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
    private Set adopters;

    /** full constructor */
    public Adoption(Integer recordId, Date adoptionDate, String dateRounding, String comments, nz.cri.gns.fred.hibernate.Record record, nz.cri.gns.fred.hibernate.Stage stage, Set adopters) {
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
    public Adoption(Integer recordId, nz.cri.gns.fred.hibernate.Stage stage, Set adopters) {
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
    }

    public nz.cri.gns.fred.model.Stage getStage() {
        return this.stage;
    }

    public void setStage(nz.cri.gns.fred.model.Stage stage) {
        this.stage = stage;
    }

    public Set getAdopters() {
        return this.adopters;
    }

    public void setAdopters(Set adopters) {
        this.adopters = adopters;
    }

	public Date getDate() {
		return getAdoptionDate();
	}

	public Set getPersons() {
		return getAdopters();
	}
}
