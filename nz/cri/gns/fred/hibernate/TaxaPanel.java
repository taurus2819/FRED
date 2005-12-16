package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.hibernate.dao.CompositeKey;
import nz.cri.gns.fred.hibernate.dao.CompositeKeyed;

/** @author Hibernate CodeGenerator */
public class TaxaPanel implements Serializable, nz.cri.gns.fred.model.TaxaPanel, CompositeKeyed {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private nz.cri.gns.fred.hibernate.TaxaPanelPK comp_id;

    /** nullable persistent field */
    private nz.cri.gns.fred.model.TaxonomicGroup taxonomicGroup;

	private boolean unsaved;
	
    /** full constructor */
    public TaxaPanel(nz.cri.gns.fred.hibernate.TaxaPanelPK comp_id, nz.cri.gns.fred.hibernate.TaxonomicGroup taxonomicGroup) {
        this.comp_id = comp_id;
        this.taxonomicGroup = taxonomicGroup;
        unsaved = true;
    }

    public TaxaPanel() {
    	throw new IllegalArgumentException("Do not use this constructor");
    }
    
    /** default constructor */
    public TaxaPanel(boolean saved) {
    	unsaved = !saved;
    }

    /** minimal constructor */
    public TaxaPanel(nz.cri.gns.fred.hibernate.TaxaPanelPK comp_id) {
        this.comp_id = comp_id;
        unsaved = true;
    }

    public nz.cri.gns.fred.hibernate.TaxaPanelPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(nz.cri.gns.fred.hibernate.TaxaPanelPK comp_id) {
        this.comp_id = comp_id;
    }

    public nz.cri.gns.fred.model.TaxonomicGroup getTaxonomicGroup() {
        return this.taxonomicGroup;
    }

    public void setTaxonomicGroup(nz.cri.gns.fred.model.TaxonomicGroup taxonomicGroup) {
        this.taxonomicGroup = taxonomicGroup;
        if (comp_id == null) {
        	comp_id = new TaxaPanelPK();
        }
        comp_id.setGroupId(taxonomicGroup.getGroupId());
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TaxaPanel) ) return false;
        TaxaPanel castOther = (TaxaPanel) other;
        return castOther.comp_id.equals(comp_id);
   }
    
	public int hashCode() {
		return comp_id.hashCode();
	}

	public Integer getUserId() {
		return (comp_id == null) ? null : comp_id.getPanelistId();
	}

	public void setUserId(Integer userId) {
		if (comp_id == null) {
			comp_id = new TaxaPanelPK();
		}
		comp_id.setPanelistId(userId);
	}

	public int compareTo(Object arg0) {
		return taxonomicGroup.compareTo(((TaxaPanel)arg0).taxonomicGroup);
	}

	public boolean isUnsaved() {
		return unsaved;
	}

	public void updateKey() {
		comp_id.setGroupId(taxonomicGroup.getGroupId());
	}

	public void setKey(CompositeKey arg1) {
		comp_id = (TaxaPanelPK)arg1;
	}

}
