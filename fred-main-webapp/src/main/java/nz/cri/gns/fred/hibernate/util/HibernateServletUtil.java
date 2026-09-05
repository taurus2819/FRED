package nz.cri.gns.fred.hibernate.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;

/**
 * Central request-boundary helper for Hibernate session cleanup.
 *
 * <p>Keeping this concern in one place is important before introducing
 * concurrency: a Hibernate Session is request/thread scoped and must not be
 * shared with virtual-thread tasks.</p>
 */
public final class HibernateServletUtil {

    private static final Logger LOG = Logger.getLogger(HibernateServletUtil.class.getName());

    private HibernateServletUtil() {
    }

    @FunctionalInterface
    public interface ServletFunction {
        void service() throws ServletException, IOException;
    }

    /**
     * Executes servlet work and always closes the current Hibernate session.
     * If request handling has already failed, a cleanup failure is attached as
     * a suppressed exception rather than replacing the original failure.
     */
    public static void withHibernateSession(ServletFunction function)
        throws ServletException, IOException {

        Throwable requestFailure = null;
        try {
            function.service();
        } catch (ServletException | IOException | RuntimeException | Error e) {
            requestFailure = e;
            throw e;
        } finally {
            try {
                FredHibernate.get().closeSession();
            } catch (RuntimeException | Error closeFailure) {
                if (requestFailure != null) {
                    requestFailure.addSuppressed(closeFailure);
                    LOG.log(Level.WARNING,
                        "Could not close Hibernate session after request failure",
                        closeFailure);
                } else {
                    LOG.log(Level.WARNING, "Could not close Hibernate session", closeFailure);
                    throw closeFailure;
                }
            }
        }
    }
}
