package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.SedimentaryFeature;

/** @author Hibernate CodeGenerator */
public class SedimentaryFeatureType implements Serializable, nz.cri.gns.fred.model.SedimentaryFeatureType {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer sedfeatureTypeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set<SedimentaryFeature> sedimentaryFeatures;

    /** full constructor */
    public SedimentaryFeatureType(Integer sedfeatureTypeId, String name, String code, Set<SedimentaryFeature> sedimentaryFeatures) {
        this.sedfeatureTypeId = sedfeatureTypeId;
        this.name = name;
        this.code = code;
        this.sedimentaryFeatures = sedimentaryFeatures;
    }

    /** default constructor */
    public SedimentaryFeatureType() {
    }

    public Integer getSedfeatureTypeId() {
        return this.sedfeatureTypeId;
    }

    public void setSedfeatureTypeId(Integer sedfeatureTypeId) {
        this.sedfeatureTypeId = sedfeatureTypeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<SedimentaryFeature> getSedimentaryFeatures() {
        return this.sedimentaryFeatures;
    }

    public void setSedimentaryFeatures(Set<SedimentaryFeature> sedimentaryFeatures) {
        this.sedimentaryFeatures = sedimentaryFeatures;
    }

	public int compareTo(nz.cri.gns.fred.model.SedimentaryFeatureType arg0) {
		return name.compareTo(arg0.getName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(sedfeatureTypeId);
	}

	public String getDisplayName() {
		return code +  ": " + name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof SedimentaryFeatureType && ((SedimentaryFeatureType)o).getSedfeatureTypeId().equals(sedfeatureTypeId);
	}
	
	@Override
	public int hashCode() {
		return 306 * sedfeatureTypeId;
	}

}
