<%@page pageEncoding="utf-8"
%><%@page extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.security.IpGrantedAuthority"
%><%!
        @Override
        public IpGrantedAuthority getRequiredRights() {
            return null;
        }
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Export Data";
	}
%><%
	ExtranetTemplate et = getExtranetTemplate();
	et.setDisplayLoadingMessage(true);

	drawTop(out, et, request, response);

	%>
        <form method="get" name="export" action="export.jsp">
            <input type="hidden" name="type" value="LOCATION"/>
            <script type="text/javascript">
                function submitType(type) {
                    exportForm = document.forms['export'];
                    exportForm.elements['type'].value = type;
                    exportForm.submit();
                }
            </script>
            <%
	if (request.getParameter("featId") != null) {
		%><input type="hidden" name="featId" value="<%=request.getParameter("featId")%>" /><%
	} else if (request.getParameter("sampId") != null) {
		%><input type="hidden" name="sampId" value="<%=request.getParameter("sampId")%>" /><%
	}
	%><p><%
	startDETable(pageContext);
	%><table width="600" border="0">
	<tr>
		<td class="deHeading" colspan="4">Select Download</td>
	</tr>
	<tr>
		<td colspan="4"><p>Select what you would like to download and then press the appropriate <i>Download</i> button.</p>
                    <p>A tab separated text file will be generated and you can either open or save it. </p><p>If a large number of localities are selected the process may take a few minutes.</p></td>
	</tr>
	<tr>
		<td colspan="4" style="font-style:italic">Please note: use of this download facility is logged</td>
	</tr>
	<tr>
		<td class="heading">Locality&nbsp;&nbsp;</td>
		<td><input type="checkbox" name="collection" checked />Collection&nbsp;&nbsp;</td>
		<td><input type="checkbox" name="stratigraphy" checked />Stratigraphy&nbsp;&nbsp;</td>
		<td><input type="checkbox" name="sedimentary" checked />Sedimentary Feature&nbsp;&nbsp;</td>
	</tr>
        <tr>
                <td></td>
                <td colspan="3">
                    <button onclick="submitType('LOCATION')" style="min-width: 140px">Download Locality</button>
                </td>
        </tr>
	<tr>
		<td class="heading">Adoption Records&nbsp;&nbsp;</td>
		<td><button onclick="submitType('ADOPTION')" style="min-width: 140px">Download Adoption</button></td>
	</tr>
	<tr>
		<td class="heading">Paleontology Records&nbsp;&nbsp;</td>
		<td><button onclick="submitType('PALEONTOLOGY')" style="min-width: 140px">Download Paleontology</button></td>
		<td><button onclick="submitType('PALEONTOLOGY_TAXONOMIC')" style="min-width: 140px">Download Paleontology Taxonomic</button></td>
	</tr>
	</table><%
	endDETable(pageContext);
	%></p>
	</form><%
	
	%></td></tr></table><%
	drawBottom(out, et);
%>