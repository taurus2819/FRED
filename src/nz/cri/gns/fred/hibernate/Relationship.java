package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.RelationType;
import nz.cri.gns.fred.model.RelationshipType;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.SampleUtil;

public class Relationship implements Serializable, nz.cri.gns.fred.model.Relationship, Cloneable {

   private static final long serialVersionUID = 20050818L;

    private Integer relationshipId;
    private RelationType relationType;
    private Integer stratUnitId;
    private String stratUnit;
    private Double distance;
    private String distanceMod;
    private Double distanceRange;
    private Sample sample;
    private Feature feature;
    private RelationshipType relationshipType;

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

    public Sample getSample() {
        return this.sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public RelationshipType getRelationshipType() {
        return this.relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
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

    public boolean equals(Object o) {
    	if (!(o instanceof nz.cri.gns.fred.model.Relationship))
    		return false;
    	nz.cri.gns.fred.model.Relationship rel = (nz.cri.gns.fred.model.Relationship)o;
    	
    	return 
		distance == rel.getDistance()
		 && distanceRange == rel.getDistanceRange()
		 && FREDUtil.equals(distanceMod, rel.getDistanceMod(), true)
		 && FREDUtil.equals(feature, rel.getFeature(), true)
		 && FREDUtil.equals(relationshipType, rel.getRelationshipType(), true)
		 && FREDUtil.equals(relationType, rel.getRelationType(), true)
		 && FREDUtil.equals(sample, rel.getSample(), true)
		 && FREDUtil.equals(stratUnit, rel.getStratUnit(), true)
		 && FREDUtil.equals(stratUnitId, rel.getStratUnitId(), true)
		 && FREDUtil.equals(relationshipId, rel.getRelationshipId(), true);
    }
    
    public String toString() {
    	return SampleUtil.getRelationshipDescription(this);
    }

	@Override
	public int compareTo(nz.cri.gns.fred.model.Relationship arg0) {
		try {
			if (sample.equals(arg0.getSample())) {
				if (relationshipType.equals(arg0.getRelationshipType()))
					return distance.compareTo(arg0.getDistance());
				return relationshipType.compareTo(arg0.getRelationshipType());
			}
			return sample.compareTo(arg0.getSample());
		} catch (Exception e) {}
		return 0;
	}
    
    /*public int hashCode() {
    	return 275 * relationshipId;
    }*/
}
