<%@page import="nz.cri.gns.fred.util.DataEntryTemplateUtil"%>
<%@page pageEncoding="utf-8" extends="nz.cri.gns.fred.FREDIPSysJspPage"
        import="nz.cri.gns.auth.security.IpGrantedAuthority,org.apache.poi.ss.usermodel.Workbook,org.apache.poi.ss.usermodel.Sheet,org.apache.poi.ss.usermodel.Row,org.apache.poi.ss.usermodel.Cell,org.apache.poi.xssf.usermodel.XSSFWorkbook,java.util.zip.ZipOutputStream,java.io.File"
%><%!
    @Override
    public IpGrantedAuthority getRequiredRights() {
        return null;
    }
%><%
    new DataEntryTemplateUtil().writeDataUploadTemplate(request, response);
%>