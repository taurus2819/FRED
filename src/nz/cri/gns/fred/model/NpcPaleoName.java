package nz.cri.gns.fred.model;

public interface NpcPaleoName extends Comparable<NpcPaleoName> {
	public Integer getNameId();
    public void setNameId(Integer nameId); 
    public String getName();
    public void setName(String name);
    public void setTaxon(Taxon taxon);
	public Taxon getTaxon();
}