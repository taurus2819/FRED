<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Person"
%><%@page import="nz.cri.gns.fred.model.AgeView"
%><%@page import="nz.cri.gns.fred.util.PersonUtil"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
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
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
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
		var queryString = "", whereSQL = "fv.feature_status = 'approved' AND ", tableName = "feature_view fv", tableJoin = "", frNum, aStart = "", aStop = "", aQuery;
		var recFlag = false, sampFlag = false, adoFlag = false, palFlag = false, siteFlag = false;
		with (form) {
			if (FRNum.value.length > 0) {
				frNum = parseFRNum(trim(FRNum.value), "");
				if (frNum == "false") {
					alert("FR Number field incorrectly formatted");
					return false;
				}
				whereSQL = "((" + frNum + ") OR (" + parseFRNum(trim(FRNum.value), "yard_") + ") OR (" + parseFRNum(trim(FRNum.value), "sample_") + ") OR (" + parseFRNum(trim(FRNum.value), "s_yard_") + ")) AND ";
				queryString = queryString + "FR Number = " + trim(FRNum.value) + " AND ";
			}
			if (Map.value.length > 0) {
				whereSQL = whereSQL + "st.nzmg_sheet = '" + Map.value.toUpperCase() + "' AND ";
				queryString = queryString + "NZMG Sheet = " + Map.value.toUpperCase() + " AND ";
				siteFlag = true;
			}
			if (QMap.value != "-") {
				whereSQL = whereSQL + "st.qmap_sheet = '" + QMap.value + "' AND ";
				queryString = queryString + "QMAP Sheet = " + QMap.value + " AND ";
				siteFlag = true;
			}
			if (Coll.value != "-") {
				whereSQL = whereSQL + "c.person_id = " + Coll.value + " AND ";
				queryString = queryString + "Collector = " + Coll.options[Coll.options.selectedIndex].text + " AND ";
				tableName = tableName + ", collector c";
				tableJoin = tableJoin + "c.sample_id = s.sample_id AND ";
				sampFlag = true;
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
					whereSQL = whereSQL + "s.collection_date BETWEEN '01-JAN-" + YearFrom.value + "' AND '31-DEC-" + YearTo.value + "' AND ";
					queryString = queryString + "Collection Date BETWEEN " + YearFrom.value + " AND " + YearTo.value + " AND ";
				} else {
					whereSQL = whereSQL + "s.collection_date BETWEEN '01-JAN-" + YearFrom.value + "' AND '31-DEC-" + YearFrom.value + "' AND ";
					queryString = queryString + "Collection Date = " + YearFrom.value + " AND ";
				}
				sampFlag = true;
			}
			if (FieldNum.value.length > 0) {
				whereSQL = whereSQL + "UPPER(fv.feature_name) LIKE '%" + replaceSingleQuote(FieldNum.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Field Number = " + FieldNum.value + " AND ";
			}
			if (StratName.value.length > 0) {
				whereSQL = whereSQL + "UPPER(s.strat_unit) LIKE '%" + replaceSingleQuote(StratName.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Stratigraphic Name = " + StratName.value + " AND ";
				sampFlag = true;
			}
			if (StratAtt.checked) {
				whereSQL = whereSQL + "(s.dip IS NOT NULL OR s.dip_direction IS NOT NULL OR s.strike IS NOT NULL) AND ";
				queryString = queryString + "Stratal Attitude present AND ";
				sampFlag = true;
			}
			if (RockNat.value.length > 0) {
				whereSQL = whereSQL + "UPPER(s.rock_nature) LIKE '%" + replaceSingleQuote(RockNat.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Nature of Rock Unit = " + RockNat.value + " AND ";
				sampFlag = true;
			}
			if (DepEnv.value.length > 0) {
				whereSQL = whereSQL + "UPPER(s.deposition_env) LIKE '%" + replaceSingleQuote(DepEnv.value.toUpperCase()) + "%' AND ";
				queryString = queryString + "Deposition Environment = " + DepEnv.value + " AND ";
				sampFlag = true;
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
						whereSQL = whereSQL + "(stv1.age_start <= " + aStart + " AND stv1.age_stop >= " + aStop + ") OR (stv2.age_start <= " + aStart + " AND stv2.age_stop >= " + aStop + ") OR ";
					} else {
						whereSQL = whereSQL + "(stv1.age_start >= " + aStop + " AND stv1.age_stop <= " + aStart + ") OR (stv2.age_start >= " + aStop + " AND stv2.age_stop <= " + aStart + ") OR ";
					}
					queryString = queryString + "Collectors/";
					tableName = tableName + ", Stage_View stv1, Stage_View stv2";
					tableJoin = tableJoin + "s.inferred_stage_id = stv1.Stage_ID(+) AND s.known_stage_id = stv2.Stage_ID(+) AND ";
					sampFlag = true;
				}
				if (AdoAge.checked) {
					if (AgeType[0].checked) { //narrow search
						whereSQL = whereSQL + "(stv3.age_start <= " + aStart + " AND stv3.age_stop >= " + aStop + ") OR ";
					} else {
						whereSQL = whereSQL + "(stv3.age_start >= " + aStop + " AND stv3.age_stop <= " + aStart + ") OR ";
					}
					queryString = queryString + "Adopted/";
					tableName = tableName + ", Stage_View stv3";
					tableJoin = tableJoin + "a.adopted_stage_id = stv3.Stage_ID(+) AND ";
					adoFlag = true;
				}
				if (PalAge.checked) {
					if (AgeType[0].checked) { //narrow search
						whereSQL = whereSQL + "(stv4.age_start <= " + aStart + " AND stv4.age_stop >= " + aStop + ") OR ";
					} else {
						whereSQL = whereSQL + "(stv4.age_start >= " + aStop + " AND stv4.age_stop <= " + aStart + ") OR ";
					}
					queryString = queryString + "Paleontology/";
					tableName = tableName + ", Stage_View stv4";
					tableJoin = tableJoin + "p.stage_id = stv4.Stage_ID(+) AND ";
					palFlag = true;
				}
				whereSQL = whereSQL.substring(0, whereSQL.length - 4) + ") AND ";
				queryString = queryString.substring(0, queryString.length - 1) + " Age = " + aQuery + " AND ";
			}
			if (adoFlag) {
				tableName = tableName + ", adoption a";
				tableJoin = tableJoin + "a.Record_ID(+) = r.record_id AND ";
				recFlag = true;
			}
			if (palFlag) {
				tableName = tableName + ", paleontology p";
				tableJoin = tableJoin + "p.record_id(+) = r.Record_id AND ";
				recFlag = true;
			}
			if (recFlag) {
				tableName = tableName + ", record r";
				tableJoin = tableJoin + "r.sample_id = s.sample_id AND ";
				sampFlag = true;
			}
			if (sampFlag) {
				tableName = tableName + ", sample s";
				tableJoin = tableJoin + "s.feature_id = fv.feature_id AND ";
			}
			if (siteFlag) {
				tableName = tableName + ", sc.site_view st";
				tableJoin = tableJoin + "st.site_id = fv.site_id AND ";
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
		return "(fv." + prefix + "map_sheet = '" + sheet + "' AND " + serial + ")";
	}
	
	function parseSerialNum(serialNum, prefix) {
		var x, y;
		if (serialNum.indexOf("-") > 0) {
			x = trim(serialNum.substring(0, serialNum.indexOf("-")));
			y = trim(serialNum.substring(serialNum.indexOf("-") + 1, serialNum.length));
			if (isNaN(x) || isNaN(y)) { return "false"; }
			return "fv." + prefix + "serial_number BETWEEN " + parseInt(x, 10) + " AND " + parseInt(y, 10);
		} else if (isNaN(serialNum.substring(serialNum.length - 1, serialNum.length)) && !isNaN(serialNum.substring(0, serialNum.length - 1))) {
			return "fv." + prefix + "serial_number = " + parseInt(serialNum.substring(0, serialNum.length - 1), 10) + " AND fv." + prefix + "recollection_number = '" + serialNum.substring(serialNum.length - 1, serialNum.length).toUpperCase() + "'";
		} else if (isNaN(serialNum)) {
			return "false";
		}
		return "fv." + prefix + "serial_number = " + parseInt(serialNum, 10);
	}
	
	</script><%

	//build array of stage ages
	int maxAgeId = new StageUtil(factory).getMaxAgeId();
	%><script language="JavaScript">
	var ageStart = new Array(<%=(maxAgeId + 1)%>);
	var ageStop = new Array(<%=(maxAgeId + 1)%>);<%
	for (AgeView age : new StageUtil(factory).getAges()) {
		%>ageStart[<%=age.getAgeId()%>] = <%=age.getAgeStart()%>;
		ageStop[<%=age.getAgeId()%>] = <%=age.getAgeStop()%><%
	}
	%></script>
	
	<form name="QueryForm" method="post" action="result_list.jsp" onsubmit="return generateSQL(this)">
	<table border="0" cellspacing="0" cellpadding="3" width="600">
	<tr><td style="color: #FF0000" colspan="5">A new <i>Advanced Query Builder</i> is now available and can be used to generate more complex queries than this form.</td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td class="heading">Fossil Record No.&nbsp;&nbsp;</td><td></td><td colspan="3"><input type="text" name="FRNum" size="30" /></td></tr> 
	<tr><td class="heading">NZMG Sheet&nbsp;&nbsp;</td><td></td><td colspan="3"><input type="text" name="Map" size="10" /></td></tr>
	<tr><td class="heading">QMap Sheet&nbsp;&nbsp;</td><td></td><td colspan="3">
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
	<tr><td class="heading">Collector&nbsp;&nbsp;</td><td></td><td colspan="3"><%	
	SelectBox<Person> personSelectBox = new SelectBox<Person>(new PersonUtil(factory).getPeople());
	Attributes attributes = Attributes.createNameOnlyAttributes("Coll");
	personSelectBox.writeBox(attributes, "-- All --", null, (Person)null, new PrintWriter(out));
	%></td></tr>
	<tr><td class="heading">Collection Date&nbsp;&nbsp;</td><td class="smallheading">Year</td><td><input type="text" name="YearFrom" size="10" /></td><td>to</td><td><input type="text" name="YearTo" size="10" /></td></tr>
	<tr><td class="heading">Field Number&nbsp;&nbsp;</td><td></td><td colspan="3"><input type="text" name="FieldNum" size="20" /></td></tr>
	<tr><td class="heading">Stratigraphic Name&nbsp;&nbsp;</td><td></td><td colspan="3"><input type="text" name="StratName" size="20" /></td></tr>
	<tr><td class="heading">Stratal Attitude&nbsp;&nbsp;</td><td class="smallheading">Presence of dip/strike</td><td colspan="3"><input type="checkbox" name="StratAtt" /></td></tr>
	<tr><td class="heading">Nature of Rock Unit&nbsp;&nbsp;</td><td></td><td colspan="3"><input type="text" name="RockNat" size="20" /></td></tr>
	<tr><td class="heading">Deposition Environment&nbsp;&nbsp;</td><td></td><td colspan="3"><input type="text" name="DepEnv" size="20" /></td></tr>
	<tr><td class="heading">Age&nbsp;&nbsp;</td><td class="smallheading">Stage Range</td><td><%
	SelectBox<AgeView> ageSelectBox = new SelectBox<AgeView>(new StageUtil(factory).getAges());
	attributes = Attributes.createNameOnlyAttributes("StageFrom");
	ageSelectBox.writeBox(attributes, "-- All --", null, (AgeView)null, new PrintWriter(out));
	%></td><td>&nbsp;to&nbsp;</td><td><%
	attributes = Attributes.createNameOnlyAttributes("StageTo");
	ageSelectBox.writeBox(attributes, "-- All --", null, (AgeView)null, new PrintWriter(out));
	%></td></tr>
	<tr><td></td><td class="smallheading">Numeric Range&nbsp;&nbsp;</td><td><input type="text" name="AgeFrom" size="10" /></td><td>&nbsp;to&nbsp;</td><td><input type="text" name="AgeTo" size="10" /></td></tr>
	<tr><td></td><td class="smallheading">Options</td><td class="smallheading"><input type="checkbox" name="StratAge" checked />&nbsp;Collectors&nbsp;Inferred/Known&nbsp;Age<br><input type="checkbox" name="AdoAge" checked />&nbsp;Adopted&nbsp;Age<br><input type="checkbox" name="PalAge" checked />&nbsp;Paleontology&nbsp;Det&nbsp;Age</td><td></td><td class="smallheading"><input type="radio" name="AgeType" value="Narrow" checked />&nbsp;Narrow Search<br><input type="radio" name="AgeType" value="Wide" />&nbsp;Wide Search</td></tr>
	</table>
	<input type="hidden" name="WhereSQL" value="" />
	<input type="hidden" name="QueryString" value="" />
	<input type="hidden" name="TableName" value="" />
	<p><input type="submit" value="Submit Query" /></p>
	</form>
	</td></tr></table><%
	drawBottom(out, et);
%>