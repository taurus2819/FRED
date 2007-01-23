package nz.cri.gns.fred.hibernate.util;

import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import nz.cri.gns.dataaccess.HibernateProvider;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.Adoption;
import nz.cri.gns.fred.hibernate.AgeView;
import nz.cri.gns.fred.hibernate.AuditEdit;
import nz.cri.gns.fred.hibernate.AuditTable;
import nz.cri.gns.fred.hibernate.BacklogStatus;
import nz.cri.gns.fred.hibernate.BedThickness;
import nz.cri.gns.fred.hibernate.Bedding;
import nz.cri.gns.fred.hibernate.Carbonate;
import nz.cri.gns.fred.hibernate.ColourModifier;
import nz.cri.gns.fred.hibernate.ConfidentialGroup;
import nz.cri.gns.fred.hibernate.Country;
import nz.cri.gns.fred.hibernate.DataOrigin;
import nz.cri.gns.fred.hibernate.DatumMethod;
import nz.cri.gns.fred.hibernate.DrillType;
import nz.cri.gns.fred.hibernate.Feature;
import nz.cri.gns.fred.hibernate.FeatureMeta;
import nz.cri.gns.fred.hibernate.Folder;
import nz.cri.gns.fred.hibernate.FolderRight;
import nz.cri.gns.fred.hibernate.FolderType;
import nz.cri.gns.fred.hibernate.FolderUser;
import nz.cri.gns.fred.hibernate.FossilGroup;
import nz.cri.gns.fred.hibernate.FrNumber;
import nz.cri.gns.fred.hibernate.FrUser;
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
import nz.cri.gns.fred.hibernate.Relationship;
import nz.cri.gns.fred.hibernate.RelationshipType;
import nz.cri.gns.fred.hibernate.RelationType;
import nz.cri.gns.fred.hibernate.RockColour;
import nz.cri.gns.fred.hibernate.Sample;
import nz.cri.gns.fred.hibernate.SampleMeta;
import nz.cri.gns.fred.hibernate.SecurityClass;
import nz.cri.gns.fred.hibernate.SedimentaryFeatureType;
import nz.cri.gns.fred.hibernate.SentTo;
import nz.cri.gns.fred.hibernate.SiteView;
import nz.cri.gns.fred.hibernate.Stage;
import nz.cri.gns.fred.hibernate.StratigraphicUnit;
import nz.cri.gns.fred.hibernate.TaxonomicGroup;
import nz.cri.gns.fred.hibernate.TaxonomicLookup;
import nz.cri.gns.fred.hibernate.UserView;
import nz.cri.gns.fred.hibernate.Weathering;
import nz.cri.gns.fred.hibernate.dao.FREDInterceptor;
import nz.cri.gns.fred.hibernate.dao.HibernateDAOFactory;

public class HibernateUtil implements HibernateProvider {

	public static HibernateUtil get() {
		return util;
	}
	
	private static final HibernateUtil util;
	private static final HibernateDAOFactory factory;
	
	private final SessionFactory sessionFactory;

    static {
    	util = new HibernateUtil();
    	factory = new HibernateDAOFactory(util);
    }
    
    private HibernateUtil() {
        try {
            // Create the SessionFactory
        	Configuration cfg = getConfiguration();
        	cfg.setInterceptor(new FREDInterceptor());
            sessionFactory = cfg.buildSessionFactory();
        } catch (Throwable ex) {
        	ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
	 * @return
     * @throws MappingException
	 */
	private Configuration getConfiguration() throws MappingException {
		Properties props = new Properties();
        props.put("hibernate.connection.datasource", "java:comp/env/jdbc/fr");
		props.put("hibernate.dialect", "net.sf.hibernate.dialect.Oracle9Dialect");
		props.put("hibernate.show_sql", "true");

    	Configuration cfg = new Configuration().setProperties(props);
    	
    	Class[] classes = getHibernateClasses();
    	for (int i=0; i<classes.length; i++) {
    		cfg.addClass(classes[i]);
    	}
    	return cfg;
 	}

	private Class[] getHibernateClasses() {
		return new Class[] {
			Adoption.class,
			AgeView.class,
			AuditEdit.class,
			AuditTable.class,
			BacklogStatus.class,
			Bedding.class,
			BedThickness.class,
			Carbonate.class,
			ColourModifier.class,
			ConfidentialGroup.class,
			Country.class,
			DataOrigin.class,
			DatumMethod.class,
			DrillType.class,
			Feature.class,
			FeatureMeta.class,
			Folder.class,
			FolderRight.class, 
			FolderType.class,
			FolderUser.class,
			FossilGroup.class,
			FrNumber.class,
			FrUser.class,
			FrUserView.class,
			GrainSize.class,
			Hardness.class,
            Lab.class,
            LabSection.class,
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
			StratigraphicUnit.class,
			TaxonomicGroup.class,
			TaxonomicLookup.class,
			UserView.class,
			Weathering.class
		};
	}

	public static final ThreadLocal<Session> session = new ThreadLocal<Session>();

    public Session currentSession() throws StorageAccessException {
        Session s = (Session) session.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) try {
            s = sessionFactory.openSession();
            session.set(s);
        } catch (HibernateException e) {
            throw new StorageAccessException(e);
        }
        return s;
    }

    public void closeSession() throws StorageAccessException {
        Session s = (Session) session.get();
        session.set(null);
        if (s != null) try {
        	s.flush();
        	s.close();
        } catch (HibernateException e) {
            throw new StorageAccessException(e);
        }
    }
    
    public DAOFactory getDAOFactory() {
    	return factory;
    }
}