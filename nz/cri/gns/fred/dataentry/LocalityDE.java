package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.NZMG;
import nz.cri.gns.util.map.NZMS260;
import nz.cri.gns.util.map.NorthingEasting;
import nz.cri.gns.util.map.TruncNorthingEasting;

public abstract class LocalityDE implements DataEntryForm {

	protected User user;
	protected PageState state;
	protected Folder folder;
	protected Feature feature;
	protected Sample sample;
	protected String featureType;
	private Integer secClassID;
	protected String[] fields = new String[100];
	private Double latitude, longitude;
	private String origSystemID, origCoord, countryCode, recoll;
	protected boolean savedFlag = false;

	public LocalityDE(
		User user,
		int folderID,
		String featureType,
		PageState state)
		throws SQLException, IOException, DataInputException {
		this.user = user;
		this.state = state;
		if (!(featureType.equals("Outcrop")
			|| featureType.equals("Drillhole")
			|| featureType.equals("VertSect")))
			throw new DataInputException("Feature Type", "Invalid value");
		this.featureType = featureType;
		this.folder = new Folder(folderID, user, state);
	}

	public LocalityDE(int id, User user, PageState state)
		throws
			IOException,
			SQLException,
			DataInputException,
			InvalidCredentialsException {
		this.user = user;
		this.state = state;
		feature = new Feature(id, user, state, true);
		if (feature.getAsString(Feature.STATUS).equals("approved"))
			throw new DataInputException("Locality", "Locality not editable");
		int sampleID =
			((Integer) feature.getAsVector(Feature.SAMPLES).firstElement())
				.intValue();
		sample = new Sample(sampleID, user, state, true);
		featureType = feature.getFeatureType();
		if (sample.get(Sample.WORKING_FOLDER_ID) != null)
			this.folder =
				new Folder(
					sample.getAsInt(Sample.WORKING_FOLDER_ID),
					user,
					state);
		setField(FEATURE_NAME, sample.getAsString(Sample.FEATURE_NAME));
		setField(REGISTRATION_AREA, sample.getAsString(Sample.REG_AREA_ID));
		String workComm = sample.getAsString(Sample.WORKING_COMMENTS);
		if (workComm != null && workComm.indexOf("*Recoll:") >= 0) {
			setField(
				RECOLLECTION,
				workComm.substring(8, workComm.indexOf("*", 2)).trim());
			setField(
				WORKING_COMMENTS,
				workComm
					.substring(workComm.indexOf("*", 2) + 1, workComm.length())
					.trim());
		} else {
			setField(WORKING_COMMENTS, workComm);
		}
		if (sample.get(Sample.ORIG_SYSTEM_ID) != null) {
			int origSystemID = sample.getAsInt(Sample.ORIG_SYSTEM_ID);
			if (origSystemID == 38) {
				setField(
					GRID_REF,
					"NZMG:"
						+ sample.getAsString(Sample.ORIG_COORD).replace(
							'|',
							'*'));
			} else if (origSystemID == 16) {
				setField(
					GRID_REF,
					"TruncNZMG:"
						+ sample.getAsString(Sample.ORIG_COORD).replace(
							'|',
							'*'));
			} else if (origSystemID == 29) {
				setField(
					GRID_REF,
					"LatLong:"
						+ sample.getAsString(Sample.COUNTRY_CODE)
						+ "*"
						+ sample.getAsString(Sample.ORIG_COORD).replace(
							'|',
							'*'));
			}
		}
		setField(METHOD, sample.getAsString(Sample.METHOD_ID));
		setField(ACCURACY, sample.getAsString(Sample.ACCURACY));
		setField(LOCALITY_DESC, sample.getAsString(Sample.LOCALITY));
		try {
			setField(
				SECURITY_TYPE,
				String.valueOf(
					FREDUtils.getSecurityType(
						sample.getAsInt(Sample.SECURITY_CLASS_ID),
						user,
						state)));
		} catch (Exception e) {
			setField(SECURITY_TYPE, "21");
		}
		savedFlag = true;
	}

	public Integer getFeatureID() {
		if (feature != null) {
			return new Integer(feature.getFeatureID());
		} else {
			return null;
		}
	}

	public Integer getSampleID() {
		if (sample != null) {
			return new Integer(sample.getSampleID());
		} else {
			return null;
		}
	}

	public String getFeatureType() {
		return featureType;
	}

	public int getFieldCount() {
		return fields.length;
	}

	public void setField(int field, String value) throws DataInputException {
		if (value != null
			&& (value.equals("") || value.equals("-") || value.equals("null")))
			value = null;
		if (value != null) {
			parseField(field, value);
		} else {
			resetHiddenField(field);
		}
		fields[field] = value;
		savedFlag = false;
	}

	public String getField(int field) {
		return fields[field];
	}

	protected void parseField(int field, String value)
		throws DataInputException {
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case REGISTRATION_AREA :
					DataEntryUtils.parseDropDownID(
						"Registration Area",
						"SELECT * FROM Lookup WHERE Lookup_ID = "
							+ value
							+ " AND FieldName = 'RegArea'",
						state);
					break;
				case GRID_REF :
					if (!parseCoord(value))
						throw new DataInputException(
							"Coordinate",
							"Invalid value");
					break;
				case METHOD :
					DataEntryUtils.parseDropDownID(
						"Method",
						"SELECT * FROM SC.Method WHERE Method_ID = " + value,
						state);
					break;
				case ACCURACY :
					try {
						Double acc = new Double(value);
					} catch (Exception e) {
						throw new DataInputException(
							"Accuracy",
							"Invalid value");
					}
					break;
				case RECOLLECTION :
					recoll = "*Recoll:" + value + "*";
					rs =
						conn.executeQuery(
							"SELECT * FROM Feature_Security_View WHERE Sample_Name = "
								+ JspUtils.sqlEscape(value)
								+ " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = "
								+ user.getPersonId()
								+ "))");
					if (!rs.next()) {
						throw new DataInputException(
							"Recollection/Sidetrack",
							value
								+ " is not an existing FR Number or temporary name.  Please use the builder to select.");
					}
					break;
				case SECURITY_TYPE :
					try {
						secClassID =
							new Integer(
								FREDUtils.getSecurityClass(
									Integer.parseInt(value),
									user,
									state));
					} catch (Exception e) {
						throw new DataInputException(
							"Security Class",
							"Invalid");
					}
					break;
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException _e) {
			throw new DataInputException();
		}
	}

	protected void resetHiddenField(int field) {
	}

	public void makeNavPanelHTML(Writer out) throws IOException {
		out.write(
			"<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>\n");
		out.write(
			"<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>\n");
		out.write("<tr><td colspan='2' align='center' class='heading'>\n");
		if (featureType.equals("Outcrop")) {
			out.write("Outcrop");
		} else if (featureType.equals("Drillhole")) {
			out.write("Drillhole");
		} else if (featureType.equals("VertSect")) {
			out.write("Vertical Section");
		}
		out.write(" Locality</td></tr>\n");
		out.write("<tr><td>&nbsp;</td></tr>\n");
		out.write(
			"<tr><td><a href='load_record.jsp?FoldID=" + folder.getFolderID());
		if (feature != null)
			out.write("&FeatID=" + feature.getFeatureID());
		out.write(
			"&RecType="
				+ featureType
				+ "'><img src='images/load.gif' height='20' width='20' border='0' alt='Copy From' /></a>&nbsp;&nbsp;</td><td><a href='load_record.jsp?FoldID="
				+ folder.getFolderID());
		if (feature != null)
			out.write("&FeatID=" + feature.getFeatureID());
		out.write(
			"&RecType="
				+ featureType
				+ "' class='boldlink'>Copy From</a></td></tr>\n");
		out.write(
			"<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
		if (folder.isAllowedSubmitLocalities())
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database' /></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
		out.write("<tr><td><a href='javascript:history.back();'><img src='images/cancel.gif' height='20' width='20' border='0' alt='Quit Without Saving' /></a>&nbsp;&nbsp;</td><td><a href='javascript:history.back();' class='heading'>Quit</a></td></tr>");
		out.write("</table>\n");
	}

	public void makeDataEntryHTML(Writer out)
		throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		out.write(
			"<tr><td class='heading'>Registration Area</td><td></td><td>");
		ComboDescriptor cd = new ComboDescriptor("Lookup", "Lookup_ID", "Name");
		cd.name = "RegAreaID";
		cd.selected = getField(LocalityDE.REGISTRATION_AREA);
		cd.join = "FieldName = 'RegArea'";
		cd.orderBy = "Lookup_ID";
		HTMLUtils.makeDropBox(out, conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>");
		if (featureType.equals("Drillhole")) {
			out.write("Sidetrack of");
		} else {
			out.write("Recollection of");
		}
		out.write(
			"</td><td></td><td><input type='text' name='Recoll' value='"
				+ FREDUtils.noNulls(getField(LocalityDE.RECOLLECTION))
				+ "' /></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Recoll\", \"Supp\", \"width=600,height=500\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write(
			"<tr><td class='heading' colspan='2'>Working Comments<br><span class='smalltext'>On submission these comments will be deleted</span></td><td><textarea name='WorkComm' rows='3' cols='40'>"
				+ FREDUtils.noNulls(getField(LocalityDE.WORKING_COMMENTS))
				+ "</textarea></td></tr>\n");
		out.write(
			"<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");

		out.write(
			"<tr><td class='heading' colspan='2'>Security Setting</td><td>");
		cd = new ComboDescriptor("Lookup", "Lookup_ID", "Name");
		cd.name = "SecType";
		if (getField(SECURITY_TYPE) != null) {
			cd.selected = getField(SECURITY_TYPE);
		} else {
			cd.selected = "21";
		}
		cd.orderBy = "Lookup_ID";
		cd.join = "FieldName = 'SecurityClass'";
		HTMLUtils.makeDropBox(out, conn, cd);
		out.write("</td></tr>\n");
		out.write(
			"<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");

		out.write(
			"<tr><td class='heading'>Location</td><td class='smallheading'>Grid Ref.</td><td><input type='text' name='GridRef' size='40' value='"
				+ FREDUtils.noNulls(getField(LocalityDE.GRID_REF))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Coord\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");

		ResultSet rs =
			conn.executeQuery("SELECT MAX(Method_ID) FROM SC.Method");
		rs.next();
		out.write(
			"<script language='JavaScript'>\nvar datumMethod = new Array("
				+ (rs.getInt(1) + 1)
				+ ");\n");
		rs =
			conn.executeQuery(
				"SELECT Method_ID, Nom_Accuracy_XY FROM SC.Method WHERE Nom_Accuracy_XY IS NOT NULL ORDER BY Method_ID");
		while (rs.next()) {
			out.write(
				"datumMethod["
					+ rs.getString(1)
					+ "] = '"
					+ FREDUtils.noNulls(rs.getString(2))
					+ "';\n");
		}
		out.write(
			"function setAccuracy(datID, form) {\nif (datID != \"-\") { form.Accuracy.value = datumMethod[datID]; }\n}\n");
		out.write("</script>\n");
		conn.releaseStatement();

		out.write("<tr><td></td><td class='smallheading'>Method</td><td>");
		cd = new ComboDescriptor("SC.Method", "Method_ID", "Method");
		cd.name = "LocMethodID";
		cd.prompt = "-- Choose --";
		cd.selected = getField(LocalityDE.METHOD);
		cd.orderBy = "Method_ID";
		cd.join = "Nom_Accuracy_XY IS NOT NULL";
		cd.tagParams = "onChange='setAccuracy(this.value, this.form)'";
		HTMLUtils.makeDropBox(out, conn, cd);
		out.write("</td></tr>\n");
		out.write(
			"<tr><td></td><td class='smallheading'>Accuracy</td><td><input type='text' name='Accuracy' value='"
				+ FREDUtils.noNulls(getField(LocalityDE.ACCURACY))
				+ "'></td></tr>\n");
		out.write(
			"<tr><td></td><td class='smallheading'>Locality<br />Description</td><td><textarea name='Loc' cols='40' rows='5'>"
				+ FREDUtils.noNulls(getField(LocalityDE.LOCALITY_DESC))
				+ "</textarea></td></tr>\n");
	}

	protected void makeEndBitHTML(Writer out) throws IOException {
		out.write("<table border='0' cellpadding='0' cellspacing='2'>\n");
		out.write("<tr><td>&nbsp;</td></tr>\n");
		out.write(
			"<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
		if (folder.isAllowedSubmitLocalities()) {
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database'/></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
		}
		out.write("</table>\n");
	}

	private boolean parseCoord(String coord) {
		if (coord.indexOf("*") == -1
			|| coord.indexOf("*") == coord.length() - 1)
			return false;
		if (coord.indexOf("NZMG:") == 0) {
			String east = coord.substring(5, coord.indexOf("*"));
			String north =
				coord.substring(coord.indexOf("*") + 1, coord.length());
			if (east.length() != 7 || north.length() != 7)
				return false;
			try {
				NorthingEasting nzmgCoord =
					new NorthingEasting(
						Double.parseDouble(north),
						Double.parseDouble(east));
				NZMG nzmg = new NZMG();
				Datum.LatLong latLong = nzmg.convertToNZGD49(nzmgCoord);
				latitude = new Double(latLong.getNorthSouth());
				longitude = new Double(latLong.getEastWest());
				origCoord = east + "|" + north;
				origSystemID = "38";
				countryCode = "NZ";
			} catch (Exception e) {
				return false;
			}
		} else if (coord.indexOf("TruncNZMG:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
				return false;
			String sheet = coord.substring(10, coord.indexOf("*"));
			String east =
				coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String north =
				coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			if (sheet.length() != 3
				|| east.length() < 3
				|| east.length() > 4
				|| north.length() < 3
				|| north.length() > 4
				|| east.length() != north.length())
				return false;
			try {
				TruncNorthingEasting truncNzmgCoord =
					new TruncNorthingEasting(
						Double.parseDouble(north),
						Double.parseDouble(east),
						sheet,
						east.length());
				NZMS260 nzms260 = new NZMS260();
				Datum.LatLong latLong = nzms260.convertToNZGD49(truncNzmgCoord);
				latitude = new Double(latLong.getNorthSouth());
				longitude = new Double(latLong.getEastWest());
				origCoord = sheet + "|" + east + "|" + north;
				origSystemID = "16";
				countryCode = "NZ";
			} catch (Exception e) {
				return false;
			}
		} else if (coord.indexOf("LatLong:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
				return false;
			String north =
				coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String east =
				coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			try {
				latitude = new Double(north);
				longitude = new Double(east);
				if (longitude.doubleValue() <= -180
					|| longitude.doubleValue() > 180
					|| latitude.doubleValue() <= -90
					|| latitude.doubleValue() > 90)
					return false;
				origCoord = latitude + "|" + longitude;
				origSystemID = "29";
				countryCode = coord.substring(8, coord.indexOf("*"));
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public int save()
		throws SQLException, IOException, InvalidCredentialsException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			String siteID = null;
			if (fields[GRID_REF] != null)
				siteID = getSiteID();
			if (secClassID == null)
				secClassID = new Integer(4);
			if (feature == null) {
				if (!folder.isAllowedCreateLocalities())
					throw new InvalidCredentialsException();
				//create new AUDIT, FEATURE and SAMPLE records
				rs = conn.executeQuery("SELECT Audit_Seq.NEXTVAL FROM DUAL");
				rs.next();
				String auditID = rs.getString(1);
				conn.executeUpdate(
					"INSERT INTO Audit_Table (Audit_ID, Status, Created_By_ID, Created_Date, Working_Comments, Working_Folder_ID, Security_Class_ID) VALUES ("
						+ auditID
						+ ", 'working', "
						+ user.getPersonId()
						+ ", SYSDATE, "
						+ JspUtils.sqlEscape(
							FREDUtils.noNulls(recoll)
								+ FREDUtils.noNulls(fields[WORKING_COMMENTS]))
						+ ", "
						+ folder.getFolderID()
						+ ", "
						+ secClassID.toString()
						+ ")");
				rs = conn.executeQuery("SELECT Feature_Seq.NEXTVAL FROM DUAL");
				rs.next();
				int featureID = rs.getInt(1);
				conn.executeUpdate(
					"INSERT INTO Feature (Feature_ID, Site_ID, Audit_ID, Feature_Type, Locality, Feature_Name, Reg_Area_ID) VALUES ("
						+ featureID
						+ ", "
						+ JspUtils.sqlEscape(siteID)
						+ ", "
						+ auditID
						+ ", "
						+ JspUtils.sqlEscape(featureType)
						+ ", "
						+ JspUtils.sqlEscape(fields[LOCALITY_DESC])
						+ ", "
						+ JspUtils.sqlEscape(fields[FEATURE_NAME])
						+ ", "
						+ JspUtils.sqlEscape(fields[REGISTRATION_AREA])
						+ ")");
				conn.executeUpdate(
					"INSERT INTO Sample (Feature_ID) VALUES ("
						+ feature.getFeatureID()
						+ ")");
				feature = new Feature(featureID, user, state, true);
				int sampleID =
					((Integer) feature
						.getAsVector(Feature.SAMPLES)
						.firstElement())
						.intValue();
				sample = new Sample(sampleID, user, state, true);
			} else { // edit
				if (!folder.isAllowedApproveLocalities() && feature.getAsString(Feature.STATUS).equals("waiting"))
					throw new InvalidCredentialsException();
				if (!folder.isAllowedEditLocalities())
					throw new InvalidCredentialsException();
				//Update AUDIT
				rs =
					conn.executeQuery(
						"SELECT Audit_ID FROM Feature WHERE Feature_ID = "
							+ feature.getFeatureID());
				rs.next();
				String auditID = rs.getString(1);
				conn.executeUpdate(
					"UPDATE Audit_Table SET Modified_By_ID = "
						+ user.getPersonId()
						+ ", Modified_Date = SYSDATE, Working_Comments = "
						+ JspUtils.sqlEscape(
							FREDUtils.noNulls(recoll)
								+ FREDUtils.noNulls(fields[WORKING_COMMENTS]))
						+ ", Security_Class_ID = "
						+ secClassID.toString()
						+ " WHERE Audit_ID = "
						+ auditID);
				conn.executeUpdate(
					"UPDATE Feature SET Site_ID = "
						+ JspUtils.sqlEscape(siteID)
						+ ", Locality = "
						+ JspUtils.sqlEscape(fields[LOCALITY_DESC])
						+ ", Feature_Name = "
						+ JspUtils.sqlEscape(fields[FEATURE_NAME])
						+ ", Reg_Area_ID = "
						+ JspUtils.sqlEscape(fields[REGISTRATION_AREA])
						+ " WHERE Feature_ID = "
						+ feature.getFeatureID());
				feature =
					new Feature(feature.getFeatureID(), user, state, true);
				int sampleID =
					((Integer) feature
						.getAsVector(Feature.SAMPLES)
						.firstElement())
						.intValue();
				sample = new Sample(sampleID, user, state, true);
			}
			conn.releaseStatement();
		}
		savedFlag = true;
		return feature.getFeatureID();
	}

	public int submit() throws SQLException, IOException, InvalidCredentialsException, DataInputException {
		if ((feature != null && feature.getAsString(Feature.STATUS).equals("waiting")) || !folder.isAllowedSubmitLocalities())
			throw new InvalidCredentialsException();
		if (featureType == null || fields[GRID_REF] == null || fields[REGISTRATION_AREA] == null)
			throw new DataInputException("Mandatory Fields", "Not all completed");
		save();
		//change status, check MF & add saved record to folder
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs =
			conn.executeQuery(
				"SELECT Code FROM Lookup WHERE FieldName = 'RegArea' AND Lookup_ID = "
					+ fields[REGISTRATION_AREA]);
		rs.next();
		String regCode = rs.getString(1);
		rs =
			conn.executeQuery(
				"SELECT Audit_ID FROM Feature WHERE Feature_ID = " + feature.getFeatureID());
		rs.next();
		String auditID = rs.getString(1);
		rs =
			conn.executeQuery(
				"SELECT Which_Masterfile("
					+ JspUtils.sqlEscape(regCode)
					+ ", "
					+ latitude
					+ ", "
					+ longitude
					+ ") FROM DUAL");
		rs.next();
		String mfID = rs.getString(1);
		conn.executeUpdate(
			"UPDATE Feature SET Masterfile_ID = "
				+ mfID
				+ " WHERE Feature_ID = "
				+ feature.getFeatureID());
		conn.executeUpdate(
			"UPDATE Audit_Table SET Status = 'waiting', Submitted_By_ID = "
				+ user.getPersonId()
				+ ", Submitted_Date = SYSDATE WHERE Audit_ID = "
				+ auditID);
		conn.releaseStatement();
		feature = new Feature(feature.getFeatureID(), user, state, true);
		return feature.getFeatureID();
	}

	public void revoke()
		throws SQLException, IOException, InvalidCredentialsException {
		if (feature == null || !feature.getAsString(Feature.STATUS).equals("waiting") || !folder.isAllowedSubmitLocalities())
			throw new InvalidCredentialsException();
		//change status, check MF & add saved record to folder
		DBConnection conn = FREDUtils.getFREDConnection(state);
		ResultSet rs =
			conn.executeQuery(
				"SELECT Audit_ID FROM Feature WHERE Feature_ID = "
					+ feature.getFeatureID());
		rs.next();
		String auditID = rs.getString(1);
		conn.executeUpdate(
			"UPDATE Audit_Table SET Status = 'working' WHERE Audit_ID = "
				+ auditID);
		conn.releaseStatement();
		feature = new Feature(feature.getFeatureID(), user, state, true);
	}

	public void delete()
		throws IOException, SQLException, InvalidCredentialsException {
		if (!folder.isAllowedDeleteLocalities() && feature != null)
			throw new InvalidCredentialsException();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		StringBuffer auditID = new StringBuffer();
		ResultSet rs =
			conn.executeQuery(
				"SELECT Audit_ID FROM Record WHERE Sample_ID IN (SELECT Sample_ID FROM Sample WHERE Feature_ID = "
					+ feature.getFeatureID()
					+ ")");
		while (rs.next()) {
			auditID.append(rs.getString(1) + ",");
		}
		rs =
			conn.executeQuery(
				"SELECT Audit_ID FROM Feature WHERE Feature_ID = "
					+ feature.getFeatureID());
		rs.next();
		auditID.append(rs.getString(1));
		conn.executeUpdate(
			"DELETE FROM Record WHERE Sample_ID IN (SELECT Sample_ID FROM Sample WHERE Feature_ID = "
				+ feature.getFeatureID()
				+ ")");
		conn.executeUpdate(
			"DELETE FROM Feature WHERE Feature_ID = " + feature.getFeatureID());
		conn.executeUpdate(
			"DELETE FROM Audit_Table WHERE Audit_ID IN (" + auditID + ")");
		conn.releaseStatement();
	}

	private String getSiteID() throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String siteID;
		ResultSet rs =
			conn.executeQuery(
				"SELECT SC.Site_Check("
					+ latitude
					+ ", "
					+ longitude
					+ ", "
					+ JspUtils.sqlEscape(fields[METHOD])
					+ ", "
					+ JspUtils.sqlEscape(fields[ACCURACY])
					+ ") FROM DUAL");
		rs.next();
		if (rs.getString(1) != null) {
			siteID = rs.getString(1);
		} else {
			rs = conn.executeQuery("SELECT SC.Site_Seq.NEXTVAL FROM DUAL");
			rs.next();
			siteID = rs.getString(1);
			conn.executeUpdate(
				"INSERT INTO SC.Site (Site_ID, Site_Name, Latitude, Longitude, Method_ID, Accuracy, Directions, Orig_System_ID, Orig_Coord, Country_Code) VALUES ("
					+ siteID
					+ ", "
					+ JspUtils.sqlEscape(fields[FIELD_NUMBER])
					+ ", "
					+ latitude
					+ ", "
					+ longitude
					+ ", "
					+ JspUtils.sqlEscape(fields[METHOD])
					+ ", "
					+ JspUtils.sqlEscape(fields[ACCURACY])
					+ ", "
					+ JspUtils.sqlEscape(fields[LOCALITY_DESC])
					+ ", "
					+ JspUtils.sqlEscape(origSystemID)
					+ ", "
					+ JspUtils.sqlEscape(origCoord)
					+ ", "
					+ JspUtils.sqlEscape(countryCode)
					+ ")");
		}
		conn.releaseStatement();
		return siteID;
	}

}
