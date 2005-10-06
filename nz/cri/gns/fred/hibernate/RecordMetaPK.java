package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.util.FREDUtil;

/** @author Hibernate CodeGenerator */
public class RecordMetaPK implements Serializable {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer recordId;

    /** identifier field */
    private Long metaId;

    /** full constructor */
    public RecordMetaPK(Integer recordId, Long metaId) {
        this.recordId = recordId;
        this.metaId = metaId;
    }

    /** default constructor */
    public RecordMetaPK() {
    }

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Long getMetaId() {
        return this.metaId;
    }

    public void setMetaId(Long metaId) {
        this.metaId = metaId;
    }

  

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof RecordMetaPK) ) return false;
        RecordMetaPK castOther = (RecordMetaPK) other;
        return FREDUtil.equals(castOther.recordId, recordId, false) && FREDUtil.equals(castOther.metaId, metaId, false);
    }

	public int hashCode() {
		return (metaId + "_" + recordId).hashCode();
	}


}
