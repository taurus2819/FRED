package nz.cri.gns.fred.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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
import nz.cri.gns.fred.export.MolluscaExport;
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

public class CenozoicMolluscaReport {
    private int featureCount = 0;
    
    public CenozoicMolluscaReport() {
  
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
                CenozoicMolluscaReport report = new CenozoicMolluscaReport();
                if (args.length==2) {
                    report.report(args[0], args[1], null);
                } else {
                    report.report(args[0], args[1], args[2]);
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
                
    }
    
    public void report(String infilename, String outdirname, String donefilename)
            throws IOException, NamingException, StorageAccessException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException {
        if (infilename== null || outdirname==null) {
            System.out.println("Usage: new CenozoicMolluscaReport( <input-file> [<output-dir>])");
        }

        File outDir = new File(outdirname);

        //Connect!
        setupJNDI();
        
        //Collect all the features
        System.out.println("Reading inputs");
        //Iterable<Integer> candidates = parseInputIdFile(infilename);
        Iterable<String> candidates = parseInputFrNumFile(infilename);
        HashSet<Integer> done = parseDoneFile(donefilename);
        Vector<Feature> features = new Vector<Feature>(1024);
        
        FredDAO dao = FredHibernate.get().getDAOFactory().getFredDAO();
        FrNumber frnum = null;
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        FeatureUtil util = new FeatureUtil(factory);

        int count=0;
        System.out.println("Gathering data");
//        for (Integer id : candidates) {
//            count++;
//            try {
//                if (done.contains(id)) {
//                    //skip features previously processed
//                    continue;
//                }
//                Feature feature = util.getFeature(id.intValue());
//                if (feature == null) {
//                    continue;
//                }
//                features.add(feature);
//                if (count % 1000==0) {
//                    System.out.println(count+"...");
//                }
//            } catch (Exception ex) {
//                System.out.println("skipping" + id);
//                System.out.println(ex);
//            }
//        }
        
        PrintWriter writer = new PrintWriter(new FileWriter(new File(outDir, "taxa.txt")));
        //MolluscanAgeValidator export = new MolluscanAgeValidator(writer, factory);
        MolluscaExport export = new MolluscaExport(writer, factory);
        
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
                System.out.println("skipping" + frnum);
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
        //FredHibernate.get().setConnection(conn); //uncomment to run and hack FredHibernate
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
        });
    }

    private Iterable<Integer> parseInputIdFile(String file) throws IOException {
        Set<Integer> ids = new HashSet<Integer>();
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));

        //skip header 
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            featureCount++;
            String[] toks = line.split("[|]");
            //strip quotes first
            //frnums.add(toks[1].substring(1, toks[1].length() - 1));
            ids.add(Integer.valueOf(toks[1]));
        }
        return ids;
    }
    
    private Iterable<String> parseInputFrNumFile(String file) throws IOException {
        Vector<String> frnums = new Vector<String>();
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));

        //skip header 
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            featureCount++;
            String[] toks = line.split("[|]");
            //strip quotes first
            frnums.add(toks[1].substring(1, toks[1].length() - 1));
            //frnums.add(toks[1]);
        }
        return frnums;
    }
    
    private HashSet<Integer> parseDoneFile(String file) throws IOException {
        HashSet<Integer> ids = new HashSet<Integer>();
        if (file == null) {
            return ids;
        }
        
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        String line;
        while ((line = br.readLine()) != null) {
            featureCount++;
            String[] toks = line.split(":");
            ids.add(Integer.parseInt(toks[1].trim()));
        }
        return ids;
    }
}
