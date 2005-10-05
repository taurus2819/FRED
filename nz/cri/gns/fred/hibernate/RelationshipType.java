package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class RelationshipType implements Serializable, nz.cri.gns.fred.model.RelationshipType {

    private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer reltypeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private nz.cri.gns.fred.model.RelationType relationshipTypeType;

    /** persistent field */
    private Set relationships;

    /** full constructor */
    public RelationshipType(Integer reltypeId, String name, nz.cri.gns.fred.hibernate.RelationType relationshipTypeType, Set relationships) {
        this.reltypeId = reltypeId;
        this.name = name;
        this.relationshipTypeType = relationshipTypeType;
        this.relationships = relationships;
    }

    /** default constructor */
    public RelationshipType() {
    }

    public Integer getReltypeId() {
        return this.reltypeId;
    }

    public void setReltypeId(Integer reltypeId) {
        this.reltypeId = reltypeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public nz.cri.gns.fred.model.RelationType getRelationType() {
        return this.relationshipTypeType;
    }

    public void setRelationType(nz.cri.gns.fred.model.RelationType relationshipTypeType) {
        this.relationshipTypeType = relationshipTypeType;
    }

    public Set getRelationships() {
        return this.relationships;
    }

    public void setRelationships(Set relationships) {
        this.relationships = relationships;
    }

}
