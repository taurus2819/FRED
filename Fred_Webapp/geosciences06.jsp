<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.fred.util.BacklogStatusUtil"
%><%@page import="nz.cri.gns.fred.util.FeatureUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="java.io.PrintWriter"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.Authenticable"
%><%@page import="nz.cri.gns.fred.website.ContentProvider"
%><%!	
	public Authenticable[] getRequiredRights(HttpServletRequest request) {
		return new Authenticable[0];
	}
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: Geosciences '06 Conference";
	}
%><%
	ExtranetTemplate et = getExtranetTemplate();
	
	drawTop(out, et, request, response);

	drawEndNavigation(out);

	%><p><%
	startDETable(pageContext);
	%><table border="0" width="500">
	<tr><td colspan="2" class="deHeading">Geosciences '06 Competition</td></tr>
	<tr><td colspan="2">Win a years free GSNZ membership PLUS a copy of <a href="http://www.vuw.ac.nz/VUP/2005titleinformation/haroldwellman.htm" target="Wellman">&quot;Harold Wellman: A Man Who Moved New Zealand&quot;</a> by Simon Nathan</td></tr>
	<tr><td colspan="2">Download the entry form below, answer the questions and pop the form in the box at the FRF stand</td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td><a href="images/geosciences06.pdf" target="comp"><img src="images/pdf_icon.gif" border="0" alt="pdf" /></a>&nbsp;&nbsp;</td>
	<td><a href="images/geosciences06.pdf" target="comp">Entry Form</a></td><tr>
	</table><%
	endDETable(pageContext);
	%></p><%

	%><p><%
	startDETable(pageContext);
	%><table border="0" width="500">
	<tr><td colspan="2" class="deHeading">Some Hints</td></tr>
	<tr><td>Question 4&nbsp;&nbsp;</td><td>Look at the backlog processing status map</td></tr>
	<tr><td>Question 5&nbsp;&nbsp;</td><td>What is the yard FR Number</td></tr>
	<tr><td>Question 6&nbsp;&nbsp;</td><td>Look at the <b>full</b> audit details for the locality</td></tr>
	<tr><td>Question 7&nbsp;&nbsp;</td><td>Use the <i>Locality Map</i> for the locality</td></tr>
	<tr><td>Questions 11 &amp; 12&nbsp;&nbsp;</td><td>Read the questions carefully - you may need to use brackets in the <i>Advanced Query Builder</i>.  Note &quot;dark red-brown&quot; means <i>colour modifier</i>=dark AND <i>primary colour</i>=red AND <i>secondary colour</i>=brown</td></tr>
	<tr><td>Question 13&nbsp;&nbsp;</td><td>Use the interactive map and zoom in closely enough to use the <i>1:50k Topography</i> or <i>Orthophotos</i> layer. Then use the <i>measure</i> tool</td></tr>
	<tr><td>Question 14&nbsp;&nbsp;</td><td>&quot;NZ mainland masterfile&quot; means one of Northern Nth Island, Central Nth Island, Southern Nth Island, Nelson, Central Sth Island and Southern Sth Island masterfiles</td></tr>
	</table><%
	endDETable(pageContext);
	%></p><%
	
	drawBottom(out, et); 
	%>
