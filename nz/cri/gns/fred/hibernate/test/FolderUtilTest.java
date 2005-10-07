package nz.cri.gns.fred.hibernate.test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.hibernate.HibernateException;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.hibernate.dao.HibernateProvider;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;

import org.xml.sax.SAXException;

/**
 * @author iainm
 */
public class FolderUtilTest extends FredHibernateTest implements HibernateProvider {

	public void testFolders() throws HibernateException, StorageAccessException, SQLException, InvalidCredentialsException, ClassNotFoundException, NotBoundException, ParserConfigurationException, FactoryConfigurationError, SAXException, IOException {
		System.out.println("Personal");
		
		List list = new FolderUtil(factory).getPersonalFolders(user);
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			System.out.println(((UserFolder)it.next()).getFolder().getName());
		}
		System.out.println("Admin");
		list = new FolderUtil(factory).getAdminFolders(user);
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			System.out.println(((UserFolder)it.next()).getFolder().getName());
		}
	}

	public void testPanels() throws StorageAccessException, HibernateException {
		List list = new TaxonomicUtil(factory).getPanelsIsMemberOf(user);
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			System.out.println(((nz.cri.gns.fred.model.TaxonomicGroup)it.next()).getName());
		}
	}
	
	public void testConcurrency() throws HibernateException, StorageAccessException {
		nz.cri.gns.fred.model.Feature feature = factory.getFeatureDAO().getFeature(1);
		
		System.out.println("Original: " + feature.getFeatureName());
		
		feature.setFeatureName("Bob");
		
		nz.cri.gns.fred.model.Feature feature1 = factory.getFeatureDAO().getFeature(1);
		
		System.out.println("Changed: " + feature.getFeatureName());
		System.out.println("Regot:" + feature1.getFeatureName());
		
	}
}
