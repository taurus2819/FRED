package nz.cri.gns.fred.data;

public class Relationship {

	private String relationship;
	private String relationshipType;
	private String distanceRelation;
	private Integer relatedFeatureID;
	private String relatedSampleName;
	private String relatedStratUnit;
	private Double distance;
	private Double distanceRange;
	private String distanceMod;
	private Integer relationTypeID;
	private String relationType;

	public Relationship() {
	}

	public Relationship(String relationship) {
		this.relationship = relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getRelationship() {
		return relationship;
	}
	
	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setDistanceRelation(String distanceRelation) {
		this.distanceRelation = distanceRelation;
	}

	public String getDistanceRelation() {
		return distanceRelation;
	}

	public void setRelatedFeatureID(Integer relatedFeatureID) {
		this.relatedFeatureID = relatedFeatureID;
	}

	public Integer getRelatedFeatureID() {
		return relatedFeatureID;
	}

	public void setRelatedSampleName(String relatedSampleName) {
		this.relatedSampleName = relatedSampleName;
	}

	public String getRelatedSampleName() {
		return relatedSampleName;
	}

	public void setRelatedStratUnit(String relatedStratUnit) {
		this.relatedStratUnit = relatedStratUnit;
	}

	public String getRelatedStratUnit() {
		return relatedStratUnit;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistanceRange(Double distanceRange) {
		this.distanceRange = distanceRange;
	}

	public Double getDistanceRange() {
		return distanceRange;
	}

	public void setDistanceMod(String distanceMod) {
		this.distanceMod = distanceMod;
	}

	public String getDistanceMod() {
		return distanceMod;
	}

	public void setRelationTypeID(Integer typeId) {
		this.relationTypeID = typeId;
	}

	public Integer getRelationTypeID() {
		return relationTypeID;
	}

	public void setRelationType(String type) {
		this.relationType = type;
	}

	public String getRelationType() {
		return relationType;
	}

}
