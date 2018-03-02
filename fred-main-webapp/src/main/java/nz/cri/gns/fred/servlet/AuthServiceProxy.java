package nz.cri.gns.fred.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.auth.AuthServiceException;
import nz.cri.gns.fred.util.UserUtil;

/**
 *
 * @author duncanw
 */
public class AuthServiceProxy extends HttpServlet {
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserUtil.updateUserRight(
                    request.getParameter("action"),
                    request.getParameter("right"),
                    Integer.parseInt(request.getParameter("userId"))
            );
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (AuthServiceException ex) {
            throw new ServletException(ex);
        }
    }    
}
