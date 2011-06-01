package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.model.MetaCatType;

public class MetaCat implements Serializable, nz.cri.gns.fred.model.MetaCat {
	
	private static final long serialVersionUID = 20060725L;

    private Integer metaId;
    private String title;
    private Integer sizeKb;
    private MetaCatType metaCatType;

	public Integer getMetaId() {
		return metaId;
	}
	
	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSizeKb(Integer sizeKb) {
		this.sizeKb = sizeKb;
	}

	public Integer getSizeKb() {
		return sizeKb;
	}

	public MetaCatType getMetaCatType() {
		return metaCatType;
	}
	
	public void setMetaCatType(MetaCatType metaCatType) {
		this.metaCatType = metaCatType;
	}

	public int compareTo(nz.cri.gns.fred.model.MetaCat arg0) {
		return title.compareTo(arg0.getTitle());
	}
	
}