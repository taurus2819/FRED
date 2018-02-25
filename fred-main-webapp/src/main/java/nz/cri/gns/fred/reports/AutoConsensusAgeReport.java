package nz.cri.gns.fred.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.sql.SQLException;
import java.util.*;
import javax.naming.NamingException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.export.AutoConsensusAgeExport;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FrNumber;
import static nz.cri.gns.fred.reports.AbstractReport.setupJNDI;
import nz.cri.gns.fred.util.FeatureUtil;

public class AutoConsensusAgeReport extends AbstractReport {
    private int featureCount = 0;
    
    public AutoConsensusAgeReport() {
  
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
                AutoConsensusAgeReport report = new AutoConsensusAgeReport();
                report.report("maindb.gns.cri.nz", "gns", "fr", "ossify", "K:\\0_THIS_WEEK\\Peck Choo\\fr_num_gns.txt", "K:\\0_THIS_WEEK\\Peck Choo");
               
        } catch (Exception ex) {
            System.out.println("Usage: new AutoConsensusAgeReport( <Oracle host> <Oracle SID> <DB username> <DB password> <input-file> [<output-dir>])");
            ex.printStackTrace();
        }                
    }
    
    public void report(String host, String sid, String user, String password, String infilename, String outdirname)
            throws IOException, NamingException, StorageAccessException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException {

        File outDir = new File(outdirname);

        //Connect!
        setupJNDI(host, sid, user, password);
        
        //Collect all the features
        System.out.println("Reading inputs");
        Iterable<String> candidates = parseInputFrNumFile(infilename);
        Vector<Feature> features = new Vector<Feature>(1024);
        
        FredDAO dao = FredHibernate.get().getDAOFactory().getFredDAO();
        FrNumber frnum = null;
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        FeatureUtil util = new FeatureUtil(factory);
        
        PrintWriter writer = new PrintWriter(new File(outDir, "autoConsensus_gns.txt"), "UTF-8");
       
        AutoConsensusAgeExport export = new AutoConsensusAgeExport(writer, factory);

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
