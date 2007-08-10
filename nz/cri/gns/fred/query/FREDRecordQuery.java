package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicDateField;
import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.BasicTextField;
import nz.cri.gns.db.querybuilder.Field;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.advanced.NumberSource;
import nz.cri.gns.db.querybuilder.advanced.PossibleValueField;
import nz.cri.gns.db.querybuilder.advanced.TwoLevelField;
import nz.cri.gns.fred.model.Country;
import nz.cri.gns.fred.model.Folder;

public class FREDRecordQuery extends FREDQuery implements NumberSource {

	private static final long serialVersionUID = 20060120L;
	
	/*private static final String SAMPLE_TABLE = "f.samples";
	private static final HqlJoin SAMPLE_JOIN = new HqlJoin(false, "sample");
	private static final String[] RECORD_TABLES = new String[] {"f.samples", "sample.records"};
	private static final HqlJoin[] RECORD_JOINS = {new HqlJoin(false, "sample"), new HqlJoin(false, "record")};
	private static final String[] PAL_LIST_TABLES = new String[] {"f.samples", "sample.records", "record.paleontology.listEntries"};
	private static final HqlJoin[] PAL_LIST_JOINS = {new HqlJoin(false, "sample"), new HqlJoin(false, "record"), new HqlJoin(false, "palList")};
	private static final String EDIT_TABLE = "f.audit.auditEdits";
	private static final HqlJoin EDIT_JOIN = new HqlJoin(false, "edit");
	*/
	
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
		
		f = new Field[9];
		f[0] = new BasicTextField("r.sample.feature.featureName", "Drillhole Name");
		f[1] = new PossibleValueField("r.sample.feature.person", "Operating Company", people);
		f[2] = new BasicDateField("r.sample.feature.startDate", "Spud Date");
		f[3] = new BasicDateField("r.sample.feature.finishDate", "Completion Date");
		f[4] = new BasicTextField("r.sample.feature.licenceArea", "Licence Area");
		f[5] = new PossibleValueField("r.sample.feature.datumType", "Datum Type", getDrillholeDatumTypes());
		f[6] = new BasicNumberField("r.sample.feature.datumElevation", "Datum Elevation (m)");
		f[7] = new MetricDepthField("r.sample.feature.startDepth", "Kick-off Depth (m)", "r.sample.feature.depthUnit");
		f[8] = new MetricDepthField("r.sample.feature.finishDepth", "Termination Depth (m)", "r.sample.feature.depthUnit");
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
		
//		f = new Field[5];
//		f[0] = new HqlUniqueSubTablePossibleValueField("collector.personId", "Collector", people, new String[] {"f.samples", "sample.collectors"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "collector")});
//		f[1] = new TableRequiredDateField("sample.collectionDate", "Collection Date", SAMPLE_TABLE, SAMPLE_JOIN);
//		f[2] = new TableRequiredPossibleValueField("sample.inPlace", "Fossils In Place", getInPlace(), SAMPLE_TABLE, SAMPLE_JOIN);
//		f[3] = new TableRequiredTextField("sample.notCollected", "Not Collected", SAMPLE_TABLE, SAMPLE_JOIN);
//		f[4] = new TableRequiredTextField("sample.significance", "Significance/Comments", SAMPLE_TABLE, SAMPLE_JOIN);
		//need to add sent to
//		add(new TwoLevelField("Collection Fields", f));
		
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
		
/*		f = new Field[14];
		f[0] = new TableRequiredPossibleValueField("sample.primaryGrainSize", "Primary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[1] = new TableRequiredPossibleValueField("sample.secondaryGrainSize", "Secondary Grain Size", getValues("FROM GrainSize AS g", GrainSize.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[2] = new TableRequiredPossibleValueField("sample.comparatorUsed", "Comparator Used", getComparatorUsed(), SAMPLE_TABLE, SAMPLE_JOIN);
		f[3] = new TableRequiredPossibleValueField("sample.bedThickness", "Bedding Thickness", getValues("FROM BedThickness AS b", BedThickness.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[4] = new TableRequiredPossibleValueField("sample.primaryBedding", "Primary Bedding", getValues("FROM Bedding AS b", Bedding.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[5] = new TableRequiredPossibleValueField("sample.secondaryBedding", "Secondary Bedding", getValues("FROM Bedding AS b", Bedding.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[6] = new TableRequiredPossibleValueField("sample.weathering", "Weathering", getValues("FROM Weathering AS w", Weathering.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[7] = new TableRequiredPossibleValueField("sample.hardness", "Hardness", getValues("FROM Hardness AS h", Hardness.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[8] = new TableRequiredPossibleValueField("sample.carbonate", "Carbonate", getValues("FROM Carbonate AS c", Carbonate.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[9] = new TableRequiredPossibleValueField("sample.colourModifier", "Colour Modifier", getValues("FROM ColourModifier AS c", ColourModifier.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[10] = new TableRequiredPossibleValueField("sample.primaryColour", "Primary Colour", getValues("FROM RockColour AS r", RockColour.class), SAMPLE_TABLE, SAMPLE_JOIN);
		f[11] = new TableRequiredPossibleValueField("sample.secondaryColour", "Secondary Colour", getValues("FROM RockColour AS r", RockColour.class), SAMPLE_TABLE, SAMPLE_JOIN);		
		f[12] = new TableRequiredTextField("sample.depositionEnv", "Inferred Environment", SAMPLE_TABLE, SAMPLE_JOIN);
		f[13] = new TableRequiredTextField("sample.rockNature", "Nature of Rock Unit", SAMPLE_TABLE, SAMPLE_JOIN);
		//need to add additional features
		add(new TwoLevelField("Sedimentary Feature Fields", f));
		
		f = new Field[1];
		f[0] = new TableRequiredTextField("sample.correspondence", "Correspondence", SAMPLE_TABLE, SAMPLE_JOIN);
		add(new TwoLevelField("Correspondence Fields", f));
		
		f = new Field[4];
		//f[0] = new HqlUniqueSubTablePossibleValueField("adoptor.personId", "Adoptor", people, new String[] {"f.samples", "sample.records", "record.adoption", "adoption.adoptors"}, new HqlJoin[] {new HqlJoin(false, "sample"), new HqlJoin(false, "record"), new HqlJoin(false, "adoption"), new HqlJoin(false, "adoptor")});
		f[0] = new TableRequiredDateField("record.adoption.adoptionDate", "Adoption Date", RECORD_TABLES, RECORD_JOINS);
		f[1] = new AgeField("record.adoption.stage", "Adopted Stage", ages, RECORD_TABLES, RECORD_JOINS);
		f[2] = new NumericAgeField("record.adoption.stage", "Adopted Stage (numeric)", RECORD_TABLES, RECORD_JOINS);
		f[3] = new TableRequiredTextField("record.adoption.comments", "Comments", RECORD_TABLES, RECORD_JOINS);
		//need to add adoptors
		add(new TwoLevelField("Adoption Fields", f));
		
		f = new Field[12];
		f[0] = new TableRequiredDateField("record.paleontology.identificationDate", "Identification Date", RECORD_TABLES, RECORD_JOINS);
		f[1] = new AgeField("record.paleontology.stage", "Stage", ages, RECORD_TABLES, RECORD_JOINS);
		f[2] = new NumericAgeField("record.paleontology.stage", "Stage (numeric)", RECORD_TABLES, RECORD_JOINS);
		f[3] = new TableRequiredTextField("record.paleontology.stageComments", "Stage Comments", RECORD_TABLES, RECORD_JOINS);
		f[4] = new TableRequiredPossibleValueField("record.paleontology.labSection", "Laboratory", getValues("FROM LabSection AS ls", LabSection.class), RECORD_TABLES, RECORD_JOINS);
		f[5] = new TableRequiredTextField("record.paleontology.labNumber", "Lab Number", RECORD_TABLES, RECORD_JOINS);
		f[6] = new TableRequiredTextField("record.paleontology.collectionComments", "Collection Comments", RECORD_TABLES, RECORD_JOINS);
		f[7] = new TableRequiredPossibleValueField("palList.taxonomicGroup", "Taxonomic Group", getValues("FROM TaxonomicGroup AS tg", TaxonomicGroup.class), PAL_LIST_TABLES, PAL_LIST_JOINS);
		f[8] = new TableRequiredTextField("palList.taxonomicName", "Taxonomic Name", PAL_LIST_TABLES, PAL_LIST_JOINS);
		f[9] = new TableRequiredNumberField("palList.specimenCount", "Specimen Count", PAL_LIST_TABLES, PAL_LIST_JOINS);
		f[10] = new TableRequiredTextField("palList.specimenCoords", "Specimen Coordinates", PAL_LIST_TABLES, PAL_LIST_JOINS);
		f[11] = new TableRequiredTextField("palList.comments", "Paleontology List Comments", PAL_LIST_TABLES, PAL_LIST_JOINS);
		//need to add identifiers
		add(new TwoLevelField("Paleontology Fields", f));
		
		f = new Field[10];
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
		return super.getHQLQuery("SELECT DISTINCT r", "Record AS r", "r.audit.confidentialFlag = TRUE", null, null);
	}

}