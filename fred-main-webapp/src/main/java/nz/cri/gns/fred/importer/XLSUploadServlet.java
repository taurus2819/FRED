package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.XLSUploaderServlet;

public class XLSUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = IPSysJspPage.getUser(req.getSession());
        if (null == user) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not logged in.");
            return;
        }

        //TODO: check user access.
        RowProcessor rp;
        Connection manageConn = null;
        Connection importConn = null;
        Connection errorConn = null;

        try {
            manageConn = FREDUtil.getConnection();
            importConn = FREDUtil.getConnection();
            errorConn = FREDUtil.getConnection();

            new XLSUploaderServlet().doUploadAndImport(
                    req,
                    resp,
                    manageConn,
                    importConn,
                    errorConn,
                    new FredRowProcessorFactory(user),
                    "/");
        } catch (NamingException | SQLException e) {
            throw new ServletException(e);
        } catch (Throwable t) {
            try {
                OutputStream o = resp.getOutputStream();
                t.printStackTrace(new PrintWriter(o));
                o.flush();
            } catch (IOException e) {
            }
            throw t;
        } finally {
            try {
                if (null != manageConn) {
                    manageConn.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (null != manageConn) {
                    importConn.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (null != manageConn) {
                    errorConn.close();
                }
            } catch (SQLException ex) {
            }
        }

    }
}
