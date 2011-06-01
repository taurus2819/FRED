package nz.cri.gns.fred.de;

public class RoundedDate {

	private java.sql.Date date;
	private String dateRnd;

	public RoundedDate() {
	}

	public RoundedDate(java.sql.Date date, String dateRnd) {
		this.date = date;
		this.dateRnd = dateRnd;
	}

	public void setDateRouding(String dateRnd) {
		this.dateRnd = dateRnd;
	}

	public String getDateRounding() {
		return dateRnd;
	}

	public void setDate(java.sql.Date date) {
		this.date = date;
	}

	public java.sql.Date getDate() {
		return date;
	}

}
