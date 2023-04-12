<%@page pageEncoding="utf-8"%>
<%@page import="nz.cri.gns.fred.util.FeatureUtil"%>
<%@page import="nz.cri.gns.fred.util.FREDUtil"%>
<%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"%>
<%@page import="nz.cri.gns.core.Environment"%>
<%@page import="nz.cri.gns.jsp.JspUtils"%>
<%
    FeatureUtil featureUtil = new FeatureUtil(FredHibernate.get().getDAOFactory());
%>

<table width="750" style="width:750px;" border="0">

<tr>
<td colspan="3" valign="top">FRED is a computer database for the New Zealand Fossil Record File (FRF) which is a recording scheme for fossil localities in NZ and nearby regions.<br><br>
FRED minimally contains registration information about fossil localities, including geographic coordinates, collection details, and sometimes stratigraphic and lithological information also.<br><br>	
For many samples, taxonomic determinations and paleontological interpretations of stratigraphic age and paleoenvironment are available.
</td>
</tr>

<tr>
<td valign="top">Please cite as:</td>
<td valign="top">GNS&nbsp;Science&nbsp;&amp;&nbsp;Geoscience&nbsp;Society&nbsp;of&nbsp;New&nbsp;Zealand&nbsp;(2003).<br>
New Zealand Fossil Record File [Data set]. GNS Science.<br>
<a href="https://doi.org/10.21420/JQQB-NK89">https://doi.org/10.21420/JQQB-NK89</a><br></td>
<td rowspan="4" valign="top"><img src="images/1a_500R.png" width="270" height="375" class="border" alt="limestone with fossils"/></td>
</tr>

<tr>
<td colspan="2" valign="top">
<%
    // only display the login form if the current user is not logged in.
    // if shifting this content take care not to put anything between here and
    // 'END of if not logged in'
    if (!JspUtils.isLoggedIn(session)) {
%>
    <%@include file="/WEB-INF/jspf/login-form.jspf" %>
<% } // END of if not logged in %>
<br></td>
</tr>

<tr>
<td colspan="2" valign="top">Links open in new window:<br>
<ul>
<li><a target="_blank" rel="noopener" href="./user_manual.pdf">User Manual</a></li>
<li><a target="_blank" rel="noopener" href="<%=Environment.getDataUrl()%>/register/password.jsp">Reset Password</a></li>
<li><a target="_blank" rel="noopener" href="<%=Environment.getDataUrl()%>/register/user_reg.jsp?DBase=FRED">New User Registration</a></li>
<li><a target="_blank" rel="noopener" href="whats_new.jsp">Recent News</a></li>
<li><a target="_blank" rel="noopener" href="<%=Environment.getDataUrl()%>/staff/email.jsp?id=frf&subject=FRF%20Feeedback">Feedback/Report Faults</a></li>
<li><a target="_blank" rel="noopener" href="about.jsp">About the NZ Fossil Record File</a></li>
</ul>
<br></td>
</tr>

<tr>
<td colspan="2" valign="top">Browser support:<br>
<ul>
<li>Windows: Chrome, Edge, Firefox</li>
<li>Apple: Chrome, Safari</li>
</ul>
</td>
</tr>

</table>