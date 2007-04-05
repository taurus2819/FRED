package nz.cri.gns.fred.export.test;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import nz.cri.gns.fred.export.Export;
import nz.cri.gns.fred.hibernate.test.FredHibernateTest;
import nz.cri.gns.fred.model.Feature;

public class MolluscaExport extends FredHibernateTest {

	public static void main(String[] args) throws Exception {
		MolluscaExport export = new MolluscaExport();
		export.setUp();
		export.testExport();
		export.tearDown();
	}
	
	public void testExport() throws Exception {
		//Creates a export handler
		
//		Writer pw = new Writer() {
//		
//			@Override
//			public void write(String str, int off, int len) throws IOException {
//				System.out.print(str.substring(off, off+len));
//			}
//		
//			@Override
//			public void write(char[] cbuf, int off, int len) throws IOException {
//				System.out.print(new String(cbuf, off, len));
//			}
//		
//			@Override
//			public void flush() throws IOException {
//				System.out.flush();
//			}
//		
//			@Override
//			public void close() throws IOException {
//			}
//		};
		
		FileWriter pw = new FileWriter(File.createTempFile("fred", ".txt"));
		
		nz.cri.gns.fred.export.MolluscaExport export = new nz.cri.gns.fred.export.MolluscaExport(pw);
		
		//Do our initial query on a different connection...
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@raptor.gns.cri.nz:1521:gns", "fr", "ossify");
		Statement statement = conn.createStatement();
		
		ResultSet rs = statement.executeQuery("SELECT DISTINCT feature0_.FEATURE_ID, fr.fr_number from FEATURE feature0_ inner join SAMPLE samples1_ on feature0_.FEATURE_ID=samples1_.FEATURE_ID inner join RECORD records2_ on samples1_.SAMPLE_ID=records2_.SAMPLE_ID inner join PALEONTOLOGY paleontolo3_ on records2_.RECORD_ID=paleontolo3_.RECORD_ID inner join PAL_LIST listentrie4_ on paleontolo3_.RECORD_ID=listentrie4_.RECORD_ID inner join TAXONOMIC_GROUP taxonomicg5_ on listentrie4_.GROUP_ID=taxonomicg5_.GROUP_ID JOIN fr_number fr ON fr.fr_id = feature0_.fr_id where (taxonomicg5_.NAME IN('BIVALVIA' , 'GASTROPODA' , 'SCAPHOPODA')) ORDER BY fr.fr_number");
		//ResultSet rs = statement.executeQuery("SELECT 85801 from dual");
		Export.setFactory(factory);
		int counter = 0;
		while (rs.next()) {
//			System.out.println("Start feature");
			final Feature feature = factory.getFeatureDAO().getFeature(rs.getInt(1));
//			System.out.println("Finish feature");
//			new java.util.Date();
			export.handleFeature(feature);
//			System.out.println("Export: " + (new Date().getTime() - date.getTime()));
			//factory.getFeatureDAO().evictComplete(feature);
			if (counter++ > 50) {
				//Clean up the last 100
				factory.closeSession();
				counter = 0;
			}
//			System.out.println("Total: " + (new Date().getTime() - date.getTime()));
//			System.out.println("");
		}
		//Tests that the result contains the string 'FOSSIL RECORD NUMBER'
		pw.flush();
		
		conn.close();
		factory.closeSession();
	}
}
