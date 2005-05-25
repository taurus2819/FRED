package nz.cri.gns.fred.data;

/**
 * @author iainm
 */
public interface FREDFolder {
	public static final int FOLDER_READ_RIGHT = 1;
	public static final int FOLDER_EDIT_RIGHT = 2;
	public static final int FOLDER_CREATE_RIGHT = 4;
	public static final int FOLDER_DELETE_RIGHT = 8;
	public static final int FOLDER_SUBMIT_RIGHT = 16;
	public static final int FOLDER_ADMIN_RIGHT = 32;
	public static final int FOLDER_APPROVE_RIGHT = 64;

	public abstract int getUserRights();

	public abstract boolean isAllowedReadLocalities();

	public abstract boolean isAllowedEditLocalities();

	public abstract boolean isAllowedCreateLocalities();

	public abstract boolean isAllowedDeleteLocalities();

	public abstract boolean isAllowedSubmitLocalities();

	public abstract boolean isAllowedAdmin();

	public abstract boolean isAllowedApproveLocalities();
}