<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDStaticIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.model.Age"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	StageUtil stageUtil = new StageUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate(request.getSession());
	drawTop(out, et, request, response);


	%><p><table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr class="midColour"><th colspan="6">FRED Stages</th></tr>
	<tr class="midColour"><td class="heading">Period (approx.)&nbsp;&nbsp;</td><td class="heading">Stage&nbsp;&nbsp;</td><td class="heading">Base Age (Ma)&nbsp;&nbsp;</td><td class="heading">Top Age (Ma)&nbsp;&nbsp;</td><td class="heading">Duplicate/Obsolete&nbsp;&nbsp;</td><td class="heading">Comments</td></tr><%
	
	for (Age age : stageUtil.getAges()) {
		%><tr class="lightColour">
			<td><%=DBUtils.nvl(age.getPeriod())%>&nbsp;&nbsp;</td>
			<td><%=age.getName()%> (<%=age.getCode()%>)&nbsp;&nbsp;</td>
			<td><%=age.getBaseAge()%>&nbsp;&nbsp;</td>
			<td><%=age.getTopAge()%>&nbsp;&nbsp;</td>
			<td><%=(age.getObsoleteFlag() == 1) ? "Obsolete" : ((age.getDuplicateFlag() == 1) ? "Duplicate" : "")%>&nbsp;&nbsp;</td>
			<td><%=DBUtils.nvl(age.getComments())%></td>
			</tr><%
	}
	
	%></table></p><%

	drawBottom(out, et); 
	%>