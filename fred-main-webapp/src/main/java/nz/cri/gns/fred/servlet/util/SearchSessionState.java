package nz.cri.gns.fred.servlet.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpSession;

/**
 * Stores and restores search state references and legacy snapshots in the HTTP session.
 */
public final class SearchSessionState {

    private static final String SEARCH_STATE_ID_KEY = "FRED.searchStateId";
    private static final String SEARCH_STATE_KEY = "FRED.searchState";
    private static final String LEGACY_SAMPLE_IDS_KEY = "FRED.samples";
    private static final String LEGACY_FEATURE_IDS_KEY = "FRED.features";
    private static final String LEGACY_QUERY_STRING_KEY = "FRED.queryString";

    private SearchSessionState() {
    }

    /**
     * Stores a lightweight reference to search state persisted in shared storage.
     */
    public static void saveReference(HttpSession session, UUID searchStateId) {
        if (session == null) {
            throw new IllegalArgumentException("HTTP session must not be null");
        }
        if (searchStateId == null) {
            throw new IllegalArgumentException("Search state ID must not be null");
        }
        session.setAttribute(SEARCH_STATE_ID_KEY, new SearchStateReference(searchStateId));
    }

    /**
     * Restores the shared-storage reference from the HTTP session.
     */
    public static Optional<UUID> restoreReference(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        Object value = session.getAttribute(SEARCH_STATE_ID_KEY);
        if (value instanceof SearchStateReference) {
            return Optional.of(((SearchStateReference) value).getSearchStateId());
        }
        if (value instanceof String) {
            try {
                return Optional.of(UUID.fromString((String) value));
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Transitional snapshot storage retained for old Blue instances.
     */
    public static void save(
            HttpSession session,
            List<Integer> featureIds,
            List<Integer> sampleIds,
            String queryString) {
        Snapshot snapshot = new Snapshot(featureIds, sampleIds, queryString);
        session.setAttribute(SEARCH_STATE_KEY, snapshot);
        writeLegacyAttributes(session, snapshot);
    }

    public static Optional<Snapshot> restore(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        Object savedState = session.getAttribute(SEARCH_STATE_KEY);
        if (savedState instanceof Snapshot) {
            Snapshot snapshot = (Snapshot) savedState;
            return snapshot.isValid() ? Optional.of(snapshot) : Optional.empty();
        }
        Optional<Snapshot> legacySnapshot = restoreLegacy(session);
        legacySnapshot.ifPresent(snapshot -> session.setAttribute(SEARCH_STATE_KEY, snapshot));
        return legacySnapshot;
    }

    public static void clear(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(SEARCH_STATE_ID_KEY);
        session.removeAttribute(SEARCH_STATE_KEY);
        removeLegacyAttributes(session);
    }

    public static boolean exists(HttpSession session) {
        return restoreReference(session).isPresent() || restore(session).isPresent();
    }

    private static Optional<Snapshot> restoreLegacy(HttpSession session) {
        List<Integer> featureIds = readIntegerList(session.getAttribute(LEGACY_FEATURE_IDS_KEY));
        String queryString = readString(session.getAttribute(LEGACY_QUERY_STRING_KEY));
        if (featureIds == null || featureIds.isEmpty() || queryString == null) {
            return Optional.empty();
        }
        List<Integer> sampleIds = readIntegerList(session.getAttribute(LEGACY_SAMPLE_IDS_KEY));
        return Optional.of(new Snapshot(featureIds, sampleIds, queryString));
    }

    private static String readString(Object value) {
        return value instanceof String ? (String) value : null;
    }

    private static List<Integer> readIntegerList(Object value) {
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
        return source == null ? null : Collections.unmodifiableList(new ArrayList<>(source));
    }

    private static void writeLegacyAttributes(HttpSession session, Snapshot snapshot) {
        session.setAttribute(LEGACY_FEATURE_IDS_KEY, snapshot.getFeatureIds());
        session.setAttribute(LEGACY_SAMPLE_IDS_KEY, snapshot.getSampleIds());
        session.setAttribute(LEGACY_QUERY_STRING_KEY, snapshot.getQueryString());
    }

    private static void removeLegacyAttributes(HttpSession session) {
        session.removeAttribute(LEGACY_FEATURE_IDS_KEY);
        session.removeAttribute(LEGACY_SAMPLE_IDS_KEY);
        session.removeAttribute(LEGACY_QUERY_STRING_KEY);
    }

    public static final class SearchStateReference implements Serializable {
        private static final long serialVersionUID = 1L;
        private final UUID searchStateId;

        public SearchStateReference(UUID searchStateId) {
            this.searchStateId = searchStateId;
        }

        public UUID getSearchStateId() {
            return searchStateId;
        }
    }

    public static final class Snapshot implements Serializable {
        private static final long serialVersionUID = 1L;
        private final List<Integer> featureIds;
        private final List<Integer> sampleIds;
        private final String queryString;

        private Snapshot(List<Integer> featureIds, List<Integer> sampleIds, String queryString) {
            this.featureIds = immutableCopy(featureIds);
            this.sampleIds = immutableCopy(sampleIds);
            this.queryString = queryString;
        }

        private boolean isValid() {
            return featureIds != null && !featureIds.isEmpty() && queryString != null;
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
