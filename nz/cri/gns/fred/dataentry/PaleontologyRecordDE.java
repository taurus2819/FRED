package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.data.PaleontologyRecord;
import nz.cri.gns.fred.data.Record;
import nz.cri.gns.fred.data.Sample;
import nz.cri.gns.fred.data.Taxa;
import nz.cri.gns.fred.data.TaxaGroup;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class PaleontologyRecordDE extends RecordDE {

	private RoundedDate identDate;
	private Vector identifiers;
	private String lab;
	private Vector taxaList;
	private Vector badTaxaList;

	public PaleontologyRecordDE(User user, int sampleID, int folderID, PageState state)
		throws SQLException, IOException, DataInputException {
		super(user, sampleID, folderID, "PAL", state);
	}

	public PaleontologyRecordDE(User user, int folderID, PageState state)
		throws DataInputException, SQLException, IOException {
		super(user, folderID, "PAL", state);
	}

	public PaleontologyRecordDE(int recID, User user, PageState state)
		throws IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		super(recID, "PAL", user, state);
		try {
			setField(IDENTIFICATION_DATE,
				DataEntryUtils.reverseParseDate(
					record.getAsDate(Record.IDENTIFICATION_DATE),
					record.getAsString(Record.IDENTIFICATION_DATE_ROUNDING)));
			if (record.get(Record.IDENTIFIER) != null) {
				StringBuffer identName = new StringBuffer();
				for (Iterator i = record.getAsVector(Record.IDENTIFIER).iterator(); i.hasNext();) {
					KeyValueObject ident = (KeyValueObject) i.next();
					identName.append(ident.getValue() + "\n");
				}
				setField(IDENTIFIERS, identName.toString());
			}
			setField(
				IDT_AGE_START,
				record.getAsString(Record.STAGE_LOWER_ID));
			setField(
				IDT_START_MOD,
				record.getAsString(Record.STAGE_LOWER_MOD));
			setField(
				IDT_AGE_STOP,
				record.getAsString(Record.STAGE_UPPER_ID));
			setField(
				IDT_STOP_MOD,
				record.getAsString(Record.STAGE_UPPER_MOD));
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
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1)
							value = value + "\n";
						rs =
							conn.executeQuery(
								"SELECT Person_ID FROM Person_View WHERE Name = "
									+ JspUtils.sqlEscape(
										value
											.substring(0, value.indexOf("\n"))
											.trim()));
						try {
							rs.next();
							identifiers.add(new Integer(rs.getInt(1)));
						} catch (Exception e) {
							throw new DataInputException(
								"Identifier",
								value.substring(0, value.indexOf("\n")).trim()
									+ " not in database - add through builder");
						}
						value =
							value.substring(
								value.indexOf("\n") + 1,
								value.length());
					}
					break;
				case IDT_AGE_START :
					parseAge(value, getField(IDT_AGE_STOP), "Age");
					break;
				case IDT_AGE_STOP :
					parseAge(getField(IDT_AGE_START), value, "Age");
					break;
				case IDT_START_MOD :
				case IDT_STOP_MOD :
					if (value != null && !value.equals("?"))
						throw new DataInputException(
							"Age",
							"Bad Modifier");
					break;
				case LAB_SECTION :
					rs = conn.executeQuery("SELECT Lab_ID FROM Lab_Section WHERE Lab_Section_ID = " + value);
					try {
						rs.next();
						lab = rs.getString(1);
					} catch (Exception e) {
						throw new DataInputException("Laboratory", "Value not in list");
					}
					break;
				case TAXA_LIST :
					taxaList = new Vector();
					badTaxaList = new Vector();
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
						rs = conn.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = " + JspUtils.sqlEscape(taxaGroup) + " AND FieldName = 'TaxaGroup'");
						Taxa taxa = new Taxa();
						try {
							rs.next();
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
							rs = conn.executeQuery("SELECT Taxa_ID FROM Taxonomic_Lookup WHERE Group_ID = " + taxa.getGroupID() + " AND Taxonomic_Name = " + JspUtils.sqlEscape(cleanName) + " AND Status IN ('approved', 'provisional')");
							try {
								rs.next();
								taxa.setTaxaID(new Integer(rs.getInt(1)));
								taxa.setTaxonomicName(taxaName);
								taxaList.add(taxa);
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
				taxaList = null;
				break;
		}
	}
	
	public void makeDataEntryHTML(Writer out)
		throws IOException, SQLException {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			super.makeDataEntryHTML(out);
			out.write(
				"<tr><td class='heading'>Identification Date</td><td></td><td><input type='text' name='PalDate' value='"
					+ FREDUtils.noNulls(getFieldForHTML(IDENTIFICATION_DATE))
					+ "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=PalDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
			out.write(
				"<tr><td class='heading'>Identifiers</td><td></td><td><textarea name='Identifier' cols='40' rows='2'>"
					+ FREDUtils.noNulls(getFieldForHTML(IDENTIFIERS))
					+ "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Identifier\", \"Supp\", \"width=600,height=400\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
			out.write(
				"<tr><td class='heading' colspan='2'>Stage Limits</td><td>\n");
			out.write("<table border='0' cellspacing='0'><tr><td>");
			ComboDescriptor cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "StageStart";
			cd.prompt = "-- Choose --";
			cd.selected = getFieldForHTML(IDT_AGE_START);
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), conn, cd);
			out.write(
				"</td><td><select name='StartMod'><option value='-' "
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
			out.write(
				"</td><td class='heading'><select name='StopMod'><option value='-' "
					+ ((getFieldForHTML(IDT_STOP_MOD) == null) ? " selected" : "")
					+ "></option><option value='?' "
					+ ((getFieldForHTML(IDT_STOP_MOD) != null
						&& getFieldForHTML(IDT_STOP_MOD).equals("?"))
						? " selected"
						: "")
					+ ">?</option></select></td></tr>\n");
			out.write("</table></td></tr>\n");
			out.write(
				"<tr><td class='heading' colspan='2'>Stage Comments</td><td><textarea name='StComm' cols='40' rows='2'>"
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
			out.write(
				"<tr><td class='heading' colspan='2'>Collection Comments</td><td><textarea name='CollComm' cols='40' rows='2'>"
					+ FREDUtils.noNulls(getFieldForHTML(COLLECTION_COMMENTS))
					+ "</textarea></td></tr>\n");
			out.write("<tr><td class='heading'>Taxonomic List</td></tr>\n");
			out.write("<tr><td colspan='3'><textarea name='Taxa' cols='80' rows='20'>" + FREDUtils.noNulls(getFieldForHTML(TAXA_LIST)) + "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Taxa\", \"Supp\", \"width=600,height=500\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
			super.makeEndBitHTML(out);
	}

	public int save()
		throws InvalidCredentialsException, SQLException, IOException {
		if (!savedFlag) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			conn.getConnection().setAutoCommit(false);
			try {
				super.save();
				conn.executeUpdate(
					"DELETE FROM Paleontology WHERE Record_ID = "
						+ record.getRecordID());
				//Create PALEONTOLOGY entry
				conn.executeUpdate(
					"INSERT INTO Paleontology (Record_ID, Identification_Date, Date_Rounding, Stage_ID, Stage_Comments, Lab_Section_ID, Lab_Number, Collection_Comments) VALUES ("
						+ record.getRecordID()
						+ ((identDate != null)
							? ", TO_DATE('"
								+ identDate.getDateString()
								+ "'), "
								+ JspUtils.sqlEscape(identDate.getDateRounding())
							: ", NULL, NULL")
						+ ", "
						+ JspUtils.sqlEscape(
							DataEntryUtils.getStageID(
								getField(IDT_AGE_START),
								getField(IDT_START_MOD),
								getField(IDT_AGE_STOP),
								getField(IDT_STOP_MOD),
								state))
						+ ", "
						+ JspUtils.sqlEscape(getField(STAGE_COMMENTS))
						+ ", "
						+ JspUtils.sqlEscape(getField(LAB_SECTION))
						+ ", "
						+ JspUtils.sqlEscape(getField(LAB_NUMBER))
						+ ", "
						+ JspUtils.sqlEscape(getField(COLLECTION_COMMENTS))
						+ ")");
				//Create IDENTIFIERS entries
				if (identifiers != null) {
					for (Iterator i = identifiers.iterator(); i.hasNext();) {
						conn.executeUpdate(
							"INSERT INTO Identifier (Record_ID, Person_ID) VALUES ("
								+ record.getRecordID()
								+ ", "
								+ (Integer) i.next()
								+ ")");
					}
				}
				
				
				//Create PAL_LIST entry
				if (taxaList != null) {
					for (Iterator i = taxaList.iterator(); i.hasNext();) {
						Taxa taxa = (Taxa) i.next();
						conn.executeUpdate("INSERT INTO Pal_List (Record_ID, Group_ID, Taxa_ID, Taxonomic_Name, Specimen_Count, Specimen_Coords, Comments) VALUES ("
							+ record.getRecordID() + ", " + taxa.getGroupID() + ", " + taxa.getTaxaID() + ", " + JspUtils.sqlEscape(taxa.getTaxonomicName()) + ", " + JspUtils.sqlEscape(taxa.getSpecimenCount()) + ", " + JspUtils.sqlEscape(taxa.getSpecimenCoords()) + ", " + JspUtils.sqlEscape(taxa.getComments()) + ")");
					}
				}
				
				conn.getConnection().commit();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = true;
				try {
					record = (PaleontologyRecord) PaleontologyRecord.getData(record.getRecordID(), user, state, true);
					sample = new Sample(sample.getSampleID(), user, state, true);
				} catch (Exception e) {
				}
			} catch (SQLException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new SQLException();
			} catch (IOException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new IOException();
			} catch (InvalidCredentialsException e) {
				conn.getConnection().rollback();
				conn.getConnection().setAutoCommit(true);
				conn.releaseStatement();
				savedFlag = false;
				throw new InvalidCredentialsException();
			}

		}
		return record.getRecordID();
	}

	protected void checkMandatoryFields() throws DataInputException {
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
	
	public static String getCleanedName(String cleanName) {
		cleanName = cleanTaxaNameOpen(cleanName, "subsp.");
		cleanName = cleanTaxaNameOpen(cleanName, "subspp.");
		cleanName = cleanTaxaNameOpen(cleanName, "sp.");
		cleanName = cleanTaxaNameOpen(cleanName, "spp.");
		cleanName = cleanTaxaNameOpen(cleanName, "subgen.");
		cleanName = cleanTaxaNameOpen(cleanName, "gen.");
		cleanName = cleanTaxaNameOpen(cleanName, "subfam.");
		cleanName = cleanTaxaNameOpen(cleanName, "fam.");
		cleanName = cleanTaxaName(cleanName, "indet.");
		cleanName = cleanTaxaName(cleanName, "?");
		cleanName = cleanTaxaName(cleanName, "cf.");
		cleanName = cleanTaxaName(cleanName, "aff.");
		cleanName = cleanTaxaName(cleanName, "MS.");
		cleanName = cleanTaxaName(cleanName, "s.s");
		cleanName = cleanTaxaName(cleanName, "s.l.");
		cleanName = cleanTaxaName(cleanName, "gr.");
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

}
