package nz.cri.gns.fred.servlet.util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpSession;

/**
 * Coordinates shared database storage with lightweight HTTP-session references.
 * Existing snapshot attributes are dual-written during the Blue/Green transition.
 */
public final class PersistentSearchSessionService {

    private final SearchStateRepository repository;

    public PersistentSearchSessionService(SearchStateRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("SearchStateRepository must not be null");
        }
        this.repository = repository;
    }

    /** Call before the expensive Hibernate query starts. */
    public UUID begin(HttpSession session, String queryString) {
        UUID id = repository.createStarted(session.getId(), queryString);
        SearchSessionState.saveReference(session, id);
        return id;
    }

    /** Call after feature/sample IDs have been fully produced. */
    public void complete(
            HttpSession session,
            UUID searchStateId,
            List<Integer> featureIds,
            List<Integer> sampleIds,
            String queryString) {
        repository.complete(searchStateId, featureIds, sampleIds);
        SearchSessionState.saveReference(session, searchStateId);
        // Transitional dual-write: old application nodes can still render results.
        SearchSessionState.save(session, featureIds, sampleIds, queryString);
    }

    public void fail(UUID searchStateId, Throwable failure) {
        String message = failure == null ? null : failure.getMessage();
        repository.fail(searchStateId, message);
    }

    /**
     * Restores from shared storage first, then falls back to legacy session data.
     */
    public Optional<PersistentSearchState> restore(HttpSession session) {
        Optional<UUID> reference = SearchSessionState.restoreReference(session);
        if (reference.isPresent()) {
            Optional<PersistentSearchState> persisted = repository.find(reference.get());
            if (persisted.isPresent()) {
                return persisted;
            }
        }

        Optional<SearchSessionState.Snapshot> legacy = SearchSessionState.restore(session);
        if (!legacy.isPresent()) {
            return Optional.empty();
        }

        SearchSessionState.Snapshot snapshot = legacy.get();
        UUID migratedId = repository.createStarted(session.getId(), snapshot.getQueryString());
        repository.complete(migratedId, snapshot.getFeatureIds(), snapshot.getSampleIds());
        SearchSessionState.saveReference(session, migratedId);

        return repository.find(migratedId);
    }

    public void clear(HttpSession session) {
        SearchSessionState.restoreReference(session).ifPresent(repository::delete);
        SearchSessionState.clear(session);
    }
}
