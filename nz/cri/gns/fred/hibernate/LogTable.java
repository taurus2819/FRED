package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;

public class LogTable implements Serializable, nz.cri.gns.fred.model.LogTable {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer logId;

    private String logType;
    private Date logDate;
    
    /** full constructor */
    public LogTable(Integer userId, String logType, Date lastLogin) {
    	this.logId = userId;
    	this.logType = logType;
    	this.logDate = lastLogin;
    }

    /** default constructor */
    public LogTable() {
    }

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Integer getLogId() {
		return logId;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public Date getLogDate() {
		return logDate;
	}

	public int compareTo(nz.cri.gns.fred.model.LogTable arg0) {
		return (logDate).compareTo(arg0.getLogDate());
	}

}
