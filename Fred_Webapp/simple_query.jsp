<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Person"
%><%@page import="nz.cri.gns.fred.model.Age"
%><%@page import="nz.cri.gns.fred.model.TaxonomicGroup"
%><%@page import="nz.cri.gns.fred.util.PersonUtil"
%><%@page import="nz.cri.gns.fred.util.TaxonomicUtil"
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
		var aQuery = "";
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

			if (TaxonomicGroup.value != "-") {
				whereSQL = whereSQL + "pal.taxonomicGroup.name = '" + TaxonomicGroup.value + "' AND ";
				queryString = queryString + "Taxonomic Group = " + TaxonomicGroup.value + " AND ";
				palListFlag = true;
			}
			if (Taxon.value.length > 0) {
				whereSQL = whereSQL + "UPPER(pal.taxon.taxonomicName) LIKE '%" + replaceSingleQuote(Taxon.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Taxonomic Name = " + Taxon.value + " AND ";
				palListFlag = true;
			}

			
			//check only stage name or numeric values entered
			if (StageFrom.value != "-" && AgeFrom.value.length > 0) {
				alert("Please select only a stage name or a numeric age");
				StageFrom.focus();
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
				whereSQL = whereSQL + "(sampleStageView.baseAge > " + aStop + " AND sampleStageView.topAge < " + aStart + ") AND ";
				tableName = tableName + " JOIN s.sampleStageViews AS sampleStageView";
				queryString = queryString + "Age= " + aQuery + " AND ";
			}

			if (palListFlag) {
				whereSQL = whereSQL + "record.audit.status = 'approved' AND ";
				tableName = tableName + " JOIN s.records AS record JOIN record.paleontology.listEntries AS pal";
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
	<tr class="lightColour"><td class="heading">NZMS260 Sheet&nbsp;&nbsp;</td><td><input type="text" name="Map" size="10" />&nbsp;&nbsp;</td><td><i>Enter a <a href="http://www.linz.govt.nz/topography/topo-maps/nz-med-scale-maps/index.aspx" target="_blank">NZ 1:50,000 map</a> sheet</i></td></tr>
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
	&nbsp;&nbsp;</td><td><i>Select a <a href="http://www.gns.cri.nz/research/qmap/aboutqmap.html" target="_blank">QMap</a> sheet</i></td></tr>
	<tr class="lightColour"><td class="heading">Collector&nbsp;&nbsp;</td><td><input type="text" name="Coll" size="30" />&nbsp;&nbsp;</td><td><i>Enter part of a collectors name</i></td></tr>
	<tr class="lightColour"><td class="heading">Collection Year&nbsp;&nbsp;</td><td><input type="text" name="YearFrom" size="10" />&nbsp;<b>to</b>&nbsp;<input type="text" name="YearTo" size="10" />&nbsp;&nbsp;</td><td><i>Enter a single year or a range</i></td></tr>
	<tr class="lightColour"><td class="heading">Field Number/Drillhole Name&nbsp;&nbsp;</td><td><input type="text" name="FieldNum" size="30" />&nbsp;&nbsp;<td><i>Enter part of a field number or drillhole name</i></td></tr>
	<tr class="lightColour"><td class="heading">Stratigraphic Name&nbsp;&nbsp;</td><td><input type="text" name="StratName" size="30" />&nbsp;&nbsp;<td><i>Enter part of a stratigraphic name</i></td></tr>
	<tr class="lightColour"><td class="heading">Stratal Attitude&nbsp;&nbsp;</td><td><input type="checkbox" name="StratAtt" />&nbsp;&nbsp;</td><td><i>Tick for presence of dip/strike</i></td></tr>
	<tr class="lightColour"><td class="heading">Nature of Rock Unit&nbsp;&nbsp;</td><td><input type="text" name="RockNat" size="30" />&nbsp;&nbsp;</td><td><i>Enter part of rock unit description</i></td></tr>
	<tr class="lightColour"><td class="heading">Deposition Environment&nbsp;&nbsp;</td><td><input type="text" name="DepEnv" size="30" />&nbsp;&nbsp;</td><td><i>Enter part of deposition environment description</i></td></tr>
	<tr class="lightColour"><td class="heading">Age (by Stage)&nbsp;&nbsp;</td><td><%
	SelectBox<Age> ageSelectBox = new SelectBox<Age>(new StageUtil(factory).getAges());
	Attributes attributes = Attributes.createNameOnlyAttributes("StageFrom");
	ageSelectBox.writeBox(attributes, "-- All --", null, (Age)null, new PrintWriter(out));
	%>&nbsp;<b>to</b>&nbsp;<%
	attributes = Attributes.createNameOnlyAttributes("StageTo");
	ageSelectBox.writeBox(attributes, "-- All --", null, (Age)null, new PrintWriter(out));
	%>&nbsp;&nbsp;</td><td><i>Select a <a href="age.jsp" target=_blank">NZ stage name</a> (or range). Sample, adopted and paleontological ages will be searched</i></td></tr>
	<tr class="lightColour"><td class="heading">Age (numeric)&nbsp;&nbsp;</td><td><input type="text" name="AgeFrom" size="10" />&nbsp;<b>to</b>&nbsp;<input type="text" name="AgeTo" size="10" />&nbsp;&nbsp;</td><td><i>Enter a numeric age (or range). Sample, adopted and paleontological ages will be searched</i></td></tr>
	<tr class="lightColour"><td class="heading">Taxonomic Group&nbsp;&nbsp;</td><td><%
	SelectBox<TaxonomicGroup> tGroupSelectBox = new SelectBox<TaxonomicGroup>(new TaxonomicUtil(factory).getTaxonomicGroups());
	attributes = Attributes.createNameOnlyAttributes("TaxonomicGroup");
	tGroupSelectBox.setNameNameFlag(true);
	tGroupSelectBox.writeBox(attributes, "-- All --", null, (TaxonomicGroup)null, new PrintWriter(out));
	%>&nbsp;&nbsp;</td><td><i>Select a taxonomic group</i></td></tr>
	<tr class="lightColour"><td class="heading">Taxonomic Name&nbsp;&nbsp;</td><td><input type="text" name="Taxon" size="30" />&nbsp;&nbsp;</td><td><i>Enter part of a taxonomic name</i></td></tr>
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