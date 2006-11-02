package nz.cri.gns.fred.model;

import java.util.Date;
import java.util.Set;

public interface Taxon extends Comparable<Taxon> {

	public static final String APPROVED_STATUS = "approved";
	public static final String PROVISIONAL_STATUS = "provisional";
	public static final String REJECTED_STATUS = "rejected";
	public static final String OBSOLETE_STATUS = "obsolete";
	
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
    public String getPanelistComments();
    public void setPanelistComments(String panelistComments);
    public String getSendMessage();
    public void setSendMessage(String sendMessage);
    public TaxonomicGroup getTaxonomicGroup();
    public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup);
	public FrUserView getSubmittedBy();
    public void setSubmittedBy(FrUserView submittedBy);
	public FrUserView getApprovedBy();
	public void setApprovedBy(FrUserView approvedBy);
    public Set<PaleontologyListEntry> getListEntries();
    public void setListEntries(Set<PaleontologyListEntry> palLists);

}