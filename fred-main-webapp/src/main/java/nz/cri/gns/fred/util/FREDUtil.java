package nz.cri.gns.fred.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import nz.cri.gns.auth.domain.User;
import nz.cri.gns.auth.security.IpGrantedAuthority;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.database.DataException;
import nz.cri.gns.db.metadata.DocumentAttacher;
import nz.cri.gns.fred.FredGrantedAuthorities;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.model.Adoption;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.PersonRelationship;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.query.FREDQuery;
import nz.cri.gns.fred.query.FREDRecordQuery;
import nz.cri.gns.jsp.PageState;
import nz.cri.gns.xss.SanitizeHttpServletRequest;

public class FREDUtil {

    public static class CopyAll implements Instruction {

        @Override
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

        @Override
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

        @Override
        public boolean include(PropertyDescriptor prop) {
            return !clazz.isAssignableFrom(prop.getPropertyType()) && (instruction == null || instruction.include(prop));
        }
    }

    private static final String QUERY_ATTRIBUTE_NAME = "fred.query";
    private static final String LOCK_ATTRIBUTE_NAME = "fred.lock";

    /**
     * Stores the given query in the session under
     * <code>QUERY_ATTRIBUTE_NAME</code>
     *
     * @param session
     * @param query
     */
    public static void setFREDQuery(HttpSession session, FREDQuery query) {
        synchronized (getSessionLock(session)) {
            session.setAttribute(QUERY_ATTRIBUTE_NAME, query);
        }
    }

    /**
     * Retrieves the query from the session, where it is stored under
     * <code>QUERY_ATTRIBUTE_NAME</code>. This method is synchronized on
     * <code>getSessionLock()</code> to alleviate concurrent access problems
     *
     * @param state
     * @return
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public static FREDQuery getFREDQuery(PageState state) throws IOException, SQLException {
        //Synchronizing on the session will ensure that two frames don't have
        //problems, whilst allowing other users to still run concurrently
        synchronized (getSessionLock(state.session)) {
            FREDQuery query = (FREDQuery) state.session.getAttribute(QUERY_ATTRIBUTE_NAME);
            if (query == null) {
                query = new FREDQuery();
                state.session.setAttribute(QUERY_ATTRIBUTE_NAME, query);
            }
            return query;
        }
    }

    private static final String RECORD_QUERY_ATTRIBUTE_NAME = "fred.Recordquery";

    /**
     * Stores the given query in the session under
     * <code>QUERY_ATTRIBUTE_NAME</code>
     *
     * @param session
     * @param query
     */
    public static void setFREDRecordQuery(HttpSession session, FREDQuery query) {
        synchronized (getSessionLock(session)) {
            session.setAttribute(RECORD_QUERY_ATTRIBUTE_NAME, query);
        }
    }

    /**
     * Retrieves the query from the session, where it is stored under
     * <code>QUERY_ATTRIBUTE_NAME</code>. This method is synchronized on
     * <code>getSessionLock()</code> to alleviate concurrent access problems
     *
     * @param state
     * @return
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public static FREDRecordQuery getFREDRecordQuery(PageState state) throws IOException, SQLException {
        //Synchronizing on the session will ensure that two frames don't have
        //problems, whilst allowing other users to still run concurrently
        synchronized (getSessionLock(state.session)) {
            FREDRecordQuery query = (FREDRecordQuery) state.session.getAttribute(RECORD_QUERY_ATTRIBUTE_NAME);
            if (query == null) {
                query = new FREDRecordQuery();
                state.session.setAttribute(RECORD_QUERY_ATTRIBUTE_NAME, query);
            }
            return query;
        }
    }

    public static void setSessionLock(HttpSession session) {
        if (session.getAttribute(LOCK_ATTRIBUTE_NAME) == null) {
            session.setAttribute(LOCK_ATTRIBUTE_NAME, new Object());
        }
    }

    public static Object getSessionLock(HttpSession session) {
        Object o = session.getAttribute(LOCK_ATTRIBUTE_NAME);
        if (o == null) {
            setSessionLock(session);
        }

        return session.getAttribute(LOCK_ATTRIBUTE_NAME);
    }

    private static final int SECURITY_CLASS_FRED_EDIT = 15;

    /**
     * The database instance that we're working in - doesn't change so is stored
     * as a constant once it is determined.
     */
    private static String instance = null;

    /**
     * @return @throws NamingException
     * @throws SQLException
     */
    public static Connection getConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource source = (DataSource) context.lookup("java:comp/env/jdbc/fr");
        return source.getConnection();
    }
    
    public static Connection getMISConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource source = (DataSource) context.lookup("java:comp/env/jdbc/mis");
        return source.getConnection();
    }

    public static String getPetWellLink(Feature feature) throws NamingException, SQLException {
        if (feature.getFeatureName() == null) {
            return null;
        }

        try (Connection conn = getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT well_name FROM petroleum.petroleum_well WHERE UPPER(well_name) = '" + feature.getFeatureName().toUpperCase() + "'");) {
            rs.next();
            String link = "https://data.gns.cri.nz/boreservice/index.html?name=" + rs.getString(1);
            statement.close();
            return link;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Joins the string array, starting from the given index, with a space
     * character as the separator
     *
     * @param parts
     * @param startFrom
     * @return
     */
    public static String join(String[] parts, int startFrom) {
        StringBuilder buffer = new StringBuilder();
        for (int i = startFrom; i < parts.length; i++) {
            buffer.append(parts[i]).append(" ");
        }
        return buffer.substring(0, buffer.length() - 1);
    }

    /**
     * @param user
     * @return
     */
    public static boolean checkEditSecurityClass(User user) {
        return user.getAuthorities().contains(new IpGrantedAuthority(FredGrantedAuthorities.FR_DATA_ENTRY));
    }

    /**
     * Returns a string of a date with appropriate formatting
     *
     * @param date
     * @param rounding
     * @return
     */
    public static String formatDateForOutput(Date date, String rounding) {
        if (date == null) {
            return "";
        }
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
     *
     * @param date
     * @param rounding
     * @return
     */
    public static String formatDateForDE(Date date, String rounding) {
        if (date == null) {
            return "";
        }
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
        if (dbl == null) {
            return null;
        }
        String dblStr = String.valueOf(dbl);
        if (dblStr.endsWith(".0")) {
            dblStr = dblStr.substring(0, dblStr.length() - 2);
            return dblStr;
        }
        StringBuilder fmt = new StringBuilder("0.");
        for (int i = 0; i < maxDp; i++) {
            fmt.append("#");
        }
        DecimalFormat f = new DecimalFormat(fmt.toString());
        return f.format(dbl);
    }

    public static String formatDateForOutput(Date date) {
        return formatDateForOutput(date, "Day");
    }

    public static Date parseDateFromDE(String date) throws ParseException {
        if (date.length() == 0) {
            return null;
        }
        if (date.length() == 4) {
            return new SimpleDateFormat("yyyy").parse(date);
        }
        if (date.length() == 7) {
            return new SimpleDateFormat("MM/yyyy").parse(date);
        }
        return new SimpleDateFormat("dd/MM/yyyy").parse(date);
    }

    public static String parseDateRoundingFromDE(String date) {
        if (date.length() == 0) {
            return null;
        }
        if (date.length() == 4) {
            return "Year";
        }
        if (date.length() == 7) {
            return "Month";
        }
        return null;//"Day";
    }

    public static String getNames(Set<? extends PersonRelationship> persons, String separator) {
        if (persons == null) {
            return "";
        }
        StringBuilder names = new StringBuilder();

        persons.stream().forEach((person) -> {
            names.append(person.getDisplayName()).append(separator);
        });

        return names.toString();
    }

    public static Set<Person> getPersons(String parameter, PersonUtil personUtil, String personLabel, boolean addIfNew) throws DataInputException {
        if (parameter.trim().length() == 0) {
            return new LinkedHashSet<>();
        }
        String[] peopleStr = parameter.split("[;\\n]");
        return getPersons(peopleStr, personUtil, personLabel, addIfNew);
    }

    public static Set<Person> getPersons(String[] peopleStr, PersonUtil personUtil, String personLabel, boolean addIfNew) throws DataInputException {
        HashSet<Person> personSet = new LinkedHashSet<>();
        ArrayList<String[]> error = new ArrayList<>();
        SanitizeHttpServletRequest sanitizeHttpRequest = new SanitizeHttpServletRequest();
        for (String personStr : peopleStr) {
            personStr = sanitizeHttpRequest.stripAllScripts(personStr);
            try {
                if (personStr.trim().length() == 0) {
                    continue;
                }
                Person person;
                if (addIfNew) {
                    person = personUtil.findOrCreatePerson(personStr.trim());
                } else {
                    person = personUtil.findPerson(personStr.trim());
                }
                if (person == null) {
                    error.add(new String[]{personLabel, "Invalid person: " + personStr});
                } else {
                    personSet.add(person);
                }
            } catch (StorageAccessException e) {
                error.add(new String[]{personLabel, "Database error: " + e.getMessage()});
            }
        }
        if (error.size() > 0) {
            throw new DataInputException(error, personSet);
        }

        return personSet;
    }

    public static DocumentAttacher getDocumentAttacher(String docType, PageState state) throws SQLException, DataException, IOException, NamingException {

        return new DocumentAttacher(state.getSession(),
                state.getContext(),
                () -> getConnection(),
                "Fossil Record File",
                1,
                docType.toUpperCase() + "_META",
                new String[]{docType.toUpperCase() + "_ID", "META_ID"},
                new Object[]{DocumentAttacher.ID_PLACEHOLDER, DocumentAttacher.DOCUMENT_PLACEHOLDER});
    }

    public static String decodeCombo(String parameter) {
        return ("-".equals(parameter) || "".equals(parameter)) ? null : parameter;
    }

    public static String toString(Object o) {
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    /**
     * Compares the two objects, handling nulls. The value of null == null is
     * given by nullEqulity
     *
     * @param o1
     * @param o2
     * @param nullEquality the value to return if both are null
     * @return
     */
    public static boolean equals(Object o1, Object o2, boolean nullEquality) {
        if (o1 == null && o2 == null) {
            return nullEquality;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    /**
     * Uses introspection to copy fields from <code>from</code> to
     * <code>to</code>
     *
     * @param <T>
     * @param from
     * @param to
     * @param instruction
     * @return to as a convenience.
     * @throws java.beans.IntrospectionException
     */
    public static <T> T beanCopy(T from, T to, Instruction instruction) throws IntrospectionException {
        BeanInfo fromInfo = Introspector.getBeanInfo(from.getClass());

        for (PropertyDescriptor prop : fromInfo.getPropertyDescriptors()) {
            if (instruction.include(prop)) {
                try {
                    prop.getWriteMethod().invoke(to, new Object[]{prop.getReadMethod().invoke(from, (Object[]) null)});
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                }
            }
        }
        return to;
    }

    /**
     * Uses introspection to copy fields from <code>from</code> to
     * <code>to</code> but uses the given class (must be a superclass of both
     * from and to) to determine which properties to copy.
     *
     * @param <T>
     * @param from
     * @param to
     * @param clazz
     * @param instruction
     * @return to as a convenience.
     * @throws java.beans.IntrospectionException
     */
    public static <T> T beanCopy(T from, T to, Class<T> clazz, Instruction instruction) throws IntrospectionException {
        BeanInfo fromInfo = Introspector.getBeanInfo(clazz);

        for (PropertyDescriptor prop : fromInfo.getPropertyDescriptors()) {
            if (instruction.include(prop)) {
                try {
                    prop.getWriteMethod().invoke(to, new Object[]{prop.getReadMethod().invoke(from, (Object[]) null)});
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                }
            }
        }
        return to;
    }

    public static <T> List<T> toVector(T... array) {
        List<T> list = new ArrayList<>(array.length);
        list.addAll(Arrays.asList(array));
        return list;
    }

    public static boolean isEmpty(Set<?> set) {
        return set == null || set.isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Warning...this method can be slow; consider FredDAO.getAdoptions instead
     *
     * @param sample
     * @return
     */
    public static Set<Adoption> getAdoptions(Sample sample) {
        Set<Adoption> adoptions = new HashSet<>();
        sample.getRecords().stream().filter((record) -> (record.getAdoption() != null)).forEach((record) -> {
            adoptions.add(record.getAdoption());
        });
        return adoptions;
    }

    /**
     * Warning...this method can be slow; consider FredDAO.getPaleontologies
     * instead
     *
     * @param sample
     * @return
     */
    public static Set<Paleontology> getPaleontologies(Sample sample) {
        Set<Paleontology> lists = new HashSet<>();
        sample.getRecords().stream().filter((record) -> (record.getPaleontology() != null)).forEach((record) -> {
            lists.add(record.getPaleontology());
        });
        return lists;
    }

    public static <T extends Comparable<? super T>> List<T> getSortedList(Set<T> set) {
        List<T> list = new ArrayList<>();
        list.addAll(set);
        Collections.sort(list);
        return list;
    }
    
    /** 
     * @param inStr String
     * @return An empty string if the inStr argument is null else a new string in this format ="inStr" ("=" sign in front)
     * Reason: Prevents excel converting field name like 13/12 to 13 December
     *
     */
    public static String nvl(String inStr) {
        //excel will interpret eg: ="13/12" as 13/12 and not 13 Dec
        return (inStr == null) ? "" : "=" + "\"" + inStr + "\"";
    }
}
