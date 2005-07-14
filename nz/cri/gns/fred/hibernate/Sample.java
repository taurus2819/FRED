package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class Sample implements Serializable {

    /** identifier field */
    private Integer sampleId;

    /** nullable persistent field */
    private double topDepth;

    /** nullable persistent field */
    private double bottomDepth;

    /** nullable persistent field */
    private String comments;

    /** nullable persistent field */
    private Integer yardFrId;

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
    private nz.cri.gns.fred.hibernate.ColourModifier colourModifier;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Hardness hardness;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Weathering weathering;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Carbonate carbonate;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.RockColour rockColourBySecondaryColourId;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.RockColour rockColourByPrimaryColourId;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Feature feature;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.AuditTable auditTable;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Bedding beddingByPrimaryBeddingId;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Bedding beddingBySecondaryBeddingId;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.FrNumber frNumber;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.DrillType drillType;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.GrainSize grainSizeByPrimaryGrainsizeId;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.GrainSize grainSizeBySecondaryGrainsizeId;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.BedThickness bedThickness;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Stage stageByKnownStageId;

    /** persistent field */
    private nz.cri.gns.fred.hibernate.Stage stageByInferredStageId;

    /** persistent field */
    private Set sedimentaryFeatures;

    /** persistent field */
    private Set sampleMetas;

    /** persistent field */
    private Set records;

    /** persistent field */
    private Set sentTos;

    /** persistent field */
    private Set collectors;

    /** persistent field */
    private Set relationships;

    /** full constructor */
    public Sample(double topDepth, double bottomDepth, String comments, Integer yardFrId, Date collectionDate, String dateRounding, String stratUnit, String inPlace, String notCollected, String significance, String columnMap, Integer dip, String dipDirection, Integer strike, String facing, String comparatorUsed, String wet, String rockNature, String depositionEnv, String correspondence, String sampleName, nz.cri.gns.fred.hibernate.ColourModifier colourModifier, nz.cri.gns.fred.hibernate.Hardness hardness, nz.cri.gns.fred.hibernate.Weathering weathering, nz.cri.gns.fred.hibernate.Carbonate carbonate, nz.cri.gns.fred.hibernate.RockColour rockColourBySecondaryColourId, nz.cri.gns.fred.hibernate.RockColour rockColourByPrimaryColourId, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.AuditTable auditTable, nz.cri.gns.fred.hibernate.Bedding beddingByPrimaryBeddingId, nz.cri.gns.fred.hibernate.Bedding beddingBySecondaryBeddingId, nz.cri.gns.fred.hibernate.FrNumber frNumber, nz.cri.gns.fred.hibernate.DrillType drillType, nz.cri.gns.fred.hibernate.GrainSize grainSizeByPrimaryGrainsizeId, nz.cri.gns.fred.hibernate.GrainSize grainSizeBySecondaryGrainsizeId, nz.cri.gns.fred.hibernate.BedThickness bedThickness, nz.cri.gns.fred.hibernate.Stage stageByKnownStageId, nz.cri.gns.fred.hibernate.Stage stageByInferredStageId, Set sedimentaryFeatures, Set sampleMetas, Set records, Set sentTos, Set collectors, Set relationships) {
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
    public Sample(nz.cri.gns.fred.hibernate.ColourModifier colourModifier, nz.cri.gns.fred.hibernate.Hardness hardness, nz.cri.gns.fred.hibernate.Weathering weathering, nz.cri.gns.fred.hibernate.Carbonate carbonate, nz.cri.gns.fred.hibernate.RockColour rockColourBySecondaryColourId, nz.cri.gns.fred.hibernate.RockColour rockColourByPrimaryColourId, nz.cri.gns.fred.hibernate.Feature feature, nz.cri.gns.fred.hibernate.AuditTable auditTable, nz.cri.gns.fred.hibernate.Bedding beddingByPrimaryBeddingId, nz.cri.gns.fred.hibernate.Bedding beddingBySecondaryBeddingId, nz.cri.gns.fred.hibernate.FrNumber frNumber, nz.cri.gns.fred.hibernate.DrillType drillType, nz.cri.gns.fred.hibernate.GrainSize grainSizeByPrimaryGrainsizeId, nz.cri.gns.fred.hibernate.GrainSize grainSizeBySecondaryGrainsizeId, nz.cri.gns.fred.hibernate.BedThickness bedThickness, nz.cri.gns.fred.hibernate.Stage stageByKnownStageId, nz.cri.gns.fred.hibernate.Stage stageByInferredStageId, Set sedimentaryFeatures, Set sampleMetas, Set records, Set sentTos, Set collectors, Set relationships) {
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

    public double getTopDepth() {
        return this.topDepth;
    }

    public void setTopDepth(double topDepth) {
        this.topDepth = topDepth;
    }

    public double getBottomDepth() {
        return this.bottomDepth;
    }

    public void setBottomDepth(double bottomDepth) {
        this.bottomDepth = bottomDepth;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getYardFrId() {
        return this.yardFrId;
    }

    public void setYardFrId(Integer yardFrId) {
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

    public nz.cri.gns.fred.hibernate.ColourModifier getColourModifier() {
        return this.colourModifier;
    }

    public void setColourModifier(nz.cri.gns.fred.hibernate.ColourModifier colourModifier) {
        this.colourModifier = colourModifier;
    }

    public nz.cri.gns.fred.hibernate.Hardness getHardness() {
        return this.hardness;
    }

    public void setHardness(nz.cri.gns.fred.hibernate.Hardness hardness) {
        this.hardness = hardness;
    }

    public nz.cri.gns.fred.hibernate.Weathering getWeathering() {
        return this.weathering;
    }

    public void setWeathering(nz.cri.gns.fred.hibernate.Weathering weathering) {
        this.weathering = weathering;
    }

    public nz.cri.gns.fred.hibernate.Carbonate getCarbonate() {
        return this.carbonate;
    }

    public void setCarbonate(nz.cri.gns.fred.hibernate.Carbonate carbonate) {
        this.carbonate = carbonate;
    }

    public nz.cri.gns.fred.hibernate.RockColour getRockColourBySecondaryColourId() {
        return this.rockColourBySecondaryColourId;
    }

    public void setRockColourBySecondaryColourId(nz.cri.gns.fred.hibernate.RockColour rockColourBySecondaryColourId) {
        this.rockColourBySecondaryColourId = rockColourBySecondaryColourId;
    }

    public nz.cri.gns.fred.hibernate.RockColour getRockColourByPrimaryColourId() {
        return this.rockColourByPrimaryColourId;
    }

    public void setRockColourByPrimaryColourId(nz.cri.gns.fred.hibernate.RockColour rockColourByPrimaryColourId) {
        this.rockColourByPrimaryColourId = rockColourByPrimaryColourId;
    }

    public nz.cri.gns.fred.hibernate.Feature getFeature() {
        return this.feature;
    }

    public void setFeature(nz.cri.gns.fred.hibernate.Feature feature) {
        this.feature = feature;
    }

    public nz.cri.gns.fred.hibernate.AuditTable getAuditTable() {
        return this.auditTable;
    }

    public void setAuditTable(nz.cri.gns.fred.hibernate.AuditTable auditTable) {
        this.auditTable = auditTable;
    }

    public nz.cri.gns.fred.hibernate.Bedding getBeddingByPrimaryBeddingId() {
        return this.beddingByPrimaryBeddingId;
    }

    public void setBeddingByPrimaryBeddingId(nz.cri.gns.fred.hibernate.Bedding beddingByPrimaryBeddingId) {
        this.beddingByPrimaryBeddingId = beddingByPrimaryBeddingId;
    }

    public nz.cri.gns.fred.hibernate.Bedding getBeddingBySecondaryBeddingId() {
        return this.beddingBySecondaryBeddingId;
    }

    public void setBeddingBySecondaryBeddingId(nz.cri.gns.fred.hibernate.Bedding beddingBySecondaryBeddingId) {
        this.beddingBySecondaryBeddingId = beddingBySecondaryBeddingId;
    }

    public nz.cri.gns.fred.hibernate.FrNumber getFrNumber() {
        return this.frNumber;
    }

    public void setFrNumber(nz.cri.gns.fred.hibernate.FrNumber frNumber) {
        this.frNumber = frNumber;
    }

    public nz.cri.gns.fred.hibernate.DrillType getDrillType() {
        return this.drillType;
    }

    public void setDrillType(nz.cri.gns.fred.hibernate.DrillType drillType) {
        this.drillType = drillType;
    }

    public nz.cri.gns.fred.hibernate.GrainSize getGrainSizeByPrimaryGrainsizeId() {
        return this.grainSizeByPrimaryGrainsizeId;
    }

    public void setGrainSizeByPrimaryGrainsizeId(nz.cri.gns.fred.hibernate.GrainSize grainSizeByPrimaryGrainsizeId) {
        this.grainSizeByPrimaryGrainsizeId = grainSizeByPrimaryGrainsizeId;
    }

    public nz.cri.gns.fred.hibernate.GrainSize getGrainSizeBySecondaryGrainsizeId() {
        return this.grainSizeBySecondaryGrainsizeId;
    }

    public void setGrainSizeBySecondaryGrainsizeId(nz.cri.gns.fred.hibernate.GrainSize grainSizeBySecondaryGrainsizeId) {
        this.grainSizeBySecondaryGrainsizeId = grainSizeBySecondaryGrainsizeId;
    }

    public nz.cri.gns.fred.hibernate.BedThickness getBedThickness() {
        return this.bedThickness;
    }

    public void setBedThickness(nz.cri.gns.fred.hibernate.BedThickness bedThickness) {
        this.bedThickness = bedThickness;
    }

    public nz.cri.gns.fred.hibernate.Stage getStageByKnownStageId() {
        return this.stageByKnownStageId;
    }

    public void setStageByKnownStageId(nz.cri.gns.fred.hibernate.Stage stageByKnownStageId) {
        this.stageByKnownStageId = stageByKnownStageId;
    }

    public nz.cri.gns.fred.hibernate.Stage getStageByInferredStageId() {
        return this.stageByInferredStageId;
    }

    public void setStageByInferredStageId(nz.cri.gns.fred.hibernate.Stage stageByInferredStageId) {
        this.stageByInferredStageId = stageByInferredStageId;
    }

    public Set getSedimentaryFeatures() {
        return this.sedimentaryFeatures;
    }

    public void setSedimentaryFeatures(Set sedimentaryFeatures) {
        this.sedimentaryFeatures = sedimentaryFeatures;
    }

    public Set getSampleMetas() {
        return this.sampleMetas;
    }

    public void setSampleMetas(Set sampleMetas) {
        this.sampleMetas = sampleMetas;
    }

    public Set getRecords() {
        return this.records;
    }

    public void setRecords(Set records) {
        this.records = records;
    }

    public Set getSentTos() {
        return this.sentTos;
    }

    public void setSentTos(Set sentTos) {
        this.sentTos = sentTos;
    }

    public Set getCollectors() {
        return this.collectors;
    }

    public void setCollectors(Set collectors) {
        this.collectors = collectors;
    }

    public Set getRelationships() {
        return this.relationships;
    }

    public void setRelationships(Set relationships) {
        this.relationships = relationships;
    }

 
}
