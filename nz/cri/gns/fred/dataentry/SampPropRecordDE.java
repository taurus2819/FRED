package nz.cri.gns.fred.dataentry;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.KeyValueObject;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.fred.RoundedDate;
import nz.cri.gns.fred.data.AccessDeniedException;
import nz.cri.gns.fred.data.Folder;
import nz.cri.gns.fred.data.SampPropRecord;
import nz.cri.gns.fred.data.SentTo;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class SampPropRecordDE extends RecordDE {

	public static final int COLLECTION_DATE = 1;
	public static final int COLLECTORS = 2;
	public static final int STRAT_NAME= 3;
	public static final int FOSSILS_IN_PLACE = 4;
	public static final int SENT_TO = 5;
	public static final int NOT_COLLECTED = 6;
	public static final int SIGNIFICANCE_COMMENTS = 7;
	
	private RoundedDate collDate;
	private Vector collectors = new Vector();
	private Vector sentTo = new Vector();
	
	private boolean outcropSamp = false;

	public SampPropRecordDE(User user, int folderID, PageState state) throws DataInputException, SQLException, IOException {
		super(user, folderID, "SMP", state);
	}

	public SampPropRecordDE(User user, int sampleID, int folderID, PageState state) throws SQLException, IOException, DataInputException {
		super(user, sampleID, folderID, "SMP", state);
	}
	
	public SampPropRecordDE(int recID, User user, PageState state) throws AccessDeniedException, IllegalArgumentException, DataInputException, SQLException, IOException, InvalidCredentialsException {
		super(recID, user, state);
		record = SampPropRecord.getSampPropData(recID, user, state);
		recordType = "SMP";
		folder = new Folder(record.getAsInt(SampPropRecord.WORKING_FOLDER_ID), user, state);
		setField(COLLECTION_DATE, FREDUtils.reverseParseDate(record.getAsDate(SampPropRecord.COLLECTION_DATE), record.getAsString(SampPropRecord.DATE_ROUNDING)));
		StringBuffer collName = new StringBuffer();
		if (record.get(SampPropRecord.COLLECTOR) != null) {
			for (Iterator i = record.getAsVector(SampPropRecord.COLLECTOR).iterator(); i.hasNext(); ) {
				KeyValueObject coll = (KeyValueObject) i.next();
				collName.append(coll.getValue() + "\n");
			}
		}
		setField(COLLECTORS, collName.toString());
		setField(STRAT_NAME, record.getAsString(SampPropRecord.STRAT_UNIT));
		setField(FOSSILS_IN_PLACE, record.getAsString(SampPropRecord.IN_PLACE));
		StringBuffer sTStr = new StringBuffer();
		if (record.get(SampPropRecord.SENT_TO) != null) {
			for (Iterator i = record.getAsVector(SampPropRecord.SENT_TO).iterator(); i.hasNext(); ) {
				SentTo sT = (SentTo) i.next();
				sTStr.append(sT.getFossilGroup() + "*" + FREDUtils.noNulls(sT.getPerson()) + "*" + FREDUtils.noNulls(sT.getLab()) + "*" + FREDUtils.noNulls(sT.getComments()) + "\n");
			}
		}
		setField(SENT_TO, sTStr.toString());
		setField(NOT_COLLECTED, record.getAsString(SampPropRecord.NOT_COLLECTED));
		setField(SIGNIFICANCE_COMMENTS, record.getAsString(SampPropRecord.SIGNIFICANCE));
	}

	protected void parseField(int field, String value) throws DataInputException {
		super.parseField(field, value);
		try {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs;
			switch (field) {
				case COLLECTION_DATE :
					collDate = FREDUtils.parseRoundedDate(value);
					break;
				case COLLECTORS :
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1) value = value + "\n";
						rs = conn.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = " + JspUtils.sqlEscape(value.substring(0, value.indexOf("\n")).trim()));
						if (rs.next()) {
							collectors.add(new Integer(rs.getInt(1)));
						} else {  //Collector not in database so throw exception
							throw new DataInputException("Collector", value.substring(0, value.indexOf("\n")).trim() + " not in database - add through builder");
						}
						value = value.substring(value.indexOf("\n") + 1, value.length());
					}
					break;
				case SENT_TO :
					String stLine, stGroup, stPerson, stLab, stComments;
					int stGroupID = 0, stPersonID = 0, stLabID = 0;
					SentTo sT;
					while (value.length() > 0) {
						if (value.indexOf("\n") == -1) value = value + "\n";
						stLine = value.substring(0, value.indexOf("\n")).trim();
						stGroup = stLine.substring(0, stLine.indexOf("*"));
						stPerson = stLine.substring(stGroup.length() + 1, stLine.indexOf("*", stGroup.length() + 1));
						stLab = stLine.substring(stGroup.length() + stPerson.length() + 2, stLine.indexOf("*", stGroup.length() + stPerson.length() + 2));
						stComments = stLine.substring(stLine.lastIndexOf("*") + 1, stLine.length());
						value = value.substring(value.indexOf("\n") + 1, value.length()).trim();
						//check againt lookup values
						rs = conn.executeQuery("SELECT Lookup_ID FROM Lookup WHERE Name = " + JspUtils.sqlEscape(stGroup) + " AND FieldName = 'FossilGroup'");
						if (rs.next()) {
							stGroupID = rs.getInt(1);
						} else {  // not valid group
							throw new DataInputException("Sent To - Group", stGroup + " not a valid sent to group");
						}
						if (!stPerson.equals("")) {
							rs = conn.executeQuery("SELECT Person_ID FROM Person_View WHERE Name = " + JspUtils.sqlEscape(stPerson));
							if (rs.next()) {
								stPersonID = rs.getInt(1);
							} else { // not valid person
								throw new DataInputException("Sent To - Person", stPerson + " not in database - add through builder");
							}
						}
						if (!stLab.equals("")) {
							rs = conn.executeQuery("SELECT Lab_ID FROM SC.Lab WHERE Lab_Name = " + JspUtils.sqlEscape(stLab));
							if (rs.next()) {
								stLabID = rs.getInt(1);
							} else { // not valid lab
								throw new DataInputException("Sent To - Lab", stLab + " not in database");
							}
						}
						sT = new SentTo();
						sT.setComments(stComments);
						sT.setFossilGroupId(new Integer(stGroupID));
						sT.setPersonId(new Integer(stPersonID));
						sT.setLabId(new Integer(stLabID));
						sentTo.add(sT);
					}

				
				
			}
		} catch (IOException e) {
			throw new DataInputException();
		} catch (SQLException e) {
			throw new DataInputException();	
		}
	}

	public void setOutcropSamp(boolean outcropSamp) {
		this.outcropSamp = outcropSamp;
	}

	public void makeDataEntryHTML(Writer out) throws IOException, SQLException {
		if (!outcropSamp) super.makeDataEntryHTML(out);
		out.write("<tr><td class='heading'>Collection Date</td><td></td><td><input type='text' name='CollDate' value='" + FREDUtils.noNulls(getField(COLLECTION_DATE)) + "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Date&Field=CollDate\", \"Supp\", \"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Collectors</td><td></td><td><textarea name='Coll' cols='40' rows='2'>" + FREDUtils.noNulls(getField(COLLECTORS)) + "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=Coll\", \"Supp\", \"width=600,height=400\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Strat Name</td><td></td><td><input type='text' name='StratName' size='40' value='" + FREDUtils.noNulls(getField(STRAT_NAME)) + "'></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=StratName\", \"Supp\", \"width=600,height=300\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Fossils In Place</td><td></td><td><select name='InPlace'><option value='' " + ((getField(FOSSILS_IN_PLACE) == null) ? " selected" : "") + ">-- Choose --</option><option value='Yes' " + ((getField(FOSSILS_IN_PLACE) != null && getField(FOSSILS_IN_PLACE).equals("Yes")) ? " selected" : "") + ">Yes</option><option value='Almost' " + ((getField(FOSSILS_IN_PLACE) != null && getField(FOSSILS_IN_PLACE).equals("Almost")) ? " selected" : "") + ">Almost</option><option value='No' " + ((getField(FOSSILS_IN_PLACE) != null && getField(FOSSILS_IN_PLACE).equals("No")) ? " selected" : "") + ">No</option><option value='Unknown' " + ((getField(FOSSILS_IN_PLACE) != null && getField(FOSSILS_IN_PLACE).equals("Unknown")) ? " selected" : "") + ">Unknown</option></select></td></tr>\n");
		out.write("<tr><td class='heading'>Sent To</td><td></td><td><textarea name='SentTo' cols='40' rows='2'>" + FREDUtils.noNulls(getField(SENT_TO)) + "</textarea></td><td><a href='#' onClick='newWin=open(\"data_entry_supp.jsp?Type=SentTo\", \"Supp\",\"width=600,height=450\");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>\n");
		out.write("<tr><td class='heading'>Not Collected<br><span class='smalltext'>specify fossils seen but not collected</span></td><td></td><td><textarea name='NotColl' cols='40' rows='3'>" + FREDUtils.noNulls(getField(NOT_COLLECTED)) + "</textarea></td></tr>\n");

/*			<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>
			<tr><td class='heading' colspan='2'>Significance/Comments</td><td><textarea name='Sig' cols='40' rows='3'><%=sig%></textarea></td></tr>
			<tr><td class='heading'>Stage Limits</td><td class='smallheading'>Inferred</td><td>
			<table border='0' cellspacing='0'>
			<tr><td>
<%			cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "InfStageStart";
			cd.prompt = "-- Choose --";
			cd.selected = infStageStart;
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td><td><select name='InfStartMod'><option value='-' <%=((infStartMod.equals("")) ? " selected" : "")%>></option><option value='?' <%=((infStartMod.equals("?")) ? " selected" : "")%>>?</option></select></td><td class='heading'> to </td></tr>
			<tr><td>
<%			cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "InfStageStop";
			cd.prompt = "-- Choose --";
			cd.selected = infStageStop;
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td><td class='heading'><select name='InfStopMod'><option value='-'></option><option value='?'>?</option></select></td></tr>
			</table>
			</td></tr>
			<tr><td></td><td class='smallheading'>Known</td><td>
			<table border='0' cellspacing='0'>
			<tr><td>
<%			cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "KnwStageStart";
			cd.prompt = "-- Choose --";
			cd.selected = knwStageStart;
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td><td><select name='KnwStartMod'><option value='-' <%=((knwStartMod.equals("")) ? " selected" : "")%>></option><option value='?' <%=((knwStartMod.equals("?")) ? " selected" : "")%>>?</option></select></td><td class='heading'> to </td></tr>
			<tr><td>
<%			cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "KnwStageStop";
			cd.prompt = "-- Choose --";
			cd.selected = knwStageStop;
			cd.orderBy = "Ag_Name";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td><td class='heading'><select name='KnwStopMod'><option value='-' <%=((knwStopMod.equals("")) ? " selected" : "")%>></option><option value='?' <%=((knwStopMod.equals("?")) ? " selected" : "")%>>?</option></select></td></tr>
			</table>
			</td></tr>
			<tr><td class='heading'>Samples Nearby</td><td></td><td><input type='text' name='PrevSamp' size='40' value='<%=prevSamp%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=PrevSamp", "Supp", "width=600,height=350");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading' colspan='2'>Sample Relationships</td><td><textarea name='SampRel' cols='40' rows='3'><%=sampRel%></textarea></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=SampRel", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading' colspan='2'>Stratigraphic Relationships</td><td><textarea name='StratRel' cols='40' rows='3'><%=stratRel%></textarea></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=StratRel", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading'>Column/Map</td><td></td><td><input type='text' name='ColMap' size='40' value='<%=colMap%>'></td></tr>
			<tr><td class='heading'>Attitude</td><td class='smallheading'>Dip</td><td><input type='text' name='Dip' size='3' value='<%=dip%>'></td></tr>
			<tr><td></td><td class='smallheading'>Dip Dirn.</td><td><select name='DipDir'><option value='' <%=((dipDir.equals("")) ? " selected" : "")%>>-- Choose --</option><option value='N' <%=((dipDir.equals("N")) ? " selected" : "")%>>North</option><option value='NE' <%=((dipDir.equals("NE")) ? " selected" : "")%>>North-East</option><option value='E' <%=((dipDir.equals("E")) ? " selected" : "")%>>East</option><option value='SE' <%=((dipDir.equals("SE")) ? " selected" : "")%>>South-East</option><option value='S' <%=((dipDir.equals("S")) ? " selected" : "")%>>South</option><option value='SW' <%=((dipDir.equals("SW")) ? " selected" : "")%>>South-West</option><option value='W' <%=((dipDir.equals("W")) ? " selected" : "")%>>West</option><option value='NW' <%=((dipDir.equals("NW")) ? " selected" : "")%>>North-West</option></select></td></tr>
			<tr><td></td><td class='smallheading'>Strike</td><td><input type='text' name='Strike' size='4' value='<%=strike%>'></td></tr>
			<tr><td></td><td class='smallheading'>Facing</td><td><select name='Facing'><option value='' <%=((facing.equals("")) ? " selected" : "")%>>-- Choose --</option><option value='Normal' <%=((facing.equals("Normal")) ? " selected" : "")%>>Normal</option><option value='Overturned' <%=((facing.equals("Overturned")) ? " selected" : "")%>>Overturned</option></select></td></tr>

			<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>
			<tr><td class='heading'>Grain Size</td><td class='smallheading'>Pri.</td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "GrainSizeP";
			cd.prompt = "-- Choose --";
			cd.selected = grainSizeP;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'GrainSize'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td></td><td class='smallheading'>Sec.</td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "GrainSizeS";
			cd.prompt = "-- Choose --";
			cd.selected = grainSizeS;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'GrainSize'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td></td><td class='smallheading'>Comp. Used</td><td><select name='GSComp'><option value='' <%=((gSComp.equals("")) ? " selected" : "")%>>-- Choose --</option><option value='Y' <%=((gSComp.equals("Y")) ? " selected" : "")%>>Yes</option><option value='N' <%=((gSComp.equals("N")) ? " selected" : "")%>>No</option></select></td></tr>
			<tr><td class='heading'>Stratification</td><td class='smallheading'>Thickness</td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "BedThick";
			cd.prompt = "-- Choose --";
			cd.selected = bedThick;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'BedThick'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td></td><td class='smallheading'>Features</td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "BeddingP";
			cd.prompt = "-- Choose --";
			cd.selected = beddingP;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'Bedding'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			<img src='images/blank.gif' height='1' width='10' /><span class='heading'>&</span><img src='images/blank.gif' height='1' width='10' />
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "BeddingS";
			cd.prompt = "-- Choose --";
			cd.selected = beddingS;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'Bedding'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td class='heading'>Weathering</td><td></td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "Weath";
			cd.prompt = "-- Choose --";
			cd.selected = weath;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'Weathering'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td class='heading'>Hardness</td><td></td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "Hard";
			cd.prompt = "-- Choose --";
			cd.selected = hard;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'Hardness'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td class='heading'>Carbonate</td><td></td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "Carb";
			cd.prompt = "-- Choose --";
			cd.selected = carb;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'Carbonate'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td class='heading'>Colour</td><td class='smallheading'>Shade</td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "ColMod";
			cd.prompt = "-- Choose --";
			cd.selected = colMod;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'ColourMod'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td></td><td class='smallheading'>Colour</td><td>
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "ColourP";
			cd.prompt = "-- Choose --";
			cd.selected = colourP;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'RockColour'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			<img src='images/blank.gif' height='1' width='10' />-<img src='images/blank.gif' height='1' width='10' />
<%			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Code || ': ' || Name");
			cd.name = "ColourS";
			cd.prompt = "-- Choose --";
			cd.selected = colourS;
			cd.orderBy = "Code";
			cd.join = "FieldName = 'RockColour'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td></td><td class='smallheading'>Wet/Dry</td><td><select name='Wet'><option value='' <%=((wet.equals("")) ? " selected" : "")%>>-- Choose --</option><option value='Wet' <%=((wet.equals("Wet")) ? " selected" : "")%>>Wet</option><option value='Dry' <%=((wet.equals("Dry")) ? " selected" : "")%>>Dry</option></select></td></tr>
			<tr><td class='heading' colspan='2'>Additional Features</td><td><input type='text' name='SedFeat' size='40' value='<%=sedFeat%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=SedFeat", "Supp", "width=600,height=350");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading' colspan='2'>Inferred Environment</td><td><select name='DepEnv1'><option value='' <%=((depEnv1.equals("")) ? " selected" : "")%>>-- Choose --</option><option value='Marine' <%=((depEnv1.equals("Marine")) ? " selected" : "")%>>Marine</option><option value='Non-marine' <%=((depEnv1.equals("Non-marine")) ? " selected" : "")%>>Non-marine</option></select></td></tr>
			<tr><td></td><td></td><td><textarea name='DepEnv2' cols='40' rows='3'><%=depEnv2%></textarea></td></tr>
			<tr><td class='heading' colspan='2'>Nature of Rock Unit</td><td><textarea name='RockNat' cols='40' rows='3'><%=rockNat%></textarea></td></tr>
			<tr><td class='heading' colspan='2'>Correspondence</td><td><textarea name='Corr' cols='40' rows='3'><%=corr%></textarea></td></tr>
			</table>
*/
		super.makeEndBitHTML(out);
	}



}
