package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface RelationshipType extends Comparable<RelationshipType>, NameableAndIdentifiable {
	public Integer getReltypeId();
	public void setReltypeId(Integer reltypeId);
	public String getName();
	public void setName(String name);
	public RelationType getRelationType();
	public void setRelationType(RelationType relationshipTypeType);
	public Set<Relationship> getRelationships();
	public void setRelationships(Set<Relationship> relationships);
}