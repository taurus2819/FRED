package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class SedimentaryFeatureType implements Serializable {

    /** identifier field */
    private Integer sedfeatureTypeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** persistent field */
    private Set sedimentaryFeatures;

    /** full constructor */
    public SedimentaryFeatureType(Integer sedfeatureTypeId, String name, String code, Set sedimentaryFeatures) {
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

    public Set getSedimentaryFeatures() {
        return this.sedimentaryFeatures;
    }

    public void setSedimentaryFeatures(Set sedimentaryFeatures) {
        this.sedimentaryFeatures = sedimentaryFeatures;
    }


}
