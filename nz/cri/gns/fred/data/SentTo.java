package nz.cri.gns.fred.data;

import java.util.Date;

public class SentTo {

	private String sentTo;
	private Integer fossilGroupID;
	private String fossilGroup;
	private Date date;
	private String dateRounding;
	private Integer personID;
	private String person;
	private Integer labID;
	private String lab;
	private String comments;

	public SentTo() {
	}

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

	public void setFossilGroupID(Integer fossilGroupID) {
		this.fossilGroupID = fossilGroupID;
	}

	public Integer getFossilGroupID() {
		return fossilGroupID;
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

	public void setPersonID(Integer personID) {
		this.personID = personID;
	}

	public Integer getPersonID() {
		return personID;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getPerson() {
		return person;
	}

	public void setLabID(Integer labID) {
		this.labID = labID;
	}

	public Integer getLabID() {
		return labID;
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