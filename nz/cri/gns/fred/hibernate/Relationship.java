package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.util.FREDUtil;

/** @author Hibernate CodeGenerator */
public class Relationship implements Serializable, nz.cri.gns.fred.model.Relationship, Cloneable {

   private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer relationshipId;

    /** persistent field */
    private RelationType relationType;

    /** nullable persistent field */
    private Integer stratUnitId;

    /** nullable persistent field */
    private String stratUnit;

    /** nullable persistent field */
    private Double distance;

    /** nullable persistent field */
    private String distanceMod;

    /** nullable persistent field */
    private Double distanceRange;

    /** persistent field */
    private nz.cri.gns.fred.model.Sample sample;

    /** persistent field */
    private nz.cri.gns.fred.model.Feature feature;

    /** persistent field */
    private nz.cri.gns.fred.model.RelationshipType relationshipType;

    /** full constructor */
    public Relationship(RelationType relationType, Integer stratUnitId, String stratUnit, Double distance, String distanceMod, Double distanceRange, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.RelationshipType relationshipType) {
        this.relationType = relationType;
        this.stratUnitId = stratUnitId;
        this.stratUnit = stratUnit;
        this.distance = distance;
        this.distanceMod = distanceMod;
        this.distanceRange = distanceRange;
        this.sample = sample;
        this.feature = feature;
        this.relationshipType = relationshipType;
    }

    /** default constructor */
    public Relationship() {
    }

    /** minimal constructor */
    public Relationship(RelationType relationType, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.RelationshipType relationshipType) {
        this.relationType = relationType;
        this.sample = sample;
        this.feature = feature;
        this.relationshipType = relationshipType;
    }

    public Integer getRelationshipId() {
        return this.relationshipId;
    }

    public void setRelationshipId(Integer relationshipId) {
        this.relationshipId = relationshipId;
    }

    public RelationType getRelationType() {
        return this.relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public Integer getStratUnitId() {
        return this.stratUnitId;
    }

    public void setStratUnitId(Integer stratUnitId) {
        this.stratUnitId = stratUnitId;
    }

    public String getStratUnit() {
        return this.stratUnit;
    }

    public void setStratUnit(String stratUnit) {
        this.stratUnit = stratUnit;
    }

    public Double getDistance() {
        return this.distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDistanceMod() {
        return this.distanceMod;
    }

    public void setDistanceMod(String distanceMod) {
        this.distanceMod = distanceMod;
    }

    public Double getDistanceRange() {
        return this.distanceRange;
    }

    public void setDistanceRange(Double distanceRange) {
        this.distanceRange = distanceRange;
    }

    public nz.cri.gns.fred.model.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.model.Sample sample) {
        this.sample = sample;
    }

    public nz.cri.gns.fred.model.Feature getFeature() {
        return this.feature;
    }

    public void setFeature(nz.cri.gns.fred.model.Feature feature) {
        this.feature = feature;
    }

    public nz.cri.gns.fred.model.RelationshipType getRelationshipType() {
        return this.relationshipType;
    }

    public void setRelationshipType(nz.cri.gns.fred.model.RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }
    
    public Object clone() { 
    	try {
    		return super.clone();
    	} catch (CloneNotSupportedException e) {
    		//But it is!
    		return null;
    	}
    }

    /**
     * Equality is determined by comparing all fields _except_ the id
     */
    public boolean equals(Object o) {
    	if (!(o instanceof Relationship))
    		return false;
    	Relationship rel = (Relationship)o;
    	
    	return 
    		distance == rel.distance
    	 && distanceRange == rel.distanceRange
    	 && FREDUtil.equals(distanceMod, rel.distanceMod, true)
    	 && FREDUtil.equals(feature, rel.feature, true)
    	 && FREDUtil.equals(relationshipType, rel.relationshipType, true)
    	 && FREDUtil.equals(relationType, rel.relationType, true)
    	 && FREDUtil.equals(sample, rel.sample, true)
    	 && FREDUtil.equals(stratUnit, rel.stratUnit, true)
    	 && FREDUtil.equals(stratUnitId, rel.stratUnitId, true);
    }
}
