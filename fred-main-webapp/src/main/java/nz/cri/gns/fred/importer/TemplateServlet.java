package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.munginator.export.ExportSpreadsheet;

public class TemplateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            
            Connection conn = FREDUtil.getConnection();
            
            String templateCode = request.getParameter("CODE");
            
            ExportSpreadsheet ss = new ExportSpreadsheet(conn, templateCode);
            ss.addSheetFromTemplate(conn, templateCode);

            // Do this as late as possible in case the above fails.
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=template.xlsx");
            OutputStream o = response.getOutputStream();
            ss.write(o);

        } catch (SQLException | NamingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
