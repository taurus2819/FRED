package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Lab extends Comparable<Lab>, NameableAndIdentifiable {
    public Integer getLabId();
    public void setLabId(Integer labId);
    public String getName();
    public void setName(String name);
    public String getAddress();
    public void setAddress(String address);
    public String getCountry();
    public void setCountry(String country);
    public Set<LabSection> getSections();
    public void setSections(Set<LabSection> sections);
	public Set<SentTo> getSentTos();
	public void setSentTos(Set<SentTo> sentTos);
}
