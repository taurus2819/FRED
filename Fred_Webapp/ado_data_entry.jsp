<%@		page extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*, nz.cri.gns.db.metadata.*"
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
	DocumentAttacher attacher = DocumentAttacher.createFREDDocumentAttacher(session, application);
	User user = getUser(session);
	String recID = "0", loadRecID, foldID, sampID, workComm = "", dateRnd = "", adoDateUnk = "", adoptor = "", stageStart = null, startMod = "", stageStop = null, stopMod = "", comm = "";
	int userID = user.getPersonId(), userRights = 0, execUp;
	java.util.Date adoDate = new java.util.Date();
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
		if (AdoDateUnk.checked == true) {
			alert ("Please enter an adoption date (uncheck Unknown)");
			AdoDateUnk.focus();
			return false;
		}
		if (Adoptor.value == "") {
			alert ("Please enter an adoptor");
			Coll.select();
			return false;
		}
	}
	return checkForm(form);
}

function checkForm(form) {
	with (form) {
		if (parseDoubleDropDown(StageStart.value, StageStop.value) == 0) {
			alert ("Please enter a valid Stage");
			InfStageStart.focus();
			return false;
		}
	}
	return true;
}

function parseDoubleDropDown(first, second) {
	// return 0 if only second dropdown is selected
	if (first == "-" && second != "-") {return 0; }
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

				rs = statement.executeQuery("SELECT Adoption_Date, Date_Rounding, Comments FROM Adoption WHERE Record_ID = " + loadRecID);
				if (rs.next()) {
					if (rs.getString(1) != null) {
						adoDate = rs.getDate(1);
						dateRnd = noNulls(rs.getString(2));
					} else { //no date
						adoDateUnk = " checked";
					}
					comm = noNulls(rs.getString(3));

				}
				rs = statement.executeQuery("SELECT Adoptor, Adopted_Stage_Lower_ID, Adopted_Stage_Lower_Mod, Adopted_Stage_Upper_ID, Adopted_Stage_Upper_Mod FROM Adoption_All_View WHERE Record_ID = " + loadRecID);
				if (rs.next()) {
					adoptor = noNulls(rs.getString(1));
					if (rs.getString(2) != null) { stageStart = rs.getString(2); }
					if (rs.getString(3) != null) { startMod = rs.getString(3); }
					if (rs.getString(4) != null) { stageStop = rs.getString(4); }
					if (rs.getString(5) != null) { stopMod = rs.getString(5); }
				}
			}
			else { //no rights to edit or not editable
				out.println("<script language='JavaScript'>alert(\"You do not have rights to edit this record or the record is not editable.  A blank data entry form will be displayed instead\");</script>");
			}
		}

		//form creation if proper rights
		if (((userRights & 4) != 0 && recID.equals("0")) || ((userRights & 2) !=0 && recID != null)) {

			out.println("<form name='form1' method='post' action='ado_data_proc.jsp'>");
			out.println("<input type='hidden' name='FoldID' value='" + foldID + "'>");
			out.println("<input type='hidden' name='RecID' value='" + recID + "'>");
			out.println("<input type='hidden' name='SampID' value='" + sampID + "'>");
			out.println("<input type='hidden' name='SaveType' value=''>");

			out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
			out.println("<tr><td colspan='2' align='center'><img src='images/ado.gif' height='20' width='20' /></td></tr>");
			out.println("<tr><td colspan='2' align='center' class='heading'>Adoption Record</td></tr>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
			out.println("<tr><td><a href='load_record.jsp?FoldID=" + foldID + "&RecID=" + recID + "&SampID=" + sampID + "&RecType=ADO' title='Copy From'><img src='images/load.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='load_record.jsp?FoldID=" + foldID + "&RecID=" + recID + "&SampID=" + sampID + "&RecType=ADO' class='heading'>Copy From</a></td></tr>");
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

			rs = statement.executeQuery("SELECT Sample_Name, Feature_Type, Feature_Name, Drillhole_Depth FROM Sample_All_View WHERE Sample_ID = " + sampID);
			rs.next();
			out.print("<tr><td class='heading'>Sample Name</td><td></td><td class='heading'>" + rs.getString(1));
			if (!rs.getString(2).equals("Outcrop")) { out.print("<br>" + noNulls(rs.getString(3)) + ": " + rs.getString(4)); }
			out.println("</td></tr>");
%>
			<tr><td class='heading' colspan='2'>Working Comments<br><span class='smalltext'>On submission these comments will be deleted</span></td><td><textarea name='WorkComm' rows='3' cols='40'><%=workComm%></textarea></td></tr>
<%			if (!recID.equals("0")) {
				out.println("<tr><td class='heading' colspan='2'>Attached Files/Images<br><span class='smalltext'>Click <a href='binary_data_entry.jsp?RecID=" + recID + "&RecType=ADO&FoldID=" + foldID + "'>here</a> to add/edit</span></td><td>");
				MetadataRecord[] mr = attacher.getDocumentsForId(Integer.parseInt(recID));
				if (mr != null) {
					for (int i = 0; i < mr.length; i++) {
						out.println(mr[i].getTitle() + "<br>");
					}
				}
				out.println("</td></tr>");
			}
%>
			</td></tr>

			<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>

			<tr><td class='heading'>Adoption Date</td><td></td><td>
<%			HTMLUtils.makeDateDropBox(new java.io.PrintWriter(out), "AdoDate", "form1", null, null, (byte)(HTMLUtils.DATE | HTMLUtils.MONTH_FULL | HTMLUtils.YEAR), adoDate, null, -50, 0, true);
%>
			</td></tr>
			<tr><td></td><td></td><td><input type='checkbox' name='AdoDateUnk'<%=adoDateUnk%>>Unknown</td></tr>
			<tr><td></td><td class='smallheading'>Rounding</td><td><input type='radio' name='DateRnd' value='' <%=((dateRnd.equals("")) ? " checked" : "")%>>None<img src='images/blank.gif' width='20' height='1' /><input type='radio' name='DateRnd' value='Month'<%=((dateRnd.equals("Month")) ? " checked" : "")%>>Month<img src='images/blank.gif' width='20' height='1' /><input type='radio' name='DateRnd' value='Year'<%=((dateRnd.equals("Year")) ? " checked" : "")%>>Year</td></tr>
			<tr><td class='heading'>Adoptor</td><td></td><td><input type='text' name='Adoptor' size='40' value='<%=adoptor%>'></td><td><a href='#' onClick='newWin=open("data_entry_supp.jsp?Type=Adoptor", "Supp", "width=600,height=350");return false;' title='Build...'><img src='images/build.gif' width='20' height='20' border='0' /></a></td></tr>
			<tr><td class='heading'>Adopted Stage</td><td></td><td>
			<table border='0' cellspacing='0'>
			<tr><td>
<%			cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "StageStart";
			cd.prompt = "-- Choose --";
			cd.selected = stageStart;
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td><td><select name='StartMod'><option value='-' <%=((startMod.equals("")) ? " selected" : "")%>></option><option value='?' <%=((startMod.equals("?")) ? " selected" : "")%>>?</option></select></td><td class='heading'> to </td></tr>
			<tr><td>
<%			cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
			cd.name = "StageStop";
			cd.prompt = "-- Choose --";
			cd.selected = stageStart;
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
			</td><td class='heading'><select name='StopMod'><option value='-' <%=((stopMod.equals("")) ? " selected" : "")%>></option><option value='?' <%=((stopMod.equals("?")) ? " selected" : "")%>>?</option></select></td></tr>
			</table>
			</td></tr>
			<tr><td class='heading'>Comments</td><td></td><td><textarea name='Comm' cols='40' rows='2'><%=comm%></textarea></td></tr>
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