package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.RecordMeta;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.RecordUtil;

/** @author Hibernate CodeGenerator */
public class Record implements Serializable, nz.cri.gns.fred.model.Record, Comparable<nz.cri.gns.fred.model.Record> {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer recordId;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Paleontology paleontology;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.Adoption adoption;

    /** persistent field */
    private nz.cri.gns.fred.model.Sample sample;

    /** persistent field */
    private nz.cri.gns.fred.model.Audit auditTable;

    /** persistent field */
    private Set<RecordMeta> recordMetas;
    
    /** full constructor */
    public Record(nz.cri.gns.fred.hibernate.Paleontology paleontology, nz.cri.gns.fred.hibernate.Adoption adoption, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.AuditTable auditTable, Set<RecordMeta> recordMetas) {
        this.paleontology = paleontology;
        this.adoption = adoption;
        this.sample = sample;
        this.auditTable = auditTable;
        this.recordMetas = recordMetas;
    }

    /** default constructor */
    public Record() {
    }

    /** minimal constructor */
    public Record(nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.AuditTable auditTable, Set<RecordMeta> recordMetas) {
        this.sample = sample;
        this.auditTable = auditTable;
        this.recordMetas = recordMetas;
    }

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
/*        if (paleontology != null) {
        	paleontology.setRecordId(recordId);
        }
        if (adoption != null) {
        	adoption.setRecordId(recordId);
        }*/
    }

    public nz.cri.gns.fred.model.Paleontology getPaleontology() {
        return this.paleontology;
    }

    public void setPaleontology(nz.cri.gns.fred.model.Paleontology paleontology) {
        this.paleontology = paleontology;
    }

    public nz.cri.gns.fred.model.Adoption getAdoption() {
        return this.adoption;
    }

    public void setAdoption(nz.cri.gns.fred.model.Adoption adoption) {
        this.adoption = adoption;
    }

    public nz.cri.gns.fred.model.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.model.Sample sample) {
        this.sample = sample;
    }

    public nz.cri.gns.fred.model.Audit getAudit() {
        return this.auditTable;
    }

    public void setAudit(nz.cri.gns.fred.model.Audit auditTable) {
        this.auditTable = auditTable;
    }

    public Set<RecordMeta> getRecordMetas() {
        return this.recordMetas;
    }

    public void setRecordMetas(Set<RecordMeta> recordMetas) {
        this.recordMetas = recordMetas;
    }
    
	public int compareTo(nz.cri.gns.fred.model.Record arg0) {
		//first compare record types
		String thisType = RecordUtil.getRecordType(this);
		String thatType = RecordUtil.getRecordType(arg0);
		if (!thisType.equals(thatType))
			return thisType.compareTo(thatType);
		
		//record types match so compare date and then person
		RecordDetails thisDetails = RecordUtil.getRecordDetails(this);
		RecordDetails thatDetails = RecordUtil.getRecordDetails(arg0);
		
		Date thisDate = thisDetails.getDate();
		Date thatDate = thatDetails.getDate();
		if (FREDUtil.equals(thisDate, thatDate, true)) {
			return RecordUtil.getRecordName(thisDetails).compareTo(RecordUtil.getRecordName(thatDetails));
		}
		else try {
			return thisDate.compareTo(thatDate);
		} catch (Exception e) {
			if (thisDate != null)
				return -1;
			else
				return 1;
		}
	}
	
}
