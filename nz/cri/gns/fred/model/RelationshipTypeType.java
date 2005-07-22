package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface RelationshipTypeType {
	public abstract Integer getTypeId();

	public abstract void setTypeId(Integer typeId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Set getRelationshipTypes();

	public abstract void setRelationshipTypes(Set relationshipTypes);
}