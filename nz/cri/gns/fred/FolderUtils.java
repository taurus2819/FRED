package nz.cri.gns.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.FRNumber;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.fred.dataentry.DataEntryFormFactory;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.LocalityDE;
import nz.cri.gns.fred.dataentry.RecordDE;
import nz.cri.gns.fred.dataentry.SampleDE;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;


public class FolderUtils {
	
	public static void addFolder(String name, User user, PageState state) throws SQLException, IOException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		if (name.length() > 32) {
			name = name.substring(0, 31);
		}
		conn.executeUpdate("INSERT INTO Folder (Name, Owner_ID, Folder_Type) VALUES (" + JspUtils.sqlEscape(name) + ", " + user.getPersonId() + ", 'personal')");
		conn.releaseStatement();	
	}
	
	public static void deleteFolder(String foldID, User user, PageState state) throws IOException, InvalidCredentialsException, SQLException, FolderUtilException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		Folder folder = new Folder(Integer.parseInt(foldID), user, state);
		if (folder.isAllowedAdmin() && folder.getLocalityCount() == 0) {
			conn.executeUpdate("DELETE FROM Folder WHERE Folder_ID = " + foldID);
		} else {
			throw new FolderUtilException("Cannot delete folder as either insufficient privileges or folder not empty");
		}
		conn.releaseStatement();
	}
	
	public static void deleteLocality(String featID, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.delete();
	}
	
	public static void deleteSample(String sampleID, User user, PageState state) throws NumberFormatException, InvalidCredentialsException, DataInputException, SQLException, IOException {
		Sample sample = new Sample(Integer.parseInt(sampleID), user, state);
		String featureID = sample.getAsString(Sample.FEATURE_ID);
		String featAuditID = sample.getAsString(Sample.FEATURE_AUDIT_ID);
		SampleDE form = DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(sampleID), user, state);
		form.delete();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT COUNT(*) FROM Sample WHERE Feature_ID = " + featureID);
		rs.next();
		if (rs.getInt(1) == 0) {
			conn.executeUpdate("INSERT INTO Sample (Feature_ID, Audit_ID) VALUES (" + featureID + ", " + featAuditID + ")");
		}	
	}
	
	public static void deleteRecord(String recID, User user, PageState state) throws NumberFormatException, DataInputException, InvalidCredentialsException, SQLException, IOException {
		RecordDE form = DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recID), user, state);
		form.delete();
	}

	public static void submitLocality(String featID, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.submit();
	}
	
	public static void submitSample(String sampleID, User user, PageState state) throws NumberFormatException, IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		SampleDE form = DataEntryFormFactory.getSampleDataEntryForm(Integer.parseInt(sampleID), user, state);
		form.submit();
	}
	
	public static void submitRecord(String recID, User user, PageState state) throws NumberFormatException, DataInputException, InvalidCredentialsException, SQLException, IOException {
		RecordDE form = DataEntryFormFactory.getRecordDataEntryForm(Integer.parseInt(recID), user, state);
		form.submit();
	}
	
	public static void revokeLocality(String featID, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.revoke();	
	}
	
	public static void approveLocality(String featID, FRNumber frNum, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.approve(frNum);			
	}
	
	public static void rejectLocality(String featID, String comments, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
		LocalityDE form = DataEntryFormFactory.getLocalityDataEntryForm(Integer.parseInt(featID), user, state);
		form.reject(comments);			
	}
	
	public static void copyLocality(String oldFeatID, String newFeatName, String foldID, User user, PageState state) throws IOException, SQLException {
		//TODO fix statements - statement2 and 3 are not seperate
		int userID = user.getPersonId();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		Statement statement2 = conn.getExtraStatement();
		Statement statement3 = conn.getExtraStatement();
		ResultSet rs2, rs3;
		String sampID, oldSampID, recID, oldRecID;
		ResultSet rs = conn.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
		rs.next();
		String auditID = rs.getString(1);
		conn.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Folder_ID, Security_Class_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + foldID + ", 4)");
		rs = conn.executeQuery("SELECT Feature_Seq.NEXTVAL FROM DUAL");
		rs.next();
		String featID = rs.getString(1);
		conn.executeUpdate("INSERT INTO Feature (Feature_ID, Site_ID, Audit_ID, Masterfile_ID, Feature_Type, Feature_Name, Locality, Reg_Area_ID, Person_ID, Start_Date, Start_Date_Rounding, Finish_Date, Finish_Date_Rounding, Drillhole_Licence_Name, Datum_Type, Datum_Elevation, Start_Depth, Finish_Depth, Comments) SELECT " + featID + " AS FeatID, Site_ID, " + auditID + " AS AuditID, Masterfile_ID, Feature_Type, " + JspUtils.sqlEscape(newFeatName) + " AS FeatName, Locality, Reg_Area_ID, Person_ID, Start_Date, Start_Date_Rounding, Finish_Date, Finish_Date_Rounding, Drillhole_Licence_Name, Datum_Type, Datum_Elevation, Start_Depth, Finish_Depth, Comments FROM Feature WHERE Feature_ID = " + oldFeatID);
		rs = conn.executeQuery("SELECT Sample_ID FROM Sample WHERE Feature_ID = " + oldFeatID);
		while (rs.next()) {
			oldSampID = rs.getString(1);
			rs2 = statement2.executeQuery("SELECT Sample_Seq.NEXTVAL FROM DUAL");
			rs2.next();
			sampID = rs2.getString(1);
			statement2.executeUpdate("INSERT INTO Sample (Sample_ID, Feature_ID, Top_Depth, Bottom_Depth, Drill_Type_ID, Comments) SELECT " + sampID + " AS SampID, " + featID + " AS FeatID, Top_Depth, Bottom_Depth, Drill_Type_ID, Comments FROM Sample WHERE Sample_ID = " + oldSampID);
			rs2 = statement2.executeQuery("SELECT Record_ID, Record_Type FROM Record_All_View WHERE Sample_ID = " + oldSampID);
			while (rs2.next()) {
				oldRecID = rs2.getString(1);
				rs3 = statement3.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
				rs3.next();
				auditID = rs3.getString(1);
				statement3.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Folder_ID, Security_Class_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + foldID + ", 4)");
				rs3 = statement3.executeQuery("SELECT Record_Seq.NEXTVAL FROM DUAL");
				rs3.next();
				recID = rs3.getString(1);
				statement3.executeUpdate("INSERT INTO Record (Record_ID, Sample_ID, Audit_ID) VALUES (" + recID + ", " + sampID + ", " + auditID + ")");
				if (rs2.getString(2).equals("SMP")) {
					statement3.executeUpdate("INSERT INTO Sample_Property (Record_ID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage_ID, Known_Stage_ID, Column_Map, Dip, Dip_Direction, Strike, Facing, Primary_Grainsize_ID, Secondary_Grainsize_ID, Comparator_Used, Bed_Thick_ID, Primary_Bedding_ID, Secondary_Bedding_ID, Weathering_ID, Hardness_ID, Carbonate_ID, Colour_Modifier_ID, Primary_Colour_ID, Secondary_Colour_ID, Wet, Rock_Nature, Deposition_Env, Correspondence) SELECT " + recID + " AS RecID, Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Inferred_Stage_ID, Known_Stage_ID, Column_Map, Dip, Dip_Direction, Strike, Facing, Primary_Grainsize_ID, Secondary_Grainsize_ID, Comparator_Used, Bed_Thick_ID, Primary_Bedding_ID, Secondary_Bedding_ID, Weathering_ID, Hardness_ID, Carbonate_ID, Colour_Modifier_ID, Primary_Colour_ID, Secondary_Colour_ID, Wet, Rock_Nature, Deposition_Env, Correspondence FROM Sample_Property WHERE Record_ID = " + oldRecID);
					statement3.executeUpdate("INSERT INTO Collector (Record_ID, Person_ID) SELECT " + recID + " AS RecID, Person_ID FROM Collector WHERE Record_ID = " + oldRecID);
					statement3.executeUpdate("INSERT INTO Relationship (Relationship_ID, Record_ID, Relationship_Type, Related_Feature_ID, Strat_Unit, Distance, Distance_Range, Distance_Mod, Relation_Type_ID) SELECT Relationship_Seq.NEXTVAL, " + recID + " AS RecID, Relationship_Type, Related_Feature_ID, Strat_Unit, Distance, Distance_Range, Distance_Mod, Relation_Type_ID FROM Relationship WHERE Record_ID = " + oldRecID);
					statement3.executeUpdate("INSERT INTO Sedimentary_Feature (Record_ID, Sed_Feature_ID, Abundant) SELECT " + recID + " AS RecID, Sed_Feature_ID, Abundant FROM Sedimentary_Feature WHERE Record_ID = " + oldRecID);
					statement3.executeUpdate("INSERT INTO Sent_To (Record_ID, Fossil_Group_ID, Person_ID, Lab_ID, Comments) SELECT " + recID + " AS RecID, Fossil_Group_ID, Person_ID, Lab_ID, Comments FROM Sent_To WHERE Record_ID = " + oldRecID);
				} else if (rs2.getString(2).equals("ADO")) {
					statement3.executeUpdate("INSERT INTO Adoption (Record_ID, Adoption_Date, Date_Rounding, Adopted_Stage_ID, Comments) SELECT " + recID + " AS RecID, Adoption_Date, Date_Rounding, Adopted_Stage_ID, Comments FROM Adoption WHERE Record_ID = " + oldRecID);
					statement3.executeUpdate("INSERT INTO Adoptor (Record_ID, Person_ID) SELECT " + recID + " AS RecID, Person_ID FROM Adoptor WHERE Record_ID = " + oldRecID);
				} else if (rs2.getString(2).equals("PAL")) {
					statement3.executeUpdate("INSERT INTO Paleontology (Record_ID, Identification_Date, Date_Rounding, Stage_ID, Stage_Comments, Lab_Section_ID, Lab_Number, Collection_Comments) SELECT " + recID + " AS RecID, Identification_Date, Date_Rounding, Stage_ID, Stage_Comments, Lab_Section_ID, Lab_Number, Collection_Comments FROM Paleontology WHERE Record_ID = " + oldRecID);
					statement3.executeUpdate("INSERT INTO Identifier (Record_ID, Person_ID) SELECT " + recID + " AS RecID, Person_ID FROM Identifier WHERE Record_ID = " + oldRecID);
					statement3.executeUpdate("INSERT INTO Pal_List (Pal_List_ID, Record_ID, Group_ID, Taxa_ID, Taxonomic_Name, Specimen_Count, Specimen_Coords, Comments) SELECT Pal_List_Seq.NEXTVAL, " + recID + " AS RecID, Group_ID, Taxa_ID, Taxonomic_Name, Specimen_Count, Specimen_Coords, Comments FROM Pal_List WHERE Record_ID = " + oldRecID);
				}
			}
		}
		conn.releaseStatement();
		statement2.close();
		statement3.close();
	}
	
	public static FRNumber getNextFRNumber(String regAreaCode, String nzmsSheet, double latitude, double longitude, PageState state) throws SQLException, IOException {
		DecimalFormat latDeg = new DecimalFormat("00");
		DecimalFormat longDeg = new DecimalFormat("000");
		String latStr = latDeg.format((Math.floor(Math.abs(latitude))));
		String longStr = longDeg.format((Math.floor(Math.abs(longitude))));
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs;
		
		String mapSheet = null;
		if (nzmsSheet != null) {
			rs = conn.executeQuery("SELECT * FROM SC.Map_Sheet WHERE MS_Series = 'NZMS260' AND MS_Map_Code = " + JspUtils.sqlEscape(nzmsSheet));
			if (rs.next()) {
				mapSheet = nzmsSheet;
			} else {
				mapSheet = (latitude >= 0 ? "N" : "S") + (longitude >= 0 ? "E" : "W") + latStr + longStr;
			}
		}
		else if (regAreaCode != null && !regAreaCode.equals("NZ") && !regAreaCode.equals("OT")) {
			mapSheet = regAreaCode;
		}
		else {
			mapSheet = (latitude >= 0 ? "N" : "S") + (longitude >= 0 ? "E" : "W") + latStr + longStr;
		}
				
		rs = conn.executeQuery("SELECT MAX(Serial_Number) FROM FR_Number WHERE Map_Sheet = " + JspUtils.sqlEscape(mapSheet) + " AND Serial_Number < 6000");
		rs.next();
		int serialNum = rs.getInt(1) + 1;
		
		return new FRNumber(mapSheet, new Integer(serialNum), null);
	}
	
	public static FRNumber getNextFRNumber(String mapSheet, int serialNumber, PageState state) throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT MAX(Recollection_Number) FROM FR_Number WHERE Map_Sheet = " + JspUtils.sqlEscape(mapSheet) + " AND Serial_Number = "  + serialNumber);
		if (rs.getString(1) == null) {
			return new FRNumber(mapSheet, new Integer(serialNumber), "A");	
		} else {
			char recollNum = rs.getString(1).charAt(0);
			return new FRNumber(mapSheet, new Integer(serialNumber), String.valueOf((char) (recollNum + 1)));
		}
	}
}
