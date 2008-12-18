package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.model.Relationship;

/** @author Hibernate CodeGenerator */
public class RelationshipType implements Serializable, nz.cri.gns.fred.model.RelationshipType {

    private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer reltypeId;

    /** persistent field */
    private String name;

    /** persistent field */
    private RelationType relationType;

    /** persistent field */
    private Set<Relationship> relationships;

    /** full constructor */
    public RelationshipType(Integer reltypeId, String name, RelationType relationType, Set<Relationship> relationships) {
        this.reltypeId = reltypeId;
        this.name = name;
        this.relationType = relationType;
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

    public RelationType getRelationType() {
        return this.relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    public void setRelationships(Set<Relationship> relationships) {
        this.relationships = relationships;
    }

	public int compareTo(nz.cri.gns.fred.model.RelationshipType arg0) {
		return reltypeId.compareTo(arg0.getReltypeId());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(reltypeId);
	}

	public String getDisplayName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof RelationshipType && ((RelationshipType)o).reltypeId.equals(reltypeId);
	}
	
	@Override
	public int hashCode() {
		return 386 * reltypeId;
	}
}
