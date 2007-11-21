package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Date;

import nz.cri.gns.fred.model.UserView;

public class LogTable implements Serializable, nz.cri.gns.fred.model.LogTable {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer logId;

    private String logType;
    private Date logDate;
    private Integer userId;
    private Integer localityCount;
    private UserView user;
    
    /** full constructor */
    public LogTable(Integer logId, String logType, Date lastLogin, Integer userId, Integer localityCount, UserView user) {
    	this.logId = logId;
    	this.logType = logType;
    	this.logDate = lastLogin;
    	this.userId = userId;
    	this.localityCount = localityCount;
    	this.user = user;
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

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}
	
	public void setLocalityCount(Integer localityCount) {
		this.localityCount = localityCount;
	}

	public Integer getLocalityCount() {
		return localityCount;
	}

	public void setUser(UserView editedBy) {
		this.user = editedBy;
	}

	public UserView getUser() {
		return user;
	}

	public int compareTo(nz.cri.gns.fred.model.LogTable arg0) {
		return (logDate).compareTo(arg0.getLogDate());
	}

}
