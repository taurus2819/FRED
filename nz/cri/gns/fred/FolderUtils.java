
package nz.cri.gns.fred;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.dataentry.DataInputException;
import nz.cri.gns.fred.dataentry.Locality;
import nz.cri.gns.fred.dataentry.LocalityFactory;
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
	
	public static void deleteRecord(String recID, PageState state) throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT Audit_ID FROM Record WHERE Record_ID = " + recID);
		if (rs.next()) {
			String auditID = rs.getString(1);
			conn.executeUpdate("DELETE FROM Record WHERE Record_ID = " + recID);
			conn.executeUpdate("DELETE FROM Audit_Table WHERE Audit_ID = " + auditID);
		}
		conn.releaseStatement();
	}

	public static void deleteFeature(String featID, String featType, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
/*		DBConnection conn = FREDUtils.getFREDConnection(state);
		StringBuffer auditID = new StringBuffer();
		ResultSet rs = conn.executeQuery("SELECT Audit_ID FROM Record WHERE Sample_ID IN (SELECT Sample_ID FROM Sample WHERE Feature_ID = " + featID + ")");
		while (rs.next()) {
			auditID.append(rs.getString(1) + ",");
		}
		rs = conn.executeQuery("SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featID);
		rs.next();
		auditID.append(rs.getString(1));
		conn.executeUpdate("DELETE FROM Record WHERE Sample_ID IN (SELECT Sample_ID FROM Sample WHERE Feature_ID = " + featID + ")");
		conn.executeUpdate("DELETE FROM Feature WHERE Feature_ID = " + featID);
		conn.executeUpdate("DELETE FROM Audit_Table WHERE Audit_ID IN (" + auditID + ")");
		conn.releaseStatement();
	*/	Locality locality = LocalityFactory.getLocality(featType, Integer.parseInt(featID), user, state);
		locality.delete();
	}

	public static void submitLocality(String featID, String featType, String foldID, User user, PageState state) throws FolderUtilException, NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
/*		int userID = user.getPersonId();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = conn.executeQuery("SELECT Audit_ID FROM Feature WHERE Site_ID IS NOT NULL AND Locality IS NOT NULL AND Feature_Type IS NOT NULL AND Feature_ID = " + featID);
		if (rs.next()) {
			String auditID = rs.getString(1);
			rs = conn.executeQuery("SELECT Feature_Type FROM Feature WHERE Feature_ID = " + featID);
			rs.next();
			if (rs.getString(1).equals("Outcrop")) { //outcrop so also check sample property record
				rs = conn.executeQuery("SELECT Audit_ID FROM Sample_Property_All_View WHERE Collection_Date IS NOT NULL AND Collector IS NOT NULL AND Strat_Unit IS NOT NULL AND In_Place IS NOT NULL AND Feature_ID = " + featID);
				if (rs.next()) {
					//OK so update sample property audit table
					conn.executeUpdate("UPDATE Audit_Table SET Status = 'approved', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL WHERE Audit_ID = " + rs.getString(1));
				} else {
					//not OK, so return error message
					throw new FolderUtilException("Cannot submit locality as not all mandatory fields in sample property record have been completed");
				}
			}
			//Update Masterfile region
			rs = conn.executeQuery("SELECT Which_Masterfile('NZ', S.Latitude, S.Longitude) FROM Feature F, SC.Site S WHERE F.Site_ID = S.Site_ID AND F.Feature_ID = " + featID);
			rs.next();
			String mfID = rs.getString(1);
			conn.executeUpdate("UPDATE Feature SET Masterfile_ID = " + mfID + " WHERE Feature_ID = " + featID);
			//Update AUDIT_TABLE
			conn.executeUpdate("UPDATE Audit_Table SET Status = 'waiting', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL WHERE Audit_ID = " + auditID);
			//Check if need to add to FOLDER_CONTENT
			rs = conn.executeQuery("SELECT * FROM Folder_Content_View WHERE Feature_ID = " + featID + " AND Folder_ID = " + foldID);
			if (!rs.next()) {
				conn.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + foldID + ", " + featID + ")");
			}
		} else {
			throw new FolderUtilException("Cannot submit locality as not all mandatory fields have been completed");
		}
		conn.releaseStatement();
	*/	Locality locality = LocalityFactory.getLocality(featType, Integer.parseInt(featID), user, state);
		locality.submit();
	}
	
	public static void submitRecord(String recID, String recType, String foldID, User user, PageState state) throws IOException, SQLException, FolderUtilException {
		int userID = user.getPersonId();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs = null;
		//check mandatory fields
		if (recType.equals("SMP")) {
			rs = conn.executeQuery("SELECT Audit_ID FROM Sample_Property_All_View WHERE Collection_Date IS NOT NULL AND Collector IS NOT NULL AND Strat_Unit IS NOT NULL AND In_Place IS NOT NULL AND Record_ID = " + recID);
		} else if (recType.equals("ADO")) {
			rs = conn.executeQuery("SELECT Audit_ID FROM Adoption_All_View WHERE Adoptor IS NOT NULL AND Adoption_Date IS NOT NULL AND Record_ID = " + recID);
		} else if (recType.equals("PAL")) {
			rs = conn.executeQuery("SELECT Audit_ID FROM Paleontology_All_View WHERE Identifier IS NOT NULL AND Identification_Date IS NOT NULL AND Record_ID = " + recID);
		}
		if (rs.next()) {
			//update audit table
			conn.executeUpdate("UPDATE Audit_Table SET Status = 'approved', Submitted_By_ID = " + userID + ", Submitted_Date = SYSDATE, Working_Folder_ID = NULL, Working_Comments = NULL WHERE Audit_ID = " + rs.getString(1));
			//add feature to FOLDER_CONTENT if not already there (as no longer listed as a working record
			rs = conn.executeQuery("SELECT S.Feature_ID FROM Sample S, Record R WHERE S.Sample_ID = R.Sample_ID AND R.Record_ID = " + recID);
			rs.next();
			String featID = rs.getString(1);
			rs = conn.executeQuery("SELECT * FROM Folder_Content WHERE Folder_ID = " + foldID + " AND Feature_ID = " + featID);
			if (!rs.next()) {
				conn.executeUpdate("INSERT INTO Folder_Content (Folder_ID, Feature_ID) VALUES (" + foldID + ", " + featID + ")");
			}
		} else {
			throw new FolderUtilException("Cannot submit record as not all mandatory fields have been completed");
		}
		conn.releaseStatement();
	}
	
	public static void revokeLocality(String featID, String featType, User user, PageState state) throws NumberFormatException, IOException, SQLException, DataInputException, InvalidCredentialsException {
/*		DBConnection conn = FREDUtils.getFREDConnection(state);
		conn.executeUpdate("UPDATE Audit_Table SET Status = 'working', Working_Folder_ID = " + foldID + " WHERE Audit_ID IN (SELECT Audit_ID FROM Feature WHERE Feature_ID = " + featID + ")");
		//decide whether drillhole or outcrop
		ResultSet rs = conn.executeQuery("SELECT Feature_Type FROM Feature WHERE Feature_ID = " + featID);
		rs.next();
		if (rs.getString(1).equals("Outcrop")) { //outcrop so also revoke sample property record
			conn.executeUpdate("UPDATE Audit_Table SET Status = 'working', Working_Folder_ID = " + foldID + " WHERE Audit_ID IN (SELECT DISTINCT Audit_ID FROM Sample_Property_All_View WHERE Feature_ID = " + featID + ")");
		}
		conn.releaseStatement();
	*/	Locality locality = LocalityFactory.getLocality(featType, Integer.parseInt(featID), user, state);
		locality.revoke();	
	}
	
	public static void copyLocality(String oldFeatID, String newFeatName, String foldID, User user, PageState state) throws IOException, SQLException {
		int userID = user.getPersonId();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		Statement statement2 = conn.getExtraStatement();
		Statement statement3 = conn.getExtraStatement();
		ResultSet rs2, rs3;
		String sampID, oldSampID, recID, oldRecID;
		ResultSet rs = conn.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
		rs.next();
		String auditID = rs.getString(1);
		conn.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + foldID + ")");
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
				statement3.executeUpdate("INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Folder_ID) VALUES (" + auditID + ", 'working', " + userID + ", SYSDATE, " + foldID + ")");
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
	
}
