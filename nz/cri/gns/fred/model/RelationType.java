package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface RelationType {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Set getRelationshipTypes();

	public abstract void setRelationshipTypes(Set relationshipTypes);

	public abstract Set getRelationships();

	public abstract void setRelationships(Set relationships);
}