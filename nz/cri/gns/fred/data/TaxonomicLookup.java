package nz.cri.gns.fred.data;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import nz.cri.gns.auth.InvalidCredentialsException;
import nz.cri.gns.auth.User;
import nz.cri.gns.fred.FREDUtils;
import nz.cri.gns.jsp.PageState;


public class TaxonomicLookup {

	public static final int TAXA_ID = 0;
	public static final int GROUP_ID = 1;
	public static final int GROUP_NAME = 2;
	public static final int TAXONOMIC_NAME = 3;
	public static final int AUTHOR = 4;
	public static final int STATUS = 5;
	public static final int SUBMITTED_BY_ID = 6;
	public static final int SUBMITTED_BY = 7;
	public static final int SUBMITTED_DATE = 8;
	public static final int APPROVED_BY_ID = 9;
	public static final int APPROVED_BY = 10;
	public static final int APPROVED_DATE = 11;
	
	public static final String APPROVED_STATUS = "approved";
	public static final String PROVISIONAL_STATUS = "provisional";
	public static final String REJECTED_STATUS = "rejected";
	public static final String OBSOLETE_STATUS = "OBSOLETE";
	
	private TaxonomicLookupData tl;
	private boolean panelMemberFlag = false;

	public TaxonomicLookup(int id, User user, PageState state, boolean forceRefresh) throws SQLException, IOException {
		this.tl = TaxonomicLookupData.getData(id, state, forceRefresh);
		this.panelMemberFlag = FREDUtils.isTaxaPanelMember(user, tl.getAsString(GROUP_ID), state);
	}

	public TaxonomicLookup(int id, User user, PageState state) throws SQLException, IOException {
		this(id, user, state, false);
	}

	public int getTaxaID() {
		return tl.getAsInt(TaxonomicLookup.TAXA_ID);
	}

	public boolean isAllowedApproveTaxa() {
		return panelMemberFlag;
	}

	public String toString() {
		return tl.toString();
	}

	/**
	 * Attempts to return the given field as an int.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an int.
	 */
	public int getAsInt(int field) throws InvalidCredentialsException {
		return tl.getAsInt(field);
	}

	/**
	 * Attempts to return the given field as an double.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an double.
	 */
	public double getAsDouble(int field) throws InvalidCredentialsException {
		return tl.getAsDouble(field);
	}

	/**
	 * Attempts to return the given field as a Date.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as an Date.
	 */
	public java.util.Date getAsDate(int field) throws InvalidCredentialsException {
		return tl.getAsDate(field);
	}

	/**
	 * Attempts to return the given field as a String.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a String.
	 */
	public String getAsString(int field) throws IOException, SQLException, InvalidCredentialsException {
		return tl.getAsString(field);
	}

	/**
	 * Attempts to return the given field as a Vector.
	 * @throws IllegalArgumentException if the field doesn't exist, or can't be returned as a Vector.
	 */
	public Vector getAsVector(int field) throws InvalidCredentialsException {
		return tl.getAsVector(field);
	}
	
	/**
	 * Returns the given field as an object. Use if all else fails.
	 * @throws IllegalArgumentException if the field doesn't exist.
	 */
	public Object get(int field) throws InvalidCredentialsException {
		return tl.get(field);
	}

	/**
	 * created for testing purposes (grrrr) - use to test object pooling.
	 */
	public static int getPoolSize() {
		return TaxonomicLookupData.getPoolSize();
	}

	/**
	 * Use to empty the pool of all objects.
	 */
	public static void purge() {
		TaxonomicLookupData.purge();
	}

}
