<%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.model.Feature"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="java.util.List"
%><%!
	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Export Data";
	}
%><%
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);
	//addButtons(et, new IconnedLink[] {new IconnedLink(queryURL, "images/search.gif", "Search Again")});

	drawTop(out, et, request, response);

	List<Feature> features = (List<Feature>) session.getAttribute("FRED.features");
	if (features != null && features.size() > 0) {

		%><form method="get" action="export.jsp">
		<p><%
		startDETable(pageContext);
		%><table width="600" border="0">
		<tr><td class="deHeading" colspan="5">Fields to Download</td></tr>
		<tr><td class="heading"><input type="checkbox" name="locality" />Locality Fields</td></tr>
		<tr><td class="heading"><input type="checkbox" name="collection" />Collection Fields&nbsp;&nbsp;</td>
		<td class="heading"><input type="checkbox" name="stratigraphy" />Stratigraphy Fields&nbsp;&nbsp;</td>
		<td class="heading"><input type="checkbox" name="sedimentary" />Sedimentary Feature Fields&nbsp;&nbsp;</td></tr>
		<tr><td class="heading"><input type="checkbox" name="adoption" />Adoption Fields</td></tr>
		<tr><td class="heading"><input type="checkbox" name="paleontology" />Paleontology Fields</td></tr>
		<tr><td class="heading"><input type="checkbox" name="palList" />Paleontology List Fields</td></tr>
		<tr><td><input type="submit" value="Download" /></td></tr>
		</table><%
		endDETable(pageContext);
		%></p>
		</form><%
	} else {
		%><p>No data</p><%
	}
	
	%></td></tr></table><%
	drawBottom(out, et);
%>