package nz.cri.gns.fred;

public class RoundedDate {

	private String dateStr;
	private String dateRnd;

	public RoundedDate() {
	}

	public RoundedDate(String dateStr, String dateRnd) {
		this.dateStr = dateStr;
		this.dateRnd = dateRnd;
	}

	public void setDateString(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getDateString() {
		return dateStr;
	}

	public void setDateRouding(String dateRnd) {
		this.dateRnd = dateRnd;
	}

	public String getDateRounding() {
		return dateRnd;
	}

}
