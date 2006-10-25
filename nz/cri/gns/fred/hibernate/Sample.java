package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.Weathering;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.util.SampleUtil;

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
    private String depthUnit;
    
    /** nullable persistent field */
    private String comments;

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

    /** persistent field */
    private ColourModifier colourModifier;

    /** persistent field */
    private Hardness hardness;

    /** persistent field */
    private Weathering weathering;

    /** persistent field */
    private Carbonate carbonate;

    /** persistent field */
    private RockColour rockColourBySecondaryColourId;

    /** persistent field */
    private RockColour rockColourByPrimaryColourId;

    /** persistent field */
    private Feature feature;

    /** persistent field */
    private Audit auditTable;

    /** persistent field */
    private Bedding beddingByPrimaryBeddingId;

    /** persistent field */
    private Bedding beddingBySecondaryBeddingId;

    /** persistent field */
    private FrNumber frNumber;

    /** persistent field */
    private FrNumber yardFrNumber;
    
    /** persistent field */
    private DrillType drillType;

    /** persistent field */
    private GrainSize grainSizeByPrimaryGrainsizeId;

    /** persistent field */
    private GrainSize grainSizeBySecondaryGrainsizeId;

    /** persistent field */
    private BedThickness bedThickness;

    /** persistent field */
    private Stage stageByKnownStageId;

    /** persistent field */
    private Stage stageByInferredStageId;

    /** persistent field */
    private Set<SedimentaryFeature> sedimentaryFeatures;

    /** persistent field */
    private Set<SampleMeta> sampleMetas;

    /** persistent field */
    private Set<Record> records;

    /** persistent field */
    private Set<SentTo> sentTos;

    /** persistent field */
    private Set<Person> collectors;

    /** persistent field */
    private Set<Relationship> relationships;

    /** full constructor */
    public Sample(Double topDepth, Double bottomDepth, String depthUnit, String comments, Date collectionDate, String dateRounding, String stratUnit, String inPlace, String notCollected, String significance, String columnMap, Integer dip, String dipDirection, Integer strike, String facing, String comparatorUsed, String wet, String rockNature, String depositionEnv, String correspondence, nz.cri.gns.fred.hibernate.ColourModifier colourModifier, nz.cri.gns.fred.hibernate.Hardness hardness, nz.cri.gns.fred.hibernate.Weathering weathering, nz.cri.gns.fred.hibernate.Carbonate carbonate, nz.cri.gns.fred.hibernate.RockColour rockColourBySecondaryColourId, nz.cri.gns.fred.hibernate.RockColour rockColourByPrimaryColourId, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.AuditTable auditTable, nz.cri.gns.fred.hibernate.Bedding beddingByPrimaryBeddingId, nz.cri.gns.fred.hibernate.Bedding beddingBySecondaryBeddingId, nz.cri.gns.fred.hibernate.FrNumber frNumber, nz.cri.gns.fred.hibernate.FrNumber yardFrNumber, nz.cri.gns.fred.hibernate.DrillType drillType, nz.cri.gns.fred.hibernate.GrainSize grainSizeByPrimaryGrainsizeId, nz.cri.gns.fred.hibernate.GrainSize grainSizeBySecondaryGrainsizeId, nz.cri.gns.fred.hibernate.BedThickness bedThickness, nz.cri.gns.fred.hibernate.Stage stageByKnownStageId, nz.cri.gns.fred.hibernate.Stage stageByInferredStageId, Set<SedimentaryFeature> sedimentaryFeatures, Set<SampleMeta> sampleMetas, Set<Record> records, Set<SentTo> sentTos, Set<Person> collectors, Set<Relationship> relationships) {
        this.topDepth = topDepth;
        this.bottomDepth = bottomDepth;
        this.depthUnit = depthUnit;
        this.comments = comments;
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
        this.yardFrNumber = yardFrNumber;
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
    public Sample(nz.cri.gns.fred.hibernate.ColourModifier colourModifier, nz.cri.gns.fred.hibernate.Hardness hardness, nz.cri.gns.fred.hibernate.Weathering weathering, nz.cri.gns.fred.hibernate.Carbonate carbonate, nz.cri.gns.fred.hibernate.RockColour rockColourBySecondaryColourId, nz.cri.gns.fred.hibernate.RockColour rockColourByPrimaryColourId, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.AuditTable auditTable, nz.cri.gns.fred.hibernate.Bedding beddingByPrimaryBeddingId, nz.cri.gns.fred.hibernate.Bedding beddingBySecondaryBeddingId, nz.cri.gns.fred.hibernate.FrNumber frNumber, nz.cri.gns.fred.hibernate.DrillType drillType, nz.cri.gns.fred.hibernate.GrainSize grainSizeByPrimaryGrainsizeId, nz.cri.gns.fred.hibernate.GrainSize grainSizeBySecondaryGrainsizeId, nz.cri.gns.fred.hibernate.BedThickness bedThickness, nz.cri.gns.fred.hibernate.Stage stageByKnownStageId, nz.cri.gns.fred.hibernate.Stage stageByInferredStageId, Set<SedimentaryFeature> sedimentaryFeatures, Set<SampleMeta> sampleMetas, Set<Record> records, Set<SentTo> sentTos, Set<Person> collectors, Set<Relationship> relationships) {
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
    
    public String getDepthUnit() {
        return this.depthUnit;
    }

    public void setDepthUnit(String depthUnit) {
        this.depthUnit = depthUnit;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public ColourModifier getColourModifier() {
        return this.colourModifier;
    }

    public void setColourModifier(ColourModifier colourModifier) {
        this.colourModifier = colourModifier;
    }

    public Hardness getHardness() {
        return this.hardness;
    }

    public void setHardness(Hardness hardness) {
        this.hardness = hardness;
    }

    public Weathering getWeathering() {
        return this.weathering;
    }

    public void setWeathering(Weathering weathering) {
        this.weathering = weathering;
    }

    public Carbonate getCarbonate() {
        return this.carbonate;
    }

    public void setCarbonate(Carbonate carbonate) {
        this.carbonate = carbonate;
    }

    public RockColour getSecondaryColour() {
        return this.rockColourBySecondaryColourId;
    }

    public void setSecondaryColour(RockColour rockColourBySecondaryColourId) {
        this.rockColourBySecondaryColourId = rockColourBySecondaryColourId;
    }

    public RockColour getPrimaryColour() {
        return this.rockColourByPrimaryColourId;
    }

    public void setPrimaryColour(RockColour rockColourByPrimaryColourId) {
        this.rockColourByPrimaryColourId = rockColourByPrimaryColourId;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public void setFeature(Feature feature) {
    	this.feature = feature;
    }

    public Audit getAudit() {
        return this.auditTable;
    }

    public void setAudit(Audit auditTable) {
        this.auditTable = auditTable;
    }

    public Bedding getPrimaryBedding() {
        return this.beddingByPrimaryBeddingId;
    }

    public void setPrimaryBedding(Bedding beddingByPrimaryBeddingId) {
        this.beddingByPrimaryBeddingId = beddingByPrimaryBeddingId;
    }

    public Bedding getSecondaryBedding() {
        return this.beddingBySecondaryBeddingId;
    }

    public void setSecondaryBedding(Bedding beddingBySecondaryBeddingId) {
        this.beddingBySecondaryBeddingId = beddingBySecondaryBeddingId;
    }

    public FrNumber getFrNumber() {
        return this.frNumber;
    }

    public void setFrNumber(FrNumber frNumber) {
        this.frNumber = frNumber;
    }

    public FrNumber getYardFrNumber() {
        return this.yardFrNumber;
    }

    public void setYardFrNumber(FrNumber yardFrNumber) {
        this.yardFrNumber = yardFrNumber;
    }
    
    public DrillType getDrillType() {
        return this.drillType;
    }

    public void setDrillType(DrillType drillType) {
        this.drillType = drillType;
    }

    public GrainSize getPrimaryGrainSize() {
        return this.grainSizeByPrimaryGrainsizeId;
    }

    public void setPrimaryGrainSize(GrainSize grainSizeByPrimaryGrainsizeId) {
        this.grainSizeByPrimaryGrainsizeId = grainSizeByPrimaryGrainsizeId;
    }

    public GrainSize getSecondaryGrainSize() {
        return this.grainSizeBySecondaryGrainsizeId;
    }

    public void setSecondaryGrainSize(GrainSize grainSizeBySecondaryGrainsizeId) {
        this.grainSizeBySecondaryGrainsizeId = grainSizeBySecondaryGrainsizeId;
    }

    public BedThickness getBedThickness() {
        return this.bedThickness;
    }

    public void setBedThickness(BedThickness bedThickness) {
        this.bedThickness = bedThickness;
    }

    public Stage getKnownStage() {
        return this.stageByKnownStageId;
    }

    public void setKnownStage(Stage stageByKnownStageId) {
        this.stageByKnownStageId = stageByKnownStageId;
    }

    public Stage getInferredStage() {
        return this.stageByInferredStageId;
    }

    public void setInferredStage(Stage stageByInferredStageId) {
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

    public Set<Record> getRecords() {
        return this.records;
    }

    public void setRecords(Set<Record> records) {
        this.records = records;
    }

    public Set<SentTo> getSentTos() {
        return this.sentTos;
    }

    public void setSentTos(Set<SentTo> sentTos) {
        this.sentTos = sentTos;
    }

    public Set<Person> getCollectors() {
        return this.collectors;
    }

    public void setCollectors(Set<Person> collectors) {
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
			if (getMetricDepth(getTopDepth(), getDepthUnit()).equals(getMetricDepth(sample.getTopDepth(), sample.getDepthUnit())) && getBottomDepth() != null && sample.getBottomDepth() != null)
				return getMetricDepth(getBottomDepth(), getDepthUnit()).compareTo(getMetricDepth(sample.getBottomDepth(), sample.getDepthUnit()));
			return getMetricDepth(getTopDepth(), getDepthUnit()).compareTo(getMetricDepth(sample.getTopDepth(), sample.getDepthUnit()));
		} 
		//Anything undepthed goes to the end
		return (getTopDepth() == null) ? 1 : -1;
	}
	
	private static Double getMetricDepth(Double depth, String unit) {
		try {
			if ("m".equals(unit))
				return depth;
			else if ("ft".equals(unit))
				return new Double(depth.doubleValue() * SampleUtil.FT_TO_M);
		} catch (Exception e) {}
		return null;
	}

}
