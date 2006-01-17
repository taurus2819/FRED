package nz.cri.gns.fred.model;


/**
 *
 */
public interface BacklogStatus extends Comparable<BacklogStatus> {
	
	public abstract Integer getObjectId();

	public abstract void setObjectId(Integer objectId);

	public abstract String getMapNumber();

	public abstract void setMapNumber(String mapNumber);

	public abstract String getMapName();

	public abstract void setMapName(String mapName);

	public abstract String getStatus();

	public abstract void setStatus(String status);
	
	public abstract Integer getMasterfileId();

	public abstract void setMasterfileId(Integer masterfileId);

	public abstract Integer getLocalityCount();

	public abstract void setLocalityCount(Integer localityCount);
	
	public abstract Integer getProcessingCount();

	public abstract void setProcessingCount(Integer processingCount);
	
	public abstract Integer getCompletedCount();

	public abstract void setCompletedCount(Integer completedCount);
}