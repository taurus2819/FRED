<%@page	extends="nz.cri.gns.fred.FREDAdminIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.NewExtranetTemplate"
%><%@page import="nz.cri.gns.fred.util.UserUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.FrUserView"
%><%@page import="nz.cri.gns.fred.model.FrUser"
%><%@page import="java.util.Date"
%><%@page import="java.util.GregorianCalendar"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: User Management";
	}
	
%><%
	UserUtil userUtil = new UserUtil(HibernateUtil.get().getDAOFactory());

	NewExtranetTemplate et = getExtranetTemplate();
	drawTop(out, et, request, response);

	GregorianCalendar cal = new GregorianCalendar();
	cal.add(GregorianCalendar.YEAR, -2);
	Date twoYearsAgo = cal.getTime();
	
	%><p><table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr class="midColour"><th colspan="4">Current FRED Users</th></tr>
	<tr class="midColour"><th>Name&nbsp;&nbsp;</th><th>Organisation&nbsp;&nbsp;</th><th>Rights&nbsp;&nbsp;</th><th>Last Access</th></tr><%
	for (FrUserView frUserView : userUtil.getActiveFrUsers()) {
		FrUser frUser = userUtil.getFrUser(frUserView.getUserId());
		if (frUser != null || !"GNS".equals(frUserView.getOrgView().getCompanyName())) {
			%><tr class="lightColour">
			<td><%=frUserView.getFullName()%>&nbsp;&nbsp;</td>
			<td><%=(frUserView.getFullName().equals(frUserView.getOrgView().getCompanyName())) ? "" : frUserView.getOrgView().getCompanyName()%>&nbsp;&nbsp;</td>
			<td><a href="http://gns-a.gns.cri.nz/online/ipreg/process_access.jsp?PersonID=<%=frUserView.getUserId()%>&Schema=FR">
				<%=(frUserView.getIrId().intValue() == UserUtil.FRED_WRITE.intValue()) ? "Read/Write" : "Read"%>
			</a>&nbsp;&nbsp;</td><%
			if (frUser != null) {
				%><td<%=(frUser.getLastLogin().before(twoYearsAgo)) ? " style=\"color: #ff0000\"" : "" %>><%=FREDUtil.formatDateForOutput(frUser.getLastLogin())%><%
			} else {
				%><td><%
			}
			%></td>
			</tr><%
		}
	}
	%></table></p><%
	
	drawBottom(out, et);
%>