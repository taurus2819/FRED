<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDAdminIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.ExtranetTemplate"
%><%@page import="nz.cri.gns.fred.dao.DAOFactory"
%><%@page import="nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate"
%><%@page import="nz.cri.gns.fred.util.StageUtil"
%><%@page import="nz.cri.gns.fred.model.Age"
%><%@page import="nz.cri.gns.db.DBUtils"
%><%
	DAOFactory factory = FredHibernate.get().getDAOFactory();
	StageUtil stageUtil = new StageUtil(factory);
	
	ExtranetTemplate et = getExtranetTemplate(request.getSession());
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
				age.setObsoleteFlag((request.getParameter("ob" + ageId) != null) ? 1 : 0);
				age.setDuplicateFlag((request.getParameter("dp" + ageId) != null) ? 1 : 0);
				age.setComments(request.getParameter("cm" + ageId));
				stageUtil.saveOrUpdate(age);
			} catch (Exception e) {}
		}
	}
    
    //adding new ?
    if (request.getParameter("x") != null) {
        System.out.println("pdnew:"+request.getParameter("pdnew"));
        System.out.println("nmnew:"+request.getParameter("nmnew"));
        System.out.println("cdnew"+request.getParameter("cdnew"));
        System.out.println("banew"+request.getParameter("banew"));
        System.out.println("tanew"+request.getParameter("tanew"));
        System.out.println("obnew"+request.getParameter("obnew"));
        System.out.println("dpnew"+request.getParameter("dpnew"));
        System.out.println("cmnew:"+request.getParameter("cmnew"));
                
        if (request.getParameter("pdnew") != null || request.getParameter("cdnew") != null ||
            request.getParameter("banew") != null || request.getParameter("tanew") != null ||
            request.getParameter("obnew") != null || request.getParameter("dpnew") != null ||
            request.getParameter("cmnew") != null || request.getParameter("nmnew") != null) {                                                                    
            try {
                Age age = stageUtil.createAge();
                age.setPeriod(request.getParameter("pdnew"));
                age.setName(request.getParameter("nmnew"));
                age.setCode(request.getParameter("cdnew"));
                age.setBaseAge(new Double(request.getParameter("banew")));
                age.setTopAge(new Double(request.getParameter("tanew")));
                age.setObsoleteFlag((request.getParameter("obnew") != null) ? 1 : 0);
                age.setDuplicateFlag((request.getParameter("dpnew") != null) ? 1 : 0);
                age.setComments(request.getParameter("cmnew"));
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
			<td><input type="checkbox" name="ob<%=age.getAgeId()%>" <%=(age.getObsoleteFlag() == 1) ? "checked " : ""%>/>&nbsp;&nbsp;</td>
			<td><input type="checkbox" name="dp<%=age.getAgeId()%>" <%=(age.getDuplicateFlag() == 1) ? "checked " : ""%>/>&nbsp;&nbsp;</td>
			<td><textarea type="text" name="cm<%=age.getAgeId()%>"><%=DBUtils.nvl(age.getComments())%></textarea>&nbsp;&nbsp;</td>
		</tr><%
	}
	
	%>
    <p><table border="0" cellpadding="3" cellspacing="2" width="550">
	<tr class="midColour"><th colspan="8">Add new FRED Stage</th></tr>
	<tr class="midColour"><td class="heading">Period&nbsp;&nbsp;</td><td class="heading">Name&nbsp;&nbsp;</td><td class="heading">Code&nbsp;&nbsp;</td><td class="heading">Base Age&nbsp;&nbsp;</td><td class="heading">Top Age&nbsp;&nbsp;</td><td class="heading">Obs&nbsp;&nbsp;</td><td class="heading">Dup&nbsp;&nbsp;</td><td class="heading">Comments</td></tr>
    <tr class="lightColour">
        <td><input type="hidden" name="agenew" value="-1" />
        <input type="text" name="pdnew" value="" />&nbsp;&nbsp;</td>
        <td><input type="text" name="nmnew" value="" />&nbsp;&nbsp;</td>
        <td><input type="text" name="cdnew" value="" size="8" />&nbsp;&nbsp;</td>
        <td><input type="text" name="banew" value="" size="6" />&nbsp;&nbsp;</td>
        <td><input type="text" name="tanew" value="" size="6" />&nbsp;&nbsp;</td>
        <td><input type="checkbox" name="obnew" />&nbsp;&nbsp;</td>
        <td><input type="checkbox" name="dpnew" />&nbsp;&nbsp;</td>
        <td><textarea type="text" name="cmnew" ></textarea>&nbsp;&nbsp;</td>
    </tr>
    
    <tr><td colspan="8"><input type="submit" value="Save" /></td></tr>
	</table></p>
	<input type="hidden" name="x" value="x" />
	</form><%

	drawBottom(out, et); 
	%>