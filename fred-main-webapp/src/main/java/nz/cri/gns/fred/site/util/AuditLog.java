/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.site.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author sitikond
 */
public class AuditLog {
    
    private static final long serialVersionUID = 1L;
    
    
    private Integer auditLogId;
    private Integer auditSiteId;
    private JsonNode logInfo;	    //example value  {"timestamp": "20200810", "loginfo":"Site updatd to new coords"}
    private SiteModel siteModel;

    public AuditLog() {
    }   
    
    public AuditLog(Integer siteId, JsonNode logInfo){ //, SiteModel siteModel) {
        super();
        this.auditSiteId = siteId;
        this.logInfo = logInfo;
//        this.siteModel = siteModel;
    }

    public Integer getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Integer auditLogId) {
        this.auditLogId = auditLogId;
    }

    public Integer getAuditSiteId() {
        return auditSiteId;
    }

    public void setAuditSiteId(Integer siteId) {
        this.auditSiteId = siteId;
    }

    public JsonNode getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(JsonNode logInfo) {
        this.logInfo = logInfo;
    }

    public SiteModel getSiteModel() {
        return siteModel;
    }

    public void setSiteModel(SiteModel siteModel) {
        this.siteModel = siteModel;
    }

    @Override
    public int hashCode() {
        return this.auditLogId == null ? 0 : this.auditLogId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuditLog other = (AuditLog) obj;
        if (!Objects.equals(this.auditLogId, other.auditLogId)) {
            return false;
        }
        if (!Objects.equals(this.auditSiteId, other.auditSiteId)) {
            return false;
        }
        if (!Objects.equals(this.siteModel, other.siteModel)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        return "AuditLog: siteid = " + this.auditSiteId + " SiteModeID = " + this.siteModel.getSiteId() + " Log = " + this.logInfo;
    }
}
