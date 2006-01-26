<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
		import="nz.cri.gns.fred.*, nz.cri.gns.db.*, nz.cri.gns.jsp.*, nz.cri.gns.intranet.*, java.sql.*, nz.cri.gns.auth.*"
%><%
	PageState state = new PageState(request, response, getServletContext());
	DBConnection connection = FREDUtils.getFREDConnection(state);
	Statement statement = connection.statement;
	ResultSet rs;
	ComboDescriptor cd;
	User user = (User)getUser(session);
	int userID = user.getPersonId();
	String mfID = "0", field = "x";


	ExtranetTemplate et = new ExtranetTemplate();
	et.setImageBase("/fred/images/fred.gif");
	et.setDisplayLogin(false);
	et.setShowGnsLogo(false);
	et.setUseNavigationColumn(false);
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");
	out.println("<tr><td>");
	
	if (request.getParameter("Field") != null) { field = request.getParameter("Field"); }
	
%>

<script language='JavaScript'>

function replaceSingleQuote(str1) {
	while(str1.indexOf("'") != -1) {
		str1 = str1.replace("'", "&quot");
	}
	while(str1.indexOf("&quot") != -1) {
		str1 = str1.replace("&quot", "''");
	}
	return str1;
}

function addData(type) {
	with (document.form1) {
		if (type == "Person") {
			if (FamilyName.value == "") {
				alert ("Please enter a value for Surname/Company");
				FamilyName.select();
				return false;
			}
			Add.value="Person";
			return true;
		}
	}
	return false;
}

function saveData(type) {
	with (document.form1) {
		if (type == "Coord") {
			if (checkCoord() == 1) {
				window.opener.form1.GridRef.value = CoordType.value + ":";
				if (CoordType.value == "NZGD49" || CoordType.value == "WGS84") {
					window.opener.form1.GridRef.value = window.opener.form1.GridRef.value + Country.value.toUpperCase() + "*";
					window.opener.form1.GridRef.value = window.opener.form1.GridRef.value + North.value + "*" + East.value;
				} else {
					if (CoordType.value == "NZMS260" || CoordType.value == "NZMS1S" || CoordType.value == "NZMS1N")
						window.opener.form1.GridRef.value = window.opener.form1.GridRef.value + MapSheet.value.toUpperCase() + "*";
					window.opener.form1.GridRef.value = window.opener.form1.GridRef.value + East.value + "*" + North.value;
				}
				window.close();
			}
		}
		else if (type == "Recoll") {
			window.opener.form1.Recoll.value = parseDropDown(SampName.value);
			window.close();
		}
		else if (type == "Date") {
			window.opener.form1.<%=field%>.value = parseDate(DateDay.value, DateMonth.value, DateYear.value, DateRnd);
			window.close();
		}
		else if (type == "FeatPer") {
			window.opener.form1.Person.value = parseDropDown(Person.value);
			window.close();
		}
		else if (type == "Coll") {
			window.opener.form1.Coll.value = window.opener.form1.Coll.value + parseDropDown(Person.value) + "\n";
		}
		else if (type == "Adoptor") {
			window.opener.form1.Adoptor.value = window.opener.form1.Adoptor.value + parseDropDown(Person.value) + "\n";
		}
		else if (type == "Identifier") {
			window.opener.form1.Identifier.value = window.opener.form1.Identifier.value + parseDropDown(Person.value) + "\n";
		}
		else if (type == "StratName") {
			if (Unk.checked == true && StratName.value != "") {
				alert("Please don't enter a value for Stratigraphic Name and check the Unknown box");
			} else if (Unk.checked == false) {
				window.opener.form1.StratName.value = StratName.value;
				window.close();
			} else {
				window.opener.form1.StratName.value = "unknown";
				window.close();
			}
		}
		else if (type == "SentTo") {
			window.opener.form1.SentTo.value = window.opener.form1.SentTo.value + parseDropDown(Group.value) + "*" + parseDropDown(Person.value) + "*" + parseDropDown(Lab.value) + "*" + Comm.value + "\n";
		}
		else if (type == "PrevSamp") {
			if (SubFeat.value == "-" && WorkFeat.value == "-") {
				alert("Please select a sample");
			} else if (SubFeat.value != "-" && WorkFeat.value != "-") {
				alert("Please only select one sample");
			} else if (SubFeat.value != "-") {
				window.opener.form1.PrevSamp.value = window.opener.form1.PrevSamp.value + parseDropDown(SubFeat.value) + ";";
			} else {
				window.opener.form1.PrevSamp.value = window.opener.form1.PrevSamp.value + parseDropDown(WorkFeat.value) + ";";
			}
		}
		else if (type == "SampRel") {
			if (checkRel("Samp") == 1) {
				window.opener.form1.SampRel.value = window.opener.form1.SampRel.value + parseDropDown(DistMod.value) + Distance.value + " m";
				if (DistRange.value != "") {
					window.opener.form1.SampRel.value = window.opener.form1.SampRel.value + " - " + DistRange.value + " m";
				}
				window.opener.form1.SampRel.value = window.opener.form1.SampRel.value + " " + parseDropDown(Rel.value) + " ";
				if (SubFeat.value == "-" && WorkFeat.value == "-") {
					alert("Please select a sample");
				} else if (SubFeat.value != "-" && WorkFeat.value != "-") {
					alert("Please only select one sample");
				} else if (SubFeat.value != "-") {
					window.opener.form1.SampRel.value = window.opener.form1.SampRel.value + parseDropDown(SubFeat.value) + "\n";
				} else {
					window.opener.form1.SampRel.value = window.opener.form1.SampRel.value + parseDropDown(WorkFeat.value) + "\n";
				}
			}
		}
		else if (type == "StratRel") {
			if (checkRel("Strat") == 1) {
				window.opener.form1.StratRel.value = window.opener.form1.StratRel.value + parseDropDown(DistMod.value) + Distance.value + " m";
				if (DistRange.value != "") {
					window.opener.form1.StratRel.value = window.opener.form1.StratRel.value + " - " + DistRange.value + " m";
				}
				window.opener.form1.StratRel.value = window.opener.form1.StratRel.value + " " + parseDropDown(Rel.value) + " " + StratName.value + "\n";
			}
		}
		else if (type == "SedFeat") {
			if (Feat.value != "-") {
				window.opener.form1.SedFeat.value = window.opener.form1.SedFeat.value + parseDropDown(Feat.value);
				if (Abund.checked) { window.opener.form1.SedFeat.value = window.opener.form1.SedFeat.value + "*"; }
				window.opener.form1.SedFeat.value = window.opener.form1.SedFeat.value + ";";
			}
		}
		else if (type == "Taxa") {
			if (checkTaxa() == 1) {
				window.opener.form1.Taxa.value = window.opener.form1.Taxa.value + parseDropDown(Group.options[Group.selectedIndex].text) + "*" + TaxaName.value + "*" + Author.value + "*" + SpecCount.value + "*" + SpecCoord.value + "*" + Comm.value + "\n";
			}
		}
	}
}

function parseDate(day, montha, year, rnd) {
	var month
	if (montha == "JAN") {
		month = "1";
	} else if (montha == "FEB") {
		month = "2";
	} else if (montha == "MAR") {
		month = "3";
	} else if (montha == "APR") {
		month = "4";
	} else if (montha == "MAY") {
		month = "5";
	} else if (montha == "JUN") {
		month = "6";
	} else if (montha == "JUL") {
		month = "7";
	} else if (montha == "AUG") {
		month = "8";
	} else if (montha == "SEP") {
		month = "9";
	} else if (montha == "OCT") {
		month = "10";
	} else if (montha == "NOV") {
		month = "11";
	} else if (montha == "DEC") {
		month = "12";
	}
	if (rnd[0].checked) {
		return day + "/" + month + "/" + year
	} else if (rnd[1].checked) {
		return month + "/" + year;
	} else {
		return year;
	}
}

function checkCoord() {
	with (document.form1) {
		if (CoordType.value == "NZMG") {
			if (East.value.length != 7 || isNaN(East.value)) {
				alert ("Please enter a valid easting (7 digits for Full NZMG)");
				East.select();
				return 0;
			}
			if (North.value.length != 7 || isNaN(North.value)) {
				alert ("Please enter a valid northing (7 digits for Full NZMG)");
				North.select();
				return 0;
			}
		} else if (CoordType.value == "NZMS260") { // Trunc NZMG
			if (MapSheet.value.length != 3 || MapSheet.value.charAt(0) <= "9" || isNaN(MapSheet.value.substring(1, 2))) {
				alert ("Please enter a valid NZMS260 map sheet");
				MapSheet.select();
				return 0;
			}
			if (East.value.length < 3 || East.value.length > 4 || isNaN(East.value)) {
				alert ("Please enter a valid easting (3 or 4 digits for NZMS260)");
				East.select();
				return 0;
			}
			if (North.value.length < 3 || North.value.length > 4 || isNaN(North.value)) {
				alert ("Please enter a valid northing (3 or 4 digits for NZMS260)");
				North.select();
				return 0;
			}
			if (East.value.length != North.value.length) {
				alert ("Please enter a valid coordinate - Easting and Northing must be the same number of digits");
				East.select();
				return 0;
			}
		} else if (CoordType.value == "NZYS" || CoordType.value == "NZYN") {
			if (East.value.length != 6 || isNaN(East.value)) {
				alert ("Please enter a valid easting (6 digits for NZ Yard)");
				East.select();
				return 0;
			}
			if (North.value.length != 6 || isNaN(North.value)) {
				alert ("Please enter a valid northing (6 digits for NZ Yard)");
				North.select();
				return 0;
			}
		} else if (CoordType.value == "NZMS1S" || CoordType.value == "NZMS1N") {
			if (MapSheet.value.length < 3 || MapSheet.value.length > 5) {
				alert ("Please enter a valid NZMS1 map sheet");
				MapSheet.select();
				return 0;
			}
			if (East.value.length < 3 || East.value.length > 4 || isNaN(East.value)) {
				alert ("Please enter a valid easting (3 or 4 digits for NZMS1)");
				East.select();
				return 0;
			}
			if (North.value.length < 3 || North.value.length > 4 || isNaN(North.value)) {
				alert ("Please enter a valid northing (3 or 4 digits for NZMS1)");
				North.select();
				return 0;
			}
			if (East.value.length != North.value.length) {
				alert ("Please enter a valid coordinate - Easting and Northing must be the same number of digits");
				East.select();
				return 0;
			}
		} else if (CoordType.value == "NZGD49" || CoordType.value == "WGS84") { // Lat/Long
			if (East.value.length == 0 || isNaN(East.value) || East.value < -180 || East.value > 180) {
				alert ("Please enter a valid longitude (between -180 and 180 for Lat/Long)");
				East.select();
				return 0;
			}
			if (North.value.length == 0 || isNaN(North.value) || North.value < -90 || North.value > 90) {
				alert ("Please enter a valid latitude (between -90 and 90 for Lat/Long)");
				North.select();
				return 0;
			}
		} else {
			if (isNaN(East.value)) {
				alert ("Please enter a numeric easting");
				East.select();
				return 0;
			}
			if (isNaN(North.value)) {
				alert ("Please enter a numeric northing");
				North.select();
				return 0;
			}
		}
	}
	return 1;
}

function checkRel(type) {
	with (document.form1) {
		if (Distance.value != "" && isNaN(Distance.value)) {
			alert ("Please enter a numeric value");
			Distance.select();
			return 0;
		}
		if (DistRange.value != "" && isNaN(DistRange.value)) {
			alert ("Please enter a numeric value");
			DistRange.select();
			return 0;
		}
		if (DistRange.value != "" && Distance.value == "") {
			alert ("Please enter a value for the start of the distance range or enter a single value in the first box");
			Distance.select();
			return 0;
		}
		if (Rel.value == "-") {
			alert ("Please select a relationship");
			Rel.select();
			return 0;
		}
		if (type == "Strat" && StratName.value == "") {
			alert ("Please select a rock type");
			StratName.select();
			return 0;
		}
		return 1;
	}
}

function checkTaxa() {
	with (document.form1) {
		if (Group.value == "-") {
			alert ("Please select a taxonomic group");
			Group.focus;
			return 0;
		}
		if (TaxaName.value == "") {
			alert ("Please enter a taxonomic name");
			TaxaName.select;
			return 0;
		}

	}
	return 1;
}

function parseDropDown(val) {
	if (val == "-") {
		return "";
	} else {
		return val;
	}
}

</script>

<form name='form1' method='post' action='data_entry_supp.jsp'>
<table border='0' cellspacing='3' cellpadding='0'>

<%	if (request.getParameter("Type") != null) {
		out.println("<input type='hidden' name='Type' value='" + request.getParameter("Type") + "' />");
		out.println("<input type='hidden' name='Add' value='' />");

		if (request.getParameter("Add") != null) {  //add data to lookup tables
			if (request.getParameter("Add").equals("Person")) {
				statement.executeUpdate("INSERT INTO Person (Name) VALUES (" + JspUtils.sqlEscape(request.getParameter("FamilyName") + ", " + request.getParameter("GivenName")) + ")");
				out.println("<script language='JavaScript'>alert(\"Name added to list.  Please now select to add to form\");</script>");
			}
		}

		if (request.getParameter("Type").equals("Coord")) {
			out.println("<tr><td class='heading' colspan='2'>Coordinates</td></tr>");
			out.println("<tr><td colspan='2'>Please select the type and then enter the coordinates in the appropriate boxes.");
			out.println("<ul><li><em>New Zealand Map Grid</em> (NZMG) - enter full 7-digit easting and northing</li>");
			out.println("<li><em>NZMS260</em> (1:50,000 map reference) - enter map sheet plus 3 or 4-digit easting and northing</li>");
			out.println("<li><em>NZ Yard Grid</em> - enter full 6-digit easting and northing</li>");
			out.println("<li><em>NZMS1</em> (1:63,000 map reference) - enter map sheet (incl S or N) plus 3 or 4-digit easting and northing</li>");
			out.println("<li><em>Chatham, Auckland and Campbell Island grids</em> - enter full metric easting and northing</li>");
			out.println("<li><em>Lat/Long NZGD49</em> (use for NZ lat/long prior to 2000) - enter latitude and longitude in decimal degrees.  South latitudes and West longitudes should be entered as negative numbers</li>");
			out.println("<li><em>Lat/Long WGS84/NZGD2000</em> (use for international or recent New Zealand lat/longs) - enter as decimal degrees as for NZGD49</li></ul>");
			out.println("Note: coordinates will be converted to NZGD49 lat/long.  Both the original and converted coordinates will be stored in the database.</td></tr>");
			
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td class='heading'>Coord Type</td><td>");
			out.println("<select name='CoordType'>");
			out.println("<option value='NZMG' selected>New Zealand Map Grid</option>");
			out.println("<option value='NZMS260'>NZMS260</option>");
			out.println("<option value='NZYS'>NZ Yard Grid (Sth Isl)</option>");
			out.println("<option value='NZYN'>NZ Yard Grid (Nth Isl)</option>");
			out.println("<option value='NZMS1S'>NZMS1 (Sth Isl)</option>");
			out.println("<option value='NZMS1N'>NZMS1 (Nth Isl)</option>");
			out.println("<option value='CHAT'>Chatham Island Grid</option>");
			out.println("<option value='AUCK'>Auckland Island Grid</option>");
			out.println("<option value='CAMP'>Campbell Island Grid</option>");
			out.println("<option value='NZGD49'>Lat/Long NZGD49</option>");
			out.println("<option value='WGS84'>Lat/Long WGS84/NZGD2000</option>");
			out.println("</select></td></tr>");
			out.println("<tr><td class='heading'>Map Sheet</td><td><input type='text' name='MapSheet' /></td></tr>");
			out.println("<tr><td class='heading'>Easting/Longitude</td><td><input type='text' name='East' /></td></tr>");
			out.println("<tr><td class='heading'>Northing/Latitude</td><td><input type='text' name='North' /></td></tr>");
			out.print("<tr><td class='heading'>Country</td><td>");
			cd = new ComboDescriptor("MIS.Country", "Country_Code", "Country_Name");
			cd.name = "Country";
			cd.prompt = "-- Choose --";
			cd.orderBy = "Country_Name";
			cd.selected = "NZ";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr></table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"Coord\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"Coord\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("Recoll")) {
			out.println("<tr><td class='heading' colspan='2'>Recollection</td></tr>");
			out.println("<tr><td colspan='2'>Please select a masterfile area from the drop-down list.  The Sample list will then be populated with all submitted samples plus working samples which you have access to in that masterfile area.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Masterfile Area</td><td>");
			cd = new ComboDescriptor("Folder", "Folder_ID", "Name");
			cd.name = "MF";
			cd.prompt = "-- Choose --";
			cd.orderBy = "Folder_ID";
			cd.tagParams = "onChange='form1.submit();'";
			cd.join = "Folder_Type = 'admin'";
			if (request.getParameter("MF") != null) {
				cd.selected = request.getParameter("MF");
				mfID = request.getParameter("MF");
			}
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.print("<tr><td class='heading'>Sample</td><td>");
			cd = new ComboDescriptor("Feature_Security_View", "Sample_Name", "Sample_Name");
			cd.name = "SampName";
			cd.prompt = "-- Choose --";
			cd.join = "Masterfile_ID = " + mfID + " AND (Status = 'approved' OR (Folder_Type = 'personal' AND User_ID = " + userID + "))";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr></table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"Recoll\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"Recoll\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("Date")) {
			out.println("<tr><td class='heading' colspan='2'>Date</td></tr>");
			out.println("<tr><td colspan='2'>Select a date from the drop-down list.  If only the month or year is known then you must still specify a full date, but select the appropriate option</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Date</td><td>");
			HTMLUtils.makeDateDropBox(new java.io.PrintWriter(out), "Date", "form1", null, null, (byte)(HTMLUtils.DATE | HTMLUtils.MONTH_FULL | HTMLUtils.YEAR), null, null, -50, 0, true);
			out.println("</td></tr>");
			out.println("<tr><td class='heading'>Rounding</td><td><input type='radio' name='DateRnd' value='' checked />None&nbsp;&nbsp;<input type='radio' name='DateRnd' value='Month' />Month&nbsp;&nbsp;<input type='radio' name='DateRnd' value='Year' />Year</td></tr>");
			out.println("</td></tr></table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"Date\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"Date\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("OpComp")) {
			out.println("<tr><td class='heading' colspan='2'>Operating Company</td></tr>");
			out.println("<tr><td colspan='2'>Select a person/company from the drop-down list.  New companies can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Person/Company</td><td>");
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr>");
			out.println("<tr><td class='heading' colspan='2'>Add to Person List</td></tr>");
			out.println("<tr><td class='smallheading'>First Name</td><td><input type='text' name='GivenName' /></td></tr>");
			out.println("<tr><td class='smallheading'>Surname/Company</td><td><input type='text' name='FamilyName' />&nbsp&nbsp");
			out.println("<input type='submit' value='Add' onClick='return addData(\"Person\");' />");
			out.println("</td></tr></table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"FeatPer\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"FeatPer\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("VertPerson")) {
			out.println("<tr><td class='heading' colspan='2'>Section Collector</td></tr>");
			out.println("<tr><td colspan='2'>Select a person/company from the drop-down list.  New collectors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Person/Company</td><td>");
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr>");
			out.println("<tr><td class='heading' colspan='2'>Add to Person List</td></tr>");
			out.println("<tr><td class='smallheading'>First Name</td><td><input type='text' name='GivenName' /></td></tr>");
			out.println("<tr><td class='smallheading'>Surname/Company</td><td><input type='text' name='FamilyName' />&nbsp&nbsp");
			out.println("<input type='submit' value='Add' onClick='return addData(\"Person\");' />");
			out.println("</td></tr></table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"FeatPer\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"FeatPer\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}
		
		else if (request.getParameter("Type").equals("Coll")) {
			out.println("<tr><td class='heading' colspan='2'>Collectors</td></tr>");
			out.println("<tr><td colspan='2'>Select a person/company from the drop-down list.  New collectors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple collectors by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Person/Company</td><td>");
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr>");
			out.println("<tr><td class='heading' colspan='2'>Add to Person List</td></tr>");
			out.println("<tr><td class='smallheading'>First Name</td><td><input type='text' name='GivenName' /></td></tr>");
			out.println("<tr><td class='smallheading'>Surname/Company</td><td><input type='text' name='FamilyName' />&nbsp&nbsp");
			out.println("<input type='submit' value='Add' onClick='return addData(\"Person\");' />");
			out.println("</td></tr></table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"Coll\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"Coll\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("Adoptor")) {
			out.println("<tr><td class='heading' colspan='2'>Adoptor</td></tr>");
			out.println("<tr><td colspan='2'>Select a person/company from the drop-down list.  New adoptors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple adoptors by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Person</td><td>");
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr>");
			out.println("<tr><td class='heading' colspan='2'>Add to Person List</td></tr>");
			out.println("<tr><td class='smallheading'>First Name</td><td><input type='text' name='GivenName' /></td></tr>");
			out.println("<tr><td class='smallheading'>Surname/Company</td><td><input type='text' name='FamilyName' />&nbsp&nbsp");
			out.println("<input type='submit' value='Add' onClick='return addData(\"Person\");' />");
			out.println("</td></tr></table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"Adoptor\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"Adoptor\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("Identifier")) {
			out.println("<tr><td class='heading' colspan='2'>Identifier</td></tr>");
			out.println("<tr><td colspan='2'>Select a person/company from the drop-down list.  New identifiers can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple identifiers by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Person</td><td>");
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr>");
			out.println("<tr><td class='heading' colspan='2'>Add to Person List</td></tr>");
			out.println("<tr><td class='smallheading'>First Name</td><td><input type='text' name='GivenName' /></td></tr>");
			out.println("<tr><td class='smallheading'>Surname/Company</td><td><input type='text' name='FamilyName' />&nbsp&nbsp");
			out.println("<input type='submit' value='Add' onClick='return addData(\"Person\");' />");
			out.println("</td></tr></table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"Identifier\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"Identifier\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("StratName")) {
			out.println("<tr><td class='heading' colspan='2'>Stratigraphic Name</td></tr>");
			out.println("<tr><td colspan='2'>Please enter a stratigraphic unit name in the text box.  You can select a unit from the NZ StratLex drop-down box if appropriate.<br />If you don't know the name of the unit tick the \"unknown\" box (please use sparingly)</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>NZ StratLex</td><td>");
			cd = new ComboDescriptor("SL.Strat_Unit", "SU_Name", "SU_Name");
			cd.name = "StratLex";
			cd.prompt = "-- Choose --";
			cd.tagParams = "onChange='form1.StratName.value = parseDropDown(StratLex.value);'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr>");
			out.print("<tr><td class='heading'>Stratigraphic Name</td><td><input type='text' name='StratName' size='40' />");
			out.print("&nbsp;&nbsp;<input type='checkbox' name='Unk' />&nbsp;unknown");
			out.println("</td></tr>");
			out.println("</table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"StratName\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"StratName\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("SentTo")) {
			out.println("<tr><td class='heading' colspan='2'>Sent To</td></tr>");
			out.println("<tr><td colspan='2'>Please select a fossil group and then one or both of a person and lab.<br />You may add enter multiple rows by clicking the Add To Main Form icon between each row and then Close to end.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Fossil Group</td><td>");
			cd = new ComboDescriptor("fossil_group", "name", "name");
			cd.name = "Group";
			cd.prompt = "-- Choose --";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr>");
			out.print("<tr><td class='heading'>Person</td><td>");
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("</td></tr>");
			out.print("<tr><td class='heading'>Lab</td><td>");
			cd = new ComboDescriptor("SC.Lab", "Lab_Name", "Lab_Name");
			cd.name = "Lab";
			cd.prompt = "-- Choose --";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("<tr><td class='heading'>Comments</td><td><textarea name='Comm' rows='3' cols='40'></textarea></td></tr>");
			out.println("<tr><td class='heading' colspan='2'>Add to Person List</td></tr>");
			out.println("<tr><td class='smallheading'>First Name</td><td><input type='text' name='GivenName' /></td></tr>");
			out.println("<tr><td class='smallheading'>Surname</td><td><input type='text' name='FamilyName' />&nbsp&nbsp");
			out.println("<input type='submit' value='Add Person' onClick='return addData(\"Person\");' />");
			out.println("</table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"SentTo\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"SentTo\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("PrevSamp")) {
			out.println("<tr><td class='heading' colspan='2'>Previous Samples Nearby</td></tr>");
			out.println("<tr><td colspan='2'>Please select a masterfile area from the drop-down list.  The Sample list will then be populated with all submitted samples plus working samples which you have access to in that masterfile area.<br />You may add multiple samples by clicking the Add To Main Form icon between each sample and then Close to end.</td></tr>");			
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Masterfile Area</td><td>");
			cd = new ComboDescriptor("folder", "folder_id", "name");
			cd.name = "MF";
			cd.prompt = "-- Choose --";
			cd.tagParams = "onChange='form1.submit();'";
			cd.join = "folder_type = 1";
			cd.orderBy = "folder_id";
			if (request.getParameter("MF") != null  && !request.getParameter("MF").equals("-")) {
				cd.selected = request.getParameter("MF");
				mfID = request.getParameter("MF");
			}
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.print("<tr><td class='heading'>Submitted Samples</td><td>");
			cd = new ComboDescriptor("feature_view", "sample_name", "sample_name");
			cd.name = "SubFeat";
			cd.prompt = "-- Choose --";
			cd.selectDistinct = true;
			cd.join = "masterfile_id = " + mfID + " AND feature_status = 'approved'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.print("<tr><td class='heading'>Samples in Folders</td><td>");
			cd = new ComboDescriptor("feature_view fv, folder_view fd", "fv.sample_name", "fv.sample_name");
			cd.name = "WorkFeat";
			cd.prompt = "-- Choose --";
			cd.selectDistinct = true;
			cd.join = "fv.feature_working_folder_id = fd.folder_id AND fv.feature_status <> 'approved' AND fd.user_id = " + user.getPersonId() + " AND fd.folder_type IN (2, 3)";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.println("</table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"PrevSamp\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"PrevSamp\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("SampRel")) {
			out.println("<tr><td class='heading' colspan='2'>Sample Relationships</td></tr>");
			out.println("<tr><td colspan='2'>Please select a masterfile area from the drop-down list.  The Sample list will then be populated with all submitted samples plus working samples which you have access to in that masterfile area.  Then select a relationship type enter an optional distance (in metres).<br />You may add multiple samples by clicking the Add To Main Form icon between each sample and then Close to end.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td class='heading'>Distance</td>");
			out.println("<td class='heading'><select name='DistMod'><option value='-' selected></option><option value='c. '>c.</option><option value='? '>?</option></select>&nbsp;&nbsp;");
			out.println("<input type='text' name='Distance' />&nbsp;m&nbsp;-&nbsp;");
			out.println("<input type='text' name='DistRange' />&nbsp;m</td></tr>");
			out.print("<tr><td class='heading'>Relationship</td><td>");
			cd = new ComboDescriptor("relationship_type", "name", "name");
			cd.name = "Rel";
			cd.prompt = "-- Choose --";
			cd.orderBy = "reltype_id";
			cd.join = "relation_type = 'Sample' AND name != 'nearby'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.print("<tr><td class='heading'>Masterfile Area</td><td>");
			cd = new ComboDescriptor("folder", "folder_id", "name");
			cd.name = "MF";
			cd.prompt = "-- Choose --";
			cd.tagParams = "onChange='form1.submit();'";
			cd.join = "folder_type = 1";
			cd.orderBy = "folder_id";
			if (request.getParameter("MF") != null  && !request.getParameter("MF").equals("-")) {
				cd.selected = request.getParameter("MF");
				mfID = request.getParameter("MF");
			}
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.print("<tr><td class='heading'>Submitted Samples</td><td>");
			cd = new ComboDescriptor("feature_view", "sample_name", "sample_name");
			cd.name = "SubFeat";
			cd.prompt = "-- Choose --";
			cd.selectDistinct = true;
			cd.join = "masterfile_id = " + mfID + " AND feature_status = 'approved'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.print("<tr><td class='heading'>Samples in Folders</td><td>");
			cd = new ComboDescriptor("feature_view fv, folder_view fd", "fv.sample_name", "fv.sample_name");
			cd.name = "WorkFeat";
			cd.prompt = "-- Choose --";
			cd.selectDistinct = true;
			cd.join = "fv.feature_working_folder_id = fd.folder_id AND fv.feature_status <> 'approved' AND fd.user_id = " + user.getPersonId() + " AND fd.folder_type IN (2, 3)";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.println("</table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"SampRel\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"SampRel\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("StratRel")) {
			out.println("<tr><td class='heading' colspan='2'>Stratigraphic Relationships</td></tr>");
			out.println("<tr><td colspan='2'>Please select a relationship type and enter an optional distance or range of distances (in metres).  Enter a stratigraphic unit name in the text box.  You can select a unit from the NZ StratLex drop-down box if appropriate.<br />You may add multiple units by clicking the Add To Main Form icon between each unit and then Close to end.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.println("<tr><td class='heading'>Distance</td>");
			out.println("<td class='heading'><select name='DistMod'><option value='-' selected></option><option value='c. '>c.</option><option value='? '>?</option></select>&nbsp;&nbsp;");
			out.println("<input type='text' name='Distance' />&nbsp;m&nbsp;-&nbsp;");
			out.println("<input type='text' name='DistRange' />&nbsp;m</td></tr>");
			out.print("<tr><td class='heading'>Relationship</td><td>");
			cd = new ComboDescriptor("relationship_type", "name", "name");
			cd.name = "Rel";
			cd.prompt = "-- Choose --";
			cd.orderBy = "reltype_id";
			cd.join = "relation_type = 'Stratigraphic'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.print("<tr><td class='heading'>NZ StratLex</td><td>");
			cd = new ComboDescriptor("SL.Strat_Unit", "SU_Name", "SU_Name");
			cd.name = "StratLex";
			cd.prompt = "-- Choose --";
			cd.tagParams = "onChange='form1.StratName.value = parseDropDown(StratLex.value);'";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.println("<tr><td class='heading'>Stratigraphic Name</td><td><input type='text' name='StratName' size='40' /></td></tr>");
			out.print("</table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"StratRel\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"StratRel\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("SedFeat")) {
			out.println("<tr><td class='heading' colspan='2'>Additional Features</td></tr>");
			out.println("<tr><td colspan='2'>Please select a feature from the list.  Check the Abundant box to indicate the feature is abundant.<br />You may add multiple features by clicking the Add To Main Form icon between each feature and then Close to end.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Feature</td><td>");
			cd = new ComboDescriptor("sedimentary_feature_type", "Name", "Code || ': ' || Name");
			cd.name = "Feat";
			cd.prompt = "-- Choose --";
			cd.orderBy = "Code";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.println("<tr><td class='heading'>Abundant</td><td><input type='checkbox' name='Abund' /></td></tr>");
			out.print("</table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"SedFeat\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"SedFeat\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}

		else if (request.getParameter("Type").equals("Taxa")) {
			String groupID = "0";
			out.println("<tr><td class='heading' colspan='2'>Taxonomic Details</td></tr>");
			out.println("<tr><td colspan='2'>Please select a Taxonomic Group from the drop-down list.  The Taxonomic Name List will then be filled with appropriate taxa.  Either choose from this list or enter a new name in the Taxonomic Name and Author (optional) boxes.<br />Note: new taxonomic names will be entered into the database as provisional and will be assesed by members of the taxonomic panel.  You will not be able to submit your record until the name has been approved.<br />You may add multiple taxa by clicking the Add To Main Form icon between each taxa and then Close to end.</td></tr>");
			out.println("<tr><td>&nbsp;</td></tr>");
			out.print("<tr><td class='heading'>Group</td><td>");
			cd = new ComboDescriptor("taxonomic_group", "group_id", "name");
			cd.name = "Group";
			cd.prompt = "-- Choose --";
			cd.orderBy = "group_ID";
			cd.tagParams = "onChange='form1.submit();'";
			if (request.getParameter("Group") != null) {
				groupID = request.getParameter("Group");
				cd.selected = groupID;
			}
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.print("<tr><td class='heading'>Taxonomic&nbsp;Name&nbsp;List</td><td>");
			cd = new ComboDescriptor("Taxonomic_Lookup", "Taxa_id", "Taxonomic_Name");
			cd.name = "TaxaList";
			cd.prompt = "-- Choose --";
			cd.orderBy = "UPPER(taxonomic_name)";
			cd.tagParams = "onChange='form1.TaxaName.value = parseDropDown(TaxaList.options[TaxaList.selectedIndex].text);'";
			cd.join = "group_id = " + groupID + " AND status IN ('approved', 'provisional') AND taxonomic_name IS NOT NULL";
			HTMLUtils.makeDropBox(new java.io.PrintWriter(out), statement, cd);
			out.println("</td></tr>");
			out.println("<tr><td class='heading'>Taxonomic Name</td><td><input type='text' name='TaxaName' size='40' /></td></tr>");
			out.println("<tr><td class='heading'>Author</td><td><input type='text' name='Author' size='40' /></td></tr>");
			out.println("<tr><td class='heading'>Specimen Count</td><td><input type='text' name='SpecCount' size='40' /></td></tr>");
			out.println("<tr><td class='heading'>Specimen Coordinates</td><td><input type='text' name='SpecCoord' size='40' /></td></tr>");
			out.println("<tr><td class='heading'>Comments</td><td><textarea name='Comm' cols='40' rows='3'></textarea></td></tr>");
			out.println("</table>");
			out.println("<table border='0' cellspacing='2' cellpadding='0'>");
			out.println("<tr><td><img src='images/blank.gif' width='1' height='5' /></td></tr>");
			out.println("<tr><td><a href='#' onClick='saveData(\"Taxa\");return false;' title='Add'><img src='images/put.gif' height='20' width='20' border='0' /></a>&nbsp;&nbsp;</td><td><a href='#' onClick='saveData(\"Taxa\");return false;' class='heading'>Add to Main Form</a></td></tr>");
		}
	}

	out.println("<tr><td><a href='javascript: window.close();'><img src='images/close.gif' height='20' width='20' border='0' alt='Close' /></a>&nbsp;&nbsp;</td><td><a href='javascript: window.close();' class='heading'>Close</a></td></tr>");
	out.println("</table></form>");

	out.println("</td></tr></table>");
	drawBottom(out, et);

%>
