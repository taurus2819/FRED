package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.FolderUtils;
import nz.cri.gns.fred.data.PaleontologyRecord;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.fred.data.Taxa;
import nz.cri.gns.fred.data.TaxaGroup;
import nz.cri.gns.fred.data.TaxonomicLookup;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

public class PaleontologyRecordDE extends RecordDE {

	private RoundedDate identDate;
	private Vector identifiers;
	private String lab;
	private Vector taxaList;
	private Vector badTaxaList;
	private boolean nonApprovedTaxaFlag = false;

	public PaleontologyRecordDE(User user, int sampleID, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, sampleID, folderID, Record.PALEONTOLOGY_RECORD, state);
	}

	public PaleontologyRecordDE(User user, int folderID, PageState state) throws DataInputException, SQLException, IOException {
		super(user, folderID, Record.PALEONTOLOGY_RECORD, state);
	}

	public PaleontologyRecordDE(int recID, User user, PageState state) throws IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		super(recID, Record.PALEONTOLOGY_RECORD, user, state);
		getFromDatabase(record);
		savedFlag = true;
	}

	protected void getFromDatabase(Record record) throws IllegalArgumentException, DataInputException {
		try {
			super.getFromDatabase(record);
			setField(IDENTIFICATION_DATE, DataEntryUtils.reverseParseDate(record.getAsDate(Record.IDENTIFICATION_DATE),	record.getAsString(Record.IDENTIFICATION_DATE_ROUNDING)));
			if (record.get(Record.IDENTIFIER) != null) {
				StringBuffer identName = new StringBuffer();
				for (Iterator i = record.getAsVector(Record.IDENTIFIER).iterator(); i.hasNext();) {
					KeyValueObject ident = (KeyValueObject) i.next();
					identName.append(ident.getValue() + "\n");
				}
				setField(IDENTIFIERS, identName.toString());
			}
			setField(IDT_AGE_START,	record.getAsString(Record.STAGE_LOWER_ID));
			setField(IDT_START_MOD,	record.getAsString(Record.STAGE_LOWER_MOD));
			setField(IDT_AGE_STOP, record.getAsString(Record.STAGE_UPPER_ID));
			setField(IDT_STOP_MOD, record.getAsString(Record.STAGE_UPPER_MOD));
			setField(STAGE_COMMENTS, record.getAsString(Record.STAGE_COMMENTS));
			setField(LAB_SECTION, record.getAsString(Record.LAB_SECTION_ID));
			setField(LAB_NUMBER, record.getAsString(Record.LAB_NUMBER));
			setField(COLLECTION_COMMENTS, record.getAsString(Record.COLLECTION_COMMENTS));
			if (record.get(Record.TAXONOMIC_LIST) != null) {
				StringBuffer taxaList = new StringBuffer();
				for (Iterator i = record.getAsVector(Record.TAXONOMIC_LIST).iterator(); i.hasNext();) {
					TaxaGroup tGroup = (TaxaGroup) i.next();
					if (tGroup.getTaxaList() != null) {
						for (Iterator j = tGroup.getTaxaList().iterator(); j.hasNext();) {
							Taxa t = (Taxa) j.next();
							taxaList.append(tGroup.getGroupName() + "*" + t.getTaxonomicName() + "*" + FREDUtils.noNulls(t.getAuthor()) + "*" + FREDUtils.noNulls(t.getSpecimenCount()) + "*" + FREDUtils.noNulls(t.getSpecimenCoords()) + "*" + FREDUtils.noNulls(t.getComments()) + "\n");
						}
					} else {
						taxaList.append(tGroup.getGroupName() + "*****\n");
					}
				}
				setField(TAXA_LIST, taxaList.toString());
			}
		} catch (TaxonomicListException e) {}		
	}

	protected void parseField(int field, String value)
		throws DataInputException, TaxonomicListException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case IDENTIFICATION_DATE :
					identDate = DataEntryUtils.parseRoundedDate(value);
					break;
				case IDENTIFIERS :
					identifiers = new Vector();
					String query = "SELECT person_id FROM person_view WHERE name = ?";
					int personID = 0;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value = value + "\n";	
						try {
							rs = conn.executeQuery(query, new int[] {Types.VARCHAR}, new Object[] {value.substring(0, value.indexOf("\n")).trim()});
							rs.next();
							personID = rs.getInt(1);
						} catch (Exception e) {
							throw new DataInputException("Identifier", value.substring(0, value.indexOf("\n")).trim() + " not in database - add through builder");
						}
						if (identifiers.indexOf(new Integer(personID)) != -1)
							throw new DataInputException("Identifier", value.substring(0, value.indexOf("\n")).trim() + " duplicated");
						identifiers.add(new Integer(personID));
						value = value.substring(value.indexOf("\n") + 1, value.length());
					}
					break;
				case IDT_AGE_START :
					DataEntryUtils.parseAge(value, getField(IDT_AGE_STOP), "Age", state);
					break;
				case IDT_AGE_STOP :
					DataEntryUtils.parseAge(getField(IDT_AGE_START), value, "Age", state);
					break;
				case IDT_START_MOD :
				case IDT_STOP_MOD :
					if (value != null && !value.equals("?"))
						throw new DataInputException("Age", "Bad Modifier");
					break;
				case LAB_SECTION :
					try {
						rs = conn.executeQuery("SELECT lab_id FROM lab_section WHERE lab_section_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(value)});
						rs.next();
						lab = rs.getString(1);
					} catch (Exception e) {
						throw new DataInputException("Laboratory", "Value not in list");
					}
					break;
				case TAXA_LIST :
					taxaList = new Vector();
					badTaxaList = new Vector();
					nonApprovedTaxaFlag = false;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value =  value + "\n";
						String taxaLine = value.substring(0, value.indexOf("\n")).trim();
						String taxaGroup, taxaName, taxaAuthor, taxaSpecCoord, taxaComm;
						Integer taxaSpecCount;
						try {
							taxaGroup = taxaLine.substring(0, taxaLine.indexOf("*"));
							taxaName = taxaLine.substring(taxaGroup.length() + 1, taxaLine.indexOf("*", taxaGroup.length() + 1));
							taxaAuthor = taxaLine.substring(taxaGroup.length() + taxaName.length() + 2, taxaLine.indexOf("*", taxaGroup.length() + taxaName.length() + 2));
							String taxaSpecCountStr = taxaLine.substring(taxaGroup.length() + taxaName.length() + taxaAuthor.length() + 3, taxaLine.indexOf("*", taxaGroup.length() + taxaName.length() + taxaAuthor.length() + 3));
							taxaSpecCount = ((taxaSpecCountStr.equals("")) ? null : new Integer(taxaSpecCountStr));
							taxaSpecCoord = taxaLine.substring(taxaGroup.length() + taxaName.length() + taxaAuthor.length() + taxaSpecCountStr.length() + 4, taxaLine.indexOf("*", taxaGroup.length() + taxaName.length() + taxaAuthor.length() + taxaSpecCountStr.length() + 4));
							taxaComm = taxaLine.substring(taxaLine.lastIndexOf("*") + 1, taxaLine.length());
						} catch (Exception e) {
							throw new DataInputException("Taxanomic", taxaLine + " not valid");
						}

						//check TaxaGroup against lookup values
						Taxa taxa;
						try {
							query = "SELECT Lookup_ID FROM Lookup WHERE Name = ? AND FieldName = ?";
							rs = conn.executeQuery(query, new int[] {Types.VARCHAR, Types.VARCHAR}, new Object[] {taxaGroup, "TaxaGroup"});
							rs.next();
							taxa = new Taxa();
							taxa.setGroupID(new Integer(rs.getInt(1)));
							taxa.setGroupName(taxaGroup);
						} catch (Exception e) {  // not valid group
							throw new DataInputException("Taxonomic", taxaGroup + " not a valid taxonomic group");
						}
						taxa.setAuthor(taxaAuthor);
						taxa.setSpecimenCount(taxaSpecCount);
						taxa.setSpecimenCoords(taxaSpecCoord);
						taxa.setComments(taxaComm);
						if (!taxaName.trim().equals("")) {
							//clean TaxaName
							String cleanName = getCleanedName(taxaName);
							//check TaxaName against thesaurus
							try {
								query = "SELECT taxa_id, status FROM taxonomic_lookup WHERE group_id = ? AND taxonomic_name = ?";
								rs = conn.executeQuery(query, new int[] {Types.NUMERIC, Types.VARCHAR}, new Object[] {taxa.getGroupID(), cleanName});
								rs.next();
								taxa.setTaxaID(new Integer(rs.getInt(1)));
								taxa.setTaxonomicName(taxaName);
								taxaList.add(taxa);
								if (!rs.getString(2).equals(TaxonomicLookup.APPROVED_STATUS)) {
									nonApprovedTaxaFlag = true;
								}
							} catch (Exception e) {  // not valid name
								taxa.setTaxonomicName(taxaName);
								taxa.setCleanTaxonomicName(cleanName);
								badTaxaList.add(taxa);
							}
						}
						value = value.substring(value.indexOf("\n") + 1, value.length()).trim();
					}
					if (badTaxaList.size() > 0)
						throw new TaxonomicListException(badTaxaList);
					break;
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException e) {
			throw new DataInputException();
		}
	}

	protected void resetHiddenField(int field) {
		switch (field) {
			case ADOPTION_DATE :
				identDate = null;
				break;
			case ADOPTORS :
				identifiers = null;
				break;
			case LAB_SECTION :
				lab = null;
				break;
			case TAXA_LIST :
				nonApprovedTaxaFlag = false;
				taxaList = null;
				break;
		}
	}
	
	public void makeDataEntryHTML(Writer out)
		throws IOException, SQLException {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			super.makeDataEntryHTML(out);
			out.write("<tr><td class='heading'>Identification Date</td><td></td><td><input type='text' name='PalDate' value='"
					+ FREDUtils.noNulls(getFieldForHTML(IDENTIFICATION_DATE))
					+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=PalDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
			out.write("<tr><td class='heading'>Identifiers</td><td></td><td><textarea name='Identifier' cols='40' rows='2'>"
					+ FREDUtils.noNulls(getFieldForHTML(IDENTIFIERS))
					+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Identifier\", \"Supp\", \"width=600,height=400\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
			out.write("<tr><td class='heading' colspan='2'>Stage Limits</td><td>\n");
			out.write("<table border='0' cellspacing='0'><tr><td>");
			ComboDescriptor cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "StageStart";
			cd.prompt = "-- Choose --";
			cd.selected = getFieldForHTML(IDT_AGE_START);
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
			out.write("</td><td><select name='StartMod'><option value='-' "
					+ ((getFieldForHTML(IDT_START_MOD) == null) ? " selected" : "")
					+ "></option><option value='?' "
					+ ((getFieldForHTML(IDT_START_MOD) != null
						&& getFieldForHTML(IDT_START_MOD).equals("?"))
						? " selected"
						: "")
					+ ">?</option></select></td><td class='heading'> to </td></tr>\n");
			out.write("<tr><td>");
			cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "StageStop";
			cd.prompt = "-- Choose --";
			cd.selected = getFieldForHTML(IDT_AGE_STOP);
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
			out.write("</td><td class='heading'><select name='StopMod'><option value='-' "
					+ ((getFieldForHTML(IDT_STOP_MOD) == null) ? " selected" : "")
					+ "></option><option value='?' "
					+ ((getFieldForHTML(IDT_STOP_MOD) != null
						&& getFieldForHTML(IDT_STOP_MOD).equals("?"))
						? " selected"
						: "")
					+ ">?</option></select></td></tr>\n");
			out.write("</table></td></tr>\n");
			out.write("<tr><td class='heading' colspan='2'>Stage Comments</td><td><textarea name='StComm' cols='40' rows='2'>"
					+ FREDUtils.noNulls(getFieldForHTML(STAGE_COMMENTS))
					+ "</textarea></td></tr>\n");
					
			//build array of labs sections
			out.write("<script language='JavaScript'>\n");
			ResultSet rs = conn.executeQuery("SELECT DISTINCT Lab_ID FROM Lab_Section");
			while (rs.next()) {
				out.write("a" + rs.getString(1) + " = new Array();\n");
				ResultSet rs2 = conn.getExtraStatement().executeQuery("SELECT Lab_Section_ID, Code FROM Lab_Section WHERE Lab_ID = " + rs.getString(1));
				int count = 0;
				while (rs2.next()) {
					out.write("a" + rs.getString(1) + "[" + count++ + "] = new Array('" + rs2.getString(1) + "','" + rs2.getString(2) + "');\n");
				}
			}
			out.write("function swapSection(frm){\n");
			out.write("if (frm.LabID.options[frm.LabID.options.selectedIndex].value!='-'){\n");
			out.write("var aArray = eval(\"a\"+frm.LabID.options[frm.LabID.options.selectedIndex].value);\n");
			out.write("frm.SectID.options.length = aArray.length + 1;\n");
			out.write("for(i = 0;i<aArray.length;i++){\n");
			out.write("frm.SectID.options[i+1].value = aArray[i][0];\n");
			out.write("frm.SectID.options[i+1].text = aArray[i][1];\n");
			out.write("}\n} else {\n");
			out.write("frm.SectID.options.length = 1;\n");
			out.write("}\nfrm.SectID.options.selectedIndex = 0;\n}\n");
			out.write("</script>\n");										

			out.write("<tr><td class='heading'>Laboratory</td><td class='smallheading'>Name</td><td>");
			cd = new ComboDescriptor("Lab_View", "Lab_ID", "Lab_Name");
			cd.name = "LabID";
			cd.prompt = "-- Choose --";
			cd.selected = lab;
			cd.orderBy = "Lab_Name";
			cd.selectDistinct = true;
			cd.tagParams = "onChange='swapSection(this.form)'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
			out.write("</td></tr>\n");
			out.write("<tr><td></td><td class='smallheading'>Code</td><td><select name='SectID'><option value='-' selected>-- Choose --</option></select></td></tr>\n");
			out.write("<tr><td></td><td class='smallheading'>Number</td><td><input type='text' name='LabNum' size='20' value='" + FREDUtils.noNulls(getFieldForHTML(LAB_NUMBER)) + "'></td><td></td></tr>\n");
			if (getFieldForHTML(LAB_SECTION) != null) {
				out.write("<script language='JavaScript'>\n");
				out.write("swapSection(form1);");
				out.write("for(i=0;i<form1.SectID.options.length;i++){ if (form1.SectID.options[i].value=='" + getFieldForHTML(LAB_SECTION) + "') { form1.SectID.options.selectedIndex = i; }}\n");
				out.write("</script>\n");
			}
			out.write("<tr><td class='heading' colspan='2'>Collection Comments</td><td><textarea name='CollComm' cols='40' rows='2'>"
					+ FREDUtils.noNulls(getFieldForHTML(COLLECTION_COMMENTS))
					+ "</textarea></td></tr>\n");
			out.write("<tr><td class='heading' colspan='3' style=\"color: #FF0000\">Taxonomic List");
			try {
				if (!FolderUtils.isTaxaApproved(record.getRecordID(), user, state))
					out.write("<br /><span class=\"smallheading\" style=\"color: #FF0000\">Some taxonomic entries listed below have not been approved and this record can not be submitted.  Click <a href=\"record_taxa_list.jsp?RecID=" + record.getRecordID() + "\" target=\"taxaList\">here</a> for more details</span>");
			} catch (Exception e) {}
			out.write("</td></tr>\n");
			out.write("<tr><td colspan='3'><textarea name='Taxa' cols='80' rows='20'>" + FREDUtils.noNulls(getFieldForHTML(TAXA_LIST)) + "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Taxa\", \"Supp\", \"width=600,height=500\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
			super.makeEndBitHTML(out);
	}

	public int save()
		throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				//Delete existing PALEONTOLOGY record
				conn.executeUpdate("DELETE FROM paleontology WHERE record_id = ?", new int[] {Types.NUMERIC}, new Object[] {new Integer(record.getRecordID())});
				//Create new PALEONTOLOGY record
				String stageID = DataEntryUtils.getStageID(getField(IDT_AGE_START), getField(IDT_START_MOD), getField(IDT_AGE_STOP), getField(IDT_STOP_MOD), state);
				String query = "INSERT INTO paleontology (record_id, identification_date, date_rounding, stage_id, stage_comments, lab_section_id, lab_number, collection_comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				conn.executeUpdate(query, new int[] {Types.NUMERIC, Types.DATE, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR, Types.VARCHAR},
					 new Object[] {new Integer(record.getRecordID()), ((identDate != null) ? identDate.getDate() : null), ((identDate != null) ? identDate.getDateRounding() : null),
					 ((stageID != null) ? new Integer(stageID) : null), getField(STAGE_COMMENTS), ((getField(LAB_SECTION) != null) ? new Integer(getField(LAB_SECTION)) : null), getField(LAB_NUMBER), getField(COLLECTION_COMMENTS)});
				//Create IDENTIFIERS entries
				if (identifiers != null) {
					query = "INSERT INTO identifier (record_id, person_id) VALUES (?, ?)";
					int[] types = new int[] {Types.NUMERIC, Types.NUMERIC};
					Object[] values = new Object[2];
					values[0] = new Integer(record.getRecordID());
					for (Iterator i = identifiers.iterator(); i.hasNext();) {
						values[1] = (Integer) i.next();
						conn.executeUpdate(query, types, values);
					}
				}
				//Create PAL_LIST entry
				if (taxaList != null) {
					query = "INSERT INTO pal_list (record_id, group_id, taxa_id, taxonomic_name, specimen_count, specimen_coords, comments) VALUES (?, ?, ?, ?, ?, ?, ?)";
					int[] types = new int[] {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, Types.VARCHAR, Types.VARCHAR};
					Object[] values = new Object[7];
					values[0] = new Integer(record.getRecordID());
					for (Iterator i = taxaList.iterator(); i.hasNext();) {
						Taxa taxa = (Taxa) i.next();
						values[1] = taxa.getGroupID();
						values[2] = taxa.getTaxaID();
						values[3] = taxa.getTaxonomicName();
						values[4] = taxa.getSpecimenCount();
						values[5] = taxa.getSpecimenCoords();
						values[6] = taxa.getComments();	
						conn.executeUpdate(query, types, values);
					}
				}
				
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				record = (PaleontologyRecord) PaleontologyRecord.getData(record.getRecordID(), user, state, true);
				sample = new Sample(sample.getSampleID(), user, state, true);
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			} catch (InvalidCredentialsException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw e;
			}

		}
		return record.getRecordID();
	}
	
	public int submit() throws SQLException, IOException, InvalidCredentialsException, DataInputException {
		int recordID = super.submit();
		record = PaleontologyRecord.getData(record.getRecordID(), user, state, true);
		return recordID;
	}
	
	protected void checkMandatoryFields() throws DataInputException {
		if (badTaxaList.size() > 0 || nonApprovedTaxaFlag)
			throw new DataInputException("Mandatory Fields", "Not all taxonomic entries are approved");
	}
	
	public static String getCleanedName(String cleanName) throws DataInputException {
		if (cleanName == null)
			throw new DataInputException();
		cleanName = cleanName.replaceAll("\"", "'");
		cleanName = cleanName.replaceAll("<", "'");
		cleanName = cleanName.replaceAll(">", "'");
		cleanName = cleanName.replaceAll("  ", " ");
		cleanName = cleanName.replaceAll("group", "gr.");
		cleanName = cleanTaxaName(cleanName, "?");
		cleanName = cleanTaxaNameOpen(cleanName, "subsp.");
		cleanName = cleanTaxaNameOpen(cleanName, "subspp.");
		cleanName = cleanTaxaNameOpen(cleanName, "sp.");
		cleanName = cleanTaxaNameOpen(cleanName, "spp.");
		cleanName = cleanTaxaNameOpen(cleanName, "subgen.");
		cleanName = cleanTaxaNameOpen(cleanName, "gen.");
		cleanName = cleanTaxaNameOpen(cleanName, "subfam.");
		cleanName = cleanTaxaNameOpen(cleanName, "fam.");
		cleanName = cleanTaxaName(cleanName, "indet.");
		cleanName = cleanTaxaName(cleanName, "cf.");
		cleanName = cleanTaxaName(cleanName, "aff.");
		cleanName = cleanTaxaName(cleanName, "MS.");
		cleanName = cleanTaxaName(cleanName, "s.s.");
		cleanName = cleanTaxaName(cleanName, "s.s");
		cleanName = cleanTaxaName(cleanName, "s.l.");
		cleanName = cleanTaxaName(cleanName, "ex gr.");
		cleanName = cleanTaxaName(cleanName, "gr.");
		cleanName = cleanTaxaName(cleanName, "var.");
		return cleanName;
	}
	
	private static String cleanTaxaName (String taxaName, String checkString) {
		while (taxaName.indexOf(checkString) >= 0) {
			taxaName = taxaName.substring(0, taxaName.indexOf(checkString)).trim() + " " + taxaName.substring(taxaName.indexOf(checkString) + checkString.length(), taxaName.length()).trim();
			taxaName = taxaName.trim();
		}
		return taxaName;
	}
	
	private static String cleanTaxaNameOpen (String taxaName, String checkString) {
		taxaName = cleanAlphaChar(taxaName, checkString);
		taxaName = cleanTaxaName(taxaName, "n." + checkString + "indet.");
		taxaName = cleanTaxaName(taxaName, "n. " + checkString + "indet.");
		taxaName = cleanTaxaName(taxaName, "n." + checkString + " indet.");
		taxaName = cleanTaxaName(taxaName, "n. " + checkString + " indet.");
		taxaName = cleanTaxaName(taxaName, "n." + checkString);
		taxaName = cleanTaxaName(taxaName, "n. " + checkString);
		taxaName = cleanTaxaName(taxaName, checkString + "indet.");
		taxaName = cleanTaxaName(taxaName, checkString + " indet.");
		taxaName = cleanTaxaName(taxaName, checkString);
		return taxaName;
	}

	private static String cleanAlphaChar (String taxaName, String checkString) {
		int len = taxaName.length();
		int pos = 0;
		boolean ok = true;
		while (ok) {
			pos = taxaName.indexOf(checkString, pos + 1);
			if (pos > 0 && pos + checkString.length() < len) {
				pos = pos + checkString.length();
				if (pos + 1 == len || pos + 2 == len) {
					taxaName = taxaName.substring(0, pos);
				} else if (taxaName.indexOf(" ", pos + 1) <= pos + 2 && taxaName.indexOf(" ", pos + 1) > 0) {
					taxaName = taxaName.substring(0, pos) + "  " + taxaName.substring(pos + 2, taxaName.length());
				}
			} else {
				ok = false;
			}
		}
		return taxaName;
	}
	
}
