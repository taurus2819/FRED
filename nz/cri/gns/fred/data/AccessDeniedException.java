package nz.cri.gns.fred.data;

public class AccessDeniedException extends Exception {

	public AccessDeniedException() {
		super("User doesn't have rights to this record");
	}
}
