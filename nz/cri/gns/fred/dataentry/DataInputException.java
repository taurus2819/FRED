package nz.cri.gns.fred.dataentry;

public class DataInputException extends Exception {
	private String field;
	
	DataInputException() {
	}
	
	DataInputException(String field, String msg) {
		super(msg);
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
}
