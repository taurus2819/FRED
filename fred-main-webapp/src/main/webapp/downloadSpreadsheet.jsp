<%@page pageEncoding="utf-8"
        %><%@page	extends="nz.cri.gns.fred.FREDIPSysJspPage"
        %><%@page import="nz.cri.gns.auth.security.IpGrantedAuthority"
        %><%@page import="org.apache.poi.ss.usermodel.Workbook"
        %><%@page import="org.apache.poi.ss.usermodel.Sheet"
        %><%@page import="org.apache.poi.ss.usermodel.Row"
        %><%@page import="org.apache.poi.ss.usermodel.Cell"
        %><%@page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"
        %><%@page import="java.util.zip.ZipOutputStream"
        %><%@page import="java.io.File"
%><%!
    @Override
    public IpGrantedAuthority getRequiredRights() {
        return null;
    }
%><%

    File spreadsheetFile = new File(getServletContext().getRealPath("FRED.xlsm"));
    
    Workbook spreadsheet = new XSSFWorkbook(spreadsheetFile.getPath());
    /*Sheet listSheet = spreadsheet.getSheet("Lists");
    Row thirdRow = listSheet.getRow(2);
    Cell firstCellThirdRow = thirdRow.getCell(0);

    firstCellThirdRow.setCellValue(request.getRequestURL().toString().replaceAll("/[^/]*$", "/"));*/

    response.setContentType("application/vnd.ms-excel.sheet.macroEnabled.12");
    response.setHeader("Content-Disposition", "filename=\"FRED.xlsm\"");
    spreadsheet.write(response.getOutputStream());
        
    //response.setContentType("application/zip");
    //response.setHeader("Content-Disposition", "filename=\"FRED.zip\"");
    //spreadsheet.write(new ZipOutputStream(response.getOutputStream()));

%>