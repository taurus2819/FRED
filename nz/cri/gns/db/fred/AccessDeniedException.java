package nz.cri.gns.db.fred;

public class AccessDeniedException extends Exception {

	public AccessDeniedException() {
		super("User doesn't have rights to this record");
	}
}
