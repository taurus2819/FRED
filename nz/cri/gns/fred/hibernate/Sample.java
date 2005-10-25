package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.PersonRelationship;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;

/** @author Hibernate CodeGenerator */
public class Sample implements Serializable, nz.cri.gns.fred.model.Sample, Cloneable {

    private static final long serialVersionUID = 20050818L;

	/** identifier field */
    private Integer sampleId;

    /** nullable persistent field */
    private Double topDepth;

    /** nullable persistent field */
    private Double bottomDepth;

    /** nullable persistent field */
    private String comments;

    /** nullable persistent field */
    private FrNumber yardFrId;

    /** nullable persistent field */
    private Date collectionDate;

    /** nullable persistent field */
    private String dateRounding;

    /** nullable persistent field */
    private String stratUnit;

    /** nullable persistent field */
    private String inPlace;

    /** nullable persistent field */
    private String notCollected;

    /** nullable persistent field */
    private String significance;

    /** nullable persistent field */
    private String columnMap;

    /** nullable persistent field */
    private Integer dip;

    /** nullable persistent field */
    private String dipDirection;

    /** nullable persistent field */
    private Integer strike;

    /** nullable persistent field */
    private String facing;

    /** nullable persistent field */
    private String comparatorUsed;

    /** nullable persistent field */
    private String wet;

    /** nullable persistent field */
    private String rockNature;

    /** nullable persistent field */
    private String depositionEnv;

    /** nullable persistent field */
    private String correspondence;

    /** nullable persistent field */
    private String sampleName;

    /** persistent field */
    private nz.cri.gns.fred.model.ColourModifier colourModifier;

    /** persistent field */
    private nz.cri.gns.fred.model.Hardness hardness;

    /** persistent field */
    private nz.cri.gns.fred.model.Weathering weathering;

    /** persistent field */
    private nz.cri.gns.fred.model.Carbonate carbonate;

    /** persistent field */
    private nz.cri.gns.fred.model.RockColour rockColourBySecondaryColourId;

    /** persistent field */
    private nz.cri.gns.fred.model.RockColour rockColourByPrimaryColourId;

    /** persistent field */
    private nz.cri.gns.fred.model.Feature feature;

    /** persistent field */
    private nz.cri.gns.fred.model.Audit auditTable;

    /** persistent field */
    private nz.cri.gns.fred.model.Bedding beddingByPrimaryBeddingId;

    /** persistent field */
    private nz.cri.gns.fred.model.Bedding beddingBySecondaryBeddingId;

    /** persistent field */
    private nz.cri.gns.fred.model.FrNumber frNumber;

    /** persistent field */
    private nz.cri.gns.fred.model.DrillType drillType;

    /** persistent field */
    private nz.cri.gns.fred.model.GrainSize grainSizeByPrimaryGrainsizeId;

    /** persistent field */
    private nz.cri.gns.fred.model.GrainSize grainSizeBySecondaryGrainsizeId;

    /** persistent field */
    private nz.cri.gns.fred.model.BedThickness bedThickness;

    /** persistent field */
    private nz.cri.gns.fred.model.Stage stageByKnownStageId;

    /** persistent field */
    private nz.cri.gns.fred.model.Stage stageByInferredStageId;

    /** persistent field */
    private Set<SedimentaryFeature> sedimentaryFeatures;

    /** persistent field */
    private Set<SampleMeta> sampleMetas;

    /** persistent field */
    private Set records;

    /** persistent field */
    private Set<SentTo> sentTos;

    /** persistent field */
    private Set<? extends PersonRelationship> collectors;

    /** persistent field */
    private Set<Relationship> relationships;

    /** full constructor */
    public Sample(Double topDepth, Double bottomDepth, String comments, FrNumber yardFrId, Date collectionDate, String dateRounding, String stratUnit, String inPlace, String notCollected, String significance, String columnMap, Integer dip, String dipDirection, Integer strike, String facing, String comparatorUsed, String wet, String rockNature, String depositionEnv, String correspondence, String sampleName, nz.cri.gns.fred.hibernate.ColourModifier colourModifier, nz.cri.gns.fred.hibernate.Hardness hardness, nz.cri.gns.fred.hibernate.Weathering weathering, nz.cri.gns.fred.hibernate.Carbonate carbonate, nz.cri.gns.fred.hibernate.RockColour rockColourBySecondaryColourId, nz.cri.gns.fred.hibernate.RockColour rockColourByPrimaryColourId, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.AuditTable auditTable, nz.cri.gns.fred.hibernate.Bedding beddingByPrimaryBeddingId, nz.cri.gns.fred.hibernate.Bedding beddingBySecondaryBeddingId, nz.cri.gns.fred.hibernate.FrNumber frNumber, nz.cri.gns.fred.hibernate.DrillType drillType, nz.cri.gns.fred.hibernate.GrainSize grainSizeByPrimaryGrainsizeId, nz.cri.gns.fred.hibernate.GrainSize grainSizeBySecondaryGrainsizeId, nz.cri.gns.fred.hibernate.BedThickness bedThickness, nz.cri.gns.fred.hibernate.Stage stageByKnownStageId, nz.cri.gns.fred.hibernate.Stage stageByInferredStageId, Set<SedimentaryFeature> sedimentaryFeatures, Set<SampleMeta> sampleMetas, Set records, Set<SentTo> sentTos, Set<PersonRelationship> collectors, Set<Relationship> relationships) {
        this.topDepth = topDepth;
        this.bottomDepth = bottomDepth;
        this.comments = comments;
        this.yardFrId = yardFrId;
        this.collectionDate = collectionDate;
        this.dateRounding = dateRounding;
        this.stratUnit = stratUnit;
        this.inPlace = inPlace;
        this.notCollected = notCollected;
        this.significance = significance;
        this.columnMap = columnMap;
        this.dip = dip;
        this.dipDirection = dipDirection;
        this.strike = strike;
        this.facing = facing;
        this.comparatorUsed = comparatorUsed;
        this.wet = wet;
        this.rockNature = rockNature;
        this.depositionEnv = depositionEnv;
        this.correspondence = correspondence;
        this.sampleName = sampleName;
        this.colourModifier = colourModifier;
        this.hardness = hardness;
        this.weathering = weathering;
        this.carbonate = carbonate;
        this.rockColourBySecondaryColourId = rockColourBySecondaryColourId;
        this.rockColourByPrimaryColourId = rockColourByPrimaryColourId;
        this.feature = feature;
        this.auditTable = auditTable;
        this.beddingByPrimaryBeddingId = beddingByPrimaryBeddingId;
        this.beddingBySecondaryBeddingId = beddingBySecondaryBeddingId;
        this.frNumber = frNumber;
        this.drillType = drillType;
        this.grainSizeByPrimaryGrainsizeId = grainSizeByPrimaryGrainsizeId;
        this.grainSizeBySecondaryGrainsizeId = grainSizeBySecondaryGrainsizeId;
        this.bedThickness = bedThickness;
        this.stageByKnownStageId = stageByKnownStageId;
        this.stageByInferredStageId = stageByInferredStageId;
        this.sedimentaryFeatures = sedimentaryFeatures;
        this.sampleMetas = sampleMetas;
        this.records = records;
        this.sentTos = sentTos;
        this.collectors = collectors;
        this.relationships = relationships;
    }

    /** default constructor */
    public Sample() {
    }

    /** minimal constructor */
    public Sample(nz.cri.gns.fred.hibernate.ColourModifier colourModifier, nz.cri.gns.fred.hibernate.Hardness hardness, nz.cri.gns.fred.hibernate.Weathering weathering, nz.cri.gns.fred.hibernate.Carbonate carbonate, nz.cri.gns.fred.hibernate.RockColour rockColourBySecondaryColourId, nz.cri.gns.fred.hibernate.RockColour rockColourByPrimaryColourId, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.AuditTable auditTable, nz.cri.gns.fred.hibernate.Bedding beddingByPrimaryBeddingId, nz.cri.gns.fred.hibernate.Bedding beddingBySecondaryBeddingId, nz.cri.gns.fred.hibernate.FrNumber frNumber, nz.cri.gns.fred.hibernate.DrillType drillType, nz.cri.gns.fred.hibernate.GrainSize grainSizeByPrimaryGrainsizeId, nz.cri.gns.fred.hibernate.GrainSize grainSizeBySecondaryGrainsizeId, nz.cri.gns.fred.hibernate.BedThickness bedThickness, nz.cri.gns.fred.hibernate.Stage stageByKnownStageId, nz.cri.gns.fred.hibernate.Stage stageByInferredStageId, Set<SedimentaryFeature> sedimentaryFeatures, Set<SampleMeta> sampleMetas, Set records, Set<SentTo> sentTos, Set<PersonRelationship> collectors, Set<Relationship> relationships) {
        this.colourModifier = colourModifier;
        this.hardness = hardness;
        this.weathering = weathering;
        this.carbonate = carbonate;
        this.rockColourBySecondaryColourId = rockColourBySecondaryColourId;
        this.rockColourByPrimaryColourId = rockColourByPrimaryColourId;
        this.feature = feature;
        this.auditTable = auditTable;
        this.beddingByPrimaryBeddingId = beddingByPrimaryBeddingId;
        this.beddingBySecondaryBeddingId = beddingBySecondaryBeddingId;
        this.frNumber = frNumber;
        this.drillType = drillType;
        this.grainSizeByPrimaryGrainsizeId = grainSizeByPrimaryGrainsizeId;
        this.grainSizeBySecondaryGrainsizeId = grainSizeBySecondaryGrainsizeId;
        this.bedThickness = bedThickness;
        this.stageByKnownStageId = stageByKnownStageId;
        this.stageByInferredStageId = stageByInferredStageId;
        this.sedimentaryFeatures = sedimentaryFeatures;
        this.sampleMetas = sampleMetas;
        this.records = records;
        this.sentTos = sentTos;
        this.collectors = collectors;
        this.relationships = relationships;
    }

    public Integer getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Double getTopDepth() {
        return this.topDepth;
    }

    public void setTopDepth(Double topDepth) {
        this.topDepth = topDepth;
    }

    public Double getBottomDepth() {
        return this.bottomDepth;
    }

    public void setBottomDepth(Double bottomDepth) {
        this.bottomDepth = bottomDepth;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public FrNumber getYardFrNumber() {
        return this.yardFrId;
    }

    public void setYardFrNumber(FrNumber yardFrId) {
        this.yardFrId = yardFrId;
    }

    public Date getCollectionDate() {
        return this.collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getDateRounding() {
        return this.dateRounding;
    }

    public void setDateRounding(String dateRounding) {
        this.dateRounding = dateRounding;
    }

    public String getStratUnit() {
        return this.stratUnit;
    }

    public void setStratUnit(String stratUnit) {
        this.stratUnit = stratUnit;
    }

    public String getInPlace() {
        return this.inPlace;
    }

    public void setInPlace(String inPlace) {
        this.inPlace = inPlace;
    }

    public String getNotCollected() {
        return this.notCollected;
    }

    public void setNotCollected(String notCollected) {
        this.notCollected = notCollected;
    }

    public String getSignificance() {
        return this.significance;
    }

    public void setSignificance(String significance) {
        this.significance = significance;
    }

    public String getColumnMap() {
        return this.columnMap;
    }

    public void setColumnMap(String columnMap) {
        this.columnMap = columnMap;
    }

    public Integer getDip() {
        return this.dip;
    }

    public void setDip(Integer dip) {
        this.dip = dip;
    }

    public String getDipDirection() {
        return this.dipDirection;
    }

    public void setDipDirection(String dipDirection) {
        this.dipDirection = dipDirection;
    }

    public Integer getStrike() {
        return this.strike;
    }

    public void setStrike(Integer strike) {
        this.strike = strike;
    }

    public String getFacing() {
        return this.facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public String getComparatorUsed() {
        return this.comparatorUsed;
    }

    public void setComparatorUsed(String comparatorUsed) {
        this.comparatorUsed = comparatorUsed;
    }

    public String getWet() {
        return this.wet;
    }

    public void setWet(String wet) {
        this.wet = wet;
    }

    public String getRockNature() {
        return this.rockNature;
    }

    public void setRockNature(String rockNature) {
        this.rockNature = rockNature;
    }

    public String getDepositionEnv() {
        return this.depositionEnv;
    }

    public void setDepositionEnv(String depositionEnv) {
        this.depositionEnv = depositionEnv;
    }

    public String getCorrespondence() {
        return this.correspondence;
    }

    public void setCorrespondence(String correspondence) {
        this.correspondence = correspondence;
    }

    public String getSampleName() {
        return this.sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public nz.cri.gns.fred.model.ColourModifier getColourModifier() {
        return this.colourModifier;
    }

    public void setColourModifier(nz.cri.gns.fred.model.ColourModifier colourModifier) {
        this.colourModifier = colourModifier;
    }

    public nz.cri.gns.fred.model.Hardness getHardness() {
        return this.hardness;
    }

    public void setHardness(nz.cri.gns.fred.model.Hardness hardness) {
        this.hardness = hardness;
    }

    public nz.cri.gns.fred.model.Weathering getWeathering() {
        return this.weathering;
    }

    public void setWeathering(nz.cri.gns.fred.model.Weathering weathering) {
        this.weathering = weathering;
    }

    public nz.cri.gns.fred.model.Carbonate getCarbonate() {
        return this.carbonate;
    }

    public void setCarbonate(nz.cri.gns.fred.model.Carbonate carbonate) {
        this.carbonate = carbonate;
    }

    public nz.cri.gns.fred.model.RockColour getSecondaryColour() {
        return this.rockColourBySecondaryColourId;
    }

    public void setSecondaryColour(nz.cri.gns.fred.model.RockColour rockColourBySecondaryColourId) {
        this.rockColourBySecondaryColourId = rockColourBySecondaryColourId;
    }

    public nz.cri.gns.fred.model.RockColour getPrimaryColour() {
        return this.rockColourByPrimaryColourId;
    }

    public void setPrimaryColour(nz.cri.gns.fred.model.RockColour rockColourByPrimaryColourId) {
        this.rockColourByPrimaryColourId = rockColourByPrimaryColourId;
    }

    public nz.cri.gns.fred.model.Feature getFeature() {
        return this.feature;
    }

    public void setFeature(nz.cri.gns.fred.model.Feature feature) {
    	if (this.feature != null && this.feature.getSamples() != null)
    		this.feature.getSamples().remove(this);
        this.feature = feature;
        //This can't be right!!!!
        try {
        	feature.getSamples().add(this);
        } catch (Exception e) {
        }
    }

    public nz.cri.gns.fred.model.Audit getAudit() {
        return this.auditTable;
    }

    public void setAudit(nz.cri.gns.fred.model.Audit auditTable) {
        this.auditTable = auditTable;
        if (auditTable.getSamples() == null)
        	auditTable.setSamples(new HashSet<nz.cri.gns.fred.model.Sample>());
        try {
        	auditTable.getSamples().add(this);
        } catch (Exception e) {
        }
    }

    public nz.cri.gns.fred.model.Bedding getPrimaryBedding() {
        return this.beddingByPrimaryBeddingId;
    }

    public void setPrimaryBedding(nz.cri.gns.fred.model.Bedding beddingByPrimaryBeddingId) {
        this.beddingByPrimaryBeddingId = beddingByPrimaryBeddingId;
    }

    public nz.cri.gns.fred.model.Bedding getSecondaryBedding() {
        return this.beddingBySecondaryBeddingId;
    }

    public void setSecondaryBedding(nz.cri.gns.fred.model.Bedding beddingBySecondaryBeddingId) {
        this.beddingBySecondaryBeddingId = beddingBySecondaryBeddingId;
    }

    public nz.cri.gns.fred.model.FrNumber getFrNumber() {
        return this.frNumber;
    }

    public void setFrNumber(nz.cri.gns.fred.model.FrNumber frNumber) {
        this.frNumber = frNumber;
    }

    public nz.cri.gns.fred.model.DrillType getDrillType() {
        return this.drillType;
    }

    public void setDrillType(nz.cri.gns.fred.model.DrillType drillType) {
        this.drillType = drillType;
    }

    public nz.cri.gns.fred.model.GrainSize getPrimaryGrainSize() {
        return this.grainSizeByPrimaryGrainsizeId;
    }

    public void setPrimaryGrainSize(nz.cri.gns.fred.model.GrainSize grainSizeByPrimaryGrainsizeId) {
        this.grainSizeByPrimaryGrainsizeId = grainSizeByPrimaryGrainsizeId;
    }

    public nz.cri.gns.fred.model.GrainSize getSecondaryGrainSize() {
        return this.grainSizeBySecondaryGrainsizeId;
    }

    public void setSecondaryGrainSize(nz.cri.gns.fred.model.GrainSize grainSizeBySecondaryGrainsizeId) {
        this.grainSizeBySecondaryGrainsizeId = grainSizeBySecondaryGrainsizeId;
    }

    public nz.cri.gns.fred.model.BedThickness getBedThickness() {
        return this.bedThickness;
    }

    public void setBedThickness(nz.cri.gns.fred.model.BedThickness bedThickness) {
        this.bedThickness = bedThickness;
    }

    public nz.cri.gns.fred.model.Stage getKnownStage() {
        return this.stageByKnownStageId;
    }

    public void setKnownStage(nz.cri.gns.fred.model.Stage stageByKnownStageId) {
        this.stageByKnownStageId = stageByKnownStageId;
    }

    public nz.cri.gns.fred.model.Stage getInferredStage() {
        return this.stageByInferredStageId;
    }

    public void setInferredStage(nz.cri.gns.fred.model.Stage stageByInferredStageId) {
        this.stageByInferredStageId = stageByInferredStageId;
    }

    public Set<SedimentaryFeature> getSedimentaryFeatures() {
        return this.sedimentaryFeatures;
    }

    public void setSedimentaryFeatures(Set<SedimentaryFeature> sedimentaryFeatures) {
        this.sedimentaryFeatures = sedimentaryFeatures;
    }

    public Set<SampleMeta> getSampleMetas() {
        return this.sampleMetas;
    }

    public void setSampleMetas(Set<SampleMeta> sampleMetas) {
        this.sampleMetas = sampleMetas;
    }

    public Set getRecords() {
        return this.records;
    }

    public void setRecords(Set records) {
        this.records = records;
    }

    public Set<SentTo> getSentTos() {
        return this.sentTos;
    }

    public void setSentTos(Set<SentTo> sentTos) {
        this.sentTos = sentTos;
    }

    public Set<? extends PersonRelationship> getCollectors() {
        return this.collectors;
    }

    public void setCollectors(Set<? extends PersonRelationship> collectors) {
        this.collectors = collectors;
    }

    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    public void setRelationships(Set<Relationship> relationships) {
        this.relationships = relationships;
    }

	public int compareTo(nz.cri.gns.fred.model.Sample sample) {
		if (getTopDepth() != null && sample.getTopDepth() != null) {
			if (getTopDepth().equals(sample.getTopDepth()) && getBottomDepth() != null && sample.getBottomDepth() != null)
				return getBottomDepth().compareTo(sample.getBottomDepth());
			return getTopDepth().compareTo(sample.getTopDepth());
		} else if (getTopDepth() == null && sample.getTopDepth() == null && getSampleName() != null && sample.getSampleName() != null)
			return getSampleName().compareTo(sample.getSampleName());
		else 
			//Anything undepthed goes to the end
			return (getTopDepth() == null) ? 1 : -1;
	}

}
