package nz.cri.gns.fred.dao;

import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;

/**
 *
 */
public interface SampleDAO {

	/**
	 * @param sample
	 * @return
	 */
	public Sample cloneSample(Sample sample);

	/**
	 * @param relationship
	 * @return
	 */
	public Relationship cloneRelationship(Relationship relationship);

	/**
	 * @param sentTo
	 * @return
	 */
	public SentTo cloneSentTo(SentTo sentTo);

	/**
	 * @param sedFeature
	 * @return
	 */
	public SedimentaryFeature cloneSedimentaryFeature(SedimentaryFeature sedFeature);

	/**
	 * @return
	 */
	public SampleMeta createSampleMeta();

	/**
	 * @param newSample
	 * @throws StorageAccessException
	 */
	public Sample save(Sample newSample) throws StorageAccessException;

}
