package nz.cri.gns.fred.hibernate.util;

import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.Adoption;
import nz.cri.gns.fred.hibernate.AuditEdit;
import nz.cri.gns.fred.hibernate.AuditTable;
import nz.cri.gns.fred.hibernate.BedThickness;
import nz.cri.gns.fred.hibernate.Bedding;
import nz.cri.gns.fred.hibernate.Carbonate;
import nz.cri.gns.fred.hibernate.ColourModifier;
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
import nz.cri.gns.fred.hibernate.GrainSize;
import nz.cri.gns.fred.hibernate.Hardness;
import nz.cri.gns.fred.hibernate.Lab;
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
import nz.cri.gns.fred.hibernate.dao.FREDInterceptor;
import nz.cri.gns.fred.hibernate.dao.HibernateDAOFactory;
import nz.cri.gns.fred.hibernate.dao.HibernateProvider;

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
			AuditEdit.class,
			AuditTable.class,
			Bedding.class,
			BedThickness.class,
			Carbonate.class,
			ColourModifier.class,
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
			GrainSize.class,
			Hardness.class,
            Lab.class,
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

	public static final ThreadLocal<Session> session = new ThreadLocal<Session>();

    public Session currentSession() throws HibernateException {
        Session s = (Session) session.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) {
            s = sessionFactory.openSession();
            session.set(s);
        }
        return s;
    }

    public void closeSession() throws HibernateException {
        Session s = (Session) session.get();
        session.set(null);
        if (s != null)
            s.close();
    }
    
    public DAOFactory getDAOFactory() {
    	return factory;
    }
}