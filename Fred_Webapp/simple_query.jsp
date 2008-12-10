<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Person"
%><%@page import="nz.cri.gns.fred.model.Age"
%><%@page import="nz.cri.gns.fred.util.PersonUtil"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="java.io.PrintWriter"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Simple Query Form";
	}
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	
	drawTop(out, et, request, response);

	%><script language="JavaScript">
	function submitForm(form) {
		if (generateSQL(form))
			form.submit();
	}
	
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
		var queryString = "";
		var whereSQL = "s.audit.status = 'approved' AND s.feature.audit.status = 'approved' AND ";
		var tableName = "Sample AS s";
		var frNum;
		var aStart = "";
		var aStop = "";
		var aQuery;
		var recFlag = false;
		var palListFlag = false;
		with (form) {
			if (Map.value.length > 0) {
				whereSQL = whereSQL + "s.feature.siteView.nzmgSheet = '" + Map.value.toUpperCase() + "' AND ";
				queryString = queryString + "NZMG Sheet = " + Map.value.toUpperCase() + " AND ";
			}
			if (QMap.value != "-") {
				whereSQL = whereSQL + "s.feature.siteView.qmapSheet = '" + QMap.value + "' AND ";
				queryString = queryString + "QMAP Sheet = " + QMap.value + " AND ";
			}
			if (Coll.value.length > 0) {
				whereSQL = whereSQL + "UPPER(person.name) LIKE '%" + replaceSingleQuote(Coll.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Collector = " + Coll.value + " AND ";
				tableName = tableName + " JOIN s.collectors AS person";
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
					whereSQL = whereSQL + "s.collectionDate BETWEEN '01-JAN-" + YearFrom.value + "' AND '31-DEC-" + YearTo.value + "' AND ";
					queryString = queryString + "Collection Date BETWEEN " + YearFrom.value + " AND " + YearTo.value + " AND ";
				} else {
					whereSQL = whereSQL + "s.collectionDate BETWEEN '01-JAN-" + YearFrom.value + "' AND '31-DEC-" + YearFrom.value + "' AND ";
					queryString = queryString + "Collection Date = " + YearFrom.value + " AND ";
				}
			}
			if (FieldNum.value.length > 0) {
				whereSQL = whereSQL + "UPPER(s.feature.featureName) LIKE '%" + replaceSingleQuote(FieldNum.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Field Number = " + FieldNum.value + " AND ";
			}
			if (StratName.value.length > 0) {
				whereSQL = whereSQL + "UPPER(s.stratUnit) LIKE '%" + replaceSingleQuote(StratName.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Stratigraphic Name = " + StratName.value + " AND ";
			}
			if (StratAtt.checked) {
				whereSQL = whereSQL + "(s.dip IS NOT NULL OR s.dipDirection IS NOT NULL OR s.strike IS NOT NULL) AND ";
				queryString = queryString + "Stratal Attitude present AND ";
			}
			if (RockNat.value.length > 0) {
				whereSQL = whereSQL + "UPPER(s.rockNature) LIKE '%" + replaceSingleQuote(RockNat.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Nature of Rock Unit = " + RockNat.value + " AND ";
			}
			if (DepEnv.value.length > 0) {
				whereSQL = whereSQL + "UPPER(s.depositionEnv) LIKE '%" + replaceSingleQuote(DepEnv.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Deposition Environment = " + DepEnv.value + " AND ";
			}
			
			if (Taxon.value.length > 0) {
				whereSQL = whereSQL + "UPPER(pal.taxon.taxonomicName) LIKE '%" + replaceSingleQuote(Taxon.value.toUpperCase()) + "%' AND ";
				palListFlag = true;
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
					if (ageStart[StageTo.value] > ageStart[StageFrom.value] || ageStop[StageFrom.value] < ageStop[StageTo.value]) {
					  alert ("Stage Names are the wrong way around"); StageFrom.select();
					  return false;
					}
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
					if (isNaN(AgeFrom.value) || isNaN(AgeTo.value)) {
					  alert ("Non-numeric ages entered");
					  AgeFrom.select();
					  return false;
					}
					if (parseFloat(AgeFrom.value, 10) < parseFloat(AgeTo.value, 10)) {
					  alert ("Numeric ages wrong way around");
					  AgeFrom.select();
					  return false;
					}
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
						whereSQL = whereSQL + "(s.inferredStage.baseAge <= " + aStart + " AND s.inferredStage.topAge >= " + aStop + ") OR (s.knownStage.baseAge <= " + aStart + " AND s.knownStage.topAge >= " + aStop + ") OR ";
					} else {
						whereSQL = whereSQL + "(s.inferredStage.baseAge >= " + aStop + " AND s.inferredStage.topAge <= " + aStart + ") OR (s.knownStage.baseAge >= " + aStop + " AND s.knownStage.topAge <= " + aStart + ") OR ";
					}
					queryString = queryString + "Collectors/";
				}
				if (AdoAge.checked) {
					if (AgeType[0].checked) { //narrow search
						whereSQL = whereSQL + "(record.adoption.stage.baseAge <= " + aStart + " AND record.adoption.stage.topAge >= " + aStop + ") OR ";
					} else {
						whereSQL = whereSQL + "(record.adoption.stage.lowerAge.baseAge >= " + aStop + " AND record.adoption.stage.topAge <= " + aStart + ") OR ";
					}
					queryString = queryString + "Adopted/";
					recFlag = true;
				}
				if (PalAge.checked) {
					if (AgeType[0].checked) { //narrow search
						whereSQL = whereSQL + "(record.paleontology.stage.baseAge <= " + aStart + " AND record.paleontology.stage.topAge >= " + aStop + ") OR ";
					} else {
						whereSQL = whereSQL + "(record.paleontology.stage.baseAge >= " + aStop + " AND record.paleontology.stage.topAge <= " + aStart + ") OR ";
					}
					queryString = queryString + "Paleontology/";
					recFlag = true;
				}
				whereSQL = whereSQL.substring(0, whereSQL.length - 4) + ") AND ";
				queryString = queryString.substring(0, queryString.length - 1) + " Age = " + aQuery + " AND ";
			}
			
			
			if (recFlag) {
				whereSQL = whereSQL + "record.audit.status = 'approved' AND ";
				tableName = tableName + " JOIN s.records AS record";
			}
			if (palListFlag) {
				if (!recFlag) {
					whereSQL = whereSQL + "record.audit.status = 'approved' AND ";
					tableName = tableName + " JOIN s.records AS record";
				}
				tableName = tableName + " JOIN record.paleontology.listEntries AS pal";
			}

			if (whereSQL.length > 0)
			whereSQL = whereSQL.substring(0, whereSQL.length - 5);
			WhereSQL.value = whereSQL;
			if (queryString.length > 0)	{
				queryString = queryString.substring(0, queryString.length - 5);
			}
			QueryString.value = queryString;
			TableName.value = tableName;
		}
		return true;
	}
	
	</script><%

	//build array of stage ages
	int maxAgeId = new StageUtil(factory).getMaxAgeId();
	%><script language="JavaScript">
	var ageStart = new Array(<%=(maxAgeId + 1)%>);
	var ageStop = new Array(<%=(maxAgeId + 1)%>);<%
	for (Age age : new StageUtil(factory).getAges()) {
		%>ageStart[<%=age.getAgeId()%>] = <%=age.getBaseAge()%>;
		ageStop[<%=age.getAgeId()%>] = <%=age.getTopAge()%>;<%
	}
	%></script>
	
	<form name="QueryForm" method="post" action="result_list.jsp" onsubmit="return generateSQL(this)">
	<p><table border="0" cellpadding="3" cellspacing="2" width="600">
	<tr class="midColour"><th colspan="2">Sample Fields</th></tr>
	<tr class="lightColour"><td class="heading">NZMG Sheet&nbsp;&nbsp;</td><td><input type="text" name="Map" size="10" /></td></tr>
	<tr class="lightColour"><td class="heading">QMap Sheet&nbsp;&nbsp;</td><td>
	<select name="QMap">
		<option value="-">-- All --</option>
		<option value="Kaitaia">Kaitaia</option>
		<option value="Whangarei">Whangarei</option>
		<option value="Auckland">Auckland</option>
		<option value="Waikato">Waikato</option>
		<option value="Rotorua">Rotorua</option>
		<option value="Raukumara">Raukumara</option>
		<option value="Taranaki">Taranaki</option>
		<option value="Hawkes Bay">Hawkes Bay</option>
		<option value="Wellington">Wellington</option>
		<option value="Wairarapa">Wairarapa</option>
		<option value="Nelson">Nelson</option>
		<option value="Greymouth">Greymouth</option>
		<option value="Kaikoura">Kaikoura</option>
		<option value="Haast">Haast</option>
		<option value="Aoraki">Aoraki</option>
		<option value="Christchurch">Christchurch</option>
		<option value="Wakatipu">Wakatipu</option>
		<option value="Waitaki">Waitaki</option>
		<option value="Fiordland">Fiordland</option>
		<option value="Murihiku">Murihiku</option>
		<option value="Dunedin">Dunedin</option>
	</select>
	</td></tr>
	<tr class="lightColour"><td class="heading">Collector&nbsp;&nbsp;</td><td><input type="text" name="Coll" size="30" /></td></tr>
	<tr class="lightColour"><td class="heading">Collection Year&nbsp;&nbsp;</td><td><input type="text" name="YearFrom" size="10" />&nbsp;<b>to</b>&nbsp;<input type="text" name="YearTo" size="10" /></td></tr>
	<tr class="lightColour"><td class="heading">Field Number/Drillhole Name&nbsp;&nbsp;</td><td><input type="text" name="FieldNum" size="30" /></td></tr>
	<tr class="lightColour"><td class="heading">Stratigraphic Name&nbsp;&nbsp;</td><td><input type="text" name="StratName" size="30" /></td></tr>
	<tr class="lightColour"><td class="heading">Stratal Attitude&nbsp;&nbsp;</td><td><input type="checkbox" name="StratAtt" />&nbsp;Presence of dip/strike</td></tr>
	<tr class="lightColour"><td class="heading">Nature of Rock Unit&nbsp;&nbsp;</td><td><input type="text" name="RockNat" size="30" /></td></tr>
	<tr class="lightColour"><td class="heading">Deposition Environment&nbsp;&nbsp;</td><td><input type="text" name="DepEnv" size="30" /></td></tr>
	<tr><td>&nbsp;</td></tr>
	
	<tr class="midColour"><th colspan="2">Ages</th></tr>
	<tr class="lightColour"><td class="heading">Stage Range</td><td><%
	SelectBox<Age> ageSelectBox = new SelectBox<Age>(new StageUtil(factory).getAges());
	Attributes attributes = Attributes.createNameOnlyAttributes("StageFrom");
	ageSelectBox.writeBox(attributes, "-- All --", null, (Age)null, new PrintWriter(out));
	%>&nbsp;<b>to</b>&nbsp;<%
	attributes = Attributes.createNameOnlyAttributes("StageTo");
	ageSelectBox.writeBox(attributes, "-- All --", null, (Age)null, new PrintWriter(out));
	%></td></tr>
	<tr class="lightColour"><td class="heading">Numeric Range&nbsp;&nbsp;</td><td><input type="text" name="AgeFrom" size="10" />&nbsp;<b>to</b>&nbsp;<input type="text" name="AgeTo" size="10" /></td></tr>
	<tr class="lightColour"><td class="heading">Search Fields&nbsp;&nbsp;</td><td><input type="checkbox" name="StratAge" checked />&nbsp;Collectors Inferred/Known Age<br><input type="checkbox" name="AdoAge" checked />&nbsp;Adopted Age<br><input type="checkbox" name="PalAge" checked />&nbsp;Paleontology Age</td></tr>
	<tr class="lightColour"><td class="heading">Search Type&nbsp;&nbsp;</td><td><input type="radio" name="AgeType" value="Narrow" checked />&nbsp;Narrow Search<br /><input type="radio" name="AgeType" value="Wide" />&nbsp;Wide Search</td></tr>
	<tr><td>&nbsp;</td></tr>
	
	<tr class="midColour"><th colspan="2">Paleontology</th></tr>
	<tr class="lightColour"><td class="heading">Taxonomic Name&nbsp;&nbsp;</td><td><input type="text" name="Taxon" size="30" /></td></tr>
	
	</table></p>
	<input type="hidden" name="WhereSQL" value="" />
	<input type="hidden" name="QueryString" value="" />
	<input type="hidden" name="TableName" value="" />
	<p><input type="submit" value="Submit Query" /></p>
	</form><%
	
	drawBottom(out, et);
	try {
		FredHibernate.get().getDAOFactory().closeSession();
	} catch (Exception e) {
	}
%>