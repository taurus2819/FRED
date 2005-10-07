package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKeyed;
import nz.cri.gns.fred.model.Record;

/** @author Hibernate CodeGenerator */
public class RecordMeta implements Serializable, nz.cri.gns.fred.model.RecordMeta, CompositeKeyed  {

    private static final long serialVersionUID = 20050818L;

   /** identifier field */
    private nz.cri.gns.fred.hibernate.RecordMetaPK comp_id;

    private boolean unsaved;

    /** nullable persistent field */
    private Record record;

    /**
     * Here to keep hibernate happy perhaps????
     */
    public RecordMeta() {
        throw new IllegalArgumentException("Do not use this constructor");
    }
    
    /** full constructor */
    public RecordMeta(nz.cri.gns.fred.hibernate.RecordMetaPK comp_id, nz.cri.gns.fred.hibernate.Record record) {
        this.comp_id = comp_id;
        this.record = record;
        unsaved = true;
   }

    /** default constructor */
    public RecordMeta(boolean saved) {
        unsaved = !saved;
    }

    /** minimal constructor */
    public RecordMeta(nz.cri.gns.fred.hibernate.RecordMetaPK comp_id) {
        this.comp_id = comp_id;
        unsaved = true;
  }

    public nz.cri.gns.fred.hibernate.RecordMetaPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.RecordMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public Record getRecord() {
        return this.record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

     public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof RecordMeta) ) return false;
        RecordMeta castOther = (RecordMeta) other;
        return castOther.comp_id.equals(comp_id);
    }
 	public int hashCode() {
		return comp_id.hashCode();
	}

    public Long getMetaId() {
        return (comp_id == null) ? null : comp_id.getMetaId();
    }

    public void setMetaId(Long id) {
        if (comp_id == null) {
            comp_id = new RecordMetaPK();
        }
        comp_id.setMetaId(id);
    }

    public boolean isUnsaved() {
        return unsaved;
    }

	public void updateKey() {
		comp_id.setRecordId(record.getRecordId());
	}

}
