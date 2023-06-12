package nz.cri.gns.fred.importer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.auth.security.IpGrantedAuthority;
import nz.cri.gns.fred.FREDHibernateServlet;
import nz.cri.gns.fred.FredGrantedAuthorities;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.servlet.util.FredHelper;
import nz.cri.gns.fred.servlet.util.JspWriterImpl;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.jsp.ExtranetTemplate;
import nz.cri.gns.jsp.IPSysJspPage;
import nz.cri.gns.munginator.upload.XLSUploaderServletHelper;

public class XLSUploadServlet extends FREDHibernateServlet {
    
    private static Logger log = Logger.getLogger("nz.cri.gns.fred.importer.XLSUploadServlet");
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        FredHelper h = new FredHelper();
        User user = IPSysJspPage.getUser(request.getSession());
        
        if (null == user) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not logged in.");
            return;
        }
        
        if (!h.checkAccess(request, response, new IpGrantedAuthority(FredGrantedAuthorities.FR_DATA_ENTRY))) {
            return;
        }
        
        if (XLSUploaderServletHelper.beyondMemoryLimit(request)) {
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                    "This server does not have enough memory to process such a large spreadsheet.");
            return;
        }
        
        response.setContentType("text/html;charset=utf-8");
        JspWriterImpl out = new JspWriterImpl(response.getOutputStream());
        
        ExtranetTemplate et = h.getExtranetTemplate(request.getSession());
        et.addStyleSheet("css/log.css");
        h.drawTop(out, et, request, response);
        
        Connection manageConn = null;
        Connection importConn = null;
        Connection errorConn = null;
        DAOFactory factory = null;
        
        try {
            manageConn = FREDUtil.getConnection();
            importConn = FREDUtil.getConnection();
            errorConn = FREDUtil.getConnection();
            factory = FredHibernate.get().getDAOFactory();
            
            new XLSUploaderServletHelper().doUploadAndImport(request,
                    response,
                    out,
                    manageConn,
                    importConn,
                    errorConn,
                    new FredRowProcessorFactory(user, factory),
                    "/fred/folder_list.jsp",
                    "/fred/xlsUploader");
            
        } catch (NamingException | SQLException e) {
            throw new ServletException(e);
        } catch (Throwable t) { // Don't worry; it gets re-thrown.
            // We catch Throwable here because Tomcat eats them and never tells anybody.
            log.log(Level.SEVERE, null, t);
            try {
                
                t.printStackTrace(new PrintWriter(out));
                out.flush();
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
                if (null != importConn) {
                    importConn.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (null != errorConn) {
                    errorConn.close();
                }
            } catch (SQLException ex) {
            }
        }
        h.drawBottom(out, et);
        out.flush();
    }
    
}
