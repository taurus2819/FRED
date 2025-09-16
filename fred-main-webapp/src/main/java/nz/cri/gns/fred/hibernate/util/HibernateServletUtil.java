package nz.cri.gns.fred.hibernate.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;

/**
 * A utility for safely closing the current hibernate session.
 * This should be the only code that directly calls FredHibernate.get().closeSession() so that
 * we can add more complex features like transaction rollback by modifying a single place.
 * 
 */

public class HibernateServletUtil {

    private static final Logger log = Logger.getLogger("nz.cri.gns.fred.hibernate.util.HibernateServletUtil");

    public interface ServletFunction {

        void service() throws ServletException, IOException;
    }

    /**
     * Wraps a function so that in the end, the current hibernate session is safely closed.
     * @param sf meant to be HttpServlet.service
     * @throws ServletException
     * @throws IOException 
     */
    public static void withHibernateSession(ServletFunction sf) throws ServletException, IOException {
        try {
            sf.service();
        } finally {
            try {
                FredHibernate.get().closeSession();
            } catch (Exception e) {
                log.log(Level.WARNING, "Could not close hibernate session", e);
                throw (e);
            }
        }
    }
}
