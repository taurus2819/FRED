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

import nz.cri.gns.auth.Right;
import nz.cri.gns.auth.SecurityClass;
import nz.cri.gns.auth.SecurityClassAccess;
import nz.cri.gns.auth.UserAccount;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.database.DataException;
import nz.cri.gns.db.BasicDatabaseApp2;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.db.HTMLUtils;
import nz.cri.gns.db.metadata.DocumentAttacher;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Meta;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.PersonRelationship;
import nz.cri.gns.fred.query.FREDQuery;
import nz.cri.gns.intranet.DBConnection;
import nz.cri.gns.jsp.PageState;

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
	
	
	private static final int SECURITY_CLASS_FRED_EDIT = 15;
	
	/**
	 * The database instance that we're working in - doesn't change
	 * so is stored as a constant once it is determined.
	 */
	private static String instance = null;

	/**
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static Connection getConnection() throws NamingException, SQLException {
		InitialContext context = new InitialContext();
		DataSource source = (DataSource)context.lookup("java:comp/env/jdbc/fr");
		System.out.println("FRED Connection - count = " + getConnCount++);
		return source.getConnection();
	}

	private static int getConnCount;
	
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

	public static String getInstance() throws SQLException, NamingException {
		if (instance == null) {
			Connection conn = getConnection();
			instance = DBUtils.getInstance(new BasicDatabaseApp2(conn, ""));
			conn.close();
		}
		return instance;
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