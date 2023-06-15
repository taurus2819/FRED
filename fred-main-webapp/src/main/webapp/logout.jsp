<%@page pageEncoding="utf-8" extends="nz.cri.gns.jsp.IPSysLogoutPage" import="nz.cri.gns.fred.FREDIPSysJspPage,nz.cri.gns.jsp.ExtranetTemplate"%>
<!DOCTYPE html>
<html>
    <head>
        <title></title>
        <meta http-equiv="refresh" content="0; url='/'">
        <meta http-equiv="pragma" content="no-cache">

        <script type="text/javascript">
            window.location.replace('/')
        </script>
    </head>
    <%!
        public String getName(HttpServletRequest request) {
            return "FRED Logout";
        }
    %><%

        ExtranetTemplate et = FREDIPSysJspPage.getFREDTemplate(request.getSession());
        et.setDisplayLogin(false);

        drawTop(out, et, request, response);

    %>
    <table border="0" cellpadding="3" cellspacing="2" width="550">
        <tr><td colspan="2"><b>You have been logged out.</b></td></tr>
        <tr><td colspan="2"><b>Thank you for using the FRED application. </b></td></tr>
        <tr><td>&nbsp;</td></tr>
    </table>
    <%            drawBottom(out, et);
    %>
</html>

