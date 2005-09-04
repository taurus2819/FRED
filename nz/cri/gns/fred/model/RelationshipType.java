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

	public abstract nz.cri.gns.fred.model.RelationType getRelationType();

	public abstract void setRelationType(
			nz.cri.gns.fred.model.RelationType relationshipTypeType);

	public abstract Set getRelationships();

	public abstract void setRelationships(Set relationships);
}