package nz.cri.gns.fred.model;

/**
 *
 */
public interface Relationship {
	public abstract Integer getRelationshipId();

	public abstract void setRelationshipId(Integer relationshipId);

	public abstract String getRelationshipTypeTypeAsString();

	public abstract void setRelationshipTypeTypeAsString(
			String relationshipTypeTypeAsString);

	public abstract Integer getStratUnitId();

	public abstract void setStratUnitId(Integer stratUnitId);

	public abstract String getStratUnit();

	public abstract void setStratUnit(String stratUnit);

	public abstract double getDistance();

	public abstract void setDistance(double distance);

	public abstract String getDistanceMod();

	public abstract void setDistanceMod(String distanceMod);

	public abstract double getDistanceRange();

	public abstract void setDistanceRange(double distanceRange);

	public abstract nz.cri.gns.fred.model.Sample getSample();

	public abstract void setSample(nz.cri.gns.fred.model.Sample sample);

	public abstract nz.cri.gns.fred.model.Feature getFeature();

	public abstract void setFeature(nz.cri.gns.fred.model.Feature feature);

	public abstract nz.cri.gns.fred.model.RelationshipType getRelationshipType();

	public abstract void setRelationshipType(
			nz.cri.gns.fred.model.RelationshipType relationshipType);
}