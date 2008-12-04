<%@page	extends="nz.cri.gns.fred.FREDStaticIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.HibernateUtil"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.model.Age"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%
	DAOFactory factory = HibernateUtil.get().getDAOFactory();
	StageUtil stageUtil = new StageUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate();
	drawTop(out, et, request, response);


	%><p><table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr class="midColour"><th colspan="5">FRED Ages</th></tr>
	<tr class="midColour"><td class="heading">Period (approx.)&nbsp;&nbsp;</td><td class="heading">Stage&nbsp;&nbsp;</td><td class="heading">Base Age (Ma)&nbsp;&nbsp;</td><td class="heading">Top Age (Ma)&nbsp;&nbsp;</td><td class="heading">Notes</td></tr><%
	
	for (Age age : stageUtil.getAges()) {
		%><tr class="lightColour">
			<td><%=DBUtils.nvl(age.getPeriod())%>&nbsp;&nbsp;</td>
			<td><%=age.getName()%> (<%=age.getCode()%>)&nbsp;&nbsp;</td>
			<td><%=age.getBaseAge()%>&nbsp;&nbsp;</td>
			<td><%=age.getTopAge()%>&nbsp;&nbsp;</td>
			<td><%=(age.getObsoleteFlag()) ? "Obsolete" : ""%></td>
			</tr><%
	}
	
	%></table></p><%

	drawBottom(out, et); 
	%>