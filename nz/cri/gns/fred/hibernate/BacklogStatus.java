package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

public class BacklogStatus implements Serializable, nz.cri.gns.fred.model.BacklogStatus {

    //private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer objectId;

    /** persistent field */
    private String mapNumber;

    /** persistent field */
    private String mapName;
    
    /** persistent field */
    private String status;

    /** persistent field */
    private Integer masterfileId;

    /** persistent field */
    private Integer localityCount;
    
    /** persistent field */
    private Integer processingCount;
    
    /** persistent field */
    private Integer completedCount;
    
    /** full constructor */
    public BacklogStatus(Integer objectId, String mapNumber, String mapName, String status, Integer masterfileId, Integer localityCount, Integer processingCount, Integer completedCount) {
        this.objectId = objectId;
        this.mapNumber = mapNumber;
        this.mapName = mapName;
        this.status = status;
        this.masterfileId = masterfileId;
        this.localityCount = localityCount;
        this.processingCount = processingCount;
        this.completedCount = completedCount;
    }

    /** default constructor */
    public BacklogStatus() {
    }

	public Integer getObjectId() {
		return this.objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public String getMapNumber() {
		return mapNumber;
	}

	public void setMapNumber(String mapNumber) {
		this.mapNumber = mapNumber;
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getMasterfileId() {
		return this.masterfileId;
	}

	public void setMasterfileId(Integer masterfileId) {
		this.masterfileId = masterfileId;
	}

	public Integer getLocalityCount() {
		return this.localityCount;
	}

	public void setLocalityCount(Integer localityCount) {
		this.localityCount = localityCount;
	}

	public Integer getProcessingCount() {
		return this.processingCount;
	}

	public void setProcessingCount(Integer processingCount) {
		this.processingCount = processingCount;
	}

	public Integer getCompletedCount() {
		return this.completedCount;
	}

	public void setCompletedCount(Integer completedCount) {
		this.completedCount = completedCount;
	}
   
    public String toString() {
        return mapNumber;
    }

}
