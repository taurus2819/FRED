package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class RelationshipTypeType implements Serializable, nz.cri.gns.fred.model.RelationType {

    private static final long serialVersionUID = 20050818L;

   /** persistent field */
    private String name;

    /** persistent field */
    private Set relationshipTypes;

    /** persistent field */
    private Set relationships;
    
    /** full constructor */
    public RelationshipTypeType(String name, Set relationshipTypes, Set relationships) {
        this.name = name;
        this.relationshipTypes = relationshipTypes;
        this.relationships = relationships;
    }

    /** default constructor */
    public RelationshipTypeType() {
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
    public Set getRelationships() {
        return this.relationships;
    }

    public void setRelationships(Set relationships) {
        this.relationships = relationships;
    }
}
