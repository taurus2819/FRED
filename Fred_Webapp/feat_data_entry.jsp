<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*, nz.cri.gns.util.map.*"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		try {
			return new Authenticable[] {
				 new IPRightAccess(
					new IPRight(
						"FRED data entry",
						getIPApp(
							request.getSession(),
							getServletConfig().getServletContext())),
					Right.ANY_RIGHT)};
		} catch (Exception e) {
			//Database error, so just block them
			return new Authenticable[] {
				 new IPRightAccess(
					IPRight.BLOCKED_IP_RIGHT,
					Right.BLOCKED_RIGHT)};
		}
	}
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	Statement statement = connection.statement;
	ResultSet rs;
	User user = getUser(session);
	String formType, featID = "0", loadFeatID, foldID, fieldNum = "", drillName = "", recoll = "", workComm = "", coord = "TruncNZMG:", locMethod = null, accuracy = "", loc = "", regAreaID = "400";
	int userID = user.getPersonId(), userRights = 0, execUp;
	java.util.Date adoDate = new java.util.Date();
	ComboDescriptor cd;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);
%>
<script language="JavaScript">

function setAccuracy(datID, form) {
	if (datID != "-") { form.Accuracy.value = datumMethod[datID]; }
}

function saveForm (form) {
	form.SaveType.value = "Save";
	return checkForm (form);
}

function submitForm (form) {
	form.SaveType.value = "Submit";
	with (form) {
		if (RegAreaID.value == "-") {
			alert ("Please enter a registration area");
			RegAreaID.focus();
			return false;
		}
		if (Coord.value == "") {
			alert ("Please enter a coordinate");
			Coord.select();
			return false;
		}
		if (Loc.value == "") {
			alert ("Please enter a locality");
			Loc.select();
			return false;
		}
	}
	return checkForm(form);
}

function checkForm(form) {
	with (form) {
		if (LocName.value == "") {
			alert ("Please enter a name");
			LocName.select();
			return false;
		}
		if (parseCoord(form, Coord.value) == 0) {
			alert ("Please enter a valid coordinate (or leave field blank) - use the builder");
			Coord.select();
			return false;
		}
	}
	return true;
}

function parseCoord(form, coord) {
	var origCoord, country, east, north, sheet;
	if (coord == "") { return 1; }
	if (coord.indexOf("*") == -1 || coord.indexOf("*") == coord.length - 1) { return 0; }
	if (coord.indexOf("NZMG:") == 0) {
		origCoord = "38";
		country = "NZ";
		sheet = "";
		east = coord.substring(5, coord.indexOf("*"));
		north = coord.substring(coord.indexOf("*") + 1, coord.length);
		if (east.length != 7 || north.length != 7 || isNaN(east) || isNaN(north)) { return 0; }
	} else if (coord.indexOf("TruncNZMG:") == 0) {
		if (coord.indexOf("*") == coord.lastIndexOf("*")) { return 0; }
		origCoord = "16";
		country = "NZ";
		sheet = coord.substring(10, coord.indexOf("*"));
		east = coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
		north = coord.substring(coord.lastIndexOf("*") + 1, coord.length);
		if (sheet.length != 3 || sheet.charAt(0) <= "9" || isNaN(sheet.substring(1, 2)) || east.length < 3 || east.length > 4 || north.length < 3 || north.length > 4 || east.length != north.length || isNaN(east) || isNaN(north)) { return 0; }
	} else if (coord.indexOf("LatLong:") == 0) {
		if (coord.indexOf("*") == coord.lastIndexOf("*")) { return 0; }
		origCoord = "29";
		country = coord.substring(8, coord.indexOf("*"));
		sheet = "";
		north = coord.substring(coord.indexOf("*") + 1, coord.lastIndexOf("*"));
		east = coord.substring(coord.lastIndexOf("*") + 1, coord.length);
		if (isNaN(east) || isNaN(north) || east <= -180 || east > 180 || north <= -90 || north > 90) { return 0; }
	} else {
		return 0;
	}
	with (form) {
		OrigCoord.value = origCoord;
		Country.value = country;
		NZMGSheet.value = sheet;
		East.value = east;
		North.value = north;
	}
	return 1;
}

</script>

<%
	if (request.getParameter("Type") != null && request.getParameter("FoldID") != null) {
		formType = request.getParameter("Type");
		foldID = request.getParameter("FoldID");
		if (request.getParameter("FeatID") != null) { featID = request.getParameter("FeatID"); }

		//get user rights for this folder
		rs = statement.executeQuery("SELECT User_Rights FROM Folder_View WHERE Folder_ID = " + foldID + " AND User_ID = " + userID);
		if (rs.next()) { userRights = rs.getInt(1); }

		if (!featID.equals("0") || request.getParameter("LoadFeatID") != null) { //editing

			//check whether loading data from this feature or other selected feature (by user clicking Copy From)
			if (request.getParameter("LoadFeatID") != null) {
				loadFeatID = request.getParameter("LoadFeatID");
			} else {
				loadFeatID = featID;
			}

			//check rights match folder rights and record is editable
			rs = statement.executeQuery("SELECT Status FROM Sample_All_View WHERE Feature_ID = " + loadFeatID);
			rs.next();
			if (((rs.getString(1).equals("working") || rs.getString(1).equals("rejected")) && (userRights & 2) != 0) || (rs.getString(1).equals("waiting") && (userRights & 64) != 0)) {
				//OK
				//Get FieldNum/Drillhole name from original FeatID not LoadFeatID
				rs = statement.executeQuery("SELECT Field_Number, Drillhole_Name FROM Sample_All_View WHERE Feature_ID = " + featID);
				if (rs.next()) {
					fieldNum = noNulls(rs.getString(1));
					drillName = noNulls(rs.getString(2));
				}
				rs = statement.executeQuery("SELECT Reg_Area_ID, Locality, Working_Comments FROM Sample_All_View WHERE Feature_ID = " + loadFeatID);
				rs.next();
				if (rs.getString(1) != null) { regAreaID = rs.getString(1); }
				loc = noNulls(rs.getString(2));
				workComm = noNulls(rs.getString(3));
				if (workComm.indexOf("*Recoll:") >= 0) {
					recoll = workComm.substring(8, workComm.indexOf("*", 2)).trim();
					workComm = workComm.substring(workComm.indexOf("*", 2) + 1, workComm.length()).trim();
				}
				rs = statement.executeQuery("SELECT Orig_System_ID, Orig_Coord, Method_ID, Accuracy, Country_Code FROM SC.Site S, Feature F WHERE F.Site_ID = S.Site_ID AND F.Feature_ID = " + loadFeatID);
				if (rs.next()) {
					if (rs.getInt(1) == 38) { //Full NZMG
						coord = "NZMG:" + rs.getString(2).replace('|', '*');
					} else if (rs.getInt(1) == 16) { //TruncNZMG
						coord = "TruncNZMG:" + rs.getString(2).replace('|', '*');
					} else if (rs.getInt(1) == 29) {
						coord = "LatLong:" + rs.getString(5) + "*" + rs.getString(2).replace('|', '*');
					}
					locMethod = noNulls(rs.getString(3));
					accuracy = noNulls(rs.getString(4));
				}
			}
			else { //no rights to edit or not editable
				out.println("<script language='JavaScript'>alert(\"You do not have rights to edit this record or the record is not editable.  A blank data entry form will be displayed instead\");</script>");
			}
		}

		//form creation if proper rights
		if (((userRights & 4) != 0 && featID.equals("0")) || ((userRights & 2) !=0 && featID != null)) {

			out.println("<form name='form1' method='post' action='feat_data_proc.jsp'>");
			out.println("<input type='hidden' name='Type' value='" + formType + "'>");
			out.println("<input type='hidden' name='FoldID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='FeatID' value='" + featID + "'>");
			out.println("<input type='hidden' name='SaveType' value=''>");

			//build array of datum methods
			rs = statement.executeQuery("SELECT MAX(Datum_ID) FROM SC.Datum_Method");
			rs.next();
			out.println("<script language='JavaScript'>var datumMethod = new Array(" + (rs.getInt(1) + 1) + ");");
			rs =statement.executeQuery("SELECT Datum_ID, Nom_Accuracy_XY FROM SC.Datum_Method WHERE Nom_Accuracy_XY IS NOT NULL ORDER BY Datum_ID");
			while (rs.next()) {
				out.println("datumMethod[" + rs.getString(1) + "] = '" + noNulls(rs.getString(2)) + "';");
			}
			out.println("</script>");

			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/loc.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Locality</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='load_record.jsp?FoldID=" + foldID + "&FeatID=" + featID + "&RecType=LOC' title='Copy From'><img src='images/load.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='load_record.jsp?FoldID=" + foldID + "&FeatID=" + featID + "&RecType=LOC' class='heading'>Copy From</a></td></tr>");
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
			if (formType.equals("Outcrop")) {
				out.println("<tr><td class='heading' colspan='2'>Field Number</td><td><input type='text' name='LocName' value='" + fieldNum + "'></td></tr>");
			} else if (formType.equals("Drillhole")) {
				out.println("<tr><td class='heading' colspan='2'>Drillhole Name</td><td><input type='text' name='LocName' value='" + drillName + "'></td></tr>");
			} else if (formType.equals("VertSect")) {
				out.println("<tr><td class='heading' colspan='2'>Vertical Section Name</td><td><input type='text' name='LocName' value='" + drillName + "'></td></tr>");
			}
			out.println("<tr><td class='heading'>Registration Area</td><td></td><td>");
			cd = new ComboDescriptor("Lookup", "Lookup_ID", "Name");
			cd.name = "RegAreaID";
			cd.selected = regAreaID;
			cd.join = "FieldName = 'RegArea'";
			cd.orderBy = "Lookup_ID";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td class='heading'>Recollection Of</td><td></td><td><input type='text' name='Recoll' value='<%=recoll%>' /></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Recoll", "Supp", "width=600,height=500");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading' colspan='2'>Working Comments<br><span class='smalltext'>On submission these comments will be deleted</span></td><td><textarea name='WorkComm' rows='3' cols='40'><%=workComm%></textarea></td></tr>

			<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>

			<tr><td class='heading'>Location</td><td class='smallheading'>Grid Ref.</td><td><input type='text' name='Coord' size='40' value='<%=coord%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Coord", "Supp", "width=600,height=450");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<input type='hidden' name='OrigCoord' value=''>
			<input type='hidden' name='Country' value=''>
			<input type='hidden' name='NZMGSheet' value=''>
			<input type='hidden' name='East' value=''>
			<input type='hidden' name='North' value=''>
			<tr><td></td><td class='smallheading'>Method</td><td>
<%			cd = new ComboDescriptor("SC.Datum_Method", "Datum_ID", "Method");
			cd.name = "LocMethodID";
			cd.prompt = "-- Choose --";
			cd.selected = locMethod;
			cd.orderBy = "Datum_ID";
			cd.join = "Nom_Accuracy_XY IS NOT NULL";
			cd.tagParams = "onChange='setAccuracy(this.value, this.form)'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td></tr>
			<tr><td></td><td class='smallheading'>Accuracy</td><td><input type='text' name='Accuracy' value='<%=accuracy%>'></td></tr>
			<tr><td></td><td class='smallheading'>Locality Description</td><td><textarea name='Loc' cols='40' rows='5'><%=loc%></textarea></td></tr>
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
%>
