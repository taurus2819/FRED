package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

public interface TaxonomicLookup {

    public Integer getTaxaId();

    public void setTaxaId(Integer taxaId);

    public String getTaxonomicName();

    public void setTaxonomicName(String taxonomicName);

    public String getAuthor();

    public void setAuthor(String author);

    public String getStatus();

    public void setStatus(String status);

    public Integer getSubmittedById();

    public void setSubmittedById(Integer submittedById);

    public Date getSubmittedDate();

    public void setSubmittedDate(Date submittedDate);

    public Integer getApprovedById();

    public void setApprovedById(Integer approvedById);

    public Date getApprovedDate();

    public void setApprovedDate(Date approvedDate);

    public String getSendMessage();

    public void setSendMessage(String sendMessage);

    public TaxonomicGroup getTaxonomicGroup();

    public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup);

    public Set getPalLists();

    public void setPalLists(Set palLists);

}