/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.util.NullOutputStream;

public class SampleNameReport {

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
            if (args.length == 5) {
                report.report(args[0], args[1], args[2], args[3], args[4], null);
            } else {
                report.report(args[0], args[1], args[2], args[3], args[4], args[5]);
            }
        } catch (Exception ex) {
            System.out.println("Usage: new SampleReport( <Oracle SID> <DB username> <DB password> <input-file> [<output-dir>])");
            ex.printStackTrace();
        }

    }

    public void report(String sid, String user, String password, String infilename, String outdirname, String donefilename)
            throws IOException, NamingException, StorageAccessException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException {

        File outDir = new File(outdirname);

        //Connect!
        setupJNDI(sid, user, password);

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

    private void setupJNDI(String sid, String user, String password) throws NamingException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, IOException {
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
        final Connection conn = DBUtils.getJavaSqlConnection(sid, user, password);
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
