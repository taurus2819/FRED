package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.db.metadata.MetadataRecord;
import nz.cri.gns.db.site.DatumMethod;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.Audit;
import nz.cri.gns.fred.data.AuditEdit;
import nz.cri.gns.fred.data.FRNumber;
import nz.cri.gns.fred.data.Feature;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.NorthingEasting;
import nz.cri.gns.util.map.TruncNorthingEasting;

public abstract class LocalityDE implements DataEntryForm {

	protected User user;
	protected PageState state;
	private Folder workingFolder = null;
	protected Feature feature;
	protected Sample sample;
	protected String featureType;
	protected String[] fields = new String[120];
	protected String[] tempFields = new String[120];
	private String origSystemID, countryCode, recoll;
	private Datum origSystem;
	private Datum.Coordinate origCoord;
	protected boolean savedFlag = false;
	private boolean isAllowedSubmit = false;

	public LocalityDE(User user, int folderID, String featureType, PageState state)	throws SQLException, IOException, DataInputException {
		this.user = user;
		this.state = state;
		if (!(featureType.equals(Feature.OUTCROP_LOCALITY) || featureType.equals(Feature.DRILLHOLE_LOCALITY) || featureType.equals(Feature.VERTICAL_SECTION_LOCALITY)))
			throw new DataInputException("Feature Type", "Invalid value");
		this.featureType = featureType;
		workingFolder = new Folder(folderID, user, state);
		if (!workingFolder.isAllowedCreateLocalities())
			throw new DataInputException("Locality", "Insufficient rights to create locality");
		isAllowedSubmit = workingFolder.isAllowedSubmitLocalities();
	}

	public LocalityDE(int featureID, User user, PageState state) throws IOException, SQLException, DataInputException, InvalidCredentialsException {
		this.user = user;
		this.state = state;
		feature = new Feature(featureID, user, state, true);
		featureType = feature.getFeatureType();
		
		//check status for editing
		if (!FREDUtils.isAllowedEditLocality(user, feature.getAsString(Feature.STATUS), String.valueOf(featureID), state))
			throw new DataInputException("Locality", "Insufficient rights to edit this locality");
		if (feature.get(Feature.WORKING_FOLDER_ID) != null)
			workingFolder = new Folder(feature.getAsInt(Feature.WORKING_FOLDER_ID), user, state);
		int sampleID = ((Integer) feature.getAsVector(Feature.SAMPLES).firstElement()).intValue();
		sample = new Sample(sampleID, user, state, true);
		isAllowedSubmit = FREDUtils.isAllowedSubmitLocality(user, feature.getAsString(Feature.STATUS), String.valueOf(feature.getFeatureID()), state);
	}

	public void copyFrom(int featureID) throws DataInputException, InvalidCredentialsException, SQLException, IOException {
		Feature copyFeature = new Feature(featureID, user, state);
		if (!featureType.equals(copyFeature.getFeatureType()))
			throw new DataInputException("Locality", "Incompatible Locality Types");
		int copySampleID = ((Integer) copyFeature.getAsVector(Feature.SAMPLES).firstElement()).intValue();
		Sample copySample = new Sample(copySampleID, user, state);
		getFromDatabase(copySample);
	}

	protected void getFromDatabase(Sample sample) throws DataInputException, InvalidCredentialsException, SQLException, IOException {
		//set fields
		setField(FEATURE_NAME, sample.getAsString(Sample.FEATURE_NAME));
		setField(REGISTRATION_AREA, sample.getAsString(Sample.REG_AREA_ID));
		String workComm = sample.getAsString(Sample.FEATURE_WORKING_COMMENTS);
		if (workComm != null && workComm.indexOf("*Recoll:") >= 0) {
			setField(RECOLLECTION, workComm.substring(8, workComm.indexOf("*", 2)).trim());
			setField(WORKING_COMMENTS, workComm.substring(workComm.indexOf("*", 2) + 1, workComm.length()).trim());
		} else {
			setField(WORKING_COMMENTS, workComm);
		}
		if (sample.get(Sample.ORIG_SYSTEM_ID) != null) {
			switch (sample.getAsInt(Sample.ORIG_SYSTEM_ID)) {
				case 38 :
					setField(GRID_REF, "NZMG:" + sample.getAsString(Sample.ORIG_COORD).replace('|',	'*'));
					break;
				case 16 :
					setField(GRID_REF, "NZMS260:"	+ sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;
				case 29 :
					setField(GRID_REF, "NZGD49:" + sample.getAsString(Sample.COUNTRY_CODE) + "*" + sample.getAsString(Sample.ORIG_COORD).replace('|',	'*'));
					break;
				case 28 :
					setField(GRID_REF, "WGS84:" + sample.getAsString(Sample.COUNTRY_CODE) + "*" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;
				case 33 :
					setField(GRID_REF, "NZYS:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;
				case 70 :
					setField(GRID_REF, "NZYN:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;
				case 17 :
					setField(GRID_REF, "NZMS1S:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;
				case 69 :
					setField(GRID_REF, "NZMS1N:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;
				case 7: 
					setField(GRID_REF, "CHAT:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;				
				case 67 :
					setField(GRID_REF, "AUCK:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;				
				case 68 :
					setField(GRID_REF, "CAMP:" + sample.getAsString(Sample.ORIG_COORD).replace('|', '*'));
					break;				
			}
		}
		setField(METHOD, sample.getAsString(Sample.METHOD_ID));
		setField(ACCURACY, sample.getAsString(Sample.ACCURACY));
		setField(LOCALITY_DESC, sample.getAsString(Sample.LOCALITY));
	}

	public Integer getFeatureID() {
		if (feature != null)
			return new Integer(feature.getFeatureID());
		return null;
	}

	public String getFeatureType() {
		return featureType;
	}

	public int getFieldCount() {
		return fields.length;
	}

	public void setField(int field, String value) throws DataInputException {
		if (value != null && (value.equals("") || value.equals("-") || value.equals("null")))
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

	public void setTempField(int field, String value) {
		tempFields[field] = value;
	}

	public String getTempField(int field) {
		return tempFields[field];
	}

	protected String getFieldForHTML(int field) {
		if (getTempField(field) != null)
			return getTempField(field);
		return getField(field);
	}

	public void setFieldsFromTemp() throws DataInputException {
		for (int i = 0; i < getFieldCount(); i++) {
			setField(i, getTempField(i));
			setTempField(i, null);
		}
	}

	protected void parseField(int field, String value)
		throws DataInputException {
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case REGISTRATION_AREA :
					DataEntryUtils.checkDropDownID("Registration Area", "SELECT * FROM lookup WHERE lookup_id = ? AND fieldname = ?", new int[] {Types.NUMERIC, Types.VARCHAR}, new Object[] {new Integer(value), "RegArea"}, state);
					break;
				case GRID_REF :
					parseCoord(value);
					break;
				case METHOD :
					DataEntryUtils.checkDropDownID("Method", "SELECT * FROM sc.method WHERE method_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)}, state);
					break;
				case ACCURACY :
					try {
						new Double(value);
					} catch (Exception e) {
						throw new DataInputException("Accuracy", "Invalid value");
					}
					break;
				case RECOLLECTION :
					recoll = "*Recoll:" + value + "*";
					String query = "SELECT * FROM Feature_Security_View WHERE Sample_Name = ? AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = ?))";
					rs = conn.executeQuery(query, new int[] {Types.VARCHAR, Types.NUMERIC}, new Object[] {value, new Integer(user.getPersonId())});
					if (!rs.next()) {
						throw new DataInputException("Recollection/Sidetrack", value + " is not an existing FR Number or temporary name.  Please use the builder to select.");
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
		out.write("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>\n");
		try {
			if (feature.getAsString(Feature.STATUS).equals(Audit.STATUS_APPROVED)) {
				out.write("<tr><td colspan='2' align='center' class='heading'>" + feature.getAsString(Feature.SAMPLE_NAMES) + "</td></tr>\n");
			} else {
				out.write("<tr><td colspan='2' align='center' class='heading'>" + featureType + " Locality</td></tr>\n");
			}
		} catch (Exception e) {}
		out.write("<tr><td>&nbsp;</td></tr>\n");
		if (workingFolder != null) {
			out.write("<tr><td><a href='load_record.jsp?FoldID=" + workingFolder.getFolderID()
				+ ((feature != null) ? "&FeatID=" + feature.getFeatureID() : "")
				+ "&RecType=" + featureType	+ "'><img src='images/load.gif' height='20' width='20' border='0' alt='Copy From' /></a>&nbsp;&nbsp;</td><td><a href='load_record.jsp?FoldID=" + workingFolder.getFolderID()
				+ ((feature != null) ? "&FeatID=" + feature.getFeatureID() : "")
				+ "&RecType=" + featureType + "' class='boldlink'>Copy From</a></td></tr>\n");
		}
		out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
		if (isAllowedSubmit)
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database' /></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
	}

	public void makeDataEntryHTML(Writer out) throws IOException, SQLException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		out.write("<table border='0' cellspacing='0' cellpadding='2'>\n");
		
		//Edit details
		try {
			if (feature.getAsString(Feature.STATUS).equals(Audit.STATUS_APPROVED)) {
				out.write("<tr><td class=\"heading\" colspan=\"4\" style=\"color: #FF0000\">You are editing an approved locality.  Previous edits are shown below.  Please enter comments on the edits you are making in the <em>Edit Comments</em> box below</td></tr>");
				Audit audit = Audit.getAudit(feature.getAsInt(Feature.AUDIT_ID), state);
				if (audit.get(Audit.CURATOR_COMMENTS) != null)
					out.write("<tr><td>" + FREDUtils.noNulls(audit.getAsString(Audit.APPROVED_BY)) + "</td><td class=\"smalltext\">"
							+ ((audit.get(Audit.APPROVED_DATE) != null) ? FREDUtils.formatDateForOutput(audit.getAsDate(Audit.APPROVED_DATE)) : "")
							+ "</td><td>Curator approval comments: " + audit.getAsString(Audit.CURATOR_COMMENTS) + "</td></tr>");
				if (audit.get(Audit.EDIT_HISTORY) != null) {
					for (Iterator i = audit.getAsVector(Audit.EDIT_HISTORY).iterator(); i.hasNext(); ) {
						AuditEdit ae = (AuditEdit) i.next();
						out.write("<tr><td>" + FREDUtils.noNulls(ae.getEditedBy()) + "</td><td class=\"smalltext\">"
							+ ((ae.getEditedDate() != null) ? FREDUtils.formatDateForOutput(ae.getEditedDate()) : "")
							+ "</td><td>" + FREDUtils.noNulls(ae.getComments()) + "</td></tr>");
					}
				}
				out.write("<tr><td class=\"heading\" colspan=\"2\">Edit Comments</td><td><textarea name=\"EditComm\" rows=\"3\" cols=\"40\">"
						+ FREDUtils.noNulls(getFieldForHTML(EDIT_COMMENTS))
						+ "</textarea></td></tr>\n");
				out.write("<tr><td>&nbsp;</td></tr>");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		if (featureType.equals(Feature.OUTCROP_LOCALITY)) {
			out.write("<tr><td class='heading' colspan='2'>Field Number</td><td><input type='text' name='FeatName' value='"
					+ FREDUtils.noNulls(getFieldForHTML(FIELD_NUMBER)) + "'></td></tr>\n");
		} else if (featureType.equals(Feature.DRILLHOLE_LOCALITY)) {
			out.write("<tr><td class='heading' colspan='2'>Drillhole Name</td><td><input type='text' name='FeatName' value='"
					+ FREDUtils.noNulls(getFieldForHTML(DRILLHOLE_NAME)) + "'></td></tr>\n");			
		} else {
			out.write("<tr><td class='heading' colspan='2'>Section Name</td><td><input type='text' name='FeatName' value='"
					+ FREDUtils.noNulls(getFieldForHTML(DRILLHOLE_NAME)) + "'></td></tr>\n");
		}
		out.write("<tr><td class='heading' style=\"color: #FF0000\">Registration Area</td><td></td><td>");
		ComboDescriptor cd = new ComboDescriptor("Lookup", "Lookup_ID", "Name");
		cd.name = "RegAreaID";
		cd.selected = getFieldForHTML(DataEntryForm.REGISTRATION_AREA);
		cd.join = "FieldName = 'RegArea'";
		cd.orderBy = "Lookup_ID";
		HTMLUtils.makeDropBox(out, conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td class='heading'>");
		if (featureType.equals(Feature.DRILLHOLE_LOCALITY)) {
			out.write("Sidetrack of");
		} else {
			out.write("Recollection of");
		}
		out.write("</td><td></td><td><input type='text' name='Recoll' value='"
				+ FREDUtils.noNulls(getFieldForHTML(LocalityDE.RECOLLECTION))
				+ "' /></td></tr>\n");
		out.write("<tr><td class='heading' colspan='2'>Working Comments<br><span class='smalltext'>On submission these comments will be deleted</span></td><td><textarea name='WorkComm' rows='3' cols='40'>"
				+ FREDUtils.noNulls(getFieldForHTML(LocalityDE.WORKING_COMMENTS))
				+ "</textarea></td></tr>\n");
		out.write("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>\n");
			
		if (feature != null) {
			out.write("<tr><td class='heading' colspan='2'>Attached Files/Images<br /><span class='smalltext'>Click <a href='binary_data_entry.jsp?ID="
				+ feature.getFeatureID() + "&RecType=" + feature.getFeatureType()
				+ ((workingFolder != null) ? "&FoldID=" + workingFolder.getFolderID() : "")
				+ " 'target='fredBinary'>here</a> to add/edit</span></td><td>");
			try {
				if (feature != null && feature.getMetadataRecordsCount() > 0) {	
					MetadataRecord[] mr = feature.getMetadataRecords();
					for (int i = 0; i < mr.length; i++)
						out.write(mr[i].getTitle() + "<br />");
				}
			} catch (Exception e) {}
			out.write("</td></tr>");
		}
		
		out.write("<tr><td class='heading' style=\"color: #FF0000\">Location</td><td class='smallheading'>Grid Ref.</td><td><input type='text' name='GridRef' size='40' value='"
				+ FREDUtils.noNulls(getFieldForHTML(LocalityDE.GRID_REF))
				+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Coord\", \"Supp\", \"width=600,height=600\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		ResultSet rs = conn.executeQuery("SELECT MAX(Method_ID) FROM SC.Method");
		rs.next();
		out.write("<script language='JavaScript'>\nvar datumMethod = new Array("
				+ (rs.getInt(1) + 1)
				+ ");\n");
		rs = conn.executeQuery("SELECT Method_ID, Nom_Accuracy_XY FROM SC.Method WHERE Nom_Accuracy_XY IS NOT NULL ORDER BY Method_ID");
		while (rs.next())
			out.write("datumMethod[" + rs.getString(1) + "] = '" + FREDUtils.noNulls(rs.getString(2)) + "';\n");
		out.write("function setAccuracy(datID, form) {\nif (datID != \"-\") { form.Accuracy.value = datumMethod[datID]; }\n}\n");
		out.write("</script>\n");
		conn.releaseStatement();

		out.write("<tr><td></td><td class='smallheading'>Method</td><td>");
		cd = new ComboDescriptor("SC.Method", "Method_ID", "Method");
		cd.name = "LocMethodID";
		cd.prompt = "-- Choose --";
		cd.selected = getFieldForHTML(LocalityDE.METHOD);
		cd.orderBy = "Method_ID";
		cd.join = "Nom_Accuracy_XY IS NOT NULL";
		cd.tagParams = "onChange='setAccuracy(this.value, this.form)'";
		HTMLUtils.makeDropBox(out, conn, cd);
		out.write("</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Accuracy</td><td><input type='text' name='Accuracy' value='"
				+ FREDUtils.noNulls(getFieldForHTML(LocalityDE.ACCURACY))
				+ "'>&nbsp;m</td></tr>\n");
		out.write("<tr><td></td><td class='smallheading'>Locality<br />Description</td><td><textarea name='Loc' cols='40' rows='5'>"
				+ FREDUtils.noNulls(getFieldForHTML(LocalityDE.LOCALITY_DESC))
				+ "</textarea></td></tr>\n");
	}

	protected void makeEndBitHTML(Writer out) throws IOException {
		out.write("<table border='0' cellpadding='0' cellspacing='2'>\n");
		out.write("<tr><td>&nbsp;</td></tr>\n");
		out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
		if (isAllowedSubmit)
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database'/></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
		out.write("</table>\n");
	}

	private void parseCoord(String coord) throws DataInputException {
		if (coord.indexOf("*") == -1 || coord.indexOf("*") == coord.length() - 1)
			throw new DataInputException("Coordinate", "Invalid value");
		if (coord.indexOf("NZMG:") == 0) {
			String east = coord.substring(5, coord.indexOf("*"));
			String north = coord.substring(coord.indexOf("*") + 1, coord.length());
			try {
				origCoord =	new NorthingEasting(Double.parseDouble(north), Double.parseDouble(east));
				origSystem = DatumFactory.createDatum("NZMG");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("NZMS260:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
				throw new DataInputException("Coordinate", "Invalid value");
			String sheet = coord.substring(8, coord.indexOf("*"));
			String east = coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String north = coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			try {
				origCoord = new TruncNorthingEasting(Double.parseDouble(north), Double.parseDouble(east), sheet, east.length());
				origSystem = DatumFactory.createDatum("NZMS260");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("NZYS:") == 0) {
			String east = coord.substring(5, coord.indexOf("*"));
			String north = coord.substring(coord.indexOf("*") + 1, coord.length());
			try {
				origCoord =	new NorthingEasting(Double.parseDouble(north), Double.parseDouble(east));
				origSystem = DatumFactory.createDatum("NZ Yard SthIsl");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("NZYN:") == 0) {
			String east = coord.substring(5, coord.indexOf("*"));
			String north = coord.substring(coord.indexOf("*") + 1, coord.length());
			try {
				origCoord =	new NorthingEasting(Double.parseDouble(north), Double.parseDouble(east));
				origSystem = DatumFactory.createDatum("NZ Yard NthIsl");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("NZMS1S:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
				throw new DataInputException("Coordinate", "Invalid value");
			String sheet = coord.substring(7, coord.indexOf("*"));
			String east = coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String north = coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			try {
				origCoord = new TruncNorthingEasting(Double.parseDouble(north), Double.parseDouble(east), sheet, east.length());
				origSystem = DatumFactory.createDatum("NZMS1 SthIsl");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("NZMS1N:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
				throw new DataInputException("Coordinate", "Invalid value");
			String sheet = coord.substring(7, coord.indexOf("*"));
			String east = coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String north = coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			try {
				origCoord = new TruncNorthingEasting(Double.parseDouble(north), Double.parseDouble(east), sheet, east.length());
				origSystem = DatumFactory.createDatum("NZMS1 NthIsl");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("CHAT:") == 0) {
			String east = coord.substring(5, coord.indexOf("*"));
			String north = coord.substring(coord.indexOf("*") + 1, coord.length());
			try {
				origCoord =	new NorthingEasting(Double.parseDouble(north), Double.parseDouble(east));
				origSystem = DatumFactory.createDatum("Chatham Island Grid");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("AUCK:") == 0) {
			String east = coord.substring(5, coord.indexOf("*"));
			String north = coord.substring(coord.indexOf("*") + 1, coord.length());
			try {
				origCoord =	new NorthingEasting(Double.parseDouble(north), Double.parseDouble(east));
				origSystem = DatumFactory.createDatum("Auckland Island Transverse Mercator");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("CAMP:") == 0) {
			String east = coord.substring(5, coord.indexOf("*"));
			String north = coord.substring(coord.indexOf("*") + 1, coord.length());
			try {
				origCoord =	new NorthingEasting(Double.parseDouble(north), Double.parseDouble(east));
				origSystem = DatumFactory.createDatum("Campbell Island Transverse Mercator");
				countryCode = "NZ";
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("NZGD49:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
			throw new DataInputException("Coordinate", "Invalid value");
			String north = coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String east = coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			try {
				origCoord = new Datum.LatLong(Double.parseDouble(north), Double.parseDouble(east));
				origSystem = DatumFactory.createDatum("NZGD49");
				countryCode = coord.substring(7, coord.indexOf("*"));
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else if (coord.indexOf("WGS84:") == 0) {
			if (coord.indexOf("*") == coord.lastIndexOf("*"))
			throw new DataInputException("Coordinate", "Invalid value");
			String north = coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
			String east = coord.substring(coord.lastIndexOf("*") + 1, coord.length());
			try {
				origCoord = new Datum.LatLong(Double.parseDouble(north), Double.parseDouble(east));
				origSystem = DatumFactory.createDatum("WGS84");
				countryCode = coord.substring(6, coord.indexOf("*"));
			} catch (Exception e) {
				throw new DataInputException("Coordinate", "Invalid value");
			}
		} else {
			throw new DataInputException("Coordinate", "Invalid value");
		}
		if (!origSystem.coordinateAcceptable(origCoord))
			throw new DataInputException("Coordinate", "Coordinate not valid for given datum");
	}

	public int save() throws SQLException, IOException, InvalidCredentialsException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			String siteID = null;
			if (fields[GRID_REF] != null)
				siteID = String.valueOf(getSite().getId());
			if (feature == null) {
				QueryDescriptor qd = new QueryDescriptor("audit_table");
				qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
				qd.addQueryColumn("created_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
				qd.addQueryColumn("created_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
				qd.addQueryColumn("working_comments", Types.VARCHAR, FREDUtils.noNulls(recoll) + FREDUtils.noNulls(fields[WORKING_COMMENTS]));
				if (workingFolder != null)
					qd.addQueryColumn("working_folder_id", Types.NUMERIC, new Integer(workingFolder.getFolderID()));
				String auditID = DBUtils.doInsertUsingSequence(qd, "audit_id", "audit_seq", conn, true);
				qd = new QueryDescriptor("feature");
				if (siteID != null) 
					qd.addQueryColumn("site_id", Types.NUMERIC, new Integer(siteID));
				qd.addQueryColumn("audit_id", Types.NUMERIC, new Integer(auditID));
				qd.addQueryColumn("feature_type", Types.VARCHAR, featureType);
				qd.addQueryColumn("locality", Types.VARCHAR, fields[LOCALITY_DESC]);
				qd.addQueryColumn("feature_name", Types.VARCHAR, fields[FEATURE_NAME]);
				if (fields[REGISTRATION_AREA] != null)
					qd.addQueryColumn("reg_area_id", Types.NUMERIC, new Integer(fields[REGISTRATION_AREA]));
				String featureID = DBUtils.doInsertUsingSequence(qd, "feature_id", "feature_seq", conn, true);
				feature = new Feature(Integer.parseInt(featureID), user, state, true);
			} else { // edit
				QueryDescriptor qd;
				if (feature.getAsString(Feature.STATUS).equals(Audit.STATUS_APPROVED)) {
					qd = new QueryDescriptor("audit_edit");
					qd.addQueryColumn("audit_id", Types.NUMERIC, new Integer(feature.getAsInt(Feature.AUDIT_ID)));
					qd.addQueryColumn("edited_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
					qd.addQueryColumn("edited_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
					qd.addQueryColumn("comments", Types.VARCHAR, fields[EDIT_COMMENTS]);
					DBUtils.doInsertUsingSequence(qd, "audit_edit_id", "audit_edit_seq", conn, false);
				} else {
					qd = new QueryDescriptor("audit_table");
					qd.addQueryColumn("working_comments", Types.VARCHAR, FREDUtils.noNulls(recoll) + FREDUtils.noNulls(fields[WORKING_COMMENTS]));
					qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getAsInt(Feature.AUDIT_ID)));
					DBUtils.doUpdate(qd, "audit_id=?", conn);
				}
				qd = new QueryDescriptor("feature");
				if (siteID != null) 
					qd.addQueryColumn("site_id", Types.NUMERIC, new Integer(siteID));
				qd.addQueryColumn("locality", Types.VARCHAR, fields[LOCALITY_DESC]);
				qd.addQueryColumn("feature_name", Types.VARCHAR, fields[FEATURE_NAME]);
				if (fields[REGISTRATION_AREA] != null)
					qd.addQueryColumn("reg_area_id", Types.NUMERIC, new Integer(fields[REGISTRATION_AREA]));
				qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getFeatureID()));
				DBUtils.doUpdate(qd, "feature_id=?", conn);
				feature = new Feature(feature.getFeatureID(), user, state, true);
			}
			if (workingFolder != null) {
				Folder folder = new Folder(workingFolder.getFolderID(), user, state, true);
			}
			savedFlag = true;
		}
		return feature.getFeatureID();
	}

	public int submit() throws SQLException, IOException, InvalidCredentialsException, DataInputException {
		if ((feature != null && feature.getAsString(Feature.STATUS).equals(Audit.STATUS_WAITING)) || !isAllowedSubmit)
			throw new InvalidCredentialsException();
		if (featureType == null || fields[GRID_REF] == null || fields[REGISTRATION_AREA] == null)
			throw new DataInputException("Mandatory Fields", "Not all mandatory fields completed");
		save();
		//change status and set Masterfile
		DBConnection conn = FREDUtils.getFREDConnection(state);
		QueryDescriptor qd = new QueryDescriptor("audit_table");
		qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WAITING);
		qd.addQueryColumn("submitted_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
		qd.addQueryColumn("submitted_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getAsInt(Feature.AUDIT_ID)));
		DBUtils.doUpdate(qd, "audit_id = ?", conn);
		int mfID = FREDUtils.getMasterfile(Integer.parseInt(fields[REGISTRATION_AREA]), getSite().getLatLong());
		qd = new QueryDescriptor("feature");
		qd.addQueryColumn("masterfile_id", Types.NUMERIC, new Integer(mfID));
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getFeatureID()));
		DBUtils.doUpdate(qd, "feature_id = ?", conn);
		feature = new Feature(feature.getFeatureID(), user, state, true);
		refreshSamples(feature, user, state);
		Folder folder = new Folder(mfID, user, state, true);
		return feature.getFeatureID();
	}

	public static void revoke(Feature feature, User user, PageState state) throws SQLException, IOException, InvalidCredentialsException {
		if (!FREDUtils.isAllowedRevokeLocality(user, feature.getAsString(Feature.STATUS), String.valueOf(feature.getFeatureID()), state))
			throw new InvalidCredentialsException();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		QueryDescriptor qd = new QueryDescriptor("audit_table");
		qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_WORKING);
		qd.addQueryColumn("submitted_by_id", Types.NUMERIC, null);
		qd.addQueryColumn("submitted_date", Types.DATE, null);
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getAsInt(Feature.AUDIT_ID)));
		DBUtils.doUpdate(qd, "audit_id = ?", conn);
		conn.releaseStatement();
		feature = new Feature(feature.getFeatureID(), user, state, true);
		refreshSamples(feature, user, state);
		Folder folder = new Folder(feature.getAsInt(Feature.MASTERFILE_ID), user, state, true);
	}

	public void approve(FRNumber frNum, String comments) throws SQLException, IOException, InvalidCredentialsException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		//generate FR number record
		QueryDescriptor qd = new QueryDescriptor("fr_number");
		qd.addQueryColumn("map_sheet", Types.VARCHAR, frNum.getMapSheet());
		qd.addQueryColumn("serial_number", Types.NUMERIC, frNum.getSerialNumber());
		qd.addQueryColumn("recollection_number", Types.VARCHAR, frNum.getRecollectionNumber());
		String frID = DBUtils.doInsertUsingSequence(qd, "fr_id", "fr_seq", conn, true);
		qd = new QueryDescriptor("sample");
		qd.addQueryColumn("fr_id", Types.NUMERIC, new Integer(frID));
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getFeatureID()));
		DBUtils.doUpdate(qd, "feature_id = ?", conn);
		try {
			//explicitly add to working folder
			String query = "INSERT INTO folder_content (folder_id, feature_id) VALUES (?, ?)";
			conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.NUMERIC}, new Object[] {new Integer(feature.getAsInt(Feature.WORKING_FOLDER_ID)), new Integer(feature.getFeatureID())});
		} catch (Exception e) {}
		//update audit table
		qd = new QueryDescriptor("audit_table");
		qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_APPROVED);
		qd.addQueryColumn("approved_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
		qd.addQueryColumn("approved_date", Types.DATE, java.sql.Date.valueOf(FREDUtils.getNowForSQL()));
		qd.addQueryColumn("working_folder_id", Types.NUMERIC, null);
		qd.addQueryColumn("working_comments", Types.VARCHAR, null);
		qd.addQueryColumn("curator_comments", Types.VARCHAR, comments);
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getAsInt(Feature.AUDIT_ID)));
		DBUtils.doUpdate(qd, "audit_id = ?", conn);
		conn.releaseStatement();
		feature = new Feature(feature.getFeatureID(), user, state, true);
		refreshSamples(feature, user, state);
		Folder folder = new Folder(feature.getAsInt(Feature.MASTERFILE_ID), user, state, true);
	}
	
	public void reject(String comments) throws SQLException, IOException, InvalidCredentialsException {
		DBConnection conn = FREDUtils.getFREDConnection(state);
		QueryDescriptor qd = new QueryDescriptor("audit_table");
		qd.addQueryColumn("status", Types.VARCHAR, Audit.STATUS_REJECTED);
		qd.addQueryColumn("curator_comments", Types.VARCHAR, comments);
		qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(feature.getAsInt(Feature.AUDIT_ID)));
		DBUtils.doUpdate(qd, "audit_id = ?", conn);
		feature = new Feature(feature.getFeatureID(), user, state, true);
		refreshSamples(feature, user, state);
		Folder folder = new Folder(feature.getAsInt(Feature.MASTERFILE_ID), user, state, true);
	}

	public void delete() throws IOException, SQLException, InvalidCredentialsException {
		if (!FREDUtils.isAllowedDeleteLocality(user, feature.getAsString(Feature.STATUS), String.valueOf(feature.getFeatureID()), state) && feature != null)
			throw new InvalidCredentialsException();
		DBConnection conn = FREDUtils.getFREDConnection(state);
		String query = "DELETE FROM feature WHERE feature_id = ?";
		conn.executeUpdate(query, new int[] {Types.NUMERIC}, new Object[] {new Integer(feature.getFeatureID())});
		conn.releaseStatement();
	}
	
	private static void refreshSamples(Feature feature, User user, PageState state) throws InvalidCredentialsException, SQLException, IOException {
		if (feature.getSampleCount() > 0) {
			for (Iterator i = feature.getAsVector(Feature.SAMPLES).iterator(); i.hasNext(); ) {
				Sample sample = new Sample(((Integer) i.next()).intValue(), user, state, true);
			}
		}
	}

	public int getWorkingFolderID() {
		if (workingFolder != null)
			return workingFolder.getFolderID();
		return -1;
	}

	private SiteRecord getSite() throws SQLException  {
		DatumMethod horzDM = null;
		try {
			if (fields[METHOD] != null) {
				horzDM = DatumMethod.getDatumMethod(Integer.parseInt(fields[METHOD]), FREDUtils.getFREDConnection(state));
				if (fields[ACCURACY] != null)
					horzDM.setHorizontalAccuracy(Float.parseFloat(fields[ACCURACY]));
			}
			return SiteRecord.insertSite(fields[FIELD_NUMBER], origSystem, origCoord, null, horzDM, null, null, fields[LOCALITY_DESC], countryCode, String.valueOf(user.getPersonId()), 0, JspUtils.getInstance(state.getContext()));
		} catch (Exception e) {
			throw new SQLException("Problem creating SITE: " + e.getMessage());
		}
	}

}
