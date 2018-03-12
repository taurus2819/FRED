<%@page import="java.util.List"%>
<%@page pageEncoding="utf-8"
%><%@page	extends="nz.cri.gns.fred.FREDAdminIPSysJspPage"
%><%@page import="nz.cri.gns.jsp.NewExtranetTemplate"
%><%@page import="nz.cri.gns.fred.util.UserUtil"
%><%@page import="nz.cri.gns.fred.util.FREDUtil"
%><%@page import="nz.cri.gns.fred.hibernate.util.FredHibernate"
%><%@page import="nz.cri.gns.fred.model.FrUserView"
%><%@page import="nz.cri.gns.fred.model.FrUser"
%><%@page import="nz.cri.gns.auth.domain.User"
%><%@page import="nz.cri.gns.fred.FredGrantedAuthorities"
%><%@page import="java.util.Date"
%><%@page import="java.util.GregorianCalendar"
%><%!
	public String getName(HttpServletRequest request) {
		return "FRED :: User Management";
	}
	
%><%
	UserUtil userUtil = new UserUtil(FredHibernate.get().getDAOFactory());

	NewExtranetTemplate et = getExtranetTemplate();
	drawTop(out, et, request, response);

	GregorianCalendar cal = new GregorianCalendar();
	cal.add(GregorianCalendar.YEAR, -2);
	Date twoYearsAgo = cal.getTime();
	
	%>
        <div id="fredUserUpdateMsg" style="display: none; position: fixed; top: 120px; left: 850px; color: #666; font-size: 16px; padding: 10px; border-radius: 5px;">
            Update saved
        </div>        
        <p><table id="fredUserTable" border="0" cellpadding="3" cellspacing="2" width="600">
	<tr class="midColour"><th colspan="7">Current FRED Users</th></tr>
	<tr class="midColour"><th>Name</th><th>Organisation</th><th>Read</th><th>Write</th><th>Admin</th><th>Last Access</th></tr><%
        List<FrUserView> users = userUtil.getAllUsers();
	for (FrUserView user : users) {
                String companyName = user.getOrgView() == null ? "" : user.getOrgView().getCompanyName();
                Date lastLogin = user.getLastLogin();
		if (lastLogin != null || !"GNS".equals(companyName)) {
			%><tr class="lightColour">
			<td><%=user.getFullName()%></td>
			<td><%=user.getFullName().equals(companyName) ? "" : companyName%></td>
                        <td><input type="checkbox" id="read-<%=user.getUserId()%>" class="rightCheckbox" value="read" <%=user.getHasWebAccessRight() ? "checked='checked'" : ""%>></td>
                        <td><input type="checkbox" id="write-<%=user.getUserId()%>" class="rightCheckbox" value="write" <%=user.getHasDataEntryRight() ? "checked='checked'" : ""%>></td>
                        <td><input type="checkbox" id="admin-<%=user.getUserId()%>" class="rightCheckbox" value="admin" <%=user.getHasAdminRight() ? "checked='checked'" : ""%>></td>
                        <%
			if (lastLogin != null) {
				%><td<%=(lastLogin.before(twoYearsAgo)) ? " style=\"color: #ff0000\"" : "" %>><%=FREDUtil.formatDateForOutput(lastLogin)%><%
			} else {
				%><td><%
			}
			%></td>
			</tr><%
		}
	}
	%></table></p>      
<script type="text/javascript" src="./scripts/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        $('#fredUserTable').on('click', '.rightCheckbox', function(){
            if ($(this).parents('tr').find('.rightCheckbox:checked').length === 0) {
                if (!window.confirm("Removing all rights will remove the user from the user list next time this page is loaded. Are you sure you want to remove this right?")) {
                    $(this).prop('checked', true);
                    return;
                }
            }
            var right = $(this).val();
            var userId = $(this).attr('id').substring((right + '-').length);
            var action = $(this).is(':checked') ? 'grant' : 'revoke';     
            $.post('updateUserRight', {right: right, action: action, userId: userId})
                .done(function(){
                    $('#fredUserUpdateMsg').show().fadeOut(4000);   
                })
                .fail(function(){
                    alert("Update failed, please try again or contact IT Support");
                });            
        });
    });
</script>          
<%
	
	drawBottom(out, et);
%>