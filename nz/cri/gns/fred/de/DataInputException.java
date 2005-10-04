package nz.cri.gns.fred.de;

public class DataInputException extends Exception {
	private String field;
	
	public DataInputException() {
	}
	
	public DataInputException(String field, String msg) {
		super(msg);
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
}
