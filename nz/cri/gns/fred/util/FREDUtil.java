package nz.cri.gns.fred.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import nz.cri.gns.auth.Right;
import nz.cri.gns.auth.SecurityClass;
import nz.cri.gns.auth.SecurityClassAccess;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.database.DataException;
import nz.cri.gns.db.BasicDatabaseApp2;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.DatabaseApp2;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.metadata.DocumentAttacher;
import nz.cri.gns.db.site.DatumMethod;
import nz.cri.gns.db.site.SiteRecord;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Meta;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.PersonRelationship;
import nz.cri.gns.fred.model.RegistrationArea;
import nz.cri.gns.fred.query.FREDQuery;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.NZMG;
import nz.cri.gns.util.map.NZMS260;
import nz.cri.gns.util.map.NorthingEasting;
import nz.cri.gns.util.map.TruncNorthingEasting;
import nz.cri.gns.util.map.Datum.Coordinate;

import org.xml.sax.SAXException;

public class FREDUtil {
	
	private static ThreadLocal<Connection> docAttacherConnections = new ThreadLocal<Connection>();
	
	public static class CopyAll implements Instruction {
		public boolean include(PropertyDescriptor prop) {
			return true;
		}
	}

	public static class ExcludeByName implements Instruction {

		private List<String> names;
		private Instruction instruction;

		public ExcludeByName(List<String> names) {
			this(names, null);
		}
		public ExcludeByName(List<String> names, Instruction furtherInstruction) {
			this.names = names;
			this.instruction = furtherInstruction;
		}

		public boolean include(PropertyDescriptor prop) {
			return !names.contains(prop.getName()) && (instruction == null || instruction.include(prop));
		}

	}

	public static interface Instruction {
		public boolean include(PropertyDescriptor prop);
	}

	public static class ExcludeByType implements Instruction {
		private Class<?> clazz;
		private Instruction instruction;
		
		public ExcludeByType(Class<?> clazz) {
			this(clazz, null);
		}
		public ExcludeByType(Class<?> clazz, Instruction furtherInstruction) {
			this.clazz = clazz;
			this.instruction = furtherInstruction;
		}

		public boolean include(PropertyDescriptor prop) {
			return !clazz.isAssignableFrom(prop.getPropertyType()) && (instruction == null || instruction.include(prop));
		}
	}
	
	private static final String QUERY_ATTRIBUTE_NAME = "fred.query";
	private static final String LOCK_ATTRIBUTE_NAME = "fred.lock";
	
	/**
	 * Stores the given query in the session under <code>QUERY_ATTRIBUTE_NAME</code>
	 */
	public static void setFREDQuery(HttpSession session, FREDQuery query) {
		synchronized(getSessionLock(session)) {
			session.setAttribute(QUERY_ATTRIBUTE_NAME, query);
		}
	}

	/**
	 * Retrieves the query from the session, where it is stored under <code>QUERY_ATTRIBUTE_NAME</code>.
	 * This method is synchronized on <code>getSessionLock()</code> to alleviate concurrent access problems
	 */
	public static FREDQuery getFREDQuery(PageState state) throws IOException, SQLException {
		//Synchronizing on the session will ensure that two frames don't have
		//problems, whilst allowing other users to still run concurrently
		synchronized(getSessionLock(state.session)) {
			FREDQuery query = (FREDQuery)state.session.getAttribute(QUERY_ATTRIBUTE_NAME);
			if (query == null) {
				query = new FREDQuery();
				state.session.setAttribute(QUERY_ATTRIBUTE_NAME, query);
			}
			return query;
		}
	}
	
	public static void setSessionLock(HttpSession session) {
		if (session.getAttribute(LOCK_ATTRIBUTE_NAME) == null) 
			session.setAttribute(LOCK_ATTRIBUTE_NAME, new Object());
	}
	
	public static Object getSessionLock(HttpSession session) {
		Object o = session.getAttribute(LOCK_ATTRIBUTE_NAME);
		if (o == null)
			setSessionLock(session);
			
		return session.getAttribute(LOCK_ATTRIBUTE_NAME);
	}
	
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
	
	//This is a special backlog masterfile folder
	public static final int MASTERFILE_NTH_NI_BACKLOG = 14;
	public static final int MASTERFILE_CEN_NI_BACKLOG = 17;
	public static final int MASTERFILE_STH_NI_BACKLOG = 19;
	public static final int MASTERFILE_NELSON_BACKLOG = 12;
	public static final int MASTERFILE_CEN_SI_BACKLOG = 20;
	public static final int MASTERFILE_STH_SI_BACKLOG = 22;
	public static final int MASTERFILE_NZ_ISLANDS_BACKLOG = 23;
	public static final int MASTERFILE_ANTARCTICA_BACKLOG = 24;
	public static final int MASTERFILE_PACIFIC_ISLANDS_BACKLOG = 25;
	public static final int MASTERFILE_NEW_CALEDONIA_BACKLOG = 26;
	public static final int MASTERFILE_OFFSHORE_BACKLOG = 27;
	
	private static final int SECURITY_CLASS_FRED_EDIT = 15;
	
	/**
	 * The database instance that we're working in - doesn't change
	 * so is stored as a constant once it is determined.
	 */
	private static String instance = null;
	
	public static int getMasterfile(Feature feature) throws SQLException, NamingException {
		boolean isBacklog = FeatureUtil.isBacklogFeature(feature);
		switch (feature.getRegistrationArea().getRegAreaId().intValue()) {
			case REG_MAINLAND_NZ :
				NorthingEasting nzmgCoord = (NorthingEasting)getSiteCoordinate(new NZMG(), feature.getSiteId().intValue());
				double easting = nzmgCoord.getEastWest();
				double northing = nzmgCoord.getNorthSouth();
				if (easting <= 2810000 && northing >= 6250000)
					return (isBacklog) ? MASTERFILE_NTH_NI_BACKLOG : MASTERFILE_NTH_NI;
				if (northing >= 6160000 || (easting >= 2730000 && northing >= 6070000))
					return (isBacklog) ? MASTERFILE_CEN_NI_BACKLOG : MASTERFILE_CEN_NI;
				if (easting >= 2650000 || northing >= 6130000)
					return 	(isBacklog) ? MASTERFILE_STH_NI_BACKLOG : MASTERFILE_STH_NI;
				if (northing >= 5920000)
					return (isBacklog) ? MASTERFILE_NELSON_BACKLOG : MASTERFILE_NELSON;
				if (easting >= 2210000 && northing >= 5620000)
					return (isBacklog) ? MASTERFILE_CEN_SI_BACKLOG : MASTERFILE_CEN_SI;
				if ((northing >= 5290000))
					return (isBacklog) ? MASTERFILE_STH_SI_BACKLOG : MASTERFILE_STH_SI;
				return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
			case REG_CHATHAM_ISLANDS :
			case REG_CAMPBELL_ISLAND :
			case REG_AUCKLAND_ISLANDS :
			case REG_ANTIPODES_ISLANDS :
			case REG_THE_SNARES :
				return (isBacklog) ? MASTERFILE_NZ_ISLANDS_BACKLOG : MASTERFILE_NZ_ISLANDS;
			case REG_ROSS_SEA :
				return (isBacklog) ? MASTERFILE_ANTARCTICA_BACKLOG : MASTERFILE_ANTARCTICA;
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
				return (isBacklog) ? MASTERFILE_PACIFIC_ISLANDS_BACKLOG : MASTERFILE_PACIFIC_ISLANDS;
			case REG_NEW_CALEDONIA :
				return (isBacklog) ? MASTERFILE_NEW_CALEDONIA_BACKLOG : MASTERFILE_NEW_CALEDONIA;
			case REG_OTHER :
				return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
		}
		return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
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
			return ll;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}
		
	}

	/**
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static Connection getConnection() throws NamingException, SQLException {
		InitialContext context = new InitialContext();
		DataSource source = (DataSource)context.lookup("java:comp/env/jdbc/fr");
		
		return source.getConnection();
	}

	/**
	 * @param user
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static boolean checkEditSecurityClass(UserAccount user) {
		Connection conn = null;
		try {
			conn = getConnection();
			BasicDatabaseApp2 app = new BasicDatabaseApp2(conn, user.getId());
			SecurityClass sc = new SecurityClass(SECURITY_CLASS_FRED_EDIT, app);
			SecurityClassAccess sca = new SecurityClassAccess(sc, Right.ANY_RIGHT);
			boolean allowed = sca.isAccessibleTo(user, app);		
			conn.close();
			return allowed;
		} catch (Exception e) {
			return false;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
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
	
	/**
	 * Returns a string of a date with appropriate formatting 
	 */
	public static String formatDateForDE(Date date, String rounding) {
		if (date == null)
			return "";
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MM/yyyy");
		SimpleDateFormat dayFormatter = new SimpleDateFormat("dd/MM/yyyy");
		if (rounding == null) {
			return dayFormatter.format(date);
		} else if (rounding.equals("Year")) {
			return yearFormatter.format(date);
		} else if (rounding.equals("Month")) {
			return monthFormatter.format(date);
		} else {
			return dayFormatter.format(date);
		}
	}

	public static String formatDoubleForOutput(Double dbl, int maxDp) {
		if (dbl == null)
			return null;
		String dblStr = String.valueOf(dbl);
		if (dblStr.endsWith(".0")) {
			dblStr = dblStr.substring(0, dblStr.length() - 2);
			return dblStr;
		}
		StringBuffer fmt = new StringBuffer("0."); 
		for (int i = 0; i < maxDp; i++)
			fmt.append("#");
		DecimalFormat f = new DecimalFormat(fmt.toString());
		return f.format(dbl);
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
		Connection conn = null;
		DatabaseApp2 app = null;
		SiteRecord sr = null;
		try {
			conn = getConnection();
			app = new BasicDatabaseApp2(conn, "");
			sr = SiteRecord.querySite(app, feature.getSiteId().intValue());
		} finally {
			if (app != null) {
				app.close();
			} else if (conn != null) try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		return sr;
	}
	
	public static Datum getFREDDatum(Feature feature) {
		if (feature.getOrigSystemId() == null)
			return null;
		Datum datum = DatumFactory.createDatum(feature.getOrigSystemId().intValue());
		return datum;
	}
	
	public static Coordinate getFREDCoordinate(Feature feature) {
		if (feature.getOrigCoord() == null || feature.getOrigSystemId() == null)
			return null;
		Datum datum = getFREDDatum(feature);
		Coordinate coord = datum.parseCoordinate(feature.getOrigCoord());
		return coord;
	}
	
	public static void makeDropBox(PrintWriter out, ComboDescriptor cd) throws SQLException, NamingException {
		Connection conn = getConnection();
		Statement statement = conn.createStatement();
		try {
			HTMLUtils.makeDropBox(out, statement, cd);
			statement.close();
		} finally {
			conn.close();
		}
	}
	
	public static String formatDateForOutput(Date date) {
		return formatDateForOutput(date, "Day");
	}

	public static Date parseDateFromDE(String date) throws ParseException {
		if (date.length() == 0)
			return null;
		if (date.length() == 4)
			return new SimpleDateFormat("yyyy").parse(date);
		if (date.length() == 7)
			return new SimpleDateFormat("MM/yyyy").parse(date);
		return new SimpleDateFormat("dd/MM/yyyy").parse(date);
	}

	public static String parseDateRoundingFromDE(String date) {
		if (date.length() == 0)
			return null;
		if (date.length() == 4)
			return "Year";
		if (date.length() == 7)
			return "Month";
		return null;//"Day";
	}

	public static String getMetaTitle(Meta meta) throws SQLException, NamingException {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT title FROM metacat.public_metacat_view WHERE meta_id = " + meta.getMetaId());
			String title = (rs.next()) ? rs.getString(1) : "";
			rs.close();
			statement.close();
			conn.close();
			return title;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}
	}

	public static List<DatumMethod> getSiteDatumMethods() throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT Method_ID, Method, Nom_Accuracy_XY, Nom_Accuracy_Z FROM SC.Method WHERE Nom_Accuracy_XY IS NOT NULL ORDER BY Nom_Accuracy_XY");
			
			List<DatumMethod> list = new Vector<DatumMethod>();
			while (rs.next()) {
				list.add(new DatumMethod(rs.getString(1), rs.getString(2), rs.getFloat(3), rs.getFloat(4)));
			}
			rs.close();
			statement.close();
			return list;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}
	}
	
	public static String getSiteMethod(SiteRecord sr) throws NamingException, SQLException {
		if (sr.isNull(SiteRecord.H_METHOD_FIELD))
			return null;
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT method FROM sc.method WHERE method_id = " + sr.getMethod());
			rs.next();
			String method = rs.getString(1);
			rs.close();
			statement.close();
			return method;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}		
	}

	public static String getSiteCountry(SiteRecord sr) throws NamingException, SQLException {
		if (sr.isNull(SiteRecord.COUNTRY_FIELD))
			return null;
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT country_name FROM mis.country WHERE country_code = '" + sr.getCountry() + "'");
			rs.next();
			String country = rs.getString(1);
			rs.close();
			statement.close();
			return country;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}		
	}	
	
	/**
	 * Gets an appropriate record from the DB for the given site, 
	 * inserting if necessary
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FactoryConfigurationError 
	 * @throws ParserConfigurationException 
	 * @throws NamingException 
	 * @throws SQLException 
	 */
	public static SiteRecord getSite(SiteRecord site) throws ParserConfigurationException, FactoryConfigurationError, SAXException, IOException, SQLException, NamingException {
		return site.insert(getInstance());
	}

	private static String getInstance() throws SQLException, NamingException {
		if (instance == null) {
			Connection conn = getConnection();
			instance = DBUtils.getInstance(new BasicDatabaseApp2(conn, ""));
			conn.close();
		}
		System.out.println("Instance = " + instance);
		return instance;
	}

	public static String getLabName(Integer labId) throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT lab_name FROM sc.lab WHERE lab_id = " + labId);
			String name = (rs.next()) ? rs.getString(1) : "";
			rs.close();
			statement.close();
			conn.close();
			return name;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}
	}

	public static Integer getLabId(String labName) throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT lab_id FROM sc.lab WHERE lab_name = " + DBUtils.sqlEscape(labName));
			Integer id = (rs.next()) ? new Integer(rs.getInt(1)) : null;
			rs.close();
			statement.close();
			conn.close();
			return id;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}
	}

	public static double[] getStageAgeRange(String stageId) throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT ta_age_start, ta_age_stop FROM age_view WHERE ag_id = " + stageId);
			double[] ages = (rs.next()) ? new double[] {rs.getDouble(1), rs.getDouble(2)} : null;
			rs.close();
			statement.close();
			return ages;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}
	}
	
	/**
	 * Returns an array of Strings representing the name of the given ageId.
	 * First item is the full name and the second item is the age code
	 */
	public static String[] getStageAgeName(String ageId) throws NamingException, SQLException {
		if (ageId == null)
			return null;
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT ag_name, ag_abbrev FROM age_view WHERE ag_id = " + ageId);
			String[] name = (rs.next()) ? new String[] {rs.getString(1), rs.getString(2)} : null;
			rs.close();
			statement.close();
			return name;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}		
	}

	public static String getPetWellLink(Feature feature) throws NamingException, SQLException {
		if (feature.getFeatureName() == null)
			return null;
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT well_name FROM petroleum.petroleum_well WHERE UPPER(well_name) = '" + feature.getFeatureName().toUpperCase() + "'");
			rs.next();
			String link = "/seismic/petwell.jsp?wellname=" + rs.getString(1);
			statement.close();
			conn.close();
			return link;
		} catch (SQLException e) {
			return null;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}
	}
	
	/**
	 * Joins the string array, starting from the given index, with a space character as the seperator
	 */
	public static String join(String[] parts, int startFrom) {
		StringBuffer buffer = new StringBuffer();
		for (int i=startFrom; i<parts.length; i++) {
			buffer.append(parts[i]).append(" ");
		}
		return buffer.substring(0, buffer.length()-1);
	}

	public static Integer getStratLexIdFor(String name) throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT su_id FROM sl.strat_unit WHERE su_name = " + DBUtils.sqlEscape(name));
			Integer id = (rs.next()) ? new Integer(rs.getInt(1)): null;
			rs.close();
			statement.close();
			return id;
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (Exception _e) {
			}
		}
	}

    public static String getNames(Set<? extends PersonRelationship> persons, String separator) {
    	if (persons == null)
    		return "";
        StringBuffer names = new StringBuffer();
        
        for (PersonRelationship person : persons) {
            names.append(person.getDisplayName()).append(separator);
        }
        
        
        return names.toString();
    }

    public static Set<Person> getPersons(String parameter, PersonUtil personUtil, String personLabel, boolean addIfNew) throws DataInputException {
        if (parameter.trim().length() == 0)
            return new HashSet<Person>();
        String[] peopleStr = parameter.split("[;\\n]");
        return getPersons(peopleStr, personUtil, personLabel, addIfNew);
    }
    
    public static Set<Person> getPersons(String[] peopleStr, PersonUtil personUtil, String personLabel, boolean addIfNew) throws DataInputException {
        HashSet<Person> personSet = new HashSet<Person>();
        Vector<String[]> error = new Vector<String[]>();
        for (String personStr : peopleStr) try {
        	if (personStr.trim().length() == 0)
        		continue;
        	Person person;
        	if (addIfNew) {
        		person = personUtil.findOrCreatePerson(personStr.trim());
        	} else {
        		person = personUtil.findPerson(personStr.trim());
        	}
            if (person == null)
           		error.add(new String[] {personLabel, "Invalid person: " + personStr});
            else {
                personSet.add(person);
           }
        } catch (StorageAccessException e) {
            error.add(new String[] {personLabel, "Database error: " + e.getMessage()});
        }
        if (error.size() > 0)
            throw new DataInputException(error, personSet);
        
        return personSet;
    }

	public static DocumentAttacher getDocumentAttacher(String docType, PageState state) throws SQLException, DataException, IOException, NamingException {
		Connection connection = getConnection();
		docAttacherConnections.set(connection);
		return new DocumentAttacher(state.getSession(),
				state.getContext(),
				new DBConnection(connection),
				"Fossil Record File",
				1,
				docType.toUpperCase() + "_META",
				new String[] {docType.toUpperCase() + "_ID", "META_ID"},
				new Object[] {DocumentAttacher.ID_PLACEHOLDER, DocumentAttacher.DOCUMENT_PLACEHOLDER});
	}
    
	public static void closeDocumentAttacherConnection() throws SQLException {
		docAttacherConnections.get().close();
	}
	
    public static String decodeCombo(String parameter) {
    	return ("-".equals(parameter) || "".equals(parameter)) ? null : parameter;
    }
    
    public static String toString(Object o) {
    	if (o == null)
    		return "";
    	return o.toString();
    }

    /**
     * Compares the two objects, handling nulls.  The value of null == null is
     * given by nullEqulity
     * @param nullEquality the value to return if both are null
     * @return
     */
    public static boolean equals(Object o1, Object o2, boolean nullEquality) {
        if (o1 == null && o2 == null)
            return nullEquality;
        if (o1 == null ^ o2 == null)
            return false;
        return o1.equals(o2);
    }

    /**
     * Uses introspection to copy fields from <code>from</code> to <code>to</code>
     *@return to as a convenience.
     */
	public static <T> T beanCopy(T from, T to, Instruction instruction) throws IntrospectionException {
		BeanInfo fromInfo = Introspector.getBeanInfo(from.getClass());
		
		for (PropertyDescriptor prop : fromInfo.getPropertyDescriptors()) {
			if (instruction.include(prop)) try {
				prop.getWriteMethod().invoke(to, new Object[] {prop.getReadMethod().invoke(from, (Object[])null)});
			} catch (Exception e) {
			}
		}
		return to;
	}

    /**
     * Uses introspection to copy fields from <code>from</code> to <code>to</code>
     * but uses the given class (must be a superclass of both from and to) to determine
     * which properties to copy.
     *@return to as a convenience.
     */
	public static <T> T beanCopy(T from, T to, Class<T> clazz, Instruction instruction) throws IntrospectionException {
		BeanInfo fromInfo = Introspector.getBeanInfo(clazz);
		
		for (PropertyDescriptor prop : fromInfo.getPropertyDescriptors()) {
			if (instruction.include(prop)) try {
				prop.getWriteMethod().invoke(to, new Object[] {prop.getReadMethod().invoke(from, (Object[])null)});
			} catch (Exception e) {
			}
		}
		return to;
	}

	public static <T> List<T> toVector(T ... array) {
		List<T> list = new Vector<T>(array.length);
		for (T t : array)
			list.add(t);
		return list;
	}
	
	public static boolean isEmpty(Set<?> set) {
		return set == null || set.size() == 0;
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
}