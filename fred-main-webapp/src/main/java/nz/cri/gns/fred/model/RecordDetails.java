package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

/**
 * A superinterface for Adoption and Paleontology
 */
public interface RecordDetails extends Comparable<RecordDetails> {
	public FREDRecord getRecord();
	public Date getDate();
	public String getDateRounding();
	public Set<Person> getPersons();
	
}
