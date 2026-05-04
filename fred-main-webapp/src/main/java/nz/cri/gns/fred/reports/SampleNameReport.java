package nz.cri.gns.fred.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import nz.cri.gns.fred.hibernate.util.hibernate6.FredHibernate;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.SampleUtil;

public class SampleNameReport extends AbstractReport {

    private int featureCount = 0;

    public SampleNameReport() {

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
            SampleNameReport report = new SampleNameReport();
            if (args.length == 6) {
                report.report(args[0], args[1], args[2], args[3], args[4], args[5], null);
            } else {
                report.report(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            }
        } catch (Exception ex) {
            System.out.println("Usage: new SampleReport( <Oracle host> <Oracle SID> <DB username> <DB password> <input-file> [<output-dir>] [<done-file>])");
            ex.printStackTrace();
        }

    }

    public void report(String host, String sid, String user, String password, String infilename, String outdirname, String donefilename)
            throws IOException, NamingException, StorageAccessException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException {

        File outDir = new File(outdirname);

        //Connect!
        setupJNDI(host, sid, user, password);

        //Collect all the features
        System.out.println("Reading inputs");
        Iterable<Integer> candidates = parseInputIdFile(infilename);
        Vector<Sample> samples = new Vector<Sample>(1024);

        FredDAO dao = FredHibernate.get().getDAOFactory().getFredDAO();
        DAOFactory factory = FredHibernate.get().getDAOFactory();
        SampleUtil util = new SampleUtil(factory);

        PrintWriter writer = new PrintWriter(new FileWriter(new File(outDir, "samplenames.txt")));

        int count = 0;
        System.out.println("Gathering data");
        for (Integer id : candidates) {
            count++;
            try {

                Sample sample = util.getSample(id.intValue());
                if (sample == null) {
                    continue;
                }
                writer.write(sample.getSampleId().toString());
                writer.write(',');
                writer.write(sample.toString());
                writer.write("\r\n");

                if (count % 1000 == 0) {
                    System.out.println(count + "...");
                }
            } catch (Exception ex) {
                System.out.println("skipping" + id);
                System.out.println(ex);
            }
        }
        writer.flush();
        writer.close();
        System.out.println("Ferme");
    }

    private Iterable<Integer> parseInputIdFile(String file) throws IOException {
        Set<Integer> ids = new HashSet<Integer>();
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));

        //skip header 
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            featureCount++;
            ids.add(Integer.valueOf(line));

        }
        return ids;
    }

}
