package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKey;
import nz.cri.gns.fred.hibernate.dao.CompositeKeyed;

/** @author Hibernate CodeGenerator */
public class PalListMeta implements Serializable, nz.cri.gns.fred.model.PalListMeta, CompositeKeyed {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private nz.cri.gns.fred.hibernate.PalListMetaPK comp_id;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.PaleontologyListEntry palList;

	private boolean unsaved;

    /** full constructor */
    public PalListMeta(nz.cri.gns.fred.hibernate.PalListMetaPK comp_id, nz.cri.gns.fred.hibernate.PalList palList) {
        this.comp_id = comp_id;
        this.palList = palList;
        unsaved = true;
    }

    public PalListMeta() {
    	throw new IllegalArgumentException("Do not use this constructor");
    }
    
    /** default constructor */
    public PalListMeta(boolean saved) {
    	unsaved = !saved;
    }

    /** minimal constructor */
    public PalListMeta(nz.cri.gns.fred.hibernate.PalListMetaPK comp_id) {
        this.comp_id = comp_id;
        unsaved = true;
   }

    public nz.cri.gns.fred.hibernate.PalListMetaPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.PalListMetaPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.model.PaleontologyListEntry getPalList() {
        return this.palList;
    }

    public void setPalList(nz.cri.gns.fred.model.PaleontologyListEntry palList) {
        this.palList = palList;
        if (comp_id == null) {
        	comp_id = new PalListMetaPK();
        }
        comp_id.setPalListId(palList.getPalListId());
   }

    public boolean equals(Object other) {
        return other instanceof PalListMeta && ((PalListMeta)other).comp_id.equals(comp_id);
    }
	public int hashCode() {
		return comp_id.hashCode();
	}

	public Long getMetaId() {
		return (comp_id == null) ? null : comp_id.getMetaId();
	}

	public void setMetaId(Long id) {
	    if (comp_id == null) {
        	comp_id = new PalListMetaPK();
        }
        comp_id.setMetaId(id);
	}

	public boolean isUnsaved() {
		return unsaved;
	}

	public void updateKey() {
		comp_id.setPalListId(palList.getPalListId());
	}

	public void setKey(CompositeKey arg1) {
		comp_id = (PalListMetaPK)arg1;
	}
}
