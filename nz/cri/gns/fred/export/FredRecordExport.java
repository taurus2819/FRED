package nz.cri.gns.fred.export;

import java.io.IOException;
import java.util.List;

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
	 */
	List<Paleontology> getListsToExport(Sample sample);
	
	/**
	 * Returns the AgeRange appropriate for the given fossil list
	 */
	AgeRange getAgeRange(Sample sample, Paleontology list);
}
