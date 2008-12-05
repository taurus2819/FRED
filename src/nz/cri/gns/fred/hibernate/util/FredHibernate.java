package nz.cri.gns.fred.hibernate.util;

import net.sf.hibernate.Session;
import nz.cri.gns.dataaccess.HibernateConfiguration;
import nz.cri.gns.dataaccess.HibernateProvider;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.hibernate.dao.HibernateDAOFactory;

public class FredHibernate implements HibernateProvider {

	public static FredHibernate get() {
		return util;
	}
	
	private static final FredHibernate util;
	private static final HibernateDAOFactory factory;
	private HibernateConfiguration config;
	
	//private final SessionFactory sessionFactory;

    static {
    	util = new FredHibernate();
    	factory = new HibernateDAOFactory(util);
    }
    
    private FredHibernate() {
        /*try {
            // Create the SessionFactory
        	Configuration cfg = getConfiguration();
        	//cfg.setInterceptor(new FREDInterceptor());
            sessionFactory = cfg.buildSessionFactory();
        } catch (Throwable ex) {
        	ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }*/
		config = new HibernateConfiguration();
		try {
			config.configureJNDI("hazchem", getClass().getResource("hibernate.cfg.xml"));
		} catch (StorageAccessException e) {
			e.printStackTrace();
		}
    }

    /**
	 * @return
     * @throws MappingException
	 */
	/*private Configuration getConfiguration() throws MappingException {
		Properties props = new Properties();
        props.put("hibernate.connection.datasource", "java:comp/env/jdbc/fr");
		props.put("hibernate.dialect", "net.sf.hibernate.dialect.Oracle9Dialect");
		props.put("query.substitutions", "true=1, false=0"); 
		props.put("hibernate.show_sql", "true");

    	Configuration cfg = new Configuration().setProperties(props);
    	
    	Class[] classes = getHibernateClasses();
    	for (int i=0; i<classes.length; i++) {
    		cfg.addClass(classes[i]);
    	}
    	return cfg;
 	}*/

	/*private Class[] getHibernateClasses() {
		return new Class[] {
			Adoption.class,
			Age.class,
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
            LogTable.class,
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
			UserRightView.class,
			UserView.class,
			Weathering.class
		};
	}*/

	public static final ThreadLocal<Session> session = new ThreadLocal<Session>();

    public Session currentSession() throws StorageAccessException {
    	return config.currentSession();
       /* Session s = (Session) session.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) try {
            s = sessionFactory.openSession();
            session.set(s);
        } catch (HibernateException e) {
            throw new StorageAccessException(e);
        }
        return s;*/
    }

    public void closeSession() throws StorageAccessException {
    	config.closeSession();
        /*Session s = (Session) session.get();
        session.set(null);
        if (s != null) try {
        	s.flush();
        	s.close();
        } catch (HibernateException e) {
            throw new StorageAccessException(e);
        }*/
    }
    
    public DAOFactory getDAOFactory() {
    	return factory;
    }
}