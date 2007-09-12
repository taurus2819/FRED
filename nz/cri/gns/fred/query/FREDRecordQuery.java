package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicDateField;
import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.BasicTextField;
import nz.cri.gns.db.querybuilder.Field;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.advanced.NumberSource;
import nz.cri.gns.db.querybuilder.advanced.PossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredNumberField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredPossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredTextField;
import nz.cri.gns.db.querybuilder.advanced.TwoLevelField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlJoin;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlUniqueSubTablePossibleValueField;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.Folder;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.model.Weathering;

public class FREDRecordQuery extends FREDQuery implements NumberSource {

	private static final long serialVersionUID = 20060120L;
	
	private static final String[] PAL_LIST_TABLES = new String[] {"r.paleontology.listEntries"};
	private static final HqlJoin[] PAL_LIST_JOINS = {new HqlJoin(false, "palList")};

	
	public FREDRecordQuery() {
		super();
	}
	
	protected void addFields() {
		//this is going to be tricky.  Need to handle multiple selections and the ORs in the individual fields
		//Field[] f = new Field[4];
		//f[0] = new FrNumberTextField("frNumber", "Fr Number");
		//f[1] = new FrNumberTextField("mapSheet", "Fr Number Map Sheet");
		//f[2] = new FrNumberNumberField("serialNumber", "Fr Number Serial Number");
		//f[3] = new FrNumberTextField("recollectionNumber", "Fr Number Recollection Number");
		//add(new TwoLevelField("FR Number Fields", f));
		
		Field[] f = new Field[14];
		f[0] = new BasicTextField("r.sample.feature.featureName", "Feature Name");
		f[1] = new PossibleValueField("r.sample.feature.featureType", "Feature Type", getFeatureTypes());
		f[2] = new PossibleValueField("r.sample.feature.masterFile", "Masterfile", getValues("FROM Folder AS f WHERE f.folderType.name='Admin'", Folder.class));
		f[3] = new BasicTextField("r.sample.feature.siteView.nzmgSheet", "NZMS260 Sheet");
		f[4] = new PossibleValueField("r.sample.feature.siteView.qmapSheet", "QMap Sheet", getQMapSheets());
		f[5] = new PossibleValueField("r.sample.feature.siteView.countryCode", "Country", getValues("FROM Country AS c", Country.class));
		f[6] = new PossibleValueField("r.sample.feature.siteView.island", "Island", getSQLValues("SELECT DISTINCT name as n, name FROM sc.island ORDER BY UPPER(name)"));
		f[7] = new BasicNumberField("r.sample.feature.siteView.nzmgEast", "NZMG Easting");
		f[8] = new BasicNumberField("r.sample.feature.siteView.nzmgNorth", "NZMG Northing");
		f[9] = new BasicNumberField("r.sample.feature.siteView.latitude", "Latitude");
		f[10] = new BasicNumberField("r.sample.feature.siteView.longitude", "Longitude");
		f[11] = new BasicTextField("r.sample.feature.locality", "Locality");
		f[12] = new BasicTextField("r.sample.feature.coordComments", "Coordinate Comments");
		f[13] = new BasicTextField("r.sample.feature.comments", "Locality Comments");
		add(new TwoLevelField("Locality Fields", f));
		
		f = new Field[10];
		f[0] = new BasicTextField("r.sample.feature.featureName", "Drillhole Name");
		f[1] = new PossibleValueField("r.sample.feature.person", "Operating Company", people);
		f[2] = new BasicDateField("r.sample.feature.startDate", "Spud Date");
		f[3] = new BasicDateField("r.sample.feature.finishDate", "Completion Date");
		f[4] = new BasicTextField("r.sample.feature.licenceArea", "Licence Area");
		f[5] = new PossibleValueField("r.sample.feature.datumType", "Datum Type", getDrillholeDatumTypes());
		f[6] = new BasicNumberField("r.sample.feature.datumElevation", "Datum Elevation (m)");
		f[7] = new MetricDepthField("r.sample.feature.startDepth", "Kick-off Depth (m)", "r.sample.feature.depthUnit");
		f[8] = new MetricDepthField("r.sample.feature.finishDepth", "Termination Depth (m)", "r.sample.feature.depthUnit");
		f[9] = new PossibleValueField("r.sample.drillType", "Sample Type", getValues("FROM DrillType AS t", DrillType.class));
		add(new TwoLevelField("Drillhole Fields", f));
		
		f = new Field[8];
		f[0] = new BasicTextField("r.sample.feature.featureName", "Vertical Section Name");
		f[1] = new PossibleValueField("r.sample.feature.person", "Section Collector", people);
		f[2] = new BasicDateField("r.sample.feature.startDate", "Sampling Start Date");
		f[3] = new BasicDateField("r.sample.feature.finishDate", "Completion Date");
		f[4] = new PossibleValueField("r.sample.feature.datumType", "Datum Type", getVertSectDatumTypes());
		f[5] = new BasicNumberField("r.sample.feature.datumElevation", "Datum Elevation (m)");
		f[6] = new MetricDepthField("r.sample.feature.startDepth", "Top Horizon (m)", "r.sample.feature.depthUnit");
		f[7] = new MetricDepthField("r.sample.feature.finishDepth", "Base Horizon (m)", "r.sample.feature.depthUnit");
		add(new TwoLevelField("Vertical Section Fields", f));
		
		f = new Field[5];
		f[0] = new HqlUniqueSubTablePossibleValueField("collector.personId", "Collector", people, new String[] {"r.sample.collectors"}, new HqlJoin[] {new HqlJoin(false, "collector")});
		f[1] = new BasicDateField("r.sample.collectionDate", "Collection Date");
		f[2] = new PossibleValueField("r.sample.inPlace", "Fossils In Place", getInPlace());
		f[3] = new BasicTextField("r.sample.notCollected", "Not Collected");
		f[4] = new BasicTextField("r.sample.significance", "Significance/Comments");
		//need to add sent to
		add(new TwoLevelField("Collection Fields", f));
		
		f = new Field[10];
		f[0] = new BasicTextField("r.sample.stratUnit", "Stratigraphic Name");
		f[1] = new BasicAgeField("r.sample.inferredStage", "Inferred Stage", ages);
		f[2] = new BasicNumericAgeField("r.sample.inferredStage", "Inferred Stage (numeric)");
		f[3] = new BasicAgeField("r.sample.knownStage", "Known Stage", ages);
		f[4] = new BasicNumericAgeField("r.sample.knownStage", "Known Stage (numeric)");
		f[5] = new BasicTextField("r.sample.columnMap", "Column/Map");
		f[6] = new BasicNumberField("r.sample.dip", "Dip");
		f[7] = new PossibleValueField("r.sample.dipDirection", "Dip Direction", getDipDirection());
		f[8] = new BasicNumberField("r.sample.strike", "Strike");
		f[9] = new PossibleValueField("r.sample.facing", "Facing", getFacing());
		//need to add relationships
		add(new TwoLevelField("Stratigraphic Fields", f));
		
		f = new Field[15];
		f[0] = new PossibleValueField("r.sample.primaryGrainSize", "Primary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class));
		f[1] = new PossibleValueField("r.sample.secondaryGrainSize", "Secondary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class));
		f[2] = new PossibleValueField("r.sample.comparatorUsed", "Comparator Used", getComparatorUsed());
		f[3] = new PossibleValueField("r.sample.bedThickness", "Bedding Thickness", getValues("FROM BedThickness AS b", BedThickness.class));
		f[4] = new PossibleValueField("r.sample.primaryBedding", "Primary Bedding", getValues("FROM Bedding AS b", Bedding.class));
		f[5] = new PossibleValueField("r.sample.secondaryBedding", "Secondary Bedding", getValues("FROM Bedding AS b", Bedding.class));
		f[6] = new PossibleValueField("r.sample.weathering", "Weathering", getValues("FROM Weathering AS w", Weathering.class));
		f[7] = new PossibleValueField("r.sample.hardness", "Hardness", getValues("FROM Hardness AS h", Hardness.class));
		f[8] = new PossibleValueField("r.sample.carbonate", "Carbonate", getValues("FROM Carbonate AS c", Carbonate.class));
		f[9] = new PossibleValueField("r.sample.colourModifier", "Colour Modifier", getValues("FROM ColourModifier AS c", ColourModifier.class));
		f[10] = new PossibleValueField("r.sample.primaryColour", "Primary Colour", getValues("FROM RockColour AS r", RockColour.class));
		f[11] = new PossibleValueField("r.sample.secondaryColour", "Secondary Colour", getValues("FROM RockColour AS r", RockColour.class));		
		f[12] = new BasicTextField("r.sample.depositionEnv", "Inferred Environment");
		f[13] = new BasicTextField("r.sample.rockNature", "Nature of Rock Unit");
		f[14] = new BasicTextField("r.sample.stratComments", "Stratigraphy Comments");
		//need to add additional features
		add(new TwoLevelField("Sedimentary Feature Fields", f));
		
		f = new Field[1];
		f[0] = new BasicTextField("r.sample.correspondence", "Correspondence");
		add(new TwoLevelField("Correspondence Fields", f));
		
		f = new Field[5];
		f[0] = new HqlUniqueSubTablePossibleValueField("adoptor.personId", "Adoptor", people, new String[] {"r.adoption.adoptors"}, new HqlJoin[] {new HqlJoin(false, "adoptor")});
		f[1] = new BasicDateField("r.adoption.adoptionDate", "Adoption Date");
		f[2] = new BasicAgeField("r.adoption.stage", "Adopted Stage", ages);
		f[3] = new BasicNumericAgeField("r.adoption.stage", "Adopted Stage (numeric)");
		f[4] = new BasicTextField("r.adoption.comments", "Comments");
		add(new TwoLevelField("Adoption Fields", f));
		
		f = new Field[13];
		f[0] = new HqlUniqueSubTablePossibleValueField("identifier.personId", "Identifier", people, new String[] {"r.paleontology.identifiers"}, new HqlJoin[] {new HqlJoin(false, "identifier")});
		f[1] = new BasicDateField("r.paleontology.identificationDate", "Identification Date");
		f[2] = new BasicAgeField("r.paleontology.stage", "Stage", ages);
		f[3] = new BasicNumericAgeField("r.paleontology.stage", "Stage (numeric)");
		f[4] = new BasicTextField("r.paleontology.stageComments", "Stage Comments");
		f[5] = new PossibleValueField("r.paleontology.labSection", "Laboratory", getValues("FROM LabSection AS ls", LabSection.class));
		f[6] = new BasicTextField("r.paleontology.labNumber", "Lab Number");
		f[7] = new BasicTextField("r.paleontology.collectionComments", "Collection Comments");
		f[8] = new TableRequiredPossibleValueField("palList.taxonomicGroup", "Taxonomic Group", getValues("FROM TaxonomicGroup AS tg", TaxonomicGroup.class), PAL_LIST_TABLES, PAL_LIST_JOINS);
		f[9] = new TableRequiredTextField("palList.taxonomicName", "Taxonomic Name", PAL_LIST_TABLES, PAL_LIST_JOINS);
		f[10] = new TableRequiredNumberField("palList.specimenCount", "Specimen Count", PAL_LIST_TABLES, PAL_LIST_JOINS);
		f[11] = new TableRequiredTextField("palList.specimenCoords", "Specimen Coordinates", PAL_LIST_TABLES, PAL_LIST_JOINS);
		f[12] = new TableRequiredTextField("palList.comments", "Paleontology List Comments", PAL_LIST_TABLES, PAL_LIST_JOINS);
		add(new TwoLevelField("Paleontology Fields", f));
		
/*		f = new Field[10];
		f[0] = new PossibleValueField("f.audit.createdById", "Created By", frUsers);
		f[1] = new BasicDateField("f.audit.createdDate", "Created Date");
		f[2] = new PossibleValueField("f.audit.submittedById", "Submitted By", frUsers);
		f[3] = new BasicDateField("f.audit.submittedDate", "Submitted Date");
		f[4] = new PossibleValueField("f.audit.approvedById", "Approved By", frUsers);
		f[5] = new BasicDateField("f.audit.approvedDate", "Approved Date");
		f[6] = new BasicTextField("f.audit.curatorComments", "Curator Comments");
		f[7] = new TableRequiredPossibleValueField("edit.editedById", "Edited By", frUsers, EDIT_TABLE, EDIT_JOIN);
		f[8] = new TableRequiredDateField("edit.editedDate", "Edited Date", EDIT_TABLE, EDIT_JOIN);
		f[9] = new TableRequiredTextField("edit.comments", "Edit Comments", EDIT_TABLE, EDIT_JOIN);
		add(new TwoLevelField("Audit Fields", f)); */
	} 

	public String getHQLQuery() throws InvalidOperatorException, InvalidValueException {
		return super.getHQLQuery("SELECT DISTINCT r", "Record AS r", null, null, null);
	}

}