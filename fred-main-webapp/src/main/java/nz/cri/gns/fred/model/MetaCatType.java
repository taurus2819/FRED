package nz.cri.gns.fred.model;

import java.util.Set;

public interface MetaCatType extends Comparable<MetaCatType> {
    public Integer getTypeId();
    public void setTypeId(Integer typeId);
    public String getName();
    public void setName(String name);
    public Set<MetaCat> getMetaCats();
    public void setMetaCats(Set<MetaCat> metaCats);
}
