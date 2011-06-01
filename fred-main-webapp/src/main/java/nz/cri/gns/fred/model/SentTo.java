package nz.cri.gns.fred.model;

import java.util.Date;

public interface SentTo extends PersonRelationship {
    public Integer getSentToId();
    public void setSentToId(Integer sentToId);
	public Date getSentDate();
	public void setSentDate(Date sentDate);
	public String getDateRounding();
	public void setDateRounding(String dateRounding);
	public String getComments();
	public void setComments(String comments);
    public Sample getSample();
    public void setSample(Sample sample);
	public FossilGroup getFossilGroup();
	public void setFossilGroup(FossilGroup fossilGroup);
	public Person getPerson();
	public void setPerson(Person person);
	public Lab getLab();
    public void setLab(Lab lab);
	public boolean equals(Object other);
	public int hashCode();
}