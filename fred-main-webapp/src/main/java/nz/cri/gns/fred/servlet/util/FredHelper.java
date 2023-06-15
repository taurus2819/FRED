package nz.cri.gns.fred.servlet.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.auth.security.IpGrantedAuthority;
import nz.cri.gns.fred.FREDIPSysJspPage;
import nz.cri.gns.jsp.ExtranetTemplate;
import static nz.cri.gns.jsp.IPSysJspPage.getUser;
import nz.cri.gns.jsp.NewExtranetTemplate;

/**
 * This class is a hack.
 *
 * If you convert a jsp file into a servlet, I'm useful for all the methods
 * found in FREDIPSysJspPage et al.
 */
public class FredHelper extends FREDIPSysJspPage {

    @Override
    public final void _jspService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new RuntimeException("Do not use this method.");
    }

    public NewExtranetTemplate getExtranetTemplate(HttpSession session) {
        return getFREDTemplate(session);
    }

    public void drawTop(JspWriter out, ExtranetTemplate et,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        super.drawTop(out, et, request, response);
    }

    public void drawBottom(JspWriter out, ExtranetTemplate et)
            throws IOException {
        super.drawBottom(out, et);
    }

    public void include(HttpServletRequest request, Writer out, String resource) throws IOException {
        ServletContext context = request.getServletContext();
        try (InputStream resourceStr = context.getResourceAsStream(resource)) {
            byte[] buf = new byte[256];
            while (true) {
                int bytesRead = resourceStr.read(buf);
                if (bytesRead <= 0) {
                    break;
                } else {
                    // Will cut compound UTF-8 characters up.
                    out.write(new String(buf, "UTF-8"), 0, bytesRead);
                }
            }
        }
    }

    public Integer paramAsInteger(HttpServletRequest req, String paramName) {
        String v = req.getParameter(paramName);
        if (null != v) {
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /** Check that the user has the given authority. If not, redirect the page and return false.
     * 
     * @param request
     * @param response
     * @param authority
     * @return Whether to continue rendering the page or not.
     * @throws IOException never.
     */
    public boolean checkAccess(HttpServletRequest request, HttpServletResponse response, IpGrantedAuthority authority) throws IOException {
        HttpSession session = request.getSession();
        User user = getUser(session);

        if (null==authority) return true;
        
        if ( null==user || !checkUserAccess(user, authority)) {
            response.sendRedirect(getLoginURL());
            return false;
        }
        return true;
    }   
}
