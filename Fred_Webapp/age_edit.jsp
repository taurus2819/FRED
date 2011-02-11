<%@page pageEncoding="utf-8"%>
<%@page	extends="nz.cri.gns.fred.FREDAdminIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.model.Age"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	StageUtil stageUtil = new StageUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate();
	drawTop(out, et, request, response);

	if (request.getParameter("x") != null) {
		for (String ageId : request.getParameterValues("ageId")) {
			try {
				Age age = stageUtil.getAge(Integer.parseInt(ageId));
				age.setPeriod(request.getParameter("pd" + ageId));
				age.setName(request.getParameter("nm" + ageId));
				age.setCode(request.getParameter("cd" + ageId));
				age.setBaseAge(new Double(request.getParameter("ba" + ageId)));
				age.setTopAge(new Double(request.getParameter("ta" + ageId)));
				age.setObsoleteFlag((request.getParameter("ob" + ageId) != null) ? true : false);
				age.setDuplicateFlag((request.getParameter("dp" + ageId) != null) ? true : false);
				age.setComments(request.getParameter("cm" + ageId));
				stageUtil.saveOrUpdate(age);
			} catch (Exception e) {}
		}
	}

	%><form action="age_edit.jsp" method="post">
	<p><table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr class="midColour"><th colspan="8">FRED Stages</th></tr>
	<tr class="midColour"><td class="heading">Period&nbsp;&nbsp;</td><td class="heading">Name&nbsp;&nbsp;</td><td class="heading">Code&nbsp;&nbsp;</td><td class="heading">Base Age&nbsp;&nbsp;</td><td class="heading">Top Age&nbsp;&nbsp;</td><td class="heading">Obs&nbsp;&nbsp;</td><td class="heading">Dup&nbsp;&nbsp;</td><td class="heading">Comments</td></tr><%
	
	for (Age age : stageUtil.getAges()) {
		%><tr class="lightColour">
			<td><input type="hidden" name="ageId" value="<%=age.getAgeId()%>" />
			<input type="text" name="pd<%=age.getAgeId()%>" value="<%=DBUtils.nvl(age.getPeriod())%>" />&nbsp;&nbsp;</td>
			<td><input type="text" name="nm<%=age.getAgeId()%>" value="<%=age.getName()%>" />&nbsp;&nbsp;</td>
			<td><input type="text" name="cd<%=age.getAgeId()%>" value="<%=age.getCode()%>" size="8" />&nbsp;&nbsp;</td>
			<td><input type="text" name="ba<%=age.getAgeId()%>" value="<%=age.getBaseAge()%>" size="6" />&nbsp;&nbsp;</td>
			<td><input type="text" name="ta<%=age.getAgeId()%>" value="<%=age.getTopAge()%>" size="6" />&nbsp;&nbsp;</td>
			<td><input type="checkbox" name="ob<%=age.getAgeId()%>" <%=(age.getObsoleteFlag()) ? "checked " : ""%>/>&nbsp;&nbsp;</td>
			<td><input type="checkbox" name="dp<%=age.getAgeId()%>" <%=(age.getDuplicateFlag()) ? "checked " : ""%>/>&nbsp;&nbsp;</td>
			<td><textarea type="text" name="cm<%=age.getAgeId()%>"><%=DBUtils.nvl(age.getComments())%></textarea>&nbsp;&nbsp;</td>
		</tr><%
	}
	
	%><tr><td colspan="8"><input type="submit" value="Save" /></td></tr>
	</table></p>
	<input type="hidden" name="x" value="x" />
	</form><%

	drawBottom(out, et); 
	%>