package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class DataOrigin implements Serializable {

	private static final long serialVersionUID = 20050818L;
	
    /** identifier field */
    private Integer originId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private Set auditTablesByDataHistoryId;

    /** persistent field */
    private Set auditTablesByDataOriginId;

    /** full constructor */
    public DataOrigin(Integer originId, String name, String description, Set auditTablesByDataHistoryId, Set auditTablesByDataOriginId) {
        this.originId = originId;
        this.name = name;
        this.description = description;
        this.auditTablesByDataHistoryId = auditTablesByDataHistoryId;
        this.auditTablesByDataOriginId = auditTablesByDataOriginId;
    }

    /** default constructor */
    public DataOrigin() {
    }

    /** minimal constructor */
    public DataOrigin(Integer originId, String name, Set auditTablesByDataHistoryId, Set auditTablesByDataOriginId) {
        this.originId = originId;
        this.name = name;
        this.auditTablesByDataHistoryId = auditTablesByDataHistoryId;
        this.auditTablesByDataOriginId = auditTablesByDataOriginId;
    }

    public Integer getOriginId() {
        return this.originId;
    }

    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set getAuditTablesByDataHistoryId() {
        return this.auditTablesByDataHistoryId;
    }

    public void setAuditTablesByDataHistoryId(Set auditTablesByDataHistoryId) {
        this.auditTablesByDataHistoryId = auditTablesByDataHistoryId;
    }

    public Set getAuditTablesByDataOriginId() {
        return this.auditTablesByDataOriginId;
    }

    public void setAuditTablesByDataOriginId(Set auditTablesByDataOriginId) {
        this.auditTablesByDataOriginId = auditTablesByDataOriginId;
    }

    public String toString() {
        return name;
    }

}
