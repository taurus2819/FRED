package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.jsp.PageState;


public class TaxaPanel {

	public static final int PANEL_ID = 0;
	public static final int NAME = 1;
	public static final int APPROVED_TAXA = 2;
	public static final int PROVISIONAL_TAXA = 3;
	public static final int REJECTED_TAXA = 4;
	public static final int OBSOLETE_TAXA = 5;

	private TaxaPanelData tpd;
	private boolean panelMemberFlag;

	public TaxaPanel(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException {
		this.tpd = TaxaPanelData.getData(id, state, forceRefresh);
		this.panelMemberFlag = FREDUtils.isTaxaPanelMember(user, String.valueOf(id), state);
	}

	public TaxaPanel(int id, User user, PageState state) throws SQLException, IOException {
		this(id, user, state, false);
	}

	public int getPanelID() {
		return tpd.getAsInt(TaxaPanel.PANEL_ID);
	}

	public boolean isPanelMember() {
		return panelMemberFlag;
	}

	public int getApprovedCount() throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsVector(APPROVED_TAXA).size();
	}

	public int getProvisionalCount() throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsVector(PROVISIONAL_TAXA).size();
	}
	
	public int getRejectedCount() throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsVector(REJECTED_TAXA).size();
	}
	
	public int getObsoleteCount() throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsVector(OBSOLETE_TAXA).size();
	}
		
	public String toString() {
		return tpd.toString();
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field) throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsInt(field);
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsDouble(field);
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsDate(field);
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws IOException, SQLException, InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsString(field);
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.getAsVector(field);
	}
	
	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws InvalidCredentialsException {
		if (!panelMemberFlag) {
			throw new InvalidCredentialsException();
		}
		return tpd.get(field);
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return TaxaPanelData.getPoolSize();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		TaxaPanelData.purge();
	}

}
