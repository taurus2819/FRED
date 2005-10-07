package nz.cri.gns.fred.hibernate.test;

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import net.sf.hibernate.HibernateException;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.SampleUtil;

public class SampleTest extends FredHibernateTest {


	private Feature feature;
	private Vector<Sample> samples;

	public void testSentTos() throws StorageAccessException, IntrospectionException {
		List<UserFolder> list = new FolderUtil(factory).getPersonalFolders(user);
		UserFolder folder = list.get(0);

		FeatureUtil featureUtil = new FeatureUtil(factory);
		feature = featureUtil.createFeature(folder.getFolderId().intValue(), FREDConstants.OUTCROP);
		featureUtil.saveFeature(feature, user, null);
		
		SampleUtil sampleUtil = new SampleUtil(factory);
		samples = new Vector<Sample>();
		Sample sample = sampleUtil.createSample(feature, folder.getFolderId().intValue(), true, user);
		samples.add(sample);
		FossilGroup group = sampleUtil.getFossilGroup("microflora");
		Person persom = new PersonUtil(factory).findPerson("Adachi, M.");
		int labId = 1;
		String comments = "La comments";
		
		SentTo sentTo = sampleUtil.findOrCreateSentTo(sample, group, persom, labId, comments);
		HashSet<SentTo> st = new HashSet<SentTo>();
		st.add(sentTo);
		sample.setSentTos(st);
		
		sampleUtil.save(sample);
		
		factory.closeSession();
		
		//Recreate it
		sample = sampleUtil.getSample(sample.getSampleId().intValue());
		
		//Check the sentto
		SentTo savedSentTo = sample.getSentTos().iterator().next();
		assertEquals(group, savedSentTo.getFossilGroup());
		assertEquals(persom, savedSentTo.getPerson());
		assertEquals(labId, savedSentTo.getLabId().intValue());
		assertEquals(comments, savedSentTo.getComments());
		
		System.out.println("Original: " + sample.getSentTos().iterator().next());
		
		st = new HashSet<SentTo>();
		sentTo = sampleUtil.findOrCreateSentTo(sample, group, persom, labId, comments);
		System.out.println("New: " + sentTo);
		st.add(sentTo);
		sample.setSentTos(st);
		//sample.getSentTos().clear();
		
		sampleUtil.saveOrUpdate(sample);
		
		//Check cloning
		Sample sample2 = featureUtil.cloneSample(feature, sample);
		sample2.setAudit(sample.getAudit());
		System.out.println(sample2);
		sampleUtil.saveOrUpdate(sample2);
		
		factory.closeSession();
		
		sample = sampleUtil.getSample(sample.getSampleId().intValue());
		sample2 = sampleUtil.getSample(sample2.getSampleId().intValue());
		
		savedSentTo = sample.getSentTos().iterator().next();
		assertEquals(group, savedSentTo.getFossilGroup());
		assertEquals(persom, savedSentTo.getPerson());
		assertEquals(labId, savedSentTo.getLabId().intValue());
		assertEquals(comments, savedSentTo.getComments());
		savedSentTo = sample2.getSentTos().iterator().next();
		assertEquals(group, savedSentTo.getFossilGroup());
		assertEquals(persom, savedSentTo.getPerson());
		assertEquals(labId, savedSentTo.getLabId().intValue());
		assertEquals(comments, savedSentTo.getComments());
		
		factory.closeSession();
	}
	
	public void tearDown() throws HibernateException {
		try {
			for (Sample sample : samples)
				factory.getSampleDAO().delete(sample);
			factory.getFeatureDAO().delete(feature);
		} catch (Exception e) {
			//throw (HibernateException)e.getCause();
		}
		super.tearDown();
	}
}
