package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.JspUtils;
import nz.cri.gns.jsp.PageState;

public class Taxa {

	private String taxonomicName;
	private String cleanTaxonomicName;
	private Integer taxaID;
	private String author;
	private Integer specimenCount;
	private String specimenCoords;
	private String comments;
	private Integer groupID;
	private String groupName;

	public Taxa() {
	}

	public Taxa(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}

	public String toString() {
		return this.taxonomicName;
	}

	public void setTaxonomicName(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}

	public String getTaxonomicName() {
		return taxonomicName;
	}

	public void setCleanTaxonomicName(String cleanTaxonomicName) {
		this.cleanTaxonomicName = cleanTaxonomicName;
	}

	public String getCleanTaxonomicName() {
		return cleanTaxonomicName;
	}

	public void setTaxaID(Integer taxaID) {
		this.taxaID = taxaID;
	}

	public Integer getTaxaID() {
		return taxaID;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setSpecimenCount(Integer specimenCount) {
		this.specimenCount = specimenCount;
	}

	public Integer getSpecimenCount() {
		return specimenCount;
	}

	public void setSpecimenCoords(String specimenCoords) {
		this.specimenCoords = specimenCoords;
	}

	public String getSpecimenCoords() {
		return specimenCoords;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	public Integer getGroupID() {
		return groupID;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public void submitProvisional(User user, PageState state) throws SQLException, IOException {
		if (user != null && state != null && groupID != null && cleanTaxonomicName != null) {
			DBConnection conn = FREDUtils.getFREDConnection(state);
			ResultSet rs = conn.executeQuery("SELECT Taxa_Seq.NEXTVAL FROM Dual");
			rs.next();
			conn.executeUpdate("INSERT INTO Taxonomic_Lookup (Taxa_ID, Group_ID, Taxonomic_Name, Author, Status, Submitted_By_ID, Submitted_Date) VALUES (" + rs.getString(1) + ", " + groupID + ", " + JspUtils.sqlEscape(cleanTaxonomicName) + ", " + JspUtils.sqlEscape(author) + ", 'provisional', " + user.getPersonId() + ", SYSDATE)");
		}
	}

}
