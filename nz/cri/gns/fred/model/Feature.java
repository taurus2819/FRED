package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

/**
 *
 */
public interface Feature {

	public Integer getFeatureId();
    public void setFeatureId(Integer featureId);
    public Integer getSiteId();
    public void setSiteId(Integer siteId);
    public String getLocality();
    public void setLocality(String locality);
    public String getComments();
    public void setComments(String comments);
    public String getFeatureType();
    public void setFeatureType(String featureType);
    public String getFeatureName();
    public void setFeatureName(String featureName);
    public String getDrillholeLicenceName();
    public void setDrillholeLicenceName(String drillholeLicenceName);
    public Date getStartDate();
    public void setStartDate(Date startDate);
    public Date getFinishDate();
    public void setFinishDate(Date finishDate);
    public double getDatumElevation();
    public void setDatumElevation(double datumElevation);
    public double getStartDepth();
    public void setStartDepth(double startDepth);
    public String getDatumType();
    public void setDatumType(String datumType);
    public double getFinishDepth();
    public void setFinishDepth(double finishDepth);
    public String getStartDateRounding();
    public void setStartDateRounding(String startDateRounding);
    public String getFinishDateRounding();
    public void setFinishDateRounding(String finishDateRounding);
    public nz.cri.gns.fred.model.Person getPerson();
    public void setPerson(nz.cri.gns.fred.model.Person person);
    public nz.cri.gns.fred.model.Folder getMasterFile();
    public void setMasterFile(nz.cri.gns.fred.model.Folder masterFile);
    public nz.cri.gns.fred.model.Audit getAudit();
    public void setAudit(nz.cri.gns.fred.model.Audit audit);
    public nz.cri.gns.fred.model.RegistrationArea getRegistrationArea();
    public void setRegistrationArea(nz.cri.gns.fred.model.RegistrationArea registrationArea);
    public Set getSamples();
    public void setSamples(Set samples);
    public Set getFolders();
    public void setFolders(Set folders);
    public Set getRelationships();
    public void setRelationships(Set relationships);
    public Set getFeatureMetas();
    public void setFeatureMetas(Set featureMetas);
}
