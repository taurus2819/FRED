package nz.cri.gns.fred.export;

import java.io.IOException;
import java.util.Collection;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;

public interface FredRecordExport {

	/**
	 * Does whatever is appropriate with the given list
	 * @param feature
	 * @throws IOException 
	 */
	void handleList(Feature feature, Sample sample, AgeRange age, Paleontology list) throws IOException;
	
	/**
	 * Returns a list of the fossil lists that are to be exported for the give sample. 
	 * @param sample
	 * @return
	 * @throws StorageAccessException 
	 */
	Collection<Paleontology> getListsToExport(Sample sample) throws StorageAccessException;
	
	/**
	 * Returns the AgeRange appropriate for the given fossil list
	 * @throws StorageAccessException 
	 */
	AgeRange getAgeRange(Sample sample, Paleontology list) throws StorageAccessException;
}
