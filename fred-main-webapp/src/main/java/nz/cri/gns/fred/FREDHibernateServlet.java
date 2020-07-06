package nz.cri.gns.fred;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.fred.hibernate.util.HibernateServletUtil;

/**
 * A servlet that safely closes the current hibernate session (if any) after handling
 * a request.
 */

public abstract class FREDHibernateServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HibernateServletUtil.withHibernateSession(() -> super.service(request, response));
    }
}
