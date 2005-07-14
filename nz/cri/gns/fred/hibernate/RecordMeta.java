package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class RecordMeta implements Serializable {

    /** identifier field */
    private nz.cri.gns.fred.hibernate.RecordMetaPK comp_id;

    /** nullable persistent field */
    private nz.cri.gns.fred.hibernate.Record record;

    /** full constructor */
    public RecordMeta(nz.cri.gns.fred.hibernate.RecordMetaPK comp_id, nz.cri.gns.fred.hibernate.Record record) {
        this.comp_id = comp_id;
        this.record = record;
    }

    /** default constructor */
    public RecordMeta() {
    }

    /** minimal constructor */
    public RecordMeta(nz.cri.gns.fred.hibernate.RecordMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.hibernate.RecordMetaPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.RecordMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.hibernate.Record getRecord() {
        return this.record;
    }

    public void setRecord(nz.cri.gns.fred.hibernate.Record record) {
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

}
