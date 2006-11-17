package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.RecordMeta;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.RecordUtil;

/** @author Hibernate CodeGenerator */
public class Record implements Serializable, nz.cri.gns.fred.model.Record {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer recordId;

    /** nullable persistent field */
    private Paleontology paleontology;

    /** nullable persistent field */
    private Adoption adoption;

    /** persistent field */
    private Sample sample;

    /** persistent field */
    private Audit audit;
    
    /** persistent field */
    private Audit palListAudit;

    /** persistent field */
    private Set<RecordMeta> recordMetas;
    
    /** full constructor */
    public Record(Paleontology paleontology, Adoption adoption, Sample sample, AuditTable audit, AuditTable palListAudit, Set<RecordMeta> recordMetas) {
        this.paleontology = paleontology;
        this.adoption = adoption;
        this.sample = sample;
        this.audit = audit;
        this.recordMetas = recordMetas;
    }

    /** default constructor */
    public Record() {
    }

    /** minimal constructor */
    public Record(Sample sample, AuditTable auditTable, Set<RecordMeta> recordMetas) {
        this.sample = sample;
        this.audit = auditTable;
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

    public Paleontology getPaleontology() {
        return this.paleontology;
    }

    public void setPaleontology(Paleontology paleontology) {
        this.paleontology = paleontology;
    }

    public Adoption getAdoption() {
        return this.adoption;
    }

    public void setAdoption(Adoption adoption) {
        this.adoption = adoption;
    }

    public Sample getSample() {
        return this.sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Audit getAudit() {
        return this.audit;
    }

    public void setAudit(Audit auditTable) {
        this.audit = auditTable;
    }

	public Audit getPalListAudit() {
		return palListAudit;
	}
	
    public void setPalListAudit(Audit palListAudit) {
		this.palListAudit = palListAudit;
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
