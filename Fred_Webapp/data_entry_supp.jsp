<%@page	extends="nz.cri.gns.fred.FREDDEIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.fred.model.FossilGroup"
%><%@page import="nz.cri.gns.fred.model.FrNumber"
%><%@page import="nz.cri.gns.fred.model.Lab"
%><%@page import="nz.cri.gns.fred.model.Person"
%><%@page import="nz.cri.gns.fred.model.RelationshipType"
%><%@page import="nz.cri.gns.fred.model.SedimentaryFeatureType"
%><%@page import="nz.cri.gns.fred.model.StratigraphicUnit"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.model.Taxon"
%><%@page import="nz.cri.gns.fred.model.UserFolder"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FolderUtil"
%><%@page import="nz.cri.gns.fred.util.PersonUtil"
%><%@page import="nz.cri.gns.fred.util.SampleUtil"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
%><%@page import="nz.cri.gns.core.SimpleNameableAndIdentifiable"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="java.io.PrintWriter"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.User"
%><%@page import="nz.cri.gns.jsp.IconnedLink"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%@page import="java.util.Iterator"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Data Entry Helper";
	}
%><%
	User user = (User) getUser(session);
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	FeatureUtil featureUtil = new FeatureUtil(factory);
	FolderUtil folderUtil = new FolderUtil(factory);
	PersonUtil personUtil = new PersonUtil(factory);
	SampleUtil sampleUtil = new SampleUtil(factory);
	TaxonomicUtil taxaUtil = new TaxonomicUtil(factory);

	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLogin(false);
	et.setButtons(new IconnedLink[0]);

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
	
	<table width="550" style="width:550px;" border="0">
	<tr><td>
	
	<form name="form1" method="post" action="data_entry_supp.jsp">
	<table border="0" cellspacing="3" cellpadding="0"><%

	if (request.getParameter("Type") != null) {
		%><input type="hidden" name="Type" value="<%=request.getParameter("Type")%>" />
		<input type="hidden" name="Add" value="" /><%

		if (request.getParameter("Add") != null) {  //add data to lookup tables
			if (request.getParameter("Add").equals("Person")) {
				Person person = personUtil.findOrCreatePerson(request.getParameter("PersonName"));
				%><script language="JavaScript">alert("<%=person.getName()%> added to list.  Please now select from drop-down list to add to form");</script><%
				try {
					HibernateUtil.get().getDAOFactory().closeSession();
				} catch (Exception e) {
				}
			}
		}
		try {
			if (request.getParameter("Type").equals("OpComp")) {
				%><tr><td class="heading" colspan="2">Operating Company</td></tr>
				<tr><td colspan="2">Select a person/company from the drop-down list.  New companies can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Person/Company</td><td><%
				SelectBox<Person> selectBox = new SelectBox<Person>(personUtil.getPeople());
				selectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Person");
				selectBox.writeBox(attributes, "-- Choose --", null, (Person)null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading" colspan="2">Add to Person List</td></tr>
				<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
				<input type="submit" value="Add" onClick="return addData('Person');" /></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('FeatPer');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('FeatPer');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("VertPerson")) {
				%><tr><td class="heading" colspan="2">Section Collector</td></tr>
				<tr><td colspan="2">Select a person/company from the drop-down list.  New collectors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Person/Company</td><td><%
				SelectBox<Person> selectBox = new SelectBox<Person>(personUtil.getPeople());
				selectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Person");
				selectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading" colspan="2">Add to Person List</td></tr>
				<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
				<input type="submit" value="Add" onClick="return addData('Person');" /></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('FeatPer');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('FeatPer');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
			
			else if (request.getParameter("Type").equals("Coll")) {
				%><tr><td class="heading" colspan="2">Collectors</td></tr>
				<tr><td colspan="2">Select a person/company from the drop-down list.  New collectors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple collectors by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Person/Company</td><td><%
				SelectBox<Person> selectBox = new SelectBox<Person>(personUtil.getPeople());
				selectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Person");
				selectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading" colspan="2">Add to Person List</td></tr>
				<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
				<input type="submit" value="Add" onClick="return addData('Person');" /></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('Coll');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('Coll');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("Adoptor")) {
				%><tr><td class="heading" colspan="2">Adoptor</td></tr>
				<tr><td colspan="2">Select a person/company from the drop-down list.  New adoptors can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple adoptors by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Person</td><td><%
				SelectBox<Person> selectBox = new SelectBox<Person>(personUtil.getPeople());
				selectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Person");
				selectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading" colspan="2">Add to Person List</td></tr>
				<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
				<input type="submit" value="Add" onClick="return addData('Person');" /></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('Adoptor');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('Adoptor');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("Identifier")) {
				%><tr><td class="heading" colspan="2">Identifier</td></tr>
				<tr><td colspan="2">Select a person/company from the drop-down list.  New identifiers can be added to the list by filling out the First and Surnames (or Company name) and pressing the Add button<br />You may add multiple identifiers by clicking the Add To Main Form icon between each collector and then Close to end.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Person</td><td><%
				SelectBox<Person> selectBox = new SelectBox<Person>(personUtil.getPeople());
				selectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Person");
				selectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading" colspan="2">Add to Person List</td></tr>
				<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
				<input type="submit" value="Add" onClick="return addData('Person');" /></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('Identifier');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('Identifier');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("StratName")) {
				%><tr><td class="heading" colspan="2">Stratigraphic Name</td></tr>
				<tr><td colspan="2">Please enter a stratigraphic unit name in the text box.  You can select a unit from the NZ StratLex drop-down box if appropriate.<br />If you don't know the name of the unit tick the "unknown" box (please use sparingly)</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">NZ StratLex</td><td><%
				SelectBox<StratigraphicUnit> selectBox = new SelectBox<StratigraphicUnit>(sampleUtil.getStratigraphicUnits());
				selectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("StratLex");
				attributes.setAttribute("onChange", "form1.StratName.value = parseDropDown(StratLex.value);");
				selectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading">Stratigraphic Name</td><td><input type="text" name="StratName" size="40" />
				&nbsp;&nbsp;<input type="checkbox" name="Unk" />&nbsp;unknown
				</td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('StratName');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('StratName');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("SentTo")) {
				%><tr><td class="heading" colspan="2">Sent To</td></tr>
				<tr><td colspan="2">Please select a fossil group and then one or both of a person and lab.<br />You may add enter multiple rows by clicking the Add To Main Form icon between each row and then Close to end.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Fossil Group</td><td><%
				SelectBox<FossilGroup> fgSelectBox = new SelectBox<FossilGroup>(sampleUtil.getFossilGroups());
				fgSelectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Group");
				fgSelectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading">Person</td><td><%
				SelectBox<Person> pSelectBox = new SelectBox<Person>(personUtil.getPeople());
				pSelectBox.setNameNameFlag(true);
				attributes = Attributes.createNameOnlyAttributes("Person");
				pSelectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading">Lab</td><td><%
				SelectBox<Lab> lSelectBox = new SelectBox<Lab>(sampleUtil.getLabs());
				lSelectBox.setNameNameFlag(true);
				attributes = Attributes.createNameOnlyAttributes("Lab");
				lSelectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%><tr><td class="heading">Comments</td><td><textarea name="Comm" rows="3" cols="40"></textarea></td></tr>
				<tr><td class="heading" colspan="2">Add to Person List</td></tr>
				<tr><td class="smallheading">Name</td><td><input type="text" name="PersonName" />&nbsp;&nbsp;
				<input type="submit" value="Add" onClick="return addData('Person');" /></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('SentTo');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('SentTo');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("PrevSamp")) {
				%><tr><td class="heading" colspan="2">Previous Samples Nearby</td></tr>
				<tr><td colspan="2">Please either select a locality in your folders or select a map sheet from the drop-down list - and then select a locality.<br />You may add multiple samples by clicking the Add To Main Form icon between each sample and then Close to end.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Localities in Folders</td><td>
				<select name="WorkFeat">
				<option value="-">-- Choose --</option><%
				for (Iterator i = folderUtil.getPersonalPlusBacklogFolders(user).iterator(); i.hasNext();) {
					UserFolder folder = (UserFolder) i.next();
					String foldName = folder.getFolderName();
					Feature[] features = featureUtil.getFeaturesInFolder(folder);
					for (int j = 0; j < features.length; j++) {
						String featName = FeatureUtil.getFeatureIdentifyingName(features[j]);
						%><option value="<%=featName%>"><%=foldName + ": " + featName%></option><%
					}
				}
				%></select>
				</td></tr>
				<tr><td class="heading">Map Sheet</td><td><%
				SelectBox<SimpleNameableAndIdentifiable> selectBox = new SelectBox<SimpleNameableAndIdentifiable>(featureUtil.getFrMapSheets());
				Attributes attributes = Attributes.createNameOnlyAttributes("MapSheet");
				attributes.setAttribute("onChange", "form1.submit();");
				selectBox.writeBox(attributes, "-- Choose --", null, (request.getParameter("MapSheet") != null  && !request.getParameter("MapSheet").equals("-")) ? new SimpleNameableAndIdentifiable(request.getParameter("MapSheet"), request.getParameter("MapSheet")) : null, new PrintWriter(out));
				if (request.getParameter("MapSheet") != null  && !request.getParameter("MapSheet").equals("-")) {
					%>&nbsp;&nbsp;
					<select name="SubFeat">
					<option value="-">-- Choose --</option><%
					for (Iterator i = featureUtil.getFrNumbers(request.getParameter("MapSheet")).iterator(); i.hasNext();) {
						FrNumber frNumber = (FrNumber) i.next();
						%><option value="<%=frNumber.getFrNumber()%>"><%=frNumber.getFrNumber()%></option><%
					}
					%></select><%
				} else {
					%><input type="hidden" name="SubFeat" value="-" /><%
				}
				%></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('PrevSamp');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('PrevSamp');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("SampRel")) {
				%><tr><td class="heading" colspan="2">Sample Relationships</td></tr>
				<tr><td colspan="2">Please select an optional distance (in metres) and a relationship type. Then either select a locality in your folders or select a map sheet from the drop-down list - and then select a locality.<br />You may add multiple localities by clicking the Add To Main Form icon between each sample and then Close to end.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Distance</td>
				<td><select name="DistMod">
					<option value="-"<%=((request.getParameter("DistMod") == null || request.getParameter("DistMod").equals("-")) ?  " selected" : "")%>></option>
					<option value="c. "<%=((request.getParameter("DistMod") != null && request.getParameter("DistMod").equals("c. ")) ?  " selected" : "")%>>c.</option>
					<option value="? "<%=((request.getParameter("DistMod") != null && request.getParameter("DistMod").equals("? ")) ?  " selected" : "")%>>?</option>
				</select>&nbsp;&nbsp;
				<input type="text" name="Distance" value="<%=DBUtils.nvl(request.getParameter("Distance"))%>" />&nbsp;m&nbsp;-&nbsp;
				<input type="text" name="DistRange" value="<%=DBUtils.nvl(request.getParameter("DistRange"))%>" />&nbsp;m</td></tr>
				<tr><td class="heading">Relationship</td><td><%
				SelectBox<RelationshipType> rSelectBox = new SelectBox<RelationshipType>(sampleUtil.getRelationshipTypes("Sample"));
				rSelectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Rel");
				rSelectBox.writeBox(attributes, "-- Choose --", null, (request.getParameter("Rel") != null && !request.getParameter("Rel").equals("-")) ? sampleUtil.findRelationshipType(request.getParameter("Rel")) : null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading">Localities in Folders</td><td>
				<select name="WorkFeat">
				<option value="-">-- Choose --</option><%
				for (Iterator i = folderUtil.getPersonalPlusBacklogFolders(user).iterator(); i.hasNext();) {
					UserFolder folder = (UserFolder) i.next();
					String foldName = folder.getFolderName();
					Feature[] features = featureUtil.getFeaturesInFolder(folder);
					for (int j = 0; j < features.length; j++) {
						String featName = FeatureUtil.getFeatureIdentifyingName(features[j]);
						%><option value="<%=featName%>"><%=foldName + ": " + featName%></option><%
					}
				}
				%></select>
				</td></tr>
				<tr><td class="heading">Map Sheet</td><td><%
				SelectBox<SimpleNameableAndIdentifiable> mSelectBox = new SelectBox<SimpleNameableAndIdentifiable>(featureUtil.getFrMapSheets());
				attributes = Attributes.createNameOnlyAttributes("MapSheet");
				attributes.setAttribute("onChange", "form1.submit();");
				mSelectBox.writeBox(attributes, "-- Choose --", null, (request.getParameter("MapSheet") != null  && !request.getParameter("MapSheet").equals("-")) ? new SimpleNameableAndIdentifiable(request.getParameter("MapSheet"), request.getParameter("MapSheet")) : null, new PrintWriter(out));
				if (request.getParameter("MapSheet") != null  && !request.getParameter("MapSheet").equals("-")) {
					%>&nbsp;&nbsp;
					<select name="SubFeat">
					<option value="-">-- Choose --</option><%
					for (Iterator i = featureUtil.getFrNumbers(request.getParameter("MapSheet")).iterator(); i.hasNext();) {
						FrNumber frNumber = (FrNumber) i.next();
						%><option value="<%=frNumber.getFrNumber()%>"><%=frNumber.getFrNumber()%></option><%
					}
					%></select><%
				} else {
					%><input type="hidden" name="SubFeat" value="-" /><%
				}
				%></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('SampRel');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('SampRel');return false;" class="heading">Add to Main Form</a></td></tr><%
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
				SelectBox<RelationshipType> rSelectBox = new SelectBox<RelationshipType>(sampleUtil.getRelationshipTypes("Stratigraphic"));
				rSelectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Rel");
				rSelectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%><tr><td class="heading">NZ StratLex</td><td><%
				SelectBox<StratigraphicUnit> slSelectBox = new SelectBox<StratigraphicUnit>(sampleUtil.getStratigraphicUnits());
				slSelectBox.setNameNameFlag(true);
				attributes = Attributes.createNameOnlyAttributes("StratLex");
				attributes.setAttribute("onChange", "form1.StratName.value = parseDropDown(StratLex.value);");
				slSelectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading">Stratigraphic Name</td><td><input type="text" name="StratName" size="40" /></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('StratRel');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('StratRel');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("SedFeat")) {
				%><tr><td class="heading" colspan="2">Additional Features</td></tr>
				<tr><td colspan="2">Please select a feature from the list.  Check the Abundant box to indicate the feature is abundant.<br />You may add multiple features by clicking the Add To Main Form icon between each feature and then Close to end.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Feature</td><td><%
				SelectBox<SedimentaryFeatureType> selectBox = new SelectBox<SedimentaryFeatureType>(sampleUtil.getSedimentaryFeatureTypes());
				selectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Feat");
				selectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
				%></td></tr>
				<tr><td class="heading">Abundant</td><td><input type="checkbox" name="Abund" /></td></tr>
				</table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('SedFeat');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('SedFeat');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
	
			else if (request.getParameter("Type").equals("Taxa")) {
				String groupID = "0";
				%><tr><td class="heading" colspan="2">Taxonomic Details</td></tr>
				<tr><td colspan="2">Please select a Taxonomic Group from the drop-down list.  The Taxonomic Name List will then be filled with appropriate taxa.  Either choose from this list or enter a new name in the Taxonomic Name and Author (optional) boxes.<br />Note: new taxonomic names will be entered into the database as provisional and will be assesed by members of the taxonomic panel.  You will not be able to submit your record until the name has been approved.<br />You may add multiple taxa by clicking the Add To Main Form icon between each taxa and then Close to end.</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td class="heading">Group</td><td><%
				SelectBox<TaxonomicGroup> tgSelectBox = new SelectBox<TaxonomicGroup>(taxaUtil.getTaxonomicGroups());
				tgSelectBox.setNameNameFlag(true);
				Attributes attributes = Attributes.createNameOnlyAttributes("Group");
				attributes.setAttribute("onChange", "form1.submit();");
				tgSelectBox.writeBox(attributes, "-- Choose --", null, (request.getParameter("Group") != null  && !request.getParameter("Group").equals("-")) ? taxaUtil.getTaxonomicGroup(request.getParameter("Group")) : null, new PrintWriter(out));
				%></td></tr><%
				if (request.getParameter("Group") != null  && !request.getParameter("Group").equals("-")) {
					%><tr><td class="heading">Taxonomic&nbsp;Name&nbsp;List</td><td><%
					SelectBox<Taxon> tSelectBox = new SelectBox<Taxon>(taxaUtil.getAppProvTaxa(request.getParameter("Group")));
					attributes = Attributes.createNameOnlyAttributes("TaxaList");
					attributes.setAttribute("onChange", "form1.TaxaName.value = parseDropDown(TaxaList.options[TaxaList.selectedIndex].text);");
					tSelectBox.writeBox(attributes, "-- Choose --", null, null, new PrintWriter(out));
					%></td></tr>
					<tr><td class="heading">Taxonomic Name</td><td><input type="text" name="TaxaName" size="40" /></td></tr>
					<tr><td class="heading">Author</td><td><input type="text" name="Author" size="40" /></td></tr>
					<tr><td class="heading">Specimen Count</td><td><input type="text" name="SpecCount" size="40" /></td></tr>
					<tr><td class="heading">Specimen Coordinates</td><td><input type="text" name="SpecCoord" size="40" /></td></tr>
					<tr><td class="heading">Comments</td><td><textarea name="Comm" cols="40" rows="3"></textarea></td></tr><%
				}
				%></table>
				<table border="0" cellspacing="2" cellpadding="0">
				<tr><td><img src="images/blank.gif" width="1" height="5" /></td></tr>
				<tr><td><a href="#" onClick="saveData('Taxa');return false;" title="Add"><img src="images/put.gif" height="20" width="20" border="0" /></a>&nbsp;&nbsp;</td><td><a href="#" onClick="saveData('Taxa');return false;" class="heading">Add to Main Form</a></td></tr><%
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	%><tr><td><a href="javascript: window.close();"><img src="images/close.gif" height="20" width="20" border="0" alt="Close" /></a>&nbsp;&nbsp;</td><td><a href="javascript: window.close();" class="heading">Close</a></td></tr>
	</table></form>

	</td></tr></table><%
	drawBottom(out, et);

%>
