package nz.cri.gns.fred.model;

import java.util.Set;

public interface Lab {
    public Integer getLabId();
    public void setLabId(Integer labId);
    public String getName();
    public void setName(String name);
    public String getAddress();
    public void setAddress(String address);
    public String getCountry();
    public void setCountry(String country);
    public Set getSections();
    public void setSections(Set sections);
}
