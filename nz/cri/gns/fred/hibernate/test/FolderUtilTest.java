package nz.cri.gns.fred.hibernate.test;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.hibernate.FeatureMeta;
import nz.cri.gns.fred.hibernate.Folder;
import nz.cri.gns.fred.hibernate.FolderRight;
import nz.cri.gns.fred.hibernate.FolderType;
import nz.cri.gns.fred.hibernate.FolderUser;
import nz.cri.gns.fred.hibernate.FossilGroup;
import nz.cri.gns.fred.hibernate.FrNumber;
import nz.cri.gns.fred.hibernate.GrainSize;
import nz.cri.gns.fred.hibernate.Hardness;
import nz.cri.gns.fred.hibernate.LabSection;
import nz.cri.gns.fred.hibernate.PalList;
import nz.cri.gns.fred.hibernate.Paleontology;
import nz.cri.gns.fred.hibernate.Person;
import nz.cri.gns.fred.hibernate.Record;
import nz.cri.gns.fred.hibernate.RecordMeta;
import nz.cri.gns.fred.hibernate.RegistrationArea;
import nz.cri.gns.fred.hibernate.Relationship;
import nz.cri.gns.fred.hibernate.RelationshipType;
import nz.cri.gns.fred.hibernate.RelationshipTypeType;
import nz.cri.gns.fred.hibernate.RockColour;
import nz.cri.gns.fred.hibernate.Sample;
import nz.cri.gns.fred.hibernate.SampleMeta;
import nz.cri.gns.fred.hibernate.SecurityClass;
import nz.cri.gns.fred.hibernate.SedimentaryFeature;
import nz.cri.gns.fred.hibernate.SedimentaryFeatureType;
import nz.cri.gns.fred.hibernate.SentTo;
import nz.cri.gns.fred.hibernate.Stage;
import nz.cri.gns.fred.hibernate.TaxaPanel;
import nz.cri.gns.fred.hibernate.TaxonomicGroup;
import nz.cri.gns.fred.hibernate.TaxonomicLookup;
import nz.cri.gns.fred.hibernate.Weathering;
import nz.cri.gns.fred.hibernate.dao.HibernateDAOFactory;
import nz.cri.gns.fred.hibernate.dao.HibernateProvider;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.TaxonomiclUtil;

import junit.framework.TestCase;

/**
 * @author iainm
 */
public class FolderUtilTest extends TestCase implements HibernateProvider {

	private SessionFactory sessions;
	private Session session;

	public void setUp() throws HibernateException {
		Properties props = new Properties();
		props.put("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver");
		props.put("hibernate.connection.url", "jdbc:oracle:thin:@raptor.gns.cri.nz:1521:dev");
		props.put("hibernate.connection.username", "fr");
		props.put("hibernate.connection.password", "ossify");
		props.put("hibernate.dialect", "net.sf.hibernate.dialect.Oracle9Dialect");
		props.put("hibernate.show_sql", "true");
		props.put("hibernate.cglib.use_reflection_optimizer", "false");

    	Configuration cfg = new Configuration().setProperties(props);
    	
    	Class[] classes = getHibernateClasses();
    	for (int i=0; i<classes.length; i++) {
    		cfg.addClass(classes[i]);
    	}
    	
    	sessions = cfg.buildSessionFactory();
	}

	private Class[] getHibernateClasses() {
		return new Class[] {
			nz.cri.gns.fred.hibernate.Adoption.class,
			nz.cri.gns.fred.hibernate.AuditEdit.class,
			nz.cri.gns.fred.hibernate.AuditTable.class,
			nz.cri.gns.fred.hibernate.Bedding.class,
			nz.cri.gns.fred.hibernate.BedThickness.class,
			nz.cri.gns.fred.hibernate.Carbonate.class,
			nz.cri.gns.fred.hibernate.ColourModifier.class,
			nz.cri.gns.fred.hibernate.DataOrigin.class,
			nz.cri.gns.fred.hibernate.DrillType.class,
			nz.cri.gns.fred.hibernate.Feature.class,
			FeatureMeta.class,
			Folder.class,
			FolderRight.class, 
			FolderType.class,
			FolderUser.class,
			FossilGroup.class,
			FrNumber.class,
			GrainSize.class,
			Hardness.class,
			LabSection.class,
			Paleontology.class,
			PalList.class,
			Person.class,
			Record.class,
			RecordMeta.class,
			RegistrationArea.class,
			Relationship.class,
			RelationshipType.class,
			RelationshipTypeType.class,
			RockColour.class,
			Sample.class,
			SampleMeta.class,
			SecurityClass.class,
			SedimentaryFeature.class,
			SedimentaryFeatureType.class,
			SentTo.class,
			Stage.class,
			TaxaPanel.class,
			TaxonomicGroup.class,
			TaxonomicLookup.class,
			Weathering.class
		};
	}
	
	public void testFolders() throws HibernateException, StorageAccessException {
		Session session = sessions.openSession();
		
		DAOFactory factory = new HibernateDAOFactory(this);
		
		System.out.println("Personal");
		List list = new FolderUtil(factory).getPersonalFolders(1988);
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			System.out.println(((Folder)it.next()).getName());
		}
		System.out.println("Admin");
		list = new FolderUtil(factory).getAdminFolders(1988);
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			System.out.println(((Folder)it.next()).getName());
		}
	}

	public void testPanels() throws StorageAccessException, HibernateException {
		Session session = sessions.openSession();
		
		DAOFactory factory = new HibernateDAOFactory(this);
	
		List list = new TaxonomiclUtil(factory).getPanelsIsMemberOf(1988);
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			System.out.println(((nz.cri.gns.fred.model.TaxonomicGroup)it.next()).getName());
		}
	}
	
	public Session currentSession() throws HibernateException {
		//Single threaded - do it the easy way
		if (session == null) {
			session = sessions.openSession();
		}
		return session;
	}

	public void closeSession() throws HibernateException {
		session.close();
		session = null;
		
	}
	
	public void tearDown() throws HibernateException {
		sessions.close();
	}
}
