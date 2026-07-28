package nz.cri.gns.fred.servlet.util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SearchStateRepository {

    UUID createStarted(String httpSessionId, String queryString);

    void complete(UUID searchStateId, List<Integer> featureIds, List<Integer> sampleIds);

    void fail(UUID searchStateId, String errorMessage);

    Optional<PersistentSearchState> find(UUID searchStateId);

    void delete(UUID searchStateId);

    int deleteExpired();
}
