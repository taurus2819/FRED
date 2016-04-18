<%@page pageEncoding="utf-8" extends="nz.cri.gns.fred.FREDIPSysJspPage"
        import="nz.cri.gns.auth.security.IpGrantedAuthority,org.apache.poi.ss.usermodel.Workbook,org.apache.poi.ss.usermodel.Sheet,org.apache.poi.ss.usermodel.Row,org.apache.poi.ss.usermodel.Cell,org.apache.poi.xssf.usermodel.XSSFWorkbook,java.util.zip.ZipOutputStream,java.io.File"
%><%!
    @Override
    public IpGrantedAuthority getRequiredRights() {
        return null;
    }
%><%

    File spreadsheetFile = new File(getServletContext().getRealPath("/FRED.xlsm"));

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