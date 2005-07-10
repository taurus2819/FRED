
package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import nz.cri.gns.auth.InsufficientPrivelegesException;

public interface DataEntryForm {

	public static final int FEATURE_NAME = 0;
	public static final int FIELD_NUMBER = 0;
	public static final int DRILLHOLE_NAME = 0;
	public static final int SECTION_NAME = 0;
	public static final int REGISTRATION_AREA = 1;
	public static final int WORKING_COMMENTS = 2;
	public static final int EDIT_COMMENTS = 17;
	public static final int GRID_REF = 3;
	public static final int METHOD = 4;
	public static final int ACCURACY = 5;
	public static final int LOCALITY_DESC = 6;
	public static final int RECOLLECTION = 7;
	public static final int SIDETRACK = 7;
	public static final int OPERATING_COMPANY = 8;
	public static final int SECTION_COLLECTOR = 8;
	public static final int SPUD_DATE = 9;
	public static final int START_DATE = 9;
	public static final int COMPLETION_DATE = 10;
	public static final int LICENCE_AREA = 11;
	public static final int DATUM_TYPE = 12;
	public static final int DATUM_ELEVATION = 13;
	public static final int KICK_OFF_DEPTH = 14;
	public static final int TOP_HORIZON = 14;
	public static final int TERMINATION_DEPTH = 15;
	public static final int BASE_HORIZON = 15;
	public static final int SECURITY_TYPE = 16;
	
	//Sample fields
	public static final int COLLECTION_DATE = 31;
	public static final int COLLECTORS = 32;
	public static final int STRAT_NAME= 33;
	public static final int FOSSILS_IN_PLACE = 34;
	public static final int SENT_TO = 35;
	public static final int NOT_COLLECTED = 36;
	public static final int SIGNIFICANCE_COMMENTS = 37;
	public static final int INF_AGE_START = 38;
	public static final int INF_START_MOD = 39;
	public static final int INF_AGE_STOP = 40;
	public static final int INF_STOP_MOD = 41;
	public static final int KNW_AGE_START = 42;
	public static final int KNW_START_MOD = 43;
	public static final int KNW_AGE_STOP = 44;
	public static final int KNW_STOP_MOD = 45;
	public static final int PREVIOUS_SAMPLE = 46;
	public static final int SAMPLE_RELATIONSHIP = 47;
	public static final int STRAT_RELATIONSHIP = 48;
	public static final int COLUMN_MAP = 49;
	public static final int DIP = 50;
	public static final int DIP_DIRECTION = 51;
	public static final int STRIKE = 52;
	public static final int FACING = 53;
	public static final int GRAIN_SIZE_P = 54;
	public static final int GRAIN_SIZE_S = 55;
	public static final int GS_COMP = 56;
	public static final int BEDDING_THICKNESS = 57;
	public static final int BEDDING_P = 58;
	public static final int BEDDING_S = 59;
	public static final int WEATHERING = 60;
	public static final int HARDNESS = 61;
	public static final int CARBONATE = 62;
	public static final int COLOUR_MOD = 63;
	public static final int COLOUR_P = 64;
	public static final int COLOUR_S = 65;
	public static final int WET = 66;
	public static final int SED_FEATURES = 67;
	public static final int DEP_ENVIRONMENT_1 = 68;
	public static final int DEP_ENVIRONMENT_2 = 69;
	public static final int ROCK_NATURE = 70;
	public static final int CORRESPONDENCE = 71;

	//Adoption Record fields
	public static final int ADOPTION_DATE = 81;
	public static final int ADOPTORS = 82;
	public static final int ADO_AGE_START = 83;
	public static final int ADO_START_MOD = 84;
	public static final int ADO_AGE_STOP = 85;
	public static final int ADO_STOP_MOD = 86;
	public static final int ADO_COMMENTS = 87;	

	//Paleontology Record fields
	public static final int IDENTIFICATION_DATE = 101;
	public static final int IDENTIFIERS = 102;
	public static final int IDT_AGE_START = 103;
	public static final int IDT_START_MOD = 104;
	public static final int IDT_AGE_STOP = 105;
	public static final int IDT_STOP_MOD = 106;	
	public static final int STAGE_COMMENTS = 107;
	public static final int LAB_SECTION = 108;
	public static final int LAB_NUMBER = 109;
	public static final int COLLECTION_COMMENTS = 110;
	public static final int TAXA_LIST = 111;
	
	public int getFieldCount();

	public void setField(int field, String value) throws DataInputException, TaxonomicListException;

	public void setTempField(int field, String values);

	public String getField(int field);
	
	public String getTempField(int field);
	
	public void setFieldsFromTemp() throws DataInputException, TaxonomicListException;

	public void copyFrom(int id) throws DataInputException, InsufficientPrivelegesException, SQLException, IOException;

	public void makeNavPanelHTML(Writer out) throws IOException;
	
	public void makeDataEntryHTML(Writer out) throws IOException, SQLException;
	
	public int save() throws SQLException, IOException, InsufficientPrivelegesException;
	
	public int submit() throws SQLException, IOException, InsufficientPrivelegesException, DataInputException;

	public void delete() throws IOException, SQLException, InsufficientPrivelegesException;
	
	public int getWorkingFolderID();

}
