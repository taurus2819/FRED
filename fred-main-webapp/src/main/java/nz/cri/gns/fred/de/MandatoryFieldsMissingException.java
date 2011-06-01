package nz.cri.gns.fred.de;


public class MandatoryFieldsMissingException extends DataInputException {

	private static final long serialVersionUID = 20050818L;

	public MandatoryFieldsMissingException() {
		super("Mandatory Fields", "Not all mandatory fields completed");
	}
	
	@Override
	public String getMessage() {
		return "Not all mandatory fields completed";
	}
}
