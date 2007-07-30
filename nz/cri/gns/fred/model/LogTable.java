package nz.cri.gns.fred.model;

import java.util.Date;

public interface LogTable extends Comparable<LogTable> {
	public void setLogId(Integer logId);
	public Integer getLogId();
	public void setLogType(String logType);
	public String getLogType();
	public void setLogDate(Date logDate);
	public Date getLogDate();
}
