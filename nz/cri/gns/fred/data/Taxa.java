package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

import nz.cri.gns.auth.User;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.QueryDescriptor;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.intranet.DBConnection;
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
			QueryDescriptor qd = new QueryDescriptor("taxonomic_lookup");
			qd.addQueryColumn("group_id", Types.NUMERIC, groupID);
			qd.addQueryColumn("taxonomic_name", Types.VARCHAR, cleanTaxonomicName);
			qd.addQueryColumn("author", Types.VARCHAR, author);
			qd.addQueryColumn("status", Types.VARCHAR, "provisional");
			qd.addQueryColumn("submitted_by_id", Types.NUMERIC, new Integer(user.getPersonId()));
			qd.addQueryColumn("submitted_date", Types.DATE, Date.valueOf(FREDUtils.getNowForSQL()));
			DBUtils.doInsertUsingSequence(qd, "taxa_id", "taxa_seq", conn, false);
			TaxaPanel tp = new TaxaPanel(groupID.intValue(), user, state, true);
		}
	}

}
