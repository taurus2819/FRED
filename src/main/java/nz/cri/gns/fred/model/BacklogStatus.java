package nz.cri.gns.fred.model;

public interface BacklogStatus extends Comparable<BacklogStatus> {
	
	public Integer getObjectId();
	public void setObjectId(Integer objectId);
	public String getMapNumber();
	public void setMapNumber(String mapNumber);
	public String getMapName();
	public void setMapName(String mapName);
	public String getStatus();
	public void setStatus(String status);
	public Integer getMasterfileId();
	public void setMasterfileId(Integer masterfileId);
	public Integer getLocalityCount();
	public void setLocalityCount(Integer localityCount);
	public Integer getProcessingCount();
	public void setProcessingCount(Integer processingCount);
	public Integer getCompletedCount();
	public void setCompletedCount(Integer completedCount);
	public Integer getNewCount();
	public void setNewCount(Integer newCount);
	public Integer getNotStartedCount();
	
}