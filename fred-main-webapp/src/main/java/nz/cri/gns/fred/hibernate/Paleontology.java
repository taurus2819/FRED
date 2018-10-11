package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.RecordUtil;

/**
 * @author Hibernate CodeGenerator
 */
public class Paleontology implements Serializable, nz.cri.gns.fred.model.Paleontology {
    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.hibernate.Paleontology");
    
    private static final long serialVersionUID = 20050818L;

    private Integer recordId;
    private Date identificationDate;
    private String dateRounding;
    private String stageComments;
    private String labNumber;
    private String collectionComments;
    private Record record;
    private LabSection labSection;
    private Stage stage;
    private Set<PaleontologyListEntry> palLists;
    private Set<Person> identifiers;

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Date getIdentificationDate() {
        return this.identificationDate;
    }

    public void setIdentificationDate(Date identificationDate) {
        this.identificationDate = identificationDate;
    }

    public String getDateRounding() {
        return this.dateRounding;
    }

    public void setDateRounding(String dateRounding) {
        this.dateRounding = dateRounding;
    }

    public String getStageComments() {
        return this.stageComments;
    }

    public void setStageComments(String stageComments) {
        this.stageComments = stageComments;
    }

    public String getLabNumber() {
        return this.labNumber;
    }

    public void setLabNumber(String labNumber) {
        this.labNumber = labNumber;
    }

    public String getCollectionComments() {
        return this.collectionComments;
    }

    public void setCollectionComments(String collectionComments) {
        this.collectionComments = collectionComments;
    }

    public nz.cri.gns.fred.model.Record getRecord() {
        return this.record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public LabSection getLabSection() {
        return this.labSection;
    }

    public void setLabSection(LabSection labSection) {
        this.labSection = labSection;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Set<PaleontologyListEntry> getListEntries() {
        return this.palLists;
    }

    public void setListEntries(Set<PaleontologyListEntry> palLists) {
        this.palLists = palLists;
    }

    public Set<Person> getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(Set<Person> identifiers) {
        this.identifiers = identifiers;
    }

    public Date getDate() {
        return getIdentificationDate();
    }

    public Set<Person> getPersons() {
        return getIdentifiers();
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
            if (FREDUtil.equals(thisDate, thatDate, true)) {
                return RecordUtil.getRecordName(this).compareTo(RecordUtil.getRecordName(arg0));
            } else {
                try {
                    return thisDate.compareTo(thatDate);
                } catch (Exception e) {
                    // TODO: This catches NullPointerException!
                    log.log(Level.SEVERE, null, e);
                    if (thisDate != null) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return record.getSample().compareTo(arg0.getRecord().getSample());
    }

    /*public boolean equals(Object o) {
		return recordId != null && o instanceof Paleontology && recordId.equals(((Paleontology)o).recordId);
	}*/
}
