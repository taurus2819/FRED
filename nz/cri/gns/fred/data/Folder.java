package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.jsp.PageState;


public class Folder {

	public static final int FOLDER_ID = 0;
	public static final int NAME = 1;
	public static final int FOLDER_TYPE = 2;
	public static final int OWNER_ID = 3;
	public static final int OWNER = 4;
	public static final int FEATURES = 5;
	
	public static final int FOLDER_READ_RIGHT = 1;
	public static final int FOLDER_EDIT_RIGHT = 2;
	public static final int FOLDER_CREATE_RIGHT = 4;
	public static final int FOLDER_DELETE_RIGHT = 8;
	public static final int FOLDER_SUBMIT_RIGHT = 16;
	public static final int FOLDER_ADMIN_RIGHT = 32;
	public static final int FOLDER_APPROVE_RIGHT = 64;
	
	
	private FolderData fd;
	private int userRights = 0;

	public Folder(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException {
		this.fd = FolderData.getData(id, state, forceRefresh);
		this.userRights = FREDUtils.getUserFolderRights(user, fd.getAsString(FOLDER_ID), state);
	}

	public Folder(int id, User user, PageState state) throws SQLException, IOException {
		this(id, user, state, false);
	}

	public int getFolderID() {
		return fd.getAsInt(Folder.FOLDER_ID);
	}

	public int getUserRights() {
		return userRights;
	}

	public boolean isAllowedReadLocalities() {
		return ((userRights & FOLDER_READ_RIGHT) != 0);
	}
	
	public boolean isAllowedEditLocalities() {
		return ((userRights & FOLDER_EDIT_RIGHT) != 0);
	}

	public boolean isAllowedCreateLocalities() {
		return ((userRights & FOLDER_CREATE_RIGHT) != 0);
	}

	public boolean isAllowedDeleteLocalities() {
		return ((userRights & FOLDER_DELETE_RIGHT) != 0);
	}

	public boolean isAllowedSubmitLocalities() {
		return ((userRights & FOLDER_SUBMIT_RIGHT) != 0);
	}

	public boolean isAllowedAdmin() {
		return ((userRights & FOLDER_ADMIN_RIGHT) != 0);
	}

	public boolean isAllowedApproveLocalities() {
		return ((userRights & FOLDER_APPROVE_RIGHT) != 0);
	}

	public int getLocalityCount() throws InvalidCredentialsException {
		if (userRights == 0) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsVector(FEATURES).size();
	}

	public String toString() {
		return fd.toString();
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field) throws InvalidCredentialsException {
		if (userRights == 0) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsInt(field);
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws InvalidCredentialsException {
		if (userRights == 0) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsDouble(field);
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws InvalidCredentialsException {
		if (userRights == 0) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsDate(field);
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws IOException, SQLException, InvalidCredentialsException {
		if (userRights == 0) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsString(field);
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws InvalidCredentialsException {
		if (userRights == 0) {
			throw new InvalidCredentialsException();
		}
		return fd.getAsVector(field);
	}
	
	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws InvalidCredentialsException {
		if (userRights == 0) {
			throw new InvalidCredentialsException();
		}
		return fd.get(field);
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return FolderData.getPoolSize();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		FolderData.purge();
	}

}
