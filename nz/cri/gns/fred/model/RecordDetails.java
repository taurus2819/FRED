package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

/**
 * A superinterface for Adoption and Paleontology
 */
public interface RecordDetails {
	
	public Date getDate();
	public String getDateRounding();
	public Set getPersons();
	
}
