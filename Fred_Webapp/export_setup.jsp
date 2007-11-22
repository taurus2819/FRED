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

	drawTop(out, et, request, response);

	%><form method="put" action="export.jsp"><%
	if (request.getParameter("featId") != null) {
		%><input type="hidden" name="featId" value="<%=request.getParameter("featId")%>" /><%
	} else if (request.getParameter("sampId") != null) {
		%><input type="hidden" name="sampId" value="<%=request.getParameter("sampId")%>" /><%
	}
	%><p><%
	startDETable(pageContext);
	%><table width="600" border="0">
	<tr>
		<td class="deHeading" colspan="4">Fields to Download</td>
	</tr>
	<tr>
		<td colspan="4">Slect which types of fields you would like to download and then press the <i>Download</i> button.  A tab seperated text file will be generated and you can either open or save it.  If a large number of localities are selected the process may take a few minutes.</td>
	</tr>
	<tr>
		<td colspan="4">Please note: use of this download facility is logged</td>
	</tr>
	<tr>
		<td class="heading">Locality&nbsp;&nbsp;</td>
		<td><input type="checkbox" name="collection" checked />Collection&nbsp;&nbsp;</td>
		<td><input type="checkbox" name="stratigraphy" checked />Stratigraphy&nbsp;&nbsp;</td>
		<td><input type="checkbox" name="sedimentary" checked />Sedimentary Feature&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td class="heading">Adoption Records&nbsp;&nbsp;</td>
		<td><input type="checkbox" name="adoption" checked />Adoption fields</td>
	</tr>
	<tr>
		<td class="heading">Paleontology Records&nbsp;&nbsp;</td>
		<td><input type="checkbox" name="paleontology" checked />Header only</td>
		<td><input type="checkbox" name="palList" checked />Taxonomic list</td>
	</tr>
	<tr>
		<td><input type="submit" value="Download" /></td>
	</tr>
	</table><%
	endDETable(pageContext);
	%></p>
	</form><%
	
	%></td></tr></table><%
	drawBottom(out, et);
%>