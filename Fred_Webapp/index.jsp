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
%><%
	ExtranetTemplate et = getExtranetTemplate();
	ContentProvider contentProvider = getContentProvider(getPageState(request, response));
	
	FeatureUtil featureUtil = new FeatureUtil(HibernateUtil.get().getDAOFactory());
	BacklogStatusUtil bsUtil = new BacklogStatusUtil(HibernateUtil.get().getDAOFactory());
	
	drawTop(out, et, request, response);

	contentProvider.getContent("index.nav").loadAll(new PrintWriter(out));
	
	drawEndNavigation(out);

	%><table style="margin-left:20px; width:550px;" border="0">
	<tr>
		<td colspan="2">
			<p>FRED is a computer database for the New Zealand Fossil Record File.  This is a recording scheme for fossil localities in NZ and nearby regions including SE Pacific Islands and the Ross Sea region of Antarctica, and is administered jointly by <a href="http://www.gsnz.org.nz" target="gsnz">Geological Society of New Zealand</a> and GNS Science.  FRED is operated by GNS Science through the FRST National Paleontological Databases Programme, with the assistance of staff at Auckland, Victoria, Canterbury and Otago universities.</p>
		</td>
	</tr>
	<tr><td><img src="images/blank.gif" width="275" height="3"></td><td><img src="images/blank.gif" width="275" height="3"></td></tr>
	<tr>
		<td>
			<img src="images/graptolites.jpg" width="250" height="180" class="border" alt="graptolites"/>
		</td>
		<td>
			FRED primarily contains initial registration information about fossil localities:
			<ul>
				<li>geographic coordinates</li>
				<li>collecting details, including geological context</li>
				<li>initial repository of specimens</li>
			</ul>
		</td>
	</tr>
	<tr><td><img src="images/blank.gif" width="275" height="3"></td><td><img src="images/blank.gif" width="275" height="3"></td></tr>
	<tr>
		<td colspan="2">
			For some localities it also provides additional data deposited by government, university, industry, and amateur geoscientists:
			<ul>
				<li>taxonomic determinations</li>
				<li>paleontological interpretations of stratigraphic age and paleoenvironment</li>
				<li>geological opinions about stratigraphic age</li>
			</ul>
			<p>FRED now contains <%=featureUtil.getTotalFeatureCount()%> locality coordinate records and was last updated on <%=FREDUtil.formatDateForOutput(featureUtil.getLastFeatureApprovalDate())%>.  A programme to enter full collection data for all registered sites is underway and currently <%=bsUtil.getSumLocalityCount() - bsUtil.getSumNewCount()%> of localities have been processed.  More detailed statistics and working areas can be viewed <a href="backlog_status.jsp">here</a>.</p>
			<p>Geographic coordinates can be queried and downloaded by anyone free of charge.  Access to detailed information, and capability to contribute new site records or other data requires a username and password - registered users agree to abide by accepted <a href="conditions.jsp">Conditions of Use</a>.</p>
			<p>If you are a new user please see the <a href="quick_start.jsp">Quick Start Guide</a>.</p>
			<p><span class="smalltext">Macintosh users: webmaps require Safari or Netscape 7+ browsers</span></p>
		</td>
	</tr>
	</table><%

	drawBottom(out, et); 
	%>
