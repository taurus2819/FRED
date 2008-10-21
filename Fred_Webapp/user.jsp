<%@page	extends="nz.cri.gns.fred.FREDAdminIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.NewExtranetTemplate"
%><%@page import="nz.cri.gns.fred.util.UserUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.model.FrUserView"
%><%@page import="nz.cri.gns.fred.model.FrUser"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: User Management";
	}
	
%><%
	UserUtil userUtil = new UserUtil(HibernateUtil.get().getDAOFactory());

	NewExtranetTemplate et = getExtranetTemplate();
	drawTop(out, et, request, response);

	%><p><table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr class="midColour"><th colspan="4">Current FRED Users</th></tr>
	<tr class="midColour"><th>Name&nbsp;&nbsp;</th><th>Organisation&nbsp;&nbsp;</th><th>Rights&nbsp;&nbsp;</th><th>Last Access</th></tr><%
	for (FrUserView frUserView : userUtil.getActiveFrUsers()) {
		FrUser frUser = userUtil.getFrUser(frUserView.getUserId());
		if (frUser != null || !"GNS".equals(frUserView.getOrgView().getCompanyName())) {
			%><tr class="lightColour">
			<td><%=frUserView.getFullName()%>&nbsp;&nbsp;</td>
			<td><%=frUserView.getOrgView().getCompanyName()%>&nbsp;&nbsp;</td>
			<td><%=(frUserView.getIrId().intValue() == UserUtil.FRED_WRITE.intValue()) ? "Read/Write" : "Read"%>&nbsp;&nbsp;</td>
			<td><%=(frUser != null) ? FREDUtil.formatDateForOutput(frUser.getLastLogin()) : ""%></td>
			</tr><%
		}
	}
	%></table></p><%
	
	drawBottom(out, et);
%>