package nz.cri.gns.db.fred;

public class Relationship {

	private String relationshipType;
	private String relationship;
	private int relatedFeatureId;
	private String relatedStratUnit;
	private double distance;
	private int typeId;
	private String type;

	public Relationship() {
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelatedFeatureId(int relatedFeatureID) {
		this.relatedFeatureId = relatedFeatureID;
	}

	public int getRelatedFeatureId() {
		return relatedFeatureId;
	}

	public void setRelatedStratUnit(String relatedStratUnit) {
		this.relatedStratUnit = relatedStratUnit;
	}

	public String getRelatedStratUnit() {
		return relatedStratUnit;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
