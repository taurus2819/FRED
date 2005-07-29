package nz.cri.gns.fred.hibernate.test;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import nz.cri.gns.auth.InvalidCredentialsException;
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

/**
 *
 */
public class FrOddities extends TestCase {

	private SessionFactory sessions;
	
	public void setUp() throws HibernateException, SQLException, InvalidCredentialsException, ClassNotFoundException {
		Properties props = new Properties();
		props.put("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver");
		props.put("hibernate.connection.url", "jdbc:oracle:thin:@raptor.gns.cri.nz:1521:gns");
		props.put("hibernate.connection.username", "fr");
		props.put("hibernate.connection.password", "ossify");
		props.put("hibernate.dialect", "net.sf.hibernate.dialect.Oracle9Dialect");
		props.put("hibernate.show_sql", "true");
		props.put("hibernate.cglib.use_reflection_optimizer", "false");

    	Configuration cfg = new Configuration().setProperties(props);
    	cfg.setInterceptor(new FREDInterceptor());
    	Class[] classes = getHibernateClasses();
    	for (int i=0; i<classes.length; i++) {
    		cfg.addClass(classes[i]);
    	}
    	
    	sessions = cfg.buildSessionFactory();
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
	
	public void testForOddities() throws HibernateException {
		Session session = sessions.openSession();
		List list = session.find("FROM Feature AS f WHERE f.featureType != 'Outcrop'");
		int count = 0;
		features: for (Iterator it = list.iterator(); it.hasNext(); ) {
			Feature feature = (Feature)it.next();
			System.out.println("Feature: " + feature.getFeatureId());
			nz.cri.gns.fred.model.FrNumber fr = null;
			for (Iterator samp = feature.getSamples().iterator(); samp.hasNext(); ) {
				Sample sample = (Sample)samp.next();
				nz.cri.gns.fred.model.FrNumber frNum = sample.getFrNumber();
				if (fr == null)
					fr = frNum;
				else if (!fr.equals(frNum)) {
					count++;
					continue features;
				}
			}
		}
		
		System.out.println("Affected features: " + count);
		session.close();
	}
}
