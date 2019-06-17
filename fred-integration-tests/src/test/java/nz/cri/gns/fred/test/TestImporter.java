package nz.cri.gns.fred.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import nz.cri.gns.auth.AuthServiceClient;
import nz.cri.gns.auth.AuthServiceException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.importer.PaleoRowProcessor;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.munginator.upload.Importer;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.XLSUploader;
import nz.cri.gns.munginator.upload.XLSUploader.SheetIdentifier;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestImporter {

    // drill_hole.xlsx  outcrop.xlsx  paleo.xlsx  vertical_section.xlsx
    private static final Logger log = Logger.getLogger("nz.cri.gns.TestImport");
    // TODO: Put @BeforeAll into a base class.
    // TODO: the next 100 or so lines were cut and paste from TestExport.
    protected Connection conn;
    protected Connection errorConn;
    protected Server server;

    private static File getResource(String filename) {
        return new File(TestImporter.class.getResource(filename).getFile());
    }

    
    @Before
    public void initializeDatabase() throws SQLException, NamingException {
        /* This doesn't work yet. Hibernate cannot find a data source. */

        try {

            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            
            InitialContext ctxt = new InitialContext(); // Loads from jndi.properties, then jdbc.properties.

            // Did we load everything from jdbc.properties properly?
            DataSource ds = (DataSource) ctxt.lookup("jdbc.fr");

            /*
            Used for debugging. You can hook up an SQL client to view the results of the test. 
            */
            /*
            server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");

            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("jdbc:h2:mem:mgdemo;MODE=Oracle");
            ds.setUser("sa");
            ds.setPassword("");
            server.start();
            */

            conn = ds.getConnection();
            conn.setAutoCommit(false);
            errorConn = conn;

            RunScript.execute(conn, new FileReader(getResource(
                    "/importer/fred_schema.sql")));
            RunScript.execute(conn, new FileReader(getResource(
                    "/importer/fred_mini_data.sql")));
            RunScript.execute(conn, new FileReader(getResource(
                    "/importer/munginator.sql")));
            RunScript.execute(conn, new FileReader(getResource(
                    "/importer/fred_mg_schema.sql")));
            conn.commit();

            /* Trying to manually make things work. This should be loaded from jdbc.properties
            */
            /*System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.SimpleContextFactory");
            System.setProperty("org.osjava.sj.root", "target/test-classes/config");
            System.setProperty("org.osjava.jndi.delimiter", "/");
            System.setProperty("org.osjava.sj.jndi.shared", "true");
            InitialContext ic = new InitialContext();
            ic.createSubcontext("java:comp");
            ic.createSubcontext("java:comp/env");
            ic.createSubcontext("java:comp/env/jdbc");
            ic.bind("java:comp/env/jdbc/fr", ds);*/
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPaleo() throws SQLException, IOException, AuthServiceException {
        Writer out = null;
        List<SheetIdentifier> sheets;
        TemporaryFolder testDir = new TemporaryFolder();

        try {
            testDir.create();
            File outF = testDir.newFile("TEST_TEMPLATE_2_log.html");
            out = new FileWriter(outF);

            // Upload it.
            XLSUploader uploader = new XLSUploader();
            InputStream in = new FileInputStream(getResource("/importer/paleo.xlsx"));
            sheets = uploader.readWorksheets(in, "paleo.xlsx", -1, conn);

            Integer sheetId = sheets.get(0).id;
            List<RowProcessor> rps = new ArrayList<>();

            Map<Integer, Record> paleoMatrix = new HashMap<>();
            DAOFactory factory = FredHibernate.get().getDAOFactory();

            User user = new AuthServiceClient().queryUsersByRight("FR_DATA_ENTRY").get(0);

            rps.add(new PaleoRowProcessor(user, factory, "PALEO", paleoMatrix));

            Importer im = new Importer(conn, conn, conn, rps, sheetId, null, out);

            assertTrue(im.doVerify());

            assertTrue(im.doImport());
        } finally {
            conn.close();
        }

    }
}
