<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*"
%><%
try {
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	ResultSet rs;
	DocumentAttacher attacher = DocumentAttacher.createFREDDocumentAttacher(session, application);
	User user = getUser(session);
	String recID = "0", loadRecID, foldID, sampID, featID, workComm = "", dateRnd = "", collDateUnk = "", coll = "", stratName = "", inPlace = "", sentTo = "", notColl = "", sig = "", infStageStart = null, infStartMod = "", infStageStop = null, infStopMod = "", knwStageStart = null, knwStartMod = "", knwStageStop = null, knwStopMod = "", prevSamp = "", sampRel = "", stratRel = "", colMap = "", dip = "", dipDir = "", strike = "", facing = "", grainSizeP = null, grainSizeS = null, gSComp = "", bedThick = null, beddingP = null, beddingS = null, weath = null, hard = null, carb = null, colMod = null, colourP = null, colourS = null, wet = "", sedFeat = "", depEnv1 = "", depEnv2 = "", rockNat = "", corr = "";
	int userID = user.getPersonId(), userRights = 0, execUp;
	java.util.Date collDate = new java.util.Date();
	ComboDescriptor cd;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);
%>
<script language="JavaScript">

function saveForm (form) {
	form.SaveType.value = "Save";
	return checkForm (form);
}

function submitForm (form) {
	form.SaveType.value = "Submit";
	with (form) {
		if (CollDateUnk.checked == true) {
			alert ("Please enter a collection date (uncheck Unknown)");
			CollDateUnk.focus();
			return false;
		}
		if (Coll.value == "") {
			alert ("Please enter a collector(s)");
			Coll.select();
			return false;
		}
		if (StratName.value == "") {
			alert ("Please enter a stratigraphic name");
			StratName.select();
			return false;
		}
		if (InPlace.value == "") {
			alert ("Please select fossil in place");
			InPlace.focus();
			return false;
		}
	}
	return checkForm(form);
}

function checkForm(form) {
	with (form) {
		if (parseSentTo(SentTo.value) == 0) {
			alert ("Please enter a valid sent to field - use the builder");
			SentTo.select();
			return false;
		}
		if (parseDoubleDropDown(InfStageStart.value, InfStageStop.value) == 0) {
			alert ("Please enter a valid Inferred Stage");
			InfStageStart.focus();
			return false;
		}
		if (parseDoubleDropDown(KnwStageStart.value, KnwStageStop.value) == 0) {
			alert ("Please enter a valid Known Stage");
			KnwStageStart.focus();
			return false;
		}
		if (isNaN(Dip.value)) {
			alert ("Please enter a numeric dip");
			Dip.select();
			return false;
		}
		if (parseInt(Dip.value) < 0 || parseInt(Dip.value) > 90) {
			alert ("Please enter dip between 0 and 90");
			Dip.select();
			return false;
		}
		if (isNaN(Strike.value)) {
			alert ("Please enter a numeric strike");
			Strike.select();
			return false;
		}
		if (parseInt(Strike.value) < 0 || parseInt(Strike.value) > 360) {
			alert ("Please enter a strike between 0 and 360");
			Strike.select();
			return false;
		}
		if (parseDoubleDropDown(GrainSizeP.value, GrainSizeS.value) == 0) {
			alert ("Please enter a valid Grain Size");
			KnwStageStart.focus();
			return false;
		}
		if (parseDoubleDropDown(BeddingP.value, BeddingS.value) == 0) {
			alert ("Please enter a valid Bedding Feature");
			KnwStageStart.focus();
			return false;
		}
		if (parseDoubleDropDown(ColourP.value, ColourS.value) == 0) {
			alert ("Please enter a valid Grain Size");
			KnwStageStart.focus();
			return false;
		}
		if (parseSedFeat(SedFeat.value) == 0) {
			alert ("Please enter a valid Additional Features field");
			SedFeat.select();
			return false;
		}
	}
	return true;
}

function parseSentTo(sentTo) {
	var stringPart;
	if (sentTo == "") { return 1; }  //if nothing in field then return OK otherwise ...
	while (sentTo.length > 0) {
		if (sentTo.indexOf("\n") == -1) { sentTo = sentTo + "\n"; }
		partString = sentTo.substring(0, sentTo.indexOf("\n"));
		if (partString.indexOf("*") == partString.lastIndexOf("*")) { return 0; } // only 1 *
		if (partString.indexOf("*", partString.indexOf("*") + 1) == partString.lastIndexOf("*")) { return 0; }  // only 2 *
		if (partString.indexOf("*", partString.indexOf("*", partString.indexOf("*") + 1) + 1) != partString.lastIndexOf("*")) { return 0; }  // more than 3 semicolons
		if (partString.indexOf(";") == 0) { return 0; } // no fossil group
		sentTo = sentTo.substring(sentTo.indexOf("\n") + 1, sentTo.length);
	}
	return 1;
}

function parseDoubleDropDown(first, second) {
	// return 0 if only second dropdown is selected
	if (first == "-" && second != "-") {return 0; }
	return 1;
}

function parseSedFeat(sedFeat) {
	if (sedFeat == "") { return 1; }  //if nothing in field then return OK otherwise ...
	while (sedFeat.length > 0) {
		if (sedFeat.indexOf(";") == -1) { sedFeat = sedFeat + ";"; }
		if (sedFeat.indexOf(";") == 0) { return 0; } //no feature
		if (sedFeat.charAt(0) == "*") { return 0; } //no feature
		sedFeat = sedFeat.substring(sedFeat.indexOf(";") + 1, sedFeat.length);
	}
	return 1;
}

</script>

<%
	if (request.getParameter("FoldID") != null && (request.getParameter("SampID") != null || request.getParameter("RecID") != null)) {
		foldID = request.getParameter("FoldID");
		sampID = request.getParameter("SampID");
		if (request.getParameter("RecID") != null) { recID = request.getParameter("RecID"); }

		//get user rights for this folder
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID);
		if (rs.next()) { userRights = rs.getInt(1); }

		if (!recID.equals("0") || request.getParameter("LoadRecID") != null) { //editing

			//check whether loading data from this record or other selected record (by user clicking Copy From)
			if (request.getParameter("LoadRecID") != null) {
				loadRecID = request.getParameter("LoadRecID");
			} else {
				loadRecID = recID;
			}

			//check rights match folder rights and record is editable
			rs = statement.executeQuery("SELECT * FROM Record_All_View WHERE Record_ID = " +  loadRecID + " AND Working_Folder_ID = " + foldID);
			if (rs.next() && (userRights & 2) != 0) {
				//OK
				rs = statement.executeQuery("SELECT Sample_ID, Working_Comments FROM Record_All_View WHERE Record_ID = " + loadRecID);
				rs.next();
				if (sampID == null) { sampID = rs.getString(1); }
				workComm = noNulls(rs.getString(2));

				rs = statement.executeQuery("SELECT Collection_Date, Date_Rounding, Strat_Unit, In_Place, Not_Collected, Significance, Column_Map, Dip, Dip_Direction, Strike, Facing, Primary_Grainsize_ID, Secondary_Grainsize_ID, Comparator_Used, Bed_Thick_ID, Primary_Bedding_ID, Secondary_Bedding_ID, Weathering_ID, Hardness_ID, Carbonate_ID, Colour_Modifier_ID, Primary_Colour_ID, Secondary_Colour_ID, Wet, Deposition_Env, Rock_Nature, Correspondence FROM Sample_Property WHERE Record_ID = " + loadRecID);
				if (rs.next()) {
					if (rs.getString(1) != null) {
						collDate = rs.getDate(1);
						dateRnd = noNulls(rs.getString(2));
					} else { //no date
						collDateUnk = " checked";
					}
					stratName = noNulls(rs.getString(3));
					inPlace = noNulls(rs.getString(4));
					notColl = noNulls(rs.getString(5));
					sig = noNulls(rs.getString(6));
					colMap = noNulls(rs.getString(7));
					dip = noNulls(rs.getString(8));
					dipDir = noNulls(rs.getString(9));
					strike = noNulls(rs.getString(10));
					facing = noNulls(rs.getString(11));
					grainSizeP = noNulls(rs.getString(12));
					grainSizeS = noNulls(rs.getString(13));
					gSComp = noNulls(rs.getString(14));
					bedThick = noNulls(rs.getString(15));
					beddingP = noNulls(rs.getString(16));
					beddingS = noNulls(rs.getString(17));
					weath = noNulls(rs.getString(18));
					hard = noNulls(rs.getString(19));
					carb = noNulls(rs.getString(20));
					colMod = noNulls(rs.getString(21));
					colourP = noNulls(rs.getString(22));
					colourS = noNulls(rs.getString(23));
					wet = noNulls(rs.getString(24));
					if (rs.getString(25) != null) {
						if (rs.getString(25).indexOf("Marine:") != -1) {
							depEnv1 = "Marine";
							depEnv2 = rs.getString(25).substring(7, rs.getString(25).length()).trim();
						} else if (rs.getString(25).indexOf("Non-marine:") != -1) {
							depEnv1 = "Non-marine";
							depEnv2 = rs.getString(25).substring(11, rs.getString(25).length()).trim();
						} else {
							depEnv2 = rs.getString(25);
						}
					}
					rockNat = noNulls(rs.getString(26));
					corr = noNulls(rs.getString(27));
				}
				rs = statement.executeQuery("SELECT DISTINCT Collector FROM Sample_Property_All_View WHERE Record_ID = " + loadRecID);
				while (rs.next()) {
					if (rs.getString(1) != null) { coll = rs.getString(1) + "\n"; }
				}
				rs = statement.executeQuery("SELECT Inferred_Stage_Lower_ID, Inferred_Stage_Lower_Mod, Inferred_Stage_Upper_ID, Inferred_Stage_Upper_Mod, Known_Stage_Lower_ID, Known_Stage_Lower_Mod, Known_Stage_Upper_ID, Known_Stage_Upper_Mod FROM Sample_Property_All_View WHERE Record_ID = " + loadRecID);
				if (rs.next()) {
					if (rs.getString(1) != null) { infStageStart = rs.getString(1); }
					if (rs.getString(2) != null) { infStartMod = rs.getString(2); }
					if (rs.getString(3) != null) { infStageStop = rs.getString(3); }
					if (rs.getString(4) != null) { infStopMod = rs.getString(4); }
					if (rs.getString(5) != null) { knwStageStart = rs.getString(5); }
					if (rs.getString(6) != null) { knwStartMod = rs.getString(6); }
					if (rs.getString(7) != null) { knwStageStop = rs.getString(7); }
					if (rs.getString(8) != null) { knwStopMod = rs.getString(8); }
				}
				rs = statement.executeQuery("SELECT Fossil_Group, Person_Name, Lab_Name, Comments FROM Sent_To_View WHERE Record_ID = " + loadRecID);
				while (rs.next()) {
					sentTo = sentTo + rs.getString(1) + "*" + noNulls(rs.getString(2)) + "*" + noNulls(rs.getString(3)) + "*" + noNulls(rs.getString(4)) + "\n";
				}
				rs = statement.executeQuery("SELECT Relationship_Type, Relation_Type_ID, Distance, Distance_Range, Distance_Mod, Relation_Type, Related_Sample_Name, Strat_Unit FROM Relationship_View WHERE Record_ID = " + loadRecID);
				while (rs.next()) {
					if (rs.getString(1).equals("Sample")) {
						if (rs.getString(2).equals("231")) { //nearby
							prevSamp = prevSamp + rs.getString(5) + ";";
						} else {
							if (rs.getString(5) != null) { sampRel = sampRel + "c. "; }
							sampRel = sampRel + noNulls(rs.getString(3));
							if (rs.getString(4) != null) { sampRel = sampRel + " - " + rs.getString(4); }
							sampRel = sampRel + " " + rs.getString(6) + " " + rs.getString(7) + "\n";
						}
					} else if (rs.getString(1).equals("Strat")) {
						if (rs.getString(5) != null) { stratRel = stratRel + "c. "; }
						stratRel = stratRel + noNulls(rs.getString(3));
						if (rs.getString(4) != null) { stratRel = stratRel + " - " + rs.getString(4); }
						stratRel = stratRel + " " + rs.getString(6) + " " + rs.getString(8) + "\n";
					}
				}
				rs = statement.executeQuery("SELECT Sed_Feature, Abundant FROM Sedimentary_Feature_View WHERE Record_ID = " + loadRecID);
				while (rs.next()) {
					sedFeat = sedFeat + rs.getString(1);
					if (rs.getString(2) != null) { sedFeat = sedFeat + "*"; }
					sedFeat = sedFeat + ";";
				}
			}
			else { //no rights to edit or not editable
				out.println("<script language='JavaScript'>alert(\"You do not have rights to edit this record or the record is not editable.  A blank data entry form will be displayed instead\");</script>");
			}
		}

		//form creation if proper rights
		if (((userRights & 4) != 0 && recID.equals("0")) || ((userRights & 2) !=0 && recID != null)) {

			out.println("<form name='form1' method='post' action='samp_prop_data_proc.jsp'>");
			out.println("<input type='hidden' name='FoldID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='RecID' value='" + recID + "'>");
			out.println("<input type='hidden' name='SampID' value='" + sampID + "'>");
			out.println("<input type='hidden' name='SaveType' value=''>");

			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/sprop.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Sample Property Record</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='load_record.jsp?FoldID=" + foldID + "&RecID=" + recID + "&SampID=" + sampID + "&RecType=SMP' title='Copy From'><img src='images/load.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='load_record.jsp?FoldID=" + foldID + "&RecID=" + recID + "&SampID=" + sampID + "&RecType=SMP' class='heading'>Copy From</a></td></tr>");
			out.println("<tr><td><a href='#' onClick='if (saveForm(form1)) {form1.submit();}' title='Save'><img src='images/save.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' onClick='if (saveForm(form1)) {form1.submit();}' class='heading'>Save</a></td></tr>");
			if ((userRights & 16) != 0) {
				out.println("<tr><td><a href='#' onClick='if (submitForm(form1)) {form1.submit();}' title='Submit to Database'><img src='images/submit.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' class='heading' onClick='if (submitForm(form1)) {form1.submit();}' class='heading'>Submit</a></td></tr>");
			}
			out.println("<tr><td><a href='folder_detail.jsp?ID=" + foldID + "' title='Quit Without Saving'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_detail.jsp?ID=" + foldID + "' class='heading'>Quit</a></td></tr>");
			out.println("</table>");

			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");

			out.println("<table border='0' cellspacing='0' cellpadding='2'>");

			rs = statement.executeQuery("SELECT Sample_Name, Drillhole_Name, Drillhole_Depth FROM Sample_All_View WHERE Sample_ID = " + sampID);
			rs.next();
			out.print("<tr><td class='heading'>Sample Name</td><td></td><td class='heading'>" + rs.getString(1));
			if (rs.getString(2) != null) { out.print("<br>" + rs.getString(2) + ": " + rs.getString(3)); }
			out.println("</td></tr>");
%>
			<tr><td class='heading' colspan='2'>Working Comments<br><span class='smalltext'>On submission these comments will be deleted</span></td><td><textarea name='WorkComm' rows='3' cols='40'><%=workComm%></textarea></td></tr>
<%			if (!recID.equals("0")) {
				out.println("<tr><td class='heading' colspan='2'>Attached Files/Images<br><span class='smalltext'>Click <a href='binary_data_entry.jsp?RecID=" + recID + "&RecType=SMP&FoldID=" + foldID + "'>here</a> to add/edit</span></td><td>");
				MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
				if (mr != null) {
					for (int i = 0; i < mr.length; i++) {
						out.println(mr[i].getTitle() + "<br>");
					}
				}
				out.println("</td></tr>");
			}
%>
			<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>

			<tr><td class='heading'>Collection Date</td><td></td><td>
<%			HTMLUtils.makeDateDropBox(new java.io.PrintWriter(out), "CollDate", "form1", null, null, (byte)(HTMLUtils.DATE | HTMLUtils.MONTH_FULL | HTMLUtils.YEAR), collDate, null, -50, 0, true);
%>
			</td></tr>
			<tr><td></td><td></td><td><input type='checkbox' name='CollDateUnk'<%=collDateUnk%>>Unknown</td></tr>
			<tr><td></td><td class='smallheading'>Rounding</td><td><input type='radio' name='DateRnd' value='' <%=((dateRnd.equals("")) ? " checked" : "")%>>None<img src='images/blank.gif' height='1' width='20' /><input type='radio' name='DateRnd' value='Month'<%=((dateRnd.equals("Month")) ? " checked" : "")%>>Month<img src='images/blank.gif' height='1' width='20' /><input type='radio' name='DateRnd' value='Year'<%=((dateRnd.equals("Year")) ? " checked" : "")%>>Year</td></tr>
			<tr><td class='heading'>Collectors</td><td></td><td><textarea name='Coll' cols='40' rows='2'><%=coll%></textarea></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Coll", "Supp", "width=600,height=400");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading'>Strat Name</td><td></td><td><input type='text' name='StratName' size='40' value='<%=stratName%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=StratName", "Supp", "width=600,height=300");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading'>Fossils In Place</td><td></td><td><select name='InPlace'><option value='' <%=((inPlace.equals("")) ? " selected" : "")%>>-- Choose --</option><option value='Yes' <%=((inPlace.equals("Yes")) ? " selected" : "")%>>Yes</option><option value='Almost' <%=((inPlace.equals("Almost")) ? " selected" : "")%>>Almost</option><option value='No'>No</option><option value='Unknown' <%=((inPlace.equals("Unknown")) ? " selected" : "")%>>Unknown</option></select></td></tr>
			<tr><td class='heading'>Sent To</td><td></td><td><textarea name='SentTo' cols='40' rows='2'><%=sentTo%></textarea></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=SentTo", "Supp","width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading'>Not Collected<br><span class='smalltext'>specify fossils seen but not collected</span></td><td></td><td><textarea name='NotColl' cols='40' rows='3'><%=notColl%></textarea></td></tr>

			<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>
			<tr><td class='heading'>Significance/ Comments</td><td></td><td><textarea name='Sig' cols='40' rows='3'><%=sig%></textarea></td></tr>
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
			<tr><td class='heading'>Sample Relationships</td><td></td><td><textarea name='SampRel' cols='40' rows='3'><%=sampRel%></textarea></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=SampRel", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading'>Stratigraphic Relationships</td><td></td><td><textarea name='StratRel' cols='40' rows='3'><%=stratRel%></textarea></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=StratRel", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
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
			<tr><td></td><td class='smallheading'>Wet</td><td><select name='Wet'><option value='' <%=((wet.equals("")) ? " selected" : "")%>>-- Choose --</option><option value='Wet' <%=((wet.equals("Wet")) ? " selected" : "")%>>Wet</option><option value='Dry' <%=((wet.equals("Dry")) ? " selected" : "")%>>Dry</option></select></td></tr>
			<tr><td class='heading'>Additional Features</td><td></td><td><input type='text' name='SedFeat' size='40' value='<%=sedFeat%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=SedFeat", "Supp", "width=600,height=350");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading'>Inf Environment</td><td></td><td><select name='DepEnv1'><option value='' <%=((depEnv1.equals("")) ? " selected" : "")%>>-- Choose --</option><option value='Marine' <%=((depEnv1.equals("Marine")) ? " selected" : "")%>>Marine</option><option value='Non-marine' <%=((depEnv1.equals("Non-marine")) ? " selected" : "")%>>Non-marine</option></select><img src='images/blank.gif' height='1' width='10' /><input type='text' name='DepEnv2' size='26' value='<%=depEnv2%>'></td></tr>
			<tr><td class='heading'>Rock Nature</td><td></td><td><textarea name='RockNat' cols='40' rows='2'><%=rockNat%></textarea></td></tr>
			<tr><td class='heading'>Correspondence</td><td></td><td><textarea name='Corr' cols='40' rows='2'><%=corr%></textarea></td></tr>
			</table>
<%		out.println("<table border='0' cellpadding='0' cellspacing='2'>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td><a href='#' onClick='if (saveForm(form1)) {form1.submit();}' title='Save'><img src='images/save.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' onClick='if (saveForm(form1)) {form1.submit();}' class='heading'>Save</a></td></tr>");
			if ((userRights & 16) != 0) {
				out.println("<tr><td><a href='#' onClick='if (submitForm(form1)) {form1.submit();}' title='Submit to Database'><img src='images/submit.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='#' class='heading' onClick='if (submitForm(form1)) {form1.submit();}' class='heading'>Submit</a></td></tr>");
			}
			out.println("</table>");
			out.println("</form>");
		}
		else {
			drawEndNavigation(out);

			out.println("<table style='margin-left:20px; width:550px;' border='0'>");
			out.println("<tr><td>");
			out.println("<p><span class='bigheading'>Access denied</span><br />You don't have sufficient rights in this folder</p>");
		}
	}
	out.println("</td></tr></table>");
	drawBottom(out, et);
} catch (Exception e) {
	e.printStackTrace(new java.io.PrintWriter(out));
}
%>
