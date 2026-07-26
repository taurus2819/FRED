package nz.cri.gns.fred.servlet.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

/**
 * Stores and restores the user's search state in the HTTP session.
 *
 * <p>The current format stores one serializable {@link Snapshot} rather than
 * three separate session attributes. {@link #restore(HttpSession)} can still
 * read the legacy attributes and migrates them to the new format so sessions
 * created by the previous deployment remain usable.</p>
 *
 * @author sitikond
 */
public final class SearchSessionState {

    private static final String SEARCH_STATE_KEY = "FRED.searchState";

    // Legacy keys retained temporarily so sessions created by the previous
    // application version can be restored during a Blue/Green deployment.
    private static final String LEGACY_SAMPLE_IDS_KEY = "FRED.samples";
    private static final String LEGACY_FEATURE_IDS_KEY = "FRED.features";
    private static final String LEGACY_QUERY_STRING_KEY = "FRED.queryString";

    private SearchSessionState() {
    }

    /**
     * Stores the search results as one immutable, serializable session value.
     *
     * @param session HTTP session to write to
     * @param featureIds IDs for matching features; must not be {@code null}
     * @param sampleIds IDs for matching samples; may be {@code null}
     * @param queryString human-readable form of the executed query
     */
    public static void save(
            HttpSession session,
            List<Integer> featureIds,
            List<Integer> sampleIds,
            String queryString) {

        Snapshot snapshot = new Snapshot(featureIds, sampleIds, queryString);
        session.setAttribute(SEARCH_STATE_KEY, snapshot);
        removeLegacyAttributes(session);
    }

    /**
     * Restores the saved search state.
     *
     * <p>The new snapshot format is checked first. If it is absent, the method
     * reads the three legacy attributes, converts them into a snapshot, stores
     * the snapshot under the new key, and removes the legacy attributes.</p>
     *
     * @param session HTTP session to read from
     * @return saved state, or an empty optional when no valid state exists
     */
    public static Optional<Snapshot> restore(HttpSession session) {
        Object savedState = session.getAttribute(SEARCH_STATE_KEY);
        if (savedState instanceof Snapshot) {
            Snapshot snapshot = (Snapshot) savedState;
            return snapshot.isValid() ? Optional.of(snapshot) : Optional.empty();
        }

        Optional<Snapshot> legacySnapshot = restoreLegacy(session);
        legacySnapshot.ifPresent(snapshot -> {
            session.setAttribute(SEARCH_STATE_KEY, snapshot);
            removeLegacyAttributes(session);
        });

        return legacySnapshot;
    }

    /**
     * Removes both current and legacy search state from the session.
     *
     * @param session HTTP session to clear
     */
    public static void clear(HttpSession session) {
        session.removeAttribute(SEARCH_STATE_KEY);
        removeLegacyAttributes(session);
    }

    /**
     * Determines whether the session contains a valid current or legacy search
     * state.
     *
     * @param session HTTP session to inspect
     * @return {@code true} when valid search state can be restored
     */
    public static boolean exists(HttpSession session) {
        return restore(session).isPresent();
    }

    private static Optional<Snapshot> restoreLegacy(HttpSession session) {
        List<Integer> featureIds = readIntegerList(
                session.getAttribute(LEGACY_FEATURE_IDS_KEY));
        String queryString = readString(
                session.getAttribute(LEGACY_QUERY_STRING_KEY));

        if (featureIds == null || featureIds.isEmpty() || queryString == null) {
            return Optional.empty();
        }

        List<Integer> sampleIds = readIntegerList(
                session.getAttribute(LEGACY_SAMPLE_IDS_KEY));
        return Optional.of(new Snapshot(featureIds, sampleIds, queryString));
    }

    private static String readString(Object value) {
        return value instanceof String ? (String) value : null;
    }

    private static List<Integer> readIntegerList(Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof List<?>)) {
            return null;
        }

        List<?> values = (List<?>) value;
        List<Integer> copy = new ArrayList<>(values.size());
        for (Object item : values) {
            if (!(item instanceof Integer)) {
                return null;
            }
            copy.add((Integer) item);
        }
        return immutableCopy(copy);
    }

    private static List<Integer> immutableCopy(List<Integer> source) {
        if (source == null) {
            return null;
        }
        return Collections.unmodifiableList(new ArrayList<>(source));
    }

    private static void removeLegacyAttributes(HttpSession session) {
        session.removeAttribute(LEGACY_FEATURE_IDS_KEY);
        session.removeAttribute(LEGACY_SAMPLE_IDS_KEY);
        session.removeAttribute(LEGACY_QUERY_STRING_KEY);
    }

    /**
     * Immutable representation of the user's saved search state.
     */
    public static final class Snapshot implements Serializable {

        private static final long serialVersionUID = 1L;

        private final List<Integer> featureIds;
        private final List<Integer> sampleIds;
        private final String queryString;

        private Snapshot(
                List<Integer> featureIds,
                List<Integer> sampleIds,
                String queryString) {
            this.featureIds = immutableCopy(featureIds);
            this.sampleIds = immutableCopy(sampleIds);
            this.queryString = queryString;
        }

        private boolean isValid() {
            return featureIds != null
                    && !featureIds.isEmpty()
                    && queryString != null;
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
