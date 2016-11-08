
package nz.cri.gns.fred;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import nz.cri.gns.fred.util.DataEntryTemplateUtil;

/**
 *
 * @author duncanw
 */
    
@WebListener
public class FredStart implements ServletContextListener {
   
    private static final Logger log = Logger.getLogger(FredStart.class.getName());
    
    public static final String SYSTEM_PROPERTIES_FILE = "fred.properties";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
 
        long start = System.currentTimeMillis();
        log.info("Generating environment-specific data entry template");
        try {
            DataEntryTemplateUtil.writeVersionedTemplate();
            log.log(Level.INFO, "Environment-specific data entry template generation took {0}ms", (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error generating environment-specific data entry template", e);
        }
 
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do
    }
}
