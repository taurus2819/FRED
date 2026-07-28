package nz.cri.gns.fred.servlet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** Shared representation of search state stored outside the HTTP session. */
public final class PersistentSearchState {

    public enum Status {
        STARTED,
        COMPLETED,
        FAILED
    }

    private final UUID id;
    private final Status status;
    private final List<Integer> featureIds;
    private final List<Integer> sampleIds;
    private final String queryString;
    private final String errorMessage;

    public PersistentSearchState(
            UUID id,
            Status status,
            List<Integer> featureIds,
            List<Integer> sampleIds,
            String queryString,
            String errorMessage) {
        this.id = id;
        this.status = status;
        this.featureIds = immutableCopy(featureIds);
        this.sampleIds = immutableCopy(sampleIds);
        this.queryString = queryString;
        this.errorMessage = errorMessage;
    }

    private static List<Integer> immutableCopy(List<Integer> values) {
        if (values == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    public UUID getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public List<Integer> getFeatureIds() {
        return featureIds;
    }

    public List<Integer> getSampleIds() {
        return sampleIds;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
