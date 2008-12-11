package nz.cri.gns.fred.model;

public interface Relationship extends Comparable<Relationship> {
	public abstract Integer getRelationshipId();
	public abstract void setRelationshipId(Integer relationshipId);
	public abstract RelationType getRelationType();
	public abstract void setRelationType(RelationType relationType);
	public abstract Integer getStratUnitId();
	public abstract void setStratUnitId(Integer stratUnitId);
	public abstract String getStratUnit();
	public abstract void setStratUnit(String stratUnit);
	public abstract Double getDistance();
	public abstract void setDistance(Double distance);
	public abstract String getDistanceMod();
	public abstract void setDistanceMod(String distanceMod);
	public abstract Double getDistanceRange();
	public abstract void setDistanceRange(Double distanceRange);
	public abstract Sample getSample();
	public abstract void setSample(Sample sample);
	public abstract Feature getFeature();
	public abstract void setFeature(Feature feature);
	public abstract RelationshipType getRelationshipType();
	public abstract void setRelationshipType(RelationshipType relationshipType);
}