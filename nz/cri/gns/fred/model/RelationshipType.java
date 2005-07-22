package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface RelationshipType {
	public abstract Integer getReltypeId();

	public abstract void setReltypeId(Integer reltypeId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract nz.cri.gns.fred.model.RelationshipTypeType getRelationshipTypeType();

	public abstract void setRelationshipTypeType(
			nz.cri.gns.fred.model.RelationshipTypeType relationshipTypeType);

	public abstract Set getRelationships();

	public abstract void setRelationships(Set relationships);
}