package nz.cri.gns.db.fred.data;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.fred.FREDUtils;
import nz.cri.gns.jsp.FREDConstants;
import nz.cri.gns.jsp.PageState;


public class Folder implements FREDConstants {

	public static final int FOLDER_ID = 0;
	public static final int NAME = 1;
	public static final int FOLDER_TYPE = 2;
	public static final int OWNER_ID = 3;
	public static final int OWNER = 4;

	private FolderData fd;
	private int userRights = 0;

	public Folder(int id, User user, PageState state) throws SQLException, IOException {
		this.fd = FolderData.getData(id, state);
		this.userRights = FREDUtils.getUserFolderRights(user, fd.getAsString(FOLDER_ID), state);
	}

	public int getUserRights() {
		return userRights;
	}

	public boolean isAllowedEdit() {
		return ((userRights & 32) != 0);
	}

	public boolean isAllowedDelete() {
		return ((userRights & 32) != 0);
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
