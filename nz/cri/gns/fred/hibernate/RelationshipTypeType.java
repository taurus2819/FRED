package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class RelationshipTypeType implements Serializable, nz.cri.gns.fred.model.RelationshipTypeType {

    /** identifier field */
    private Integer typeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private Set relationshipTypes;

    /** full constructor */
    public RelationshipTypeType(Integer typeId, String name, Set relationshipTypes) {
        this.typeId = typeId;
        this.name = name;
        this.relationshipTypes = relationshipTypes;
    }

    /** default constructor */
    public RelationshipTypeType() {
    }

    public Integer getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set getRelationshipTypes() {
        return this.relationshipTypes;
    }

    public void setRelationshipTypes(Set relationshipTypes) {
        this.relationshipTypes = relationshipTypes;
    }


}
