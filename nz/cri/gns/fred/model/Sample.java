package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

/**
 *
 */
public interface Sample extends Audited, Comparable<Sample> {
	public abstract Integer getSampleId();

	public abstract void setSampleId(Integer sampleId);

	public abstract Double getTopDepth();

	public abstract void setTopDepth(Double topDepth);

	public abstract Double getBottomDepth();

	public abstract void setBottomDepth(Double bottomDepth);

	public abstract String getComments();

	public abstract void setComments(String comments);

	public abstract Date getCollectionDate();

	public abstract void setCollectionDate(Date collectionDate);

	public abstract String getDateRounding();

	public abstract void setDateRounding(String dateRounding);

	public abstract String getStratUnit();

	public abstract void setStratUnit(String stratUnit);

	public abstract String getInPlace();

	public abstract void setInPlace(String inPlace);

	public abstract String getNotCollected();

	public abstract void setNotCollected(String notCollected);

	public abstract String getSignificance();

	public abstract void setSignificance(String significance);

	public abstract String getColumnMap();

	public abstract void setColumnMap(String columnMap);

	public abstract Integer getDip();

	public abstract void setDip(Integer dip);

	public abstract String getDipDirection();

	public abstract void setDipDirection(String dipDirection);

	public abstract Integer getStrike();

	public abstract void setStrike(Integer strike);

	public abstract String getFacing();

	public abstract void setFacing(String facing);

	public abstract String getComparatorUsed();

	public abstract void setComparatorUsed(String comparatorUsed);

	public abstract String getWet();

	public abstract void setWet(String wet);

	public abstract String getRockNature();

	public abstract void setRockNature(String rockNature);

	public abstract String getDepositionEnv();

	public abstract void setDepositionEnv(String depositionEnv);

	public abstract String getCorrespondence();

	public abstract void setCorrespondence(String correspondence);

	public abstract nz.cri.gns.fred.model.ColourModifier getColourModifier();

	public abstract void setColourModifier(
			nz.cri.gns.fred.model.ColourModifier colourModifier);

	public abstract nz.cri.gns.fred.model.Hardness getHardness();

	public abstract void setHardness(nz.cri.gns.fred.model.Hardness hardness);

	public abstract nz.cri.gns.fred.model.Weathering getWeathering();

	public abstract void setWeathering(
			nz.cri.gns.fred.model.Weathering weathering);

	public abstract nz.cri.gns.fred.model.Carbonate getCarbonate();

	public abstract void setCarbonate(
			nz.cri.gns.fred.model.Carbonate carbonate);

	public abstract nz.cri.gns.fred.model.RockColour getSecondaryColour();

	public abstract void setSecondaryColour(
			nz.cri.gns.fred.model.RockColour rockColourBySecondaryColourId);

	public abstract nz.cri.gns.fred.model.RockColour getPrimaryColour();

	public abstract void setPrimaryColour(
			nz.cri.gns.fred.model.RockColour rockColourByPrimaryColourId);

	public abstract nz.cri.gns.fred.model.Feature getFeature();

	public abstract void setFeature(nz.cri.gns.fred.model.Feature feature);

	public abstract nz.cri.gns.fred.model.Bedding getPrimaryBedding();

	public abstract void setPrimaryBedding(
			nz.cri.gns.fred.model.Bedding beddingByPrimaryBeddingId);

	public abstract nz.cri.gns.fred.model.Bedding getSecondaryBedding();

	public abstract void setSecondaryBedding(
			nz.cri.gns.fred.model.Bedding beddingBySecondaryBeddingId);

	public abstract nz.cri.gns.fred.model.FrNumber getFrNumber();

	public abstract void setFrNumber(nz.cri.gns.fred.model.FrNumber frNumber);

	public abstract FrNumber getYardFrNumber();

	public abstract void setYardFrNumber(FrNumber yardFrNumber);
	
	public abstract nz.cri.gns.fred.model.DrillType getDrillType();

	public abstract void setDrillType(
			nz.cri.gns.fred.model.DrillType drillType);

	public abstract nz.cri.gns.fred.model.GrainSize getPrimaryGrainSize();

	public abstract void setPrimaryGrainSize(
			nz.cri.gns.fred.model.GrainSize grainSizeByPrimaryGrainsizeId);

	public abstract nz.cri.gns.fred.model.GrainSize getSecondaryGrainSize();

	public abstract void setSecondaryGrainSize(
			nz.cri.gns.fred.model.GrainSize grainSizeBySecondaryGrainSize);

	public abstract nz.cri.gns.fred.model.BedThickness getBedThickness();

	public abstract void setBedThickness(
			nz.cri.gns.fred.model.BedThickness bedThickness);

	public abstract nz.cri.gns.fred.model.Stage getKnownStage();

	public abstract void setKnownStage(
			nz.cri.gns.fred.model.Stage stageByKnownStageId);

	public abstract nz.cri.gns.fred.model.Stage getInferredStage();

	public abstract void setInferredStage(
			nz.cri.gns.fred.model.Stage stageByInferredStageId);

	public abstract Set<SedimentaryFeature> getSedimentaryFeatures();

	public abstract void setSedimentaryFeatures(Set<SedimentaryFeature> sedimentaryFeatures);

	public abstract Set<SampleMeta> getSampleMetas();

	public abstract void setSampleMetas(Set<SampleMeta> sampleMetas);

	public abstract Set<Record> getRecords();

	public abstract void setRecords(Set<Record> records);

	public abstract Set<SentTo> getSentTos();

	public abstract void setSentTos(Set<SentTo> sentTos);

	public abstract Set<Person> getCollectors();

	public abstract void setCollectors(Set<Person> collectors);

	public abstract Set<Relationship> getRelationships();

	public abstract void setRelationships(Set<Relationship> relationships);
}