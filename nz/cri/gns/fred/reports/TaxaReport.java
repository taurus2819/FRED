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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.dao.TaxonomicDAO;
import nz.cri.gns.fred.dao.TaxonomicGroupDAO;
import nz.cri.gns.fred.hibernate.util.HibernateUtil;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.util.NullOutputStream;

public class TaxaReport {

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
		if (args.length < 2) {
			System.out.println("Usage: TaxaReport <group> <input-file> [<output-dir>]");
			System.exit(1);
		}
		
		File outDir = args.length < 3 ? new File(".") : new File(args[2]);
		
		//Connect!
		setupJNDI();
		
		//Collect all the samples
		Set<Sample> samples = new HashSet<Sample>();
		Set<Feature> features = new HashSet<Feature>();
		
		TaxonomicDAO dao = HibernateUtil.get().getDAOFactory().getTaxonomicDAO();
		TaxonomicGroupDAO groupDAO = HibernateUtil.get().getDAOFactory().getTaxonomicGroupDAO();
		
		System.out.println("Gathering data");
		for (String name : parseInputFile(args[1])) {
			System.out.println(name);
			Taxon taxonName = dao.getTaxon(groupDAO.findTaxonomicGroup(args[0]), name);
			if (taxonName == null)
				continue;
			Set<PaleontologyListEntry> entries = taxonName.getListEntries();
			for (PaleontologyListEntry entry : entries) {
				Sample sample = entry.getPaleontology().getRecord().getSample();
				samples.add(sample);
				features.add(sample.getFeature());
			}
		}
		
		//Now we can do the reports
		System.out.println("Generating locality report");
		PrintWriter localities = new PrintWriter(new FileWriter(new File(outDir, "localities.csv")));
		for (Feature feature : features) {
			System.out.println(feature.getFrNumber().getFrNumber());
			SiteRecord site = FREDUtil.getSite(feature);
			localities.println(
				feature.getFrNumber().getFrNumber()
				+ ",\""
				+ DBUtils.nvl(feature.getFeatureName())
				+ "\","
				+ feature.getFeatureType()
				+ ","
				+ site.getLatAsDouble()
				+ ","
				+ site.getLonAsDouble()
				+ ","
				+ (feature.getStartDate() == null ? "" : FREDUtil.formatDateForOutput(feature.getStartDate(), feature.getStartDateRounding()))
				+ ","
				+ (feature.getFinishDate() == null ? "" : FREDUtil.formatDateForOutput(feature.getFinishDate(), feature.getFinishDateRounding()))
			);
		}
		localities.close();
		
		System.out.println("Generating taxonomic report");
		PrintWriter taxa = new PrintWriter(new FileWriter(new File(outDir, "taxa.csv")));
		for (Sample sample : samples) {
			String frNum = sample.getFeature().getFrNumber().getFrNumber();
			System.out.println(frNum);
			boolean single = sample.getFeature().getSamples().size() == 1;
			
			for (Record record : sample.getRecords()) {
				if (record.getPaleontology() != null) {
					for (PaleontologyListEntry entry : record.getPaleontology().getListEntries()) {
						taxa.println(
							frNum
							+ ","
							+ ((single) ? "" : (sample.getTopDepth() + "-" + sample.getBottomDepth()))
							+ ","
							+ record.getRecordId()
							+ ","
							+ (entry.getTaxon() == null ? entry.getTaxonomicName() : entry.getTaxon().getTaxonomicName())
						);
					}
				}
			}
		}
		taxa.close();
	}

	private static void setupJNDI() throws NamingException, ClassNotFoundException, NotBoundException, SQLException, ParserConfigurationException, FactoryConfigurationError, SAXException, IOException {
		JNDI.setup();
		InitialContext context = new InitialContext();
		final Connection conn = DBUtils.getJavaSqlConnection("gns", "fr");
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
		});
	}

	private static Iterable<String> parseInputFile(String file) throws IOException {
		List<String> names = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		
		String line;
		while ((line = br.readLine()) != null) {
			String[] bits = line.split(",");
			StringBuilder rebuild = new StringBuilder();
			for (String bit : bits) {
				if (bit.length() > 0)
					rebuild.append(" ").append(bit);
			}
			names.add(rebuild.substring(1));
		}
		
		return names;
	}

}
