package nz.cri.gns.fred.export.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.hibernate.test.FredHibernateTest;
import nz.cri.gns.fred.model.Feature;

public class MolluscaExport extends FredHibernateTest {

	public void testExport() throws IOException, StorageAccessException {
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
		
		Iterator<Feature> features = factory.getFeatureDAO().getFeatures("SELECT DISTINCT f FROM Feature AS f JOIN f.samples AS s JOIN s.records AS r JOIN r.paleontology AS p JOIN p.listEntries AS e JOIN e.taxonomicGroup AS g WHERE g.name IN ('BIVALVIA', 'GASTROPODA', 'SCAPHOPODA')");
		
		while (features.hasNext()) {
			Feature feature = features.next();
			export.handleFeature(feature);
			
			factory.getFeatureDAO().evictComplete(feature);
		}
		//Tests that the result contains the string 'FOSSIL RECORD NUMBER'
		pw.flush();
	}
}
