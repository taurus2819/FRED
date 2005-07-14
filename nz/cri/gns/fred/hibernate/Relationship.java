package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class Relationship implements Serializable {

    /** identifier field */
    private Integer relationshipId;

    /** persistent field */
    private String relationshipTypeTypeAsString;

    /** nullable persistent field */
    private Integer stratUnitId;

    /** nullable persistent field */
    private String stratUnit;

    /** nullable persistent field */
    private double distance;

    /** nullable persistent field */
    private String distanceMod;

    /** nullable persistent field */
    private double distanceRange;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Sample sample;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Feature feature;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.RelationshipType relationshipType;

    /** full constructor */
    public Relationship(String relationshipTypeTypeAsString, Integer stratUnitId, String stratUnit, double distance, String distanceMod, double distanceRange, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.RelationshipType relationshipType) {
        this.relationshipTypeTypeAsString = relationshipTypeTypeAsString;
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
    public Relationship(String relationshipTypeTypeAsString, nz.cri.gns.fred.hibernate.Sample sample, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.RelationshipType relationshipType) {
        this.relationshipTypeTypeAsString = relationshipTypeTypeAsString;
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

    public String getRelationshipTypeTypeAsString() {
        return this.relationshipTypeTypeAsString;
    }

    public void setRelationshipTypeTypeAsString(String relationshipTypeTypeAsString) {
        this.relationshipTypeTypeAsString = relationshipTypeTypeAsString;
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

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDistanceMod() {
        return this.distanceMod;
    }

    public void setDistanceMod(String distanceMod) {
        this.distanceMod = distanceMod;
    }

    public double getDistanceRange() {
        return this.distanceRange;
    }

    public void setDistanceRange(double distanceRange) {
        this.distanceRange = distanceRange;
    }

    public nz.cri.gns.fred.hibernate.Sample getSample() {
        return this.sample;
    }

    public void setSample(nz.cri.gns.fred.hibernate.Sample sample) {
        this.sample = sample;
    }

    public nz.cri.gns.fred.hibernate.Feature getFeature() {
        return this.feature;
    }

    public void setFeature(nz.cri.gns.fred.hibernate.Feature feature) {
        this.feature = feature;
    }

    public nz.cri.gns.fred.hibernate.RelationshipType getRelationshipType() {
        return this.relationshipType;
    }

    public void setRelationshipType(nz.cri.gns.fred.hibernate.RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }
}
