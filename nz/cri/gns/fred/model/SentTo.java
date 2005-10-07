package nz.cri.gns.fred.model;

import java.util.Date;

/**
 *
 */
public interface SentTo extends PersonRelationship {

	public abstract Date getSentDate();

	public abstract void setSentDate(Date sentDate);

	public abstract String getDateRounding();

	public abstract void setDateRounding(String dateRounding);

	public abstract Integer getLabId();

	public abstract void setLabId(Integer labId);

	public abstract String getComments();

	public abstract void setComments(String comments);

	public abstract nz.cri.gns.fred.model.FossilGroup getFossilGroup();

	public abstract void setFossilGroup(
			nz.cri.gns.fred.model.FossilGroup fossilGroup);

	public abstract nz.cri.gns.fred.model.Person getPerson();

	public abstract void setPerson(nz.cri.gns.fred.model.Person person);

	public abstract boolean equals(Object other);

	public abstract int hashCode();
}