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
import javax.naming.NamingException;
import nz.cri.gns.auth.AuthServiceException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.fred.dao.DAOFactory;
//import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;
import nz.cri.gns.fred.importer.PaleoRowProcessor;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.munginator.upload.Importer;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.XLSUploader;
import nz.cri.gns.munginator.upload.XLSUploader.SheetIdentifier;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestImporter {
//
//    // drill_hole.xlsx  outcrop.xlsx  paleo.xlsx  vertical_section.xlsx
//    private static final Logger log = Logger.getLogger("nz.cri.gns.TestImport");
//    // TODO: Put @BeforeAll into a base class.
//    // TODO: the next 100 or so lines were cut and paste from TestExport.
//    protected Connection conn;
//    protected Connection errorConn;
//    protected Server server;
//
//    private static File getResource(String filename) {
//        return new File(TestImporter.class.getResource(filename).getFile());
//    }
//
//    
//    @Before
//    public void initializeDatabase() throws SQLException, NamingException {
//        /* This doesn't work yet. Hibernate cannot find a data source. */
//
//        try {
//
//            try {
//                Class.forName("org.h2.Driver");
//            } catch (ClassNotFoundException ex) {
//                throw new RuntimeException(ex);
//            }
//
//            /*
//            Used for debugging. You can hook up an SQL client to view the results of the test. 
//            */
//            server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "5433");
//
//            JdbcDataSource ds = new JdbcDataSource();
//            ds.setURL("jdbc:h2:mem:mgdemo;MODE=PostgreSQL");
//            //ds.setURL("jdbc:h2:~/fredtests;MODE=Oracle");
//            ds.setUser("sa");
//            ds.setPassword("");
//            server.start();
//
//
//            conn = ds.getConnection();
//            conn.setAutoCommit(false);
//            errorConn = conn;
//
//            RunScript.execute(conn, new FileReader(getResource(
//                    "/importer/fred_schema.sql")));
//            RunScript.execute(conn, new FileReader(getResource(
//                    "/importer/fred_mini_data.sql")));
//            RunScript.execute(conn, new FileReader(getResource(
//                    "/importer/munginator.sql")));
//            RunScript.execute(conn, new FileReader(getResource(
//                    "/importer/fred_mg_schema.sql")));
//            RunScript.execute(conn, new FileReader(getResource(
//                    "/importer/lu_country_h2.sql")));
//            conn.commit();
//
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    public void testPaleo() throws SQLException, IOException, AuthServiceException {
//        Writer out = null;
//        List<SheetIdentifier> sheets;
//        TemporaryFolder testDir = new TemporaryFolder();
//
//        try {
//            testDir.create();
//            File outF = testDir.newFile("TEST_TEMPLATE_2_log.html");
//            out = new FileWriter(outF);
//
//            // Upload it.
//            XLSUploader uploader = new XLSUploader();
//            InputStream in = new FileInputStream(getResource("/importer/paleo.xlsm"));
//            sheets = uploader.readWorksheets(in, "paleo.xlsm", -1, conn);
//
//            Integer sheetId = sheets.get(0).id;
//            List<RowProcessor> rps = new ArrayList<>();
//
//            Map<Integer, Record> paleoMatrix = new HashMap<>();
//            DAOFactory factory = FredHibernate.usingConnection(conn).getDAOFactory();
//
//            User user = new User();
//            user.setId(1L);
//
//            rps.add(new PaleoRowProcessor(user, factory, "PALEO", paleoMatrix));
//
//            Importer im = new Importer(conn, conn, conn, rps, sheetId, null, out);
//
//            assertEquals(RowProcessor.State.OK, im.doVerify());
//
//            assertEquals(RowProcessor.State.ERROR, im.doImport());
//        } finally {
//            conn.close();
//        }
//
//    }
}
