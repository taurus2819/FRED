<%@page	extends="nz.cri.gns.jsp.FREDIPSysJspPage"
		import="nz.cri.gns.db.*, nz.cri.gns.jsp.*, java.net.URL, nz.cri.gns.intranet.*, java.sql.*, java.lang.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	nz.cri.gns.intranet.DBConnection connection = JspUtils.createDatabaseConnection(session, CONNECTION, DB_NAME, application);
	java.sql.Statement statement = connection.statement;
	ResultSet rs;
	ComboDescriptor cd;

	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

%>
<script language='JavaScript'>

function trim(str)
{
	return( (""+str).replace(/^\s*([\s\S]*\S+)\s*$|^\s*$/,'$1') );
}

function replaceSingleQuote(str1) {
	while(str1.indexOf("'") != -1) {
		str1 = str1.replace("'", "&quot");
	}
	while(str1.indexOf("&quot") != -1) {
		str1 = str1.replace("&quot", "''");
	}
	return str1;
}

function generateSQL(form) {
	var queryString = "", whereSQL = "SV.Status = 'approved' AND ", tableName = "Sample_All_View SV", tableJoin = "", frNum, aStart = "", aStop = "", aQuery;
	var recFlag = false, sampPropFlag = false, adoFlag = false, palFlag = false;
	with (form) {
		if (FRNum.value.length > 0) {
			frNum = parseFRNum(trim(FRNum.value), "");
			if (frNum == "false") {
				alert("FR Number field incorrectly formatted");
				return false;
			}
			whereSQL = "((" + frNum + ") OR (" + parseFRNum(trim(FRNum.value), "Yard_") + ")) AND ";
			queryString = queryString + "FR Number = " + trim(FRNum.value) + " AND ";
		}
		if (Map.value.length > 0) {
			whereSQL = whereSQL + "SV.NZMG_Sheet = '" + Map.value.toUpperCase() + "' AND ";
			queryString = queryString + "NZMG Sheet = " + Map.value.toUpperCase() + " AND ";
		}
		if (QMap.value != "-") {
			whereSQL = whereSQL + "SV.QMAP_Sheet = '" + QMap.value + "' AND ";
			queryString = queryString + "QMAP Sheet = " + QMap.value + " AND ";
		}
		if (Coll.value != "-") {
			whereSQL = whereSQL + "C.Person_ID = " + Coll.value + " AND ";
			queryString = queryString + "Collector = " + Coll.options[Coll.options.selectedIndex].text + " AND ";
			tableName = tableName + ", Collector C";
			tableJoin = tableJoin + "C.Record_ID = R.Record_ID AND ";
			recFlag = true;
		}
		if (YearFrom.value.length > 0) {
			if (isNaN(YearFrom.value) || YearFrom.value.length != 4) {
				alert("Year must be numeric and 4 digits");
				return false;
			}
			if (YearTo.value.length > 0) {
				if (isNaN(YearTo.value) || YearTo.value.length != 4) {
					alert("Year must be numeric and 4 digits");
					return false;
				}
				whereSQL = whereSQL + "SP.Collection_Date BETWEEN '01-JAN-" + YearFrom.value + "' AND '31-DEC-" + YearTo.value + "' AND ";
				queryString = queryString + "Collection Date BETWEEN " + YearFrom.value + " AND " + YearTo.value + " AND ";
			} else {
				whereSQL = whereSQL + "SP.Collection_Date BETWEEN '01-JAN-" + YearFrom.value + "' AND '31-DEC-" + YearFrom.value + "' AND ";
				queryString = queryString + "Collection Date = " + YearFrom.value + " AND ";
			}
			sampPropFlag = true;
		}
		if (FieldNum.value.length > 0) {
			whereSQL = whereSQL + "UPPER(SV.Field_Number) LIKE '%" + replaceSingleQuote(FieldNum.value.toUpperCase()) + "%' AND ";
			queryString = queryString + "Field Number = " + FieldNum.value + " AND ";
		}
		if (StratName.value.length > 0) {
			whereSQL = whereSQL + "UPPER(SP.Strat_Unit) LIKE '%" + replaceSingleQuote(StratName.value.toUpperCase()) + "%' AND ";
			queryString = queryString + "Stratigraphic Name = " + StratName.value + " AND ";
			sampPropFlag = true;
		}
		if (StratAtt.checked) {
			whereSQL = whereSQL + "(SP.Dip IS NOT NULL OR SP.Dip_Direction IS NOT NULL OR SP.Strike IS NOT NULL) AND ";
			queryString = queryString + "Stratal Attitude present AND ";
			sampPropFlag = true;
		}
		if (RockNat.value.length > 0) {
			whereSQL = whereSQL + "UPPER(SP.Rock_Nature) LIKE '%" + replaceSingleQuote(RockNat.value.toUpperCase()) + "%' AND ";
			queryString = queryString + "Nature of Rock Unit = " + RockNat.value + " AND ";
			sampPropFlag = true;
		}
		if (DepEnv.value.length > 0) {
			whereSQL = whereSQL + "UPPER(SP.Deposition_Env) LIKE '%" + replaceSingleQuote(DepEnv.value.toUpperCase()) + "%' AND ";
			queryString = queryString + "Deposition Environment = " + DepEnv.value + " AND ";
			sampPropFlag = true;
		}
		//check only stage name or numeric values entered
		if (StageFrom.value != "-" && AgeFrom.value.length > 0) {
			alert("Please select only a stage name or a numeric age");
			StageFrom.focus();
			return false;
		}
		if (!(StratAge.checked || AdoAge.checked || PalAge.checked)) {
			alert("Please select an age to search - eg Collectors Age");
			StratAge.select();
			return false;
		}
		if (StageFrom.value != "-") {
			if (StageTo.value != "-") {
				if (ageStart[StageTo.value] > ageStart[StageFrom.value] || ageStop[StageFrom.value] < ageStop[StageTo.value]) { alert ("Stage Names are the wrong way around"); StageFrom.select(); return false; }
				aStart = ageStart[StageFrom.value];
				aStop = ageStop[StageTo.value];
				aQuery = StageFrom.options[StageFrom.options.selectedIndex].text + " to " + StageTo.options[StageTo.options.selectedIndex].text;
			} else {
				aStart = ageStart[StageFrom.value];
				aStop = ageStop[StageFrom.value];
				aQuery = StageFrom.options[StageFrom.options.selectedIndex].text;
			}
		} else if (StageTo.value != "-") {
			alert ("From stage not selected");
			StageFrom.focus();
			return false;
		}
		if (AgeFrom.value.length > 0) {
			if (AgeTo.value.length > 0) {
				if (isNaN(AgeFrom.value) || isNaN(AgeTo.value)) { alert ("Non-numeric ages entered"); AgeFrom.select(); return false; }
				if (parseFloat(AgeFrom.value, 10) < parseFloat(AgeTo.value, 10)) { alert ("Numeric ages wrong way around"); AgeFrom.select(); return false; }
				aStart = AgeFrom.value;
				aStop = AgeTo.value;
				aQuery = AgeFrom.value + " to " + AgeTo.value;
			} else {
				aStart = AgeFrom.value;
				aStop = AgeFrom.value;
				aQuery = AgeFrom.value;
			}
		} else if (AgeTo.value.length > 0) {
			alert ("From Age not entered");
			AgeFrom.select();
			return false;
		}
		if (aStart != "") {
			whereSQL = whereSQL + "(";
			if (StratAge.checked) {
				if (AgeType[0].checked) { //narrow search
					whereSQL = whereSQL + "(StV1.Age_Start <= " + aStart + " AND StV1.Age_Stop >= " + aStop + ") OR (StV2.Age_Start <= " + aStart + " AND StV2.Age_Stop >= " + aStop + ") OR ";
				} else {
					whereSQL = whereSQL + "(StV1.Age_Start >= " + aStop + " AND StV1.Age_Stop <= " + aStart + ") OR (StV2.Age_Start >= " + aStop + " AND StV2.Age_Stop <= " + aStart + ") OR ";
				}
				queryString = queryString + "Collectors/";
				tableName = tableName + ", Stage_View StV1, Stage_View StV2";
				tableJoin = tableJoin + "SP.Inferred_Stage_ID = StV1.Stage_ID(+) AND SP.Known_Stage_ID = StV2.Stage_ID(+) AND ";
				sampPropFlag = true;
			}
			if (AdoAge.checked) {
				if (AgeType[0].checked) { //narrow search
					whereSQL = whereSQL + "(StV3.Age_Start <= " + aStart + " AND StV3.Age_Stop >= " + aStop + ") OR ";
				} else {
					whereSQL = whereSQL + "(StV3.Age_Start >= " + aStop + " AND StV3.Age_Stop <= " + aStart + ") OR ";
				}
				queryString = queryString + "Adopted/";
				tableName = tableName + ", Stage_View StV3";
				tableJoin = tableJoin + "A.Adopted_Stage_ID = StV3.Stage_ID(+) AND ";
				adoFlag = true;
			}
			if (PalAge.checked) {
				if (AgeType[0].checked) { //narrow search
					whereSQL = whereSQL + "(StV4.Age_Start <= " + aStart + " AND StV4.Age_Stop >= " + aStop + ") OR ";
				} else {
					whereSQL = whereSQL + "(StV4.Age_Start >= " + aStop + " AND StV4.Age_Stop <= " + aStart + ") OR ";
				}
				queryString = queryString + "Paleontology/";
				tableName = tableName + ", Stage_View StV4";
				tableJoin = tableJoin + "P.Stage_ID = StV4.Stage_ID(+) AND ";
				palFlag = true;
			}
			whereSQL = whereSQL.substring(0, whereSQL.length - 4) + ") AND ";
			queryString = queryString.substring(0, queryString.length - 1) + " Age = " + aQuery + " AND ";
		}
		if (sampPropFlag) {
			tableName = tableName + ", Sample_Property SP";
			tableJoin = tableJoin + "SP.Record_ID(+) = R.Record_ID AND ";
			recFlag = true;
		}
		if (adoFlag) {
			tableName = tableName + ", Adoption A";
			tableJoin = tableJoin + "A.Record_ID(+) = R.Record_ID AND ";
			recFlag = true;
		}
		if (palFlag) {
			tableName = tableName + ", Paleontology P";
			tableJoin = tableJoin + "P.Record_ID(+) = R.Record_ID AND ";
			recFlag = true;
		}
		if (recFlag) {
			tableName = tableName + ", Record R";
			tableJoin = tableJoin + "R.Sample_ID = SV.Sample_ID AND ";
		}
		if (whereSQL.length > 0)
		whereSQL = whereSQL.substring(0, whereSQL.length - 5);
		if (tableJoin.length > 0)
		{
			tableJoin = tableJoin.substring(0, tableJoin.length - 5);
			whereSQL = "(" + whereSQL + ") AND " + tableJoin;
		}
		WhereSQL.value = whereSQL;
		if (queryString.length > 0)
		{
			queryString = queryString.substring(0, queryString.length - 5);
		}
		QueryString.value = queryString;
		TableName.value = tableName;
		
	}
	return true;
}

function parseFRNum(frNum, prefix) {
	var x, y = "", z;
	x = trim(frNum);
	if (x.substring(x.length - 1, x.length) != ",") { x = x + ","; }
	while (x.indexOf(",") > 0) {
		z = parseIndivFRNum(trim(x.substring(0, x.indexOf(","))), prefix);
		if (z == "false") { return "false"; }
		y = y + z + " OR ";
		x = trim(x.substring(x.indexOf(",") + 1, x.length));
	}
	y = y.substring(0, y.length - 4);
	return y
}

function parseIndivFRNum(frNum, prefix) {
	var sheet, serial
	if (frNum.indexOf("/f") == -1) { return "false"; }
	sheet = frNum.substring(0, frNum.indexOf("/f")).toUpperCase();
	serial = parseSerialNum(frNum.substring(frNum.indexOf("/f") + 2, frNum.length), prefix);
	if (serial == "false") { return "false"; }
	return "(SV." + prefix + "Map_Sheet = '" + sheet + "' AND " + serial + ")";
}

function parseSerialNum(serialNum, prefix) {
	var x, y;
	if (serialNum.indexOf("-") > 0) {
		x = trim(serialNum.substring(0, serialNum.indexOf("-")));
		y = trim(serialNum.substring(serialNum.indexOf("-") + 1, serialNum.length));
		if (isNaN(x) || isNaN(y)) { return "false"; }
		return "SV." + prefix + "Serial_Number BETWEEN " + parseInt(x) + " AND " + parseInt(y);
	} else if (isNaN(serialNum.substring(serialNum.length - 1, serialNum.length)) && !isNaN(serialNum.substring(0, serialNum.length - 1))) {
		return "SV." + prefix + "Serial_Number = " + parseInt(serialNum.substring(0, serialNum.length - 1)) + " AND SV." + prefix + "Recollection_Number = '" + serialNum.substring(serialNum.length - 1, serialNum.length).toUpperCase() + "'";
	} else if (isNaN(serialNum)) {
		return "false";
	}
	return "SV." + prefix + "Serial_Number = " + parseInt(serialNum);
}

</script>

<%	//build array of stage ages
	rs = statement.executeQuery("SELECT MAX(Ag_ID) FROM Age_View");
	rs.next();
	out.println("<script language='JavaScript'>");
	out.println("var ageStart = new Array(" + (rs.getInt(1) + 1) + "); var ageStop = new Array(" + (rs.getInt(1) + 1) + ");");
	rs =statement.executeQuery("SELECT Ag_ID, TA_Age_Start, TA_Age_Stop FROM Age_View ORDER BY Ag_ID");
	while (rs.next()) {
		out.println("ageStart[" + rs.getString(1) + "] = " + rs.getString(2) + ";");
		out.println("ageStop[" + rs.getString(1) + "] = " + rs.getString(3) + ";");
	}
	out.println("</script>");

	out.println("<form name='QueryForm' method='post' action='result_list.jsp'>");
	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td colspan='2' align='center'><img src='images/search.gif' height='20' width='20' /></td></tr>");
	out.println("<tr><td colspan='2' class='bigheading' align='center'>Search FRED</td></tr>");
	out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
	out.println("<tr><td><a href='#' onClick='if(generateSQL(QueryForm)) {QueryForm.submit();}' title='Run Query'><img src='images/ok.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a a href='#' onClick='if(generateSQL(QueryForm)) {QueryForm.submit();}' class='heading'>Run Query</a></td></tr>");
	if (request.getParameter("FoldID") != null) {
		out.println("<tr><td><a href='folder_detail.jsp?ID=" + request.getParameter("FoldID") + "' title='Quit'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_detail.jsp?ID=" + request.getParameter("FoldID") + "' class='heading'>Quit</a></td></tr>");
		out.println("<input type='hidden' name='FoldID' value='" + request.getParameter("FoldID") + "'>");
	} else {
		out.println("<tr><td><a href='index.jsp' title='Quit'><img src='images/cancel.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='index.jsp' class='heading'>Quit</a></td></tr>");
	}
	out.println("</table>");


	drawEndNavigation(out);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");
%>

<table border='0' cellspacing='0' cellpadding='3'>
<tr><td class='heading'>Fossil Record No.&nbsp;&nbsp;</td><td></td><td colspan='3'><input type='text' name='FRNum' size='30' /></td></tr>
<tr><td class='heading'>NZMG Sheet&nbsp;&nbsp;</td><td></td><td colspan='3'><input type='text' name='Map' size='10' /></td></tr>
<tr><td class='heading'>QMap Sheet&nbsp;&nbsp;</td><td></td><td colspan='3'>
	<select name='QMap'>
		<option value='-'>-- All --</option>
		<option value='Kaitaia'>Kaitaia</option>
		<option value='Whangarei'>Whangarei</option>
		<option value='Auckland'>Auckland</option>
		<option value='Waikato'>Waikato</option>
		<option value='Rotorua'>Rotorua</option>
		<option value='Raukumara'>Raukumara</option>
		<option value='Taranaki'>Taranaki</option>
		<option value='Hawkes Bay'>Hawkes Bay</option>
		<option value='Wellington'>Wellington</option>
		<option value='Wairarapa'>Wairarapa</option>
		<option value='Nelson'>Nelson</option>
		<option value='Greymouth'>Greymouth</option>
		<option value='Kaikoura'>Kaikoura</option>
		<option value='Haast'>Haast</option>
		<option value='Aoraki'>Aoraki</option>
		<option value='Christchurch'>Christchurch</option>
		<option value='Wakatipu'>Wakatipu</option>
		<option value='Waitaki'>Waitaki</option>
		<option value='Fiordland'>Fiordland</option>
		<option value='Murihiku'>Murihiku</option>
		<option value='Dunedin'>Dunedin</option>
	</select>
</td></tr>
<tr><td class='heading'>Collector&nbsp;&nbsp;</td><td></td><td colspan='3'>
<%	cd = new ComboDescriptor("Person_View", "Person_ID", "Name");
	cd.name = "Coll";
	cd.prompt = "-- All --";
	cd.orderBy = "UPPER(Name)";
	HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
</td></tr>
<tr><td class='heading'>Collection Date&nbsp;&nbsp;</td><td class='smallheading'>Year</td><td><input type='text' name='YearFrom' size='10' /></td><td>to</td><td><input type='text' name='YearTo' size='10' /></td></tr>
<tr><td class='heading'>Field Number&nbsp;&nbsp;</td><td></td><td colspan='3'><input type='text' name='FieldNum' size='20' /></td></tr>
<tr><td class='heading'>Stratigraphic Name&nbsp;&nbsp;</td><td></td><td colspan='3'><input type='text' name='StratName' size='20' /></td></tr>
<tr><td class='heading'>Stratal Attitude&nbsp;&nbsp;</td><td class='smallheading'>Presence of dip/strike</td><td colspan='3'><input type='checkbox' name='StratAtt' /></td></tr>
<tr><td class='heading'>Nature of Rock Unit&nbsp;&nbsp;</td><td></td><td colspan='3'><input type='text' name='RockNat' size='20' /></td></tr>
<tr><td class='heading'>Deposition Environment&nbsp;&nbsp;</td><td></td><td colspan='3'><input type='text' name='DepEnv' size='20' /></td></tr>
<tr><td class='heading'>Age&nbsp;&nbsp;</td><td class='smallheading'>Stage Range</td><td>
<%	cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
	cd.name = "StageFrom";
	cd.prompt = "-- All --";
	cd.orderBy = "UPPER(Ag_Name)";
	HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
</td><td>&nbsp;to&nbsp;</td><td>
<%	cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
	cd.name = "StageTo";
	cd.prompt = "-- All --";
	cd.orderBy = "UPPER(Ag_Name)";
	HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
%>
</td></tr>
<tr><td></td><td class='smallheading'>Numeric Range&nbsp;&nbsp;</td><td><input type='text' name='AgeFrom' size='10' /></td><td>&nbsp;to&nbsp;</td><td><input type='text' name='AgeTo' size='10' /></td></tr>
<tr><td></td><td class='smallheading'>Options</td><td class='smallheading'><input type='checkbox' name='StratAge' checked />&nbsp;Collectors&nbsp;Inferred/Known&nbsp;Age<br><input type='checkbox' name='AdoAge' checked />&nbsp;Adopted&nbsp;Age<br><input type='checkbox' name='PalAge' checked />&nbsp;Paleontology&nbsp;Det&nbsp;Age</td><td></td><td class='smallheading'><input type='radio' name='AgeType' value='Narrow' checked />&nbsp;Narrow Search<br><input type='radio' name='AgeType' value='Wide' />&nbsp;Wide Search</td></tr>
</table>
<input type='hidden' name='WhereSQL' value='' />
<input type='hidden' name='QueryString' value='' />
<input type='hidden' name='TableName' value='' />
<%	out.println("<p><a href='#' onClick='if(generateSQL(QueryForm)) {QueryForm.submit();}' title='Run Query'><img src='images/ok.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /><a a href='#' onClick='if(generateSQL(QueryForm)) {QueryForm.submit();}' class='heading'>Run Query</a></p>");
%>
</form>

<%	out.println("</td></tr></table>");
	drawBottom(out, et);
%>