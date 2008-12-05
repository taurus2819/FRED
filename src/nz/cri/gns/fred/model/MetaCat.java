package nz.cri.gns.fred.model;

public interface MetaCat extends Comparable<MetaCat> {
    public void setMetaId(Integer metaId);
	public Integer getMetaId();
	public void setTitle(String title);
	public String getTitle();
	public MetaCatType getMetaCatType();
	public void setMetaCatType(MetaCatType metaCatType);
}