<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
		import="nz.cri.gns.jsp.*, nz.cri.gns.auth.*"
%><%!	public Authenticable[] getRequiredRights(HttpServletRequest request) { return new Authenticable[0]; }
%><%
	ExtranetTemplate et = getExtranetTemplate();

	drawTop(out, et, request, response);

	out.println("<table style='margin-left:20px; margin-top:20px; width:150px;' border='0'>");
	out.println("<tr><td colspan='2' class='bigheading' align='center'>FRED Home</td></tr>");
	out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
	out.println("<tr><td><a href='simple_query.jsp' title='Query'><img src='images/search.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='simple_query.jsp' class='heading'>Query</a></td></tr>");
	out.println("<tr><td><a href='http://maps.gns.cri.nz/website/fred/index.html' title='Map'><img src='images/search.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='http://maps.gns.cri.nz/website/fred/index.html' class='heading'>Map</a></td></tr>");
	out.println("<tr><td><a href='folder_list.jsp' title='Data Entry'><img src='images/folder.gif' height='20' width='20' border='0' /></a><img src='images/blank.gif' height='20' width='10' border='0' /></td><td><a href='folder_list.jsp' class='heading'>Data Entry</a></td></tr>");
	out.println("</table>");

	drawEndNavigation(out);

	out.println("<table style='margin-left:20px; width:550px;' border='0'>");

	out.println("<tr><td>The New Zealand Fossil Record File was established by the NZ Geological Survey in 1946 to systematically record details of fossil collection and identification; as an aid to stratigraphy and paleontology.  It had predecessors in the manuscript and typescript catalogues of collections begun in the early years of the Survey, and continued to this day.  A single form, the 'Fossil Record Form' was developed and registration of localities within standard map areas promoted among New Zealand geologists, both government and non-government, with masterfiles kept in several regional centres.</td>");
	out.println("<td align='right'><img src='images/graptolites.jpg' width='250' height='180' class='border'/></td></tr>");
	out.println("<tr><td><img src='images/blank.gif' width='1' height='10' /></td></tr>");
	out.println("<tr><td><img src='images/bivalves.jpg' width='280' height='150' class='border'/></td>");
	out.println("<td>The scheme continues under the sponsorship of the Geological Society of New Zealand, but with a continuing involvement of the Institute of Geological & Nuclear Sciences.  It is archival, with continuous addition of new observations and reassessment of old.<br />Users of the Fossil Record File can now enter and query data online through this web interface.</td></tr>");

	out.println("</table>");

	drawBottom(out, et); %>
