package nz.cri.gns.db.fred;

import java.util.Date;

public class SentTo {

	private String sentTo;
	private Integer fossilGroupId;
	private String fossilGroup;
	private Date date;
	private String dateRounding;
	private Integer personId;
	private String person;
	private Integer labId;
	private String lab;
	private String comments;

	public SentTo(String sentTo) {
		this.sentTo = sentTo;
	}

	public String toString() {
		return sentTo;
	}

	public void setSentTo(String sentTo) {
		this.sentTo = sentTo;
	}
	
	public String getSentTo() {
		return sentTo;
	}

	public void setFossilGroupId(Integer fossilGroupId) {
		this.fossilGroupId = fossilGroupId;
	}

	public Integer getFossilGroupId() {
		return fossilGroupId;
	}

	public void setFossilGroup(String fossilGroup) {
		this.fossilGroup = fossilGroup;
	}

	public String getFossilGroup() {
		return fossilGroup;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setDateRounding(String dateRounding) {
		this.dateRounding = dateRounding;
	}

	public String getDateRounding() {
		return dateRounding;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getPerson() {
		return person;
	}

	public void setLabId(Integer labId) {
		this.labId = labId;
	}

	public Integer getLabId() {
		return labId;
	}

	public void setLab(String lab) {
		this.lab = lab;
	}

	public String getLab() {
		return lab;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}
	
}