<%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.auth.Authenticable"
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

	%><p><%
	startDETable(pageContext);
	%><table border="0" width="500">
	<tr><td colspan="2" class="deHeading">Geosciences '06 Competition</td></tr>
	<tr><td colspan="2">Win a years free GSNZ membership PLUS a copy of <a href="http://www.vuw.ac.nz/VUP/2005titleinformation/haroldwellman.htm" target="Wellman">&quot;Harold Wellman: A Man Who Moved New Zealand&quot;</a> by Simon Nathan</td></tr>
	<tr><td>&nbsp;</td></tr>
	</table><%
	endDETable(pageContext);
	%></p><%

	%><p><%
	startDETable(pageContext);
	%><table border="0" width="500">
	<tr><td colspan="2" class="deHeading">The Answers</td></tr>
	<tr><td>Question 1&nbsp;&nbsp;</td><td>1946</td></tr>
	<tr><td>Question 2&nbsp;&nbsp;</td><td>NU</td></tr>
	<tr><td>Question 3&nbsp;&nbsp;</td><td>Outcrop, Drillhole and Vertical Section</td></tr>
	<tr><td>Question 4&nbsp;&nbsp;</td><td>Nelson</td></tr>
	<tr><td>Question 5&nbsp;&nbsp;</td><td>S140/f505</td></tr>
	<tr><td>Question 6&nbsp;&nbsp;</td><td>Ben Morrison and Paul Viskovic</td></tr>
	<tr><td>Question 7&nbsp;&nbsp;</td><td>3</td></tr>
	<tr><td>Question 8&nbsp;&nbsp;</td><td>M27/f186</td></tr>
	<tr><td>Question 9&nbsp;&nbsp;</td><td>Henderson, J.</td></tr>
	<tr><td>Question 10&nbsp;&nbsp;</td><td>Arakamu-1</td></tr>
	<tr><td>Question 11&nbsp;&nbsp;</td><td>10</td></tr>
	<tr><td>Question 12&nbsp;&nbsp;</td><td>276</td></tr>
	<tr><td>Question 13&nbsp;&nbsp;</td><td>4.1 - 4.2km</td></tr>
	<tr><td>Question 14&nbsp;&nbsp;</td><td>1 December 1983</td></tr>
	</table><%
	endDETable(pageContext);
	%></p><%
	
	drawBottom(out, et); 
	%>
