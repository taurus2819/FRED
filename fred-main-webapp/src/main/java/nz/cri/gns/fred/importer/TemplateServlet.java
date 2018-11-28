package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.jsp.IPSysJspPage;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.export.SpreadsheetExporter;

public class TemplateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        try {

            User user = IPSysJspPage.getUser(request.getSession());
            DAOFactory factory = FredHibernate.get().getDAOFactory();
            if (null == user) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You must log in first.");
                return;
            }
            String templateCode = request.getParameter("CODE");

            conn = FREDUtil.getConnection();
            {
                // Dummy run to catch any errors.
                // If you change the content type or write to the OutputStream, you can't show errors to the user.

                SpreadsheetExporter ss;
                if ("FRED_PALEO".equals(templateCode)) {
                    ss = new PaleoSpreadsheetExporter(conn, templateCode, "Paleo", factory);
                } else {
                    ss = new SpreadsheetExporter(conn, templateCode, null, SpreadsheetExporter.Filetype.XLSX, new FredCustomExportSpreadsheetHandler(conn, user));
                }
                ss.write(new DiscardOutputStream());
            }

            // Now do it for real.
            SpreadsheetExporter ss;
            if ("FRED_PALEO".equals(templateCode)) {
                ss = new PaleoSpreadsheetExporter(conn, templateCode, "Paleo", factory);
            } else {
                ss = new SpreadsheetExporter(conn, templateCode, null, SpreadsheetExporter.Filetype.XLSX, new FredCustomExportSpreadsheetHandler(conn, user));
            }

            ss.write(response);

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
