package nz.cri.gns.fred.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.NotBoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.export.MolluscanAgeValidator;
import nz.cri.gns.fred.export.MinimumOverlapExport;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.SiteUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.util.NullOutputStream;

public class MinimumOverlapReport {
    private int featureCount = 0;
    
    public MinimumOverlapReport() {
  
    }
    
    /**
     * @param args
     * @throws IOException
     * @throws NamingException
     * @throws StorageAccessException
     * @throws SAXException
     * @throws FactoryConfigurationError
     * @throws ParserConfigurationException
     * @throws SQLException
     * @throws NotBoundException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, NamingException, StorageAccessException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException {
        String s = "Commencing";
        try {
                MinimumOverlapReport report = new MinimumOverlapReport();
                if (args.length==2) {
                    report.report(args[0], args[1]);
                } else {
                    report.report("//tmp//james.txt", "//tmp");
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
                
    }
    
    public void report(String infilename, String outdirname)
            throws IOException, NamingException, StorageAccessException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException {
        if (infilename== null || outdirname==null) {
            System.out.println("Usage: new MinimumOverlapReport( <input-file> [<output-dir>])");
        }

        File outDir = new File(outdirname);

        //Connect!
        setupJNDI();
        
        //Collect all the features
        System.out.println("Reading inputs");
        Iterable<String> candidates = parseInputFrNumFile(infilename);
        Vector<Feature> features = new Vector<Feature>(1024);
        
        FredDAO dao = FredHibernate.get().getDAOFactory().getFredDAO();
        FrNumber frnum = null;
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        FeatureUtil util = new FeatureUtil(factory);
        
        PrintWriter writer = new PrintWriter(new File(outDir, "overlap.txt"), "UTF-8");
       
        MinimumOverlapExport export = new MinimumOverlapExport(writer, factory);

        int count=0;
        System.out.println("Gathering data");       
        System.out.println("Generating report in batches");
        HashSet<String> hs = new HashSet<String>();
        for (String num : candidates) {
            count++;
            try {
                frnum = util.getFrNumber(num);
                if (hs.contains(frnum.toString())) {
                    continue;
                }
                
                hs.add(frnum.toString());
                Feature feature = util.getFeature(frnum);
                if (feature == null) {
                    continue;
                }
                
                try {
                    export.handleFeature(feature);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (count % 1000==0) {
                   System.out.println(count+"...");
                }
                
            } catch (Exception ex) {
                System.out.println("skipping" + num);
                System.out.println(ex);
            }
        }

        writer.flush();
        writer.close();
        System.out.println("Ferme");
    }

    private void setupJNDI() throws NamingException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, IOException {
        try {
            JNDI.setup();
            
        } catch (Exception ex) {
            if (ex instanceof IllegalStateException) {
                if ("InitialContextFactoryBuilder already set".equals(ex.getMessage())) {
                    System.out.println("Using previous JNDI setup");
                    return;
                }
            }
        }
        
        InitialContext context = new InitialContext();
        final Connection conn = DBUtils.getJavaSqlConnection("gns", "fr");
        FredHibernate.get().configure(conn);
        context.bind("java:comp/env/jdbc/fr", new DataSource() {

            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            public void setLoginTimeout(int seconds) throws SQLException {
            }

            public void setLogWriter(PrintWriter out) throws SQLException {
            }

            public PrintWriter getLogWriter() throws SQLException {
                return new PrintWriter(new NullOutputStream());
            }

            public Connection getConnection(String username, String password)
                    throws SQLException {
                return null;
            }

            public Connection getConnection() throws SQLException {
                return UnclosableConnection.create(conn);
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return conn.isWrapperFor(iface);
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return conn.unwrap(iface);
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

   
    private Iterable<String> parseInputFrNumFile(String file) throws IOException {
        Vector<String> frnums = new Vector<String>();
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));

        //skip header 
        //br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            featureCount++;
            frnums.add(line);
        }
        return frnums;
    }
    
    
}
