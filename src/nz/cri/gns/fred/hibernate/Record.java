package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.RecordDetails;
import nz.cri.gns.fred.model.MetaCat;
import nz.cri.gns.fred.model.RecordStageView;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.RecordUtil;

/** @author Hibernate CodeGenerator */
public class Record implements Serializable, nz.cri.gns.fred.model.Record {

    private static final long serialVersionUID = 20050818L;

    private Integer recordId;
    private Paleontology paleontology;
    private Adoption adoption;
    private Sample sample;
    private Audit audit;
    private Audit palListAudit;
    private Set<MetaCat> metaCats;
    private Set<RecordStageView> recordStageViews;

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
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

	public Set<MetaCat> getMetaCats() {
        return this.metaCats;
    }

    public void setMetaCats(Set<MetaCat> metaCats) {
        this.metaCats = metaCats;
    }
    
	public void setRecordStageViews(Set<RecordStageView> recordStageViews) {
		this.recordStageViews = recordStageViews;
	}

	public Set<RecordStageView> getRecordStageViews() {
		return recordStageViews;
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

	@Override
	public String toString() {
		return sample.toString() + ": " + RecordUtil.getRecordName(this);
	}
	
	/*public boolean equals(Object o) {
		return o instanceof Record && ((Record)o).recordId.equals(recordId);
	}
	
	public int hashCode() {
		return 845 * recordId;
	}*/
}
