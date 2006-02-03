<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Person"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.util.PersonUtil"
%><%@page import="nz.cri.gns.db.ComboDescriptor"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.User"
%><%
	ComboDescriptor cd;
	User user = (User) getUser(session);
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	String mfID = "0";

	ExtranetTemplate et = new ExtranetTemplate();
	et.setNewHeaderStyle(true);
	et.setImageBase("/fred/images/fred.gif");
	et.setDisplayLogin(false);
	et.setShowGnsLogo(false);
	et.setUseNavigationColumn(false);
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);
	
	%><script language="JavaScript">
	
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
				if (PersonName.value == "") {
					alert ("Please enter a name");
					PersonName.select();
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
			if (type == "FeatPer") {
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
	
	<table style="margin-left:20px; width:550px;" border="0">
	<tr><td>
	
	<form name="form1" method="post" action="data_entry_supp.jsp">
	<table border="0" cellspacing="3" cellpadding="0"><%

	if (request.getParameter("Type") != null) {
		%><input type="hidden" name="Type" value="<%=request.getParameter("Type")%>" />
		<input type="hidden" name="Add" value="" /><%

		if (request.getParameter("Add") != null) {  //add data to lookup tables
			if (request.getParameter("Add").equals("Person")) {
				PersonUtil personUtil = new PersonUtil(factory);
				Person person = personUtil.findOrCreatePerson(request.getParameter("PersonName"));
				%><script language="JavaScript">alert("<%=person.getName()%> added to list.  Please now select from drop-down list to add to form");</script><%
			}
		}

		if (request.getParameter("Type").equals("OpComp")) {
			%><tr><td class="heading" colspan="2">Operating Company</td></tr>
			<tr><td colspan="2">Select a person/company from the drop-down list.  New companies can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Person/Company</td><td><%
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading" colspan="2">Add to Person List</td></tr>
			<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
			<input type="submit" value="Add" onClick="return addData(\"Person\");" /></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"FeatPer\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"FeatPer\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("VertPerson")) {
			%><tr><td class="heading" colspan="2">Section Collector</td></tr>
			<tr><td colspan="2">Select a person/company from the drop-down list.  New collectors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Person/Company</td><td><%
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading" colspan="2">Add to Person List</td></tr>
			<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
			<input type="submit" value="Add" onClick="return addData(\"Person\");" /></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"FeatPer\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"FeatPer\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}
		
		else if (request.getParameter("Type").equals("Coll")) {
			%><tr><td class="heading" colspan="2">Collectors</td></tr>
			<tr><td colspan="2">Select a person/company from the drop-down list.  New collectors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple collectors by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Person/Company</td><td><%
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading" colspan="2">Add to Person List</td></tr>
			<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
			<input type="submit" value="Add" onClick="return addData(\"Person\");" /></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"Coll\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"Coll\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("Adoptor")) {
			%><tr><td class="heading" colspan="2">Adoptor</td></tr>
			<tr><td colspan="2">Select a person/company from the drop-down list.  New adoptors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple adoptors by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Person</td><td><%
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading" colspan="2">Add to Person List</td></tr>
			<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
			<input type="submit" value="Add" onClick="return addData(\"Person\");" /></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"Adoptor\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"Adoptor\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("Identifier")) {
			%><tr><td class="heading" colspan="2">Identifier</td></tr>
			<tr><td colspan="2">Select a person/company from the drop-down list.  New identifiers can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple identifiers by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Person</td><td><%
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading" colspan="2">Add to Person List</td></tr>
			<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
			<input type="submit" value="Add" onClick="return addData(\"Person\");" /></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"Identifier\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"Identifier\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("StratName")) {
			%><tr><td class="heading" colspan="2">Stratigraphic Name</td></tr>
			<tr><td colspan="2">Please enter a stratigraphic unit name in the text box.  You can select a unit from the NZ StratLex drop-down box if appropriate.<br />If you don't know the name of the unit tick the "unknown" box (please use sparingly)</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">NZ StratLex</td><td><%
			cd = new ComboDescriptor("SL.Strat_Unit", "SU_Name", "SU_Name");
			cd.name = "StratLex";
			cd.prompt = "-- Choose --";
			cd.tagParams = "onChange='form1.StratName.value = parseDropDown(StratLex.value);'";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Stratigraphic Name</td><td><input type="text" name="StratName" size="40" />
			&nbsp;&nbsp;<input type="checkbox" name="Unk" />&nbsp;unknown
			</td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"StratName\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"StratName\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("SentTo")) {
			%><tr><td class="heading" colspan="2">Sent To</td></tr>
			<tr><td colspan="2">Please select a fossil group and then one or both of a person and lab.<br />You may add enter multiple rows by clicking the Add To Main Form icon between each row and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Fossil Group</td><td><%
			cd = new ComboDescriptor("fossil_group", "name", "name");
			cd.name = "Group";
			cd.prompt = "-- Choose --";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Person</td><td><%
			cd = new ComboDescriptor("Person_View", "Name", "Name");
			cd.name = "Person";
			cd.prompt = "-- Choose --";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Lab</td><td><%
			cd = new ComboDescriptor("SC.Lab", "Lab_Name", "Lab_Name");
			cd.name = "Lab";
			cd.prompt = "-- Choose --";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%><tr><td class="heading">Comments</td><td><textarea name="Comm" rows="3" cols="40"></textarea></td></tr>
			<tr><td class="heading" colspan="2">Add to Person List</td></tr>
			<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
			<input type="submit" value="Add" onClick="return addData(\"Person\");" /></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"SentTo\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"SentTo\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("PrevSamp")) {
			%><tr><td class="heading" colspan="2">Previous Samples Nearby</td></tr>
			<tr><td colspan="2">Please select a masterfile area from the drop-down list.  The Sample list will then be populated with all submitted samples plus working samples which you have access to in that masterfile area.<br />You may add multiple samples by clicking the Add To Main Form icon between each sample and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Masterfile Area</td><td><%
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
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Submitted Samples</td><td><%
			cd = new ComboDescriptor("feature_view", "sample_name", "sample_name");
			cd.name = "SubFeat";
			cd.prompt = "-- Choose --";
			cd.selectDistinct = true;
			cd.join = "masterfile_id = " + mfID + " AND feature_status = 'approved'";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Samples in Folders</td><td><%
			cd = new ComboDescriptor("feature_view fv, folder_view fd", "fv.sample_name", "fv.sample_name");
			cd.name = "WorkFeat";
			cd.prompt = "-- Choose --";
			cd.selectDistinct = true;
			cd.join = "fv.feature_working_folder_id = fd.folder_id AND fv.feature_status <> 'approved' AND fd.user_id = " + user.getPersonId() + " AND fd.folder_type IN (2, 3)";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"PrevSamp\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"PrevSamp\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("SampRel")) {
			%><tr><td class="heading" colspan="2">Sample Relationships</td></tr>
			<tr><td colspan="2">Please select a masterfile area from the drop-down list.  The Sample list will then be populated with all submitted samples plus working samples which you have access to in that masterfile area.  Then select a relationship type enter an optional distance (in metres).<br />You may add multiple samples by clicking the Add To Main Form icon between each sample and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Distance</td>
			<td class="heading"><select name="DistMod"><option value="-" selected></option><option value="c. ">c.</option><option value="? ">?</option></select>&nbsp;&nbsp;
			<input type="text" name="Distance" />&nbsp;m&nbsp;-&nbsp;
			<input type="text" name="DistRange" />&nbsp;m</td></tr>
			<tr><td class="heading">Relationship</td><td><%
			cd = new ComboDescriptor("relationship_type", "name", "name");
			cd.name = "Rel";
			cd.prompt = "-- Choose --";
			cd.orderBy = "reltype_id";
			cd.join = "relation_type = 'Sample' AND name != 'nearby'";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Masterfile Area</td><td><%
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
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Submitted Samples</td><td><%
			cd = new ComboDescriptor("feature_view", "sample_name", "sample_name");
			cd.name = "SubFeat";
			cd.prompt = "-- Choose --";
			cd.selectDistinct = true;
			cd.join = "masterfile_id = " + mfID + " AND feature_status = 'approved'";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Samples in Folders</td><td><%
			cd = new ComboDescriptor("feature_view fv, folder_view fd", "fv.sample_name", "fv.sample_name");
			cd.name = "WorkFeat";
			cd.prompt = "-- Choose --";
			cd.selectDistinct = true;
			cd.join = "fv.feature_working_folder_id = fd.folder_id AND fv.feature_status <> 'approved' AND fd.user_id = " + user.getPersonId() + " AND fd.folder_type IN (2, 3)";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"SampRel\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"SampRel\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("StratRel")) {
			%><tr><td class="heading" colspan="2">Stratigraphic Relationships</td></tr>
			<tr><td colspan="2">Please select a relationship type and enter an optional distance or range of distances (in metres).  Enter a stratigraphic unit name in the text box.  You can select a unit from the NZ StratLex drop-down box if appropriate.<br />You may add multiple units by clicking the Add To Main Form icon between each unit and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Distance</td>
			<td class="heading"><select name="DistMod"><option value="-" selected></option><option value="c. ">c.</option><option value="? ">?</option></select>&nbsp;&nbsp;
			<input type="text" name="Distance" />&nbsp;m&nbsp;-&nbsp;
			<input type="text" name="DistRange" />&nbsp;m</td></tr>
			<tr><td class="heading">Relationship</td><td><%
			cd = new ComboDescriptor("relationship_type", "name", "name");
			cd.name = "Rel";
			cd.prompt = "-- Choose --";
			cd.orderBy = "reltype_id";
			cd.join = "relation_type = 'Stratigraphic'";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%><tr><td class="heading">NZ StratLex</td><td><%
			cd = new ComboDescriptor("SL.Strat_Unit", "SU_Name", "SU_Name");
			cd.name = "StratLex";
			cd.prompt = "-- Choose --";
			cd.tagParams = "onChange='form1.StratName.value = parseDropDown(StratLex.value);'";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Stratigraphic Name</td><td><input type="text" name="StratName" size="40" /></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"StratRel\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"StratRel\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("SedFeat")) {
			%><tr><td class="heading" colspan="2">Additional Features</td></tr>
			<tr><td colspan="2">Please select a feature from the list.  Check the Abundant box to indicate the feature is abundant.<br />You may add multiple features by clicking the Add To Main Form icon between each feature and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Feature</td><td><%
			cd = new ComboDescriptor("sedimentary_feature_type", "Name", "Code || ': ' || Name");
			cd.name = "Feat";
			cd.prompt = "-- Choose --";
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Abundant</td><td><input type="checkbox" name="Abund" /></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"SedFeat\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"SedFeat\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}

		else if (request.getParameter("Type").equals("Taxa")) {
			String groupID = "0";
			%><tr><td class="heading" colspan="2">Taxonomic Details</td></tr>
			<tr><td colspan="2">Please select a Taxonomic Group from the drop-down list.  The Taxonomic Name List will then be filled with appropriate taxa.  Either choose from this list or enter a new name in the Taxonomic Name and Author (optional) boxes.<br />Note: new taxonomic names will be entered into the database as provisional and will be assesed by members of the taxonomic panel.  You will not be able to submit your record until the name has been approved.<br />You may add multiple taxa by clicking the Add To Main Form icon between each taxa and then Close to end.</td></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td class="heading">Group</td><td><%
			cd = new ComboDescriptor("taxonomic_group", "group_id", "name");
			cd.name = "Group";
			cd.prompt = "-- Choose --";
			cd.orderBy = "group_ID";
			cd.tagParams = "onChange='form1.submit();'";
			if (request.getParameter("Group") != null) {
				groupID = request.getParameter("Group");
				cd.selected = groupID;
			}
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Taxonomic&nbsp;Name&nbsp;List</td><td><%
			cd = new ComboDescriptor("Taxonomic_Lookup", "Taxa_id", "Taxonomic_Name");
			cd.name = "TaxaList";
			cd.prompt = "-- Choose --";
			cd.orderBy = "UPPER(taxonomic_name)";
			cd.tagParams = "onChange='form1.TaxaName.value = parseDropDown(TaxaList.options[TaxaList.selectedIndex].text);'";
			cd.join = "group_id = " + groupID + " AND status IN ('approved', 'provisional') AND taxonomic_name IS NOT NULL";
			FREDUtil.makeDropBox(new java.io.PrintWriter(out), cd);
			%></td></tr>
			<tr><td class="heading">Taxonomic Name</td><td><input type="text" name="TaxaName" size="40" /></td></tr>
			<tr><td class="heading">Author</td><td><input type="text" name="Author" size="40" /></td></tr>
			<tr><td class="heading">Specimen Count</td><td><input type="text" name="SpecCount" size="40" /></td></tr>
			<tr><td class="heading">Specimen Coordinates</td><td><input type="text" name="SpecCoord" size="40" /></td></tr>
			<tr><td class="heading">Comments</td><td><textarea name="Comm" cols="40" rows="3"></textarea></td></tr>
			</table>
			<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
			<tr><td><a href="#" onClick="saveData(\"Taxa\");return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData(\"Taxa\");return false;" class="heading">Add to Main Form</a></td></tr><%
		}
	}

	%><tr><td><a href="javascript: window.close();"><img src="images/close.gif" height="20" width="20" border="0" alt="Close" /></a>&nbsp;&nbsp;</td><td><a href="javascript: window.close();" class="heading">Close</a></td></tr>
	</table></form>

	</td></tr></table><%
	drawBottom(out, et);

%>
