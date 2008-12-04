package nz.cri.gns.fred.hibernate.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.dataaccess.HibernateProvider;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.BasicDatabaseApp2;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.Adoption;
import nz.cri.gns.fred.hibernate.Age;
import nz.cri.gns.fred.hibernate.AuditEdit;
import nz.cri.gns.fred.hibernate.AuditTable;
import nz.cri.gns.fred.hibernate.BedThickness;
import nz.cri.gns.fred.hibernate.Bedding;
import nz.cri.gns.fred.hibernate.Carbonate;
import nz.cri.gns.fred.hibernate.ColourModifier;
import nz.cri.gns.fred.hibernate.ConfidentialGroup;
import nz.cri.gns.fred.hibernate.DataOrigin;
import nz.cri.gns.fred.hibernate.DrillType;
import nz.cri.gns.fred.hibernate.Feature;
import nz.cri.gns.fred.hibernate.FeatureMeta;
import nz.cri.gns.fred.hibernate.Folder;
import nz.cri.gns.fred.hibernate.FolderRight;
import nz.cri.gns.fred.hibernate.FolderType;
import nz.cri.gns.fred.hibernate.FolderUser;
import nz.cri.gns.fred.hibernate.FossilGroup;
import nz.cri.gns.fred.hibernate.FrNumber;
import nz.cri.gns.fred.hibernate.FrUserView;
import nz.cri.gns.fred.hibernate.GrainSize;
import nz.cri.gns.fred.hibernate.Hardness;
import nz.cri.gns.fred.hibernate.Lab;
import nz.cri.gns.fred.hibernate.LabSection;
import nz.cri.gns.fred.hibernate.OrgView;
import nz.cri.gns.fred.hibernate.PalList;
import nz.cri.gns.fred.hibernate.PalListMeta;
import nz.cri.gns.fred.hibernate.Paleontology;
import nz.cri.gns.fred.hibernate.Person;
import nz.cri.gns.fred.hibernate.Record;
import nz.cri.gns.fred.hibernate.RecordMeta;
import nz.cri.gns.fred.hibernate.RegistrationArea;
import nz.cri.gns.fred.hibernate.RelationType;
import nz.cri.gns.fred.hibernate.Relationship;
import nz.cri.gns.fred.hibernate.RelationshipType;
import nz.cri.gns.fred.hibernate.RockColour;
import nz.cri.gns.fred.hibernate.Sample;
import nz.cri.gns.fred.hibernate.SampleMeta;
import nz.cri.gns.fred.hibernate.SecurityClass;
import nz.cri.gns.fred.hibernate.SedimentaryFeatureType;
import nz.cri.gns.fred.hibernate.SentTo;
import nz.cri.gns.fred.hibernate.SiteView;
import nz.cri.gns.fred.hibernate.Stage;
import nz.cri.gns.fred.hibernate.TaxonomicGroup;
import nz.cri.gns.fred.hibernate.TaxonomicLookup;
import nz.cri.gns.fred.hibernate.UserView;
import nz.cri.gns.fred.hibernate.Weathering;
import nz.cri.gns.fred.hibernate.dao.FREDInterceptor;
import nz.cri.gns.fred.hibernate.dao.HibernateDAOFactory;

public class FredHibernateTest extends TestCase implements HibernateProvider {

	private SessionFactory sessions;
	private Session session;
	protected User user;
	protected DAOFactory factory;

	public void setUp() throws HibernateException, SQLException, InvalidCredentialsException, ClassNotFoundException {
		Properties props = new Properties();
		props.put("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver");
		props.put("hibernate.connection.url", "jdbc:oracle:thin:@gryphon.gns.cri.nz:1521:dev");
		props.put("hibernate.connection.username", "fr");
		props.put("hibernate.connection.password", "ossify");
		props.put("hibernate.dialect", "net.sf.hibernate.dialect.Oracle9Dialect");
		props.put("hibernate.show_sql", "false");
		props.put("hibernate.cglib.use_reflection_optimizer", "true");
	
		Configuration cfg = new Configuration().setProperties(props);
		cfg.setInterceptor(new FREDInterceptor());
		Class[] classes = getHibernateClasses();
		for (int i=0; i<classes.length; i++) {
			cfg.addClass(classes[i]);
		}
		
		sessions = cfg.buildSessionFactory();
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:ip/manying@raptor:1521:gns");
		BasicDatabaseApp2 app = new BasicDatabaseApp2(conn, "1988");
		
		factory = new HibernateDAOFactory(this);
		
		try {
			user = new User("ben", "St.Bathans", app);
		} catch (Exception e) {
			System.out.println("No user created");
		}
	}

	private Class[] getHibernateClasses() {
		return new Class[] {
			Adoption.class,
			Age.class,
			AuditEdit.class,
			AuditTable.class,
			Bedding.class,
			BedThickness.class,
			Carbonate.class,
			ColourModifier.class,
			ConfidentialGroup.class,
			DataOrigin.class,
			DrillType.class,
			Feature.class,
			FeatureMeta.class,
			Folder.class,
			FolderRight.class, 
			FolderType.class,
			FolderUser.class,
			FossilGroup.class,
			FrNumber.class,
			FrUserView.class,
			GrainSize.class,
			Hardness.class,
	        LabSection.class,
	        Lab.class,
	        OrgView.class,
	        Paleontology.class,
			PalList.class,
			PalListMeta.class,
			Person.class,
			Record.class,
			RecordMeta.class,
			RegistrationArea.class,
			Relationship.class,
			RelationshipType.class,
			RelationType.class,
			RockColour.class,
			Sample.class,
			SampleMeta.class,
			SecurityClass.class,
			//SedimentaryFeature.class,
			SedimentaryFeatureType.class,
			SentTo.class,
			SiteView.class,
			Stage.class,
			TaxonomicGroup.class,
			TaxonomicLookup.class,
			UserView.class,
			Weathering.class
		};
	}

	public Session currentSession() throws StorageAccessException {
		//Single threaded - do it the easy way
		if (session == null) try {
			session = sessions.openSession();
		} catch (Exception e) {
		    throw new StorageAccessException(e);
        }
		return session;
	}

	public void closeSession() throws StorageAccessException {
        try {
    		session.flush();
    		session.close();
    		session = null;
        } catch (Exception e) {
            throw new StorageAccessException(e);
        }
	}

	public void tearDown() throws HibernateException {
		sessions.close();
	}

}
