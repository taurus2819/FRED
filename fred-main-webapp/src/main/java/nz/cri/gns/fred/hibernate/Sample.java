package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.FREDConstants;
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
import nz.cri.gns.fred.model.MetaCat;
import nz.cri.gns.fred.model.SampleStageView;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.util.SampleUtil;

public class Sample implements Serializable, nz.cri.gns.fred.model.Sample, Cloneable {

    private static final long serialVersionUID = 20050818L;

    private Integer sampleId;
    private Double topDepth;
    private Double bottomDepth;
    private String depthUnit;
    private String comments;
    private Date collectionDate;
    private String dateRounding;
    private String stratUnit;
    private String inPlace;
    private String notCollected;
    private String significance;
    private String columnMap;
    private Integer dip;
    private String dipDirection;
    private Integer strike;
    private String facing;
    private String stratComments;
    private String comparatorUsed;
    private String wet;
    private String rockNature;
    private String depositionEnv;
    private String correspondence;
    private ColourModifier colourModifier;
    private Hardness hardness;
    private Weathering weathering;
    private Carbonate carbonate;
    private RockColour rockColourBySecondaryColourId;
    private RockColour rockColourByPrimaryColourId;
    private Feature feature;
    private Audit auditTable;
    private Bedding beddingByPrimaryBeddingId;
    private Bedding beddingBySecondaryBeddingId;
    private FrNumber frNumber;
    private FrNumber yardFrNumber;
    private DrillType drillType;
    private GrainSize grainSizeByPrimaryGrainsizeId;
    private GrainSize grainSizeBySecondaryGrainsizeId;
    private BedThickness bedThickness;
    private Stage stageByKnownStageId;
    private Stage stageByInferredStageId;
    private Set<SedimentaryFeature> sedimentaryFeatures;
    private Set<MetaCat> metaCats;
    private Set<Record> records;
    private Set<SentTo> sentTos;
    private Set<Person> collectors;
    private Set<Relationship> relationships;
    private Set<SampleStageView> sampleStageViews;

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

	public String getStratComments() {
		return stratComments;
	}
	
    public void setStratComments(String stratComments) {
		this.stratComments = stratComments;
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

    public Set<MetaCat> getMetaCats() {
        return this.metaCats;
    }

    public void setMetaCats(Set<MetaCat> metaCats) {
        this.metaCats = metaCats;
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

	public void setSampleStageViews(Set<SampleStageView> sampleStageViews) {
		this.sampleStageViews = sampleStageViews;
	}

	public Set<SampleStageView> getSampleStageViews() {
		return sampleStageViews;
	}

	/**
	 * Compares by top depth then by bottom depth then by object id. Comparing by object id if all else is equal, 
	 * is important here so that this method is consistent with the .equals method.
	 */
	public int compareTo(nz.cri.gns.fred.model.Sample sample) {
		if (feature==null) {
                  return sampleId.compareTo(sample.getSampleId());
                }
                  
                if (feature.equals(sample.getFeature())) {
			if (getTopDepth() != null && sample.getTopDepth() != null) {
				if (getMetricDepth(getTopDepth(), getDepthUnit()).equals(getMetricDepth(sample.getTopDepth(), sample.getDepthUnit())) && getBottomDepth() != null && sample.getBottomDepth() != null){
					if (getMetricDepth(getBottomDepth(), getDepthUnit()).equals(getMetricDepth(sample.getBottomDepth(), sample.getDepthUnit())))
						return (sampleId.intValue() - sample.getSampleId().intValue()); // to be consistent with .equals
					return getMetricDepth(getBottomDepth(), getDepthUnit()).compareTo(getMetricDepth(sample.getBottomDepth(), sample.getDepthUnit()));
				}
				if (getMetricDepth(getTopDepth(), getDepthUnit()).equals(getMetricDepth(sample.getTopDepth(), sample.getDepthUnit())))
					return (sampleId.intValue() - sample.getSampleId().intValue()); // to be consistent with .equals				
				return getMetricDepth(getTopDepth(), getDepthUnit()).compareTo(getMetricDepth(sample.getTopDepth(), sample.getDepthUnit()));
			} 
			//Anything undepthed goes to the end
			return (getTopDepth() == null) ? 1 : -1;
		}
		return feature.compareTo(sample.getFeature());
	}
	
	private static Double getMetricDepth(Double depth, String unit) {
		try {
			if ("m".equals(unit))
				return depth;
			else if ("ft".equals(unit))
				return new Double(depth.doubleValue() * FREDConstants.FT_TO_M);
		} catch (Exception e) {}
		return null;
	}

	@Override
	public String toString() {
		return feature.toString() + ((!feature.getFeatureType().equals(FREDConstants.OUTCROP)) ? ": " + SampleUtil.getDrillHoleDepthDescription(this) : "");
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Sample)) 
			return false;
		return (sampleId.intValue() == ((Sample)obj).getSampleId().intValue());
	}
	
	public int hashCode() {
		return 286 * sampleId;
	}
        
        
        public Sample(){        
        }
        
        /**
         * Partial Constructor for hibernate
         */
        public Sample(int sampleId) {
            this.sampleId= Integer.valueOf(sampleId);
        }
}
