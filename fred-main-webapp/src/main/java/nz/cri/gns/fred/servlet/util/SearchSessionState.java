package nz.cri.gns.fred.servlet.util;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

/**
 * Stores only lightweight, serializable search state in the HTTP session.
 *
 * <p>The modern implementation uses {@link List#copyOf(java.util.Collection)}
 * for defensive immutable copies and a Java record for the restored snapshot.</p>
 */
public final class SearchSessionState {

    private static final String SAMPLE_IDS_KEY = "FRED.samples";
    private static final String FEATURE_IDS_KEY = "FRED.features";
    private static final String QUERY_STRING_KEY = "FRED.queryString";

    private SearchSessionState() {
    }

    /**
     * Stores search identifiers rather than Hibernate entities in the session.
     */
    public static void save(HttpSession session,
        List<Integer> featureIds,
        List<Integer> sampleIds,
        String queryString) {

        session.setAttribute(FEATURE_IDS_KEY, immutableCopy(featureIds));
        session.setAttribute(SAMPLE_IDS_KEY, immutableCopy(sampleIds));
        session.setAttribute(QUERY_STRING_KEY, queryString);
    }

    /**
     * Restores a consistent immutable view of the current search state.
     */
    @SuppressWarnings("unchecked")
    public static Optional<Snapshot> restore(HttpSession session) {
        List<Integer> featureIds = immutableCopy(
            (List<Integer>) session.getAttribute(FEATURE_IDS_KEY));
        String queryString = (String) session.getAttribute(QUERY_STRING_KEY);

        if (featureIds == null || featureIds.isEmpty() || queryString == null) {
            return Optional.empty();
        }

        List<Integer> sampleIds = immutableCopy(
            (List<Integer>) session.getAttribute(SAMPLE_IDS_KEY));
        return Optional.of(new Snapshot(featureIds, sampleIds, queryString));
    }

    private static <T> List<T> immutableCopy(List<T> source) {
        return source == null ? null : List.copyOf(source);
    }

    /**
     * Immutable search-state value object.
     *
     * <p>The JavaBean-style accessors remain for compatibility with existing
     * JSP/servlet code while new code can use record accessors such as
     * {@code featureIds()} and {@code queryString()}.</p>
     */
    public record Snapshot(List<Integer> featureIds,
                           List<Integer> sampleIds,
                           String queryString) {

        public Snapshot {
            featureIds = List.copyOf(featureIds);
            sampleIds = immutableCopy(sampleIds);
        }

        public List<Integer> getFeatureIds() {
            return featureIds;
        }

        public List<Integer> getSampleIds() {
            return sampleIds;
        }

        public boolean hasSampleIds() {
            return sampleIds != null && !sampleIds.isEmpty();
        }

        public String getQueryString() {
            return queryString;
        }
    }
}
