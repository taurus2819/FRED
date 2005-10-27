package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

/**
 *
 */
public interface Feature extends Audited, Comparable<Feature> {

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
    public Double getDatumElevation();
    public void setDatumElevation(Double datumElevation);
    public Double getStartDepth();
    public void setStartDepth(Double startDepth);
    public String getDatumType();
    public void setDatumType(String datumType);
    public Double getFinishDepth();
    public void setFinishDepth(Double finishDepth);
    public String getStartDateRounding();
    public void setStartDateRounding(String startDateRounding);
    public String getFinishDateRounding();
    public void setFinishDateRounding(String finishDateRounding);
    public nz.cri.gns.fred.model.Person getPerson();
    public void setPerson(nz.cri.gns.fred.model.Person person);
    public nz.cri.gns.fred.model.Folder getMasterFile();
    public void setMasterFile(nz.cri.gns.fred.model.Folder masterFile);
    public nz.cri.gns.fred.model.RegistrationArea getRegistrationArea();
    public void setRegistrationArea(nz.cri.gns.fred.model.RegistrationArea registrationArea);
    public Set<Sample> getSamples();
    public void setSamples(Set<Sample> samples);
    public Set<Folder> getFolders();
    public void setFolders(Set<Folder> folders);
    public Set getRelationships();
    public void setRelationships(Set relationships);
    public Set<FeatureMeta> getFeatureMetas();
    public void setFeatureMetas(Set<FeatureMeta> featureMetas);
}
