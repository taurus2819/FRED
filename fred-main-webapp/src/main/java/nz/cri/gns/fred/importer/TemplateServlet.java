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
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.jsp.IPSysJspPage;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.export.ExportSpreadsheet;

public class TemplateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        try {

            conn = FREDUtil.getConnection();
            User user = IPSysJspPage.getUser(request.getSession());
            String templateCode = request.getParameter("CODE");

            {
                // Dummy run to catch any errors.
                // If you change the content type or write to the OutputStream, you can't show errors to the user.
                ExportSpreadsheet ss = new ExportSpreadsheet(conn, templateCode, null, ExportSpreadsheet.Filetype.XLSX, new FredCustomExportSpreadsheetHandler(conn, user));
                ss.write(new DiscardOutputStream());
            }

            // Now do it for real.
            ExportSpreadsheet ss = new ExportSpreadsheet(conn, templateCode, null, ExportSpreadsheet.Filetype.XLSX, new FredCustomExportSpreadsheetHandler(conn, user));

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=template.xlsx");
            OutputStream o = response.getOutputStream();
            ss.write(o);

        } catch (SQLException | NamingException | MgException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (null != conn) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
    }
}
