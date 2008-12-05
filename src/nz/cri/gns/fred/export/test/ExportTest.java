package nz.cri.gns.fred.export.test;

import java.io.IOException;
import java.io.Writer;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.export.OldFormatFredExport;
import nz.cri.gns.fred.hibernate.test.FredHibernateTest;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;

public class ExportTest extends FredHibernateTest {

	/**
	 * Test method to test the export feature.
	 * @throws StorageAccessException 
	 * @throws IOException 
	 */
	public void testExport() throws StorageAccessException, IOException {
		//Creates a export handler
		
		Writer pw = new Writer() {
		
			@Override
			public void write(String str, int off, int len) throws IOException {
				System.out.print(str.substring(off, off+len));
			}
		
			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				System.out.print(new String(cbuf, off, len));
			}
		
			@Override
			public void flush() throws IOException {
				System.out.flush();
			}
		
			@Override
			public void close() throws IOException {
			}
		};
		OldFormatFredExport offe = new OldFormatFredExport(pw);
		
		//Sends feature 51060 and 1651 to it
		Feature feature = factory.getFredDAO().get(51060, Feature.class);
		Sample sample = feature.getSamples().iterator().next();
		Paleontology list = sample.getRecords().iterator().next().getPaleontology();
		AgeRange age = offe.getAgeRange(sample, list);
		offe.handleList(feature, sample, age, list);
		feature = factory.getFredDAO().get(1651, Feature.class);
		sample = feature.getSamples().iterator().next();
		list = sample.getRecords().iterator().next().getPaleontology();
		age = offe.getAgeRange(sample, list);
		offe.handleList(feature, sample, age, list);
		
		//Tests that the result contains the string 'FOSSIL RECORD NUMBER'
		pw.flush();
		
	}

}
