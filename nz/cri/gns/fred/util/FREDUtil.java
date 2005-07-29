package nz.cri.gns.fred.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import nz.cri.gns.auth.Right;
import nz.cri.gns.auth.SecurityClass;
import nz.cri.gns.auth.SecurityClassAccess;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.db.BasicDatabaseApp2;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.NZMG;
import nz.cri.gns.util.map.NZMS260;
import nz.cri.gns.util.map.NorthingEasting;
import nz.cri.gns.util.map.TruncNorthingEasting;

/**
 *
 */
public class FREDUtil {
	
	public static final int REG_MAINLAND_NZ = 400;
	public static final int REG_CHATHAM_ISLANDS = 401;
	public static final int REG_ROSS_SEA = 402;
	public static final int REG_NEW_CALEDONIA = 403;
	public static final int REG_TOKELAU = 404;
	public static final int REG_FIJI = 405;
	public static final int REG_SAMOA = 406;
	public static final int REG_NIUE = 407;
	public static final int REG_COOK_ISLANDS = 408;
	public static final int REG_NORFOLK_ISLAND = 409;
	public static final int REG_TONGA = 410;
	public static final int REG_LORD_HOWE_ISLAND = 411;
	public static final int REG_KERMADEC_ISLANDS = 412;
	public static final int REG_BOUNTY_ISLANDS = 413;
	public static final int REG_THE_SNARES = 414;
	public static final int REG_CAMPBELL_ISLAND = 415;
	public static final int REG_AUCKLAND_ISLANDS = 416;
	public static final int REG_ANTIPODES_ISLANDS = 417;
	public static final int REG_MACQUARIE_ISLAND = 418;
	public static final int REG_OTHER = 419;
	
	public static final int MASTERFILE_NTH_NI = 1;
	public static final int MASTERFILE_CEN_NI = 2;
	public static final int MASTERFILE_STH_NI = 3;
	public static final int MASTERFILE_NELSON = 4;
	public static final int MASTERFILE_CEN_SI = 5;
	public static final int MASTERFILE_STH_SI = 6;
	public static final int MASTERFILE_NZ_ISLANDS = 7;
	public static final int MASTERFILE_ANTARCTICA = 8;
	public static final int MASTERFILE_PACIFIC_ISLANDS = 9;
	public static final int MASTERFILE_NEW_CALEDONIA = 10;
	public static final int MASTERFILE_OFFSHORE = 11;
	
	private static final int SECURITY_CLASS_FRED_EDIT = 15;
	

	public static int getMasterfile(Feature feature) throws SQLException, NamingException {
		switch (feature.getRegistrationArea().getRegAreaId().intValue()) {
			case REG_MAINLAND_NZ :
				NorthingEasting nzmgCoord = (NorthingEasting)getSiteCoordinate(new NZMG(), feature.getSiteId().intValue());
				double easting = nzmgCoord.getEastWest();
				double northing = nzmgCoord.getNorthSouth();
				if (easting <= 2810000 && northing >= 6250000)
					return MASTERFILE_NTH_NI;
				if (northing >= 6160000 || (easting >= 2730000 && northing >= 6070000))
					return MASTERFILE_CEN_NI;
				if (easting >= 2650000)
					return 	MASTERFILE_STH_NI;
				if (northing >= 5920000)
					return MASTERFILE_NELSON;
				if (easting >= 2210000 && northing >= 5620000)
					return MASTERFILE_CEN_SI;
				return MASTERFILE_STH_SI;
			case REG_CHATHAM_ISLANDS :
			case REG_CAMPBELL_ISLAND :
			case REG_AUCKLAND_ISLANDS :
			case REG_ANTIPODES_ISLANDS :
			case REG_THE_SNARES :
				return MASTERFILE_NZ_ISLANDS;
			case REG_ROSS_SEA :
				return MASTERFILE_ANTARCTICA;
			case REG_TOKELAU :
			case REG_FIJI :
			case REG_SAMOA :
			case REG_NIUE :
			case REG_COOK_ISLANDS :
			case REG_NORFOLK_ISLAND :
			case REG_TONGA :
			case REG_LORD_HOWE_ISLAND :
			case REG_KERMADEC_ISLANDS :
			case REG_BOUNTY_ISLANDS :
			case REG_MACQUARIE_ISLAND :
				return MASTERFILE_PACIFIC_ISLANDS;
			case REG_NEW_CALEDONIA :
				return MASTERFILE_NEW_CALEDONIA;
			case REG_OTHER :
				return MASTERFILE_OFFSHORE;
		}
		return MASTERFILE_OFFSHORE;
	}
	
	/**
	 * @param i
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	private static Datum.Coordinate getSiteCoordinate(Datum datum, int siteId) throws SQLException, NamingException {
		Datum.LatLong ll = getSiteLatLong(siteId);
		if (ll == null)
			return null;
		return datum.convertFromNZGD49(ll);
	}
	
	public static Datum.LatLong getSiteLatLong(int siteId) throws SQLException, NamingException {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT latitude, longitude FROM sc.site WHERE site_id = " + siteId);
			if (!rs.next()) {
				rs.close();
				statement.close();
				conn.close();
				return null;
			}
			Datum.LatLong ll = new Datum.LatLong(rs.getDouble(1), rs.getDouble(2));
			rs.close();
			statement.close();
			conn.close();
			return ll;
		} catch (SQLException e) {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
			throw e;
		}
		
	}

	public static String getUserName(int userId) throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT given_name || ' ' || family_name FROM ip.person_view WHERE pe_id = " + userId);
			String name = (rs.next()) ? rs.getString(1) : "";
			rs.close();
			statement.close();
			conn.close();
			return name;
		} catch (SQLException e) {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
			throw e;
		}
	}
	
	/**
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	private static Connection getConnection() throws NamingException, SQLException {
		InitialContext context = new InitialContext();
		Context ctx = (Context)context.lookup("java:/comp/env");
		DataSource source = (DataSource)ctx.lookup("jdbc/fr");
		
		return source.getConnection();
	}

	/**
	 * @param user
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static boolean checkEditSecurityClass(UserAccount user) throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			BasicDatabaseApp2 app = new BasicDatabaseApp2(conn, user.getId());
			SecurityClass sc = new SecurityClass(SECURITY_CLASS_FRED_EDIT, app);
			SecurityClassAccess sca = new SecurityClassAccess(sc, Right.ANY_RIGHT);
			boolean allowed = sca.isAccessibleTo(user, app);		
			conn.close();
			return allowed;
		} catch (SQLException e) {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
			throw e;
		}
	}
	
	/**
	 * Returns a string of a date with appropriate formatting 
	 */
	public static String formatDateForOutput(Date date, String rounding) {
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy");
		if (rounding == null) {
			return DateFormat.getDateInstance(DateFormat.LONG).format(date);
		} else if (rounding.equals("Year")) {
			return yearFormatter.format(date);
		} else if (rounding.equals("Month")) {
			return monthFormatter.format(date);
		} else {
			return DateFormat.getDateInstance(DateFormat.LONG).format(date);
		}
	}

	public static String getFrNumberMapSheet(Feature feature) throws SQLException, NamingException {
		RegistrationArea area = feature.getRegistrationArea();

		Datum.LatLong ll = getSiteLatLong(feature.getSiteId().intValue());
		
		//Try and make this into a NZMS260 coord
		TruncNorthingEasting tne = null;
		try {
			tne = (TruncNorthingEasting)new NZMS260().convertFromNZGD49(ll);
		} catch (Exception e) {
		}
		
		if (tne != null && NZMS260.isValidMapSheet(tne.getMapSheet())) {
			return tne.getMapSheet();
		} else if (!(area.getCode().equals("NZ") || area.getCode().equals("OT"))) {
			return area.getCode();
		} else {
			DecimalFormat format = new DecimalFormat("00");
			String mapSheet = ((ll.getNorthSouth() > 0) ? "N" : "S") + ((ll.getEastWest() > 0) ? "E" : "W") 
				+ format.format(Math.floor(Math.abs(ll.getNorthSouth())));
			format.applyPattern("000");
			return mapSheet + format.format(Math.floor(Math.abs(ll.getEastWest())));
		}
	}
	
	public static SiteRecord getSite(Feature feature) throws NamingException, SQLException {
		Connection conn = getConnection();
		SiteRecord sr = null;
		try {
			sr = SiteRecord.querySite(new BasicDatabaseApp2(getConnection(), ""), feature.getSiteId().intValue());
		} catch (SQLException e) {
			conn.close();
			throw e;
		}
		return sr;
	}
}
