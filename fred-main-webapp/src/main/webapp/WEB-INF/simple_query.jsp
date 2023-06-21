<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
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
%><%@page import="nz.cri.gns.auth.security.IpGrantedAuthority"
%><%@page import="nz.cri.gns.html.select.SelectBox"
%><%@page import="nz.cri.gns.html.Attributes"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%!
        @Override
        public IpGrantedAuthority getRequiredRights() {
            return null;
        }
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Simple Query Form";
	}
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	ExtranetTemplate et = getExtranetTemplate(request.getSession());
	et.setDisplayLoadingMessage(true);
	
	User user = (User) getUser(session);
	
	drawTop(out, et, request, response);

	%><script language="JavaScript">
	function submitForm() {
                        var form = document.getElementsByName("QueryForm")[0];
                        if (validateForm(form)) {
                            form.submit();
                        }
	}
	

	function validateForm(form) {
		with (form) {
			<%
			
		if (user != null) {
			%>

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
				}
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
				}
			} else if (AgeTo.value.length > 0) {
				alert ("From Age not entered");
				AgeFrom.select();
				return false;
			}<%
		}

			%>
		}
		return true;
	}
    
    function definePolygon(){
        window.open('map_popup_frame.jsp','popuppage','width=960,toolbar=1,resizable=1,scrollbars=yes,height=700,top=100,left=100');
    }
	
	</script><%

	if (user != null) {
		//build array of stage ages
		int maxAgeId = new StageUtil(factory).getMaxAgeId();
		%><script language="JavaScript">
		var ageStart = new Array(<%=(maxAgeId + 1)%>);
		var ageStop = new Array(<%=(maxAgeId + 1)%>);<%
		for (Age age : new StageUtil(factory).getAges()) {
			%>ageStart[<%=age.getAgeId()%>] = <%=age.getBaseAge()%>;
			ageStop[<%=age.getAgeId()%>] = <%=age.getTopAge()%>;<%
		}
		%></script><%
	}
	
	%><form name="QueryForm" action="simple_query.jsp" method="post" >
	<p><table border="0" cellpadding="3" cellspacing="2" width="600">
            <div><input type="hidden" id="token" name="token" /></div>
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
	&nbsp;&nbsp;</td><td><i>Select a <a href="https://www.gns.cri.nz/data-and-resources/digital-qmap-geological-maps-at-1250000/" target="_blank">QMap</a> sheet</i></td></tr>
	<tr class="lightColour"><td class="heading">Field Number/Drillhole Name&nbsp;&nbsp;</td><td><input type="text" name="FieldNum" size="30" />&nbsp;&nbsp;<td><i>Enter part of a field number or drillhole name</i></td></tr><%
	
	if (user != null) {
		%><tr class="lightColour"><td class="heading">Collector&nbsp;&nbsp;</td><td><input type="text" name="Coll" size="30" />&nbsp;&nbsp;</td><td><i>Enter part of a collectors name</i></td></tr>
		<tr class="lightColour"><td class="heading">Collection Year&nbsp;&nbsp;</td><td><input type="text" name="YearFrom" size="10" />&nbsp;<b>to</b>&nbsp;<input type="text" name="YearTo" size="10" />&nbsp;&nbsp;</td><td><i>Enter a single year or a range</i></td></tr>
		<tr class="lightColour"><td class="heading">Stratigraphic Name&nbsp;&nbsp;</td><td><input type="text" name="StratName" size="30" />&nbsp;&nbsp;<td><i>Enter part of a stratigraphic name</i></td></tr>
		<tr class="lightColour"><td class="heading">Stratal Attitude&nbsp;&nbsp;</td><td><input type="checkbox" name="StratAtt" />&nbsp;&nbsp;</td><td><i>Tick for presence of dip/strike</i></td></tr>
		<tr class="lightColour"><td class="heading">Nature of Rock Unit&nbsp;&nbsp;</td><td><input type="text" name="RockNat" size="30" />&nbsp;&nbsp;</td><td><i>Enter part of rock unit description</i></td></tr>
		<tr class="lightColour"><td class="heading">Deposition Environment&nbsp;&nbsp;</td><td><input type="text" name="DepEnv" size="30" />&nbsp;&nbsp;</td><td><i>Enter part of deposition environment description</i></td></tr>
		<tr class="lightColour"><td class="heading">Age (by Stage)&nbsp;&nbsp;</td><td><%
		SelectBox<Age> ageSelectBox = new SelectBox<Age>(new StageUtil(factory).getNonDuplicateAges());
		Attributes attributes = Attributes.createNameOnlyAttributes("StageFrom");
		ageSelectBox.writeBox(attributes, "-- All --", null, (Age)null, new PrintWriter(out));
		%>&nbsp;<b>to</b>&nbsp;<%
		attributes = Attributes.createNameOnlyAttributes("StageTo");
		ageSelectBox.writeBox(attributes, "-- All --", null, (Age)null, new PrintWriter(out));
		%>&nbsp;&nbsp;</td><td><i>Select a <a href="age.jsp" target=_blank">NZ stage name</a> (or range). Sample, adopted and paleontological ages will be searched</i></td></tr>
		<tr class="lightColour"><td class="heading">Age (numeric)&nbsp;&nbsp;</td><td><input type="text" name="AgeFrom" size="10" />&nbsp;<b>to</b>&nbsp;<input type="text" name="AgeTo" size="10" />&nbsp;&nbsp;</td><td><i>Enter a numeric age (or range). Sample, adopted and paleontological ages will be searched</i></td></tr>

                                    <%-- Squirrel Wide Age --%>
                		<tr class="lightColour"><td class="heading">Auto-Consensus-Age (broad)&nbsp;&nbsp;</td><td>
                                     <%
                                            Attributes squirrelWideAges = Attributes.createNameOnlyAttributes("SquirrelWideAgeFrom");
                                            ageSelectBox.writeBox(squirrelWideAges, "-- All --", null, (Age)null, new PrintWriter(out));
		%>&nbsp;<b>to</b>&nbsp;
                                    <%
                                            squirrelWideAges = Attributes.createNameOnlyAttributes("SquirrelWideAgeTo");
                                            ageSelectBox.writeBox(squirrelWideAges, "-- All --", null, (Age)null, new PrintWriter(out));
		%>&nbsp;&nbsp;</td>
                                    <td><i>Select a <a href="age.jsp" target=_blank">NZ stage name</a> (or range). 
                                            The automatically calculated  <a href="javascript:show('squirrelAgeHelp');">consensus age fields</a> will be searched</i></td></tr>
		
                
                                    <%-- Squirrel Narrow Age --%>
                                    <tr class="lightColour"><td class="heading">Auto-Consensus-Age (narrow)&nbsp;&nbsp;</td><td><%
		Attributes squirrelNarrowAgeFrom = Attributes.createNameOnlyAttributes("SquirrelNarrowAgeFrom");
		ageSelectBox.writeBox(squirrelNarrowAgeFrom, "-- All --", null, (Age)null, new PrintWriter(out));
		%>&nbsp;<b>to</b>&nbsp;<%
		Attributes squirrelNarrowAgeTo = Attributes.createNameOnlyAttributes("SquirrelNarrowAgeTo");
		ageSelectBox.writeBox(squirrelNarrowAgeTo, "-- All --", null, (Age)null, new PrintWriter(out));
		%>&nbsp;&nbsp;</td>
                                    <td><i>Select a <a href="age.jsp" target=_blank">NZ stage name</a> (or range). 
                                            The automatically calculated <a href="javascript:show('squirrelAgeHelp');">consensus age fields</a> will be searched</i></td></tr>                
                
                                    
                <tr class="lightColour"><td class="heading">Taxonomic Group&nbsp;&nbsp;</td><td><%
		SelectBox<TaxonomicGroup> tGroupSelectBox = new SelectBox<TaxonomicGroup>(new TaxonomicUtil(factory).getTaxonomicGroups());
		attributes = Attributes.createNameOnlyAttributes("TaxonomicGroup");
		tGroupSelectBox.setNameNameFlag(true);
		tGroupSelectBox.writeBox(attributes, "-- All --", null, (TaxonomicGroup)null, new PrintWriter(out));
		%>&nbsp;&nbsp;</td><td><i>Select a taxonomic group</i></td></tr>
		<tr class="lightColour"><td class="heading">Taxonomic Name&nbsp;&nbsp;</td><td><input type="text" name="Taxon" size="30" />&nbsp;&nbsp;</td><td><i>Enter part of a taxonomic name</i></td></tr>
        <!-- new feature: polygon query -->
        <tr class="lightColour"><td class="heading">Spatial Filter&nbsp;&nbsp;</td><td><button onclick="definePolygon()" type="button">Create map polygon</button><div id="isPolygon"></div>&nbsp;&nbsp;</td><td><i>Click here to define a search polygon</i></td></tr>
        <%
	} else {
		%><tr class="lightColour"><td colspan="3">More query fields are available to logged in users</td></tr><%
	}
	%></table></p>
	<input type="hidden" id="idList" name="idList" value="" />
	<input type="hidden" id="polygon" name="polygon" value="" />
        <p><input type="button" value="Submit Query" onclick="submitForm()" /></p>
	</form>
                <div id="squirrelAgeHelp" style="visibility:hidden" class="dialog">
                    <div>
                    <h4>Calculated Age fields</h4>
                    <p>For every sample recorded in FRED, two age fields are calculated automatically from all 
                        available information, namely “auto consensus-age (narrow)” and “auto consensus-age 
                        (broad)”.</p>
                    <p>If an adopted age has been entered, the narrow and broad calculated fields will both be set to 
                        the adopted age.</p>
                    <p>Otherwise, the narrow field is set to identify the minimum overlap (if overlap exists) between 
                        multiple age estimates. It is the more discriminating of the two fields – i.e., it will yield the fewest 
                        results in a search. The broad field will reflect the maximum possible range that is consistent 
                        with all age estimates and therefore “casts the widest net”. This field should be used for 
                        searches that aim to return every possible matching record.</p>               
                    <p><img src="images/squirrel_age.png" width="80%"/></p>
                    <p><button onclick="javascript:hide('squirrelAgeHelp')">Hide</button></p>
                    </div>
                </div>
                
                <%
	
	drawBottom(out, et);
%>
