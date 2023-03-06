package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

public class BacklogStatus implements Serializable, nz.cri.gns.fred.model.BacklogStatus {

    private static final long serialVersionUID = 20050818L;

    /**
     * identifier field
     */
    private Integer objectId;

    /**
     * persistent field
     */
    private String mapNumber;

    /**
     * persistent field
     */
    private String mapName;

    /**
     * persistent field
     */
    private String status;

    /**
     * persistent field
     */
    private Integer masterfileId;

    /**
     * persistent field
     */
    private Integer localityCount;

    /**
     * persistent field
     */
    private Integer processingCount;

    /**
     * persistent field
     */
    private Integer completedCount;

    /**
     * persistent field
     */
    private Integer newCount;

    /**
     * full constructor
     *
     * @param objectId
     * @param mapNumber
     * @param mapName
     * @param status
     * @param masterfileId
     * @param localityCount
     * @param processingCount
     * @param completedCount
     * @param newCount
     */
    public BacklogStatus(Integer objectId, String mapNumber, String mapName, String status, Integer masterfileId, Integer localityCount, Integer processingCount, Integer completedCount, Integer newCount) {
        this.objectId = objectId;
        this.mapNumber = mapNumber;
        this.mapName = mapName;
        this.status = status;
        this.masterfileId = masterfileId;
        this.localityCount = localityCount;
        this.processingCount = processingCount;
        this.completedCount = completedCount;
        this.newCount = newCount;
    }

    /**
     * default constructor
     */
    public BacklogStatus() {
    }

    @Override
    public Integer getObjectId() {
        return this.objectId;
    }

    @Override
    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    @Override
    public String getMapNumber() {
        return mapNumber;
    }

    @Override
    public void setMapNumber(String mapNumber) {
        this.mapNumber = mapNumber;
    }

    @Override
    public String getMapName() {
        return this.mapName;
    }

    @Override
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Integer getMasterfileId() {
        return this.masterfileId;
    }

    @Override
    public void setMasterfileId(Integer masterfileId) {
        this.masterfileId = masterfileId;
    }

    @Override
    public Integer getLocalityCount() {
        return this.localityCount;
    }

    @Override
    public void setLocalityCount(Integer localityCount) {
        this.localityCount = localityCount;
    }

    @Override
    public Integer getProcessingCount() {
        return this.processingCount;
    }

    @Override
    public void setProcessingCount(Integer processingCount) {
        this.processingCount = processingCount;
    }

    @Override
    public Integer getCompletedCount() {
        return this.completedCount;
    }

    @Override
    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    @Override
    public Integer getNewCount() {
        return this.newCount;
    }

    @Override
    public void setNewCount(Integer newCount) {
        this.newCount = newCount;
    }

    @Override
    public Integer getNotStartedCount() {
        return this.localityCount - this.processingCount - this.completedCount - this.newCount;
    }

    @Override
    public String toString() {
        return mapNumber;
    }

    @Override
    public int compareTo(nz.cri.gns.fred.model.BacklogStatus arg0) {
        return mapNumber.compareTo(((BacklogStatus) arg0).getMapNumber());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BacklogStatus && ((BacklogStatus) o).getObjectId().equals(objectId);
    }

    @Override
    public int hashCode() {
        return 943 * objectId;
    }
}
