package nz.cri.gns.fred.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.auth.AuthServiceException;
import nz.cri.gns.auth.security.IpGrantedAuthority;
import static nz.cri.gns.fred.FredGrantedAuthorities.FR_ADMIN;
import nz.cri.gns.fred.util.UserUtil;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.xss.SanitizeHttpServletRequest;

/**
 *
 * @author duncanw
 */
public class AuthServiceProxy extends HttpServlet {
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SanitizeHttpServletRequest sanitizeHttpRequest = new SanitizeHttpServletRequest();
        if (!JspUtils.userIsAuthorized(JspUtils.getUser(request.getSession()), new IpGrantedAuthority(FR_ADMIN))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        try {
            UserUtil.updateUserRight(
                    sanitizeHttpRequest.stripAllScripts(request.getParameter("action")),
                    sanitizeHttpRequest.stripAllScripts(request.getParameter("right")),
                    Integer.parseInt(sanitizeHttpRequest.stripAllScripts(request.getParameter("userId")))
            );
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (AuthServiceException ex) {
            throw new ServletException(ex);
        }
    }    
}
