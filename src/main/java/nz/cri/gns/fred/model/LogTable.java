package nz.cri.gns.fred.model;

import java.util.Date;

public interface LogTable extends Comparable<LogTable> {
	public void setLogId(Integer logId);
	public Integer getLogId();
	public void setLogType(String logType);
	public String getLogType();
	public void setLogDate(Date logDate);
	public Date getLogDate();
	public void setUserId(Integer userId);
	public Integer getUserId();
	public void setLocalityCount(Integer localityCount);
	public Integer getLocalityCount();
	public void setUser(UserView editedBy);
	public UserView getUser();
}
