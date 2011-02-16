package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.RecordUtil;

/** @author Hibernate CodeGenerator */
public class Adoption implements Serializable, nz.cri.gns.fred.model.Adoption {

	private static final long serialVersionUID = 20050818L;
	
    private Integer recordId;
    private Date adoptionDate;
    private String dateRounding;
    private String comments;
    private Record record;
    private Stage stage;
    private Set<Person> adoptors;

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

    public Record getRecord() {
        return this.record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Set<Person> getAdoptors() {
        return this.adoptors;
    }

    public void setAdoptors(Set<Person> adoptors) {
        this.adoptors = adoptors;
    }

	public Date getDate() {
		return getAdoptionDate();
	}

	public Set<Person> getPersons() {
		return getAdoptors();
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

	/*public boolean equals(Object o) {
		return recordId != null && o instanceof Adoption && recordId.equals(((Adoption)o).getRecordId());
	}
	
	public int hashCode() {
		return 764 * recordId;
	}*/
}
