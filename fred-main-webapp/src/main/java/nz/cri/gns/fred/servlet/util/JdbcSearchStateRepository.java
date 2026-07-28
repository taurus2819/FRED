package nz.cri.gns.fred.servlet.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;

/** PostgreSQL-backed shared storage for Blue/Green search state. */
public final class JdbcSearchStateRepository implements SearchStateRepository {

    private static final Duration DEFAULT_EXPIRY = Duration.ofHours(4);
    private final DataSource dataSource;
    private final Duration expiry;

    public JdbcSearchStateRepository(DataSource dataSource) {
        this(dataSource, DEFAULT_EXPIRY);
    }

    public JdbcSearchStateRepository(DataSource dataSource, Duration expiry) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource must not be null");
        }
        this.dataSource = dataSource;
        this.expiry = expiry == null ? DEFAULT_EXPIRY : expiry;
    }

    @Override
    public UUID createStarted(String httpSessionId, String queryString) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        String sql = "insert into fr.search_session_state "
                + "(search_state_id, http_session_id, query_string, status, created_at, updated_at, expires_at) "
                + "values (?, ?, ?, 'STARTED', ?, ?, ?)";
        executeUpdate(sql, statement -> {
            statement.setObject(1, id);
            statement.setString(2, httpSessionId);
            statement.setString(3, queryString);
            statement.setTimestamp(4, Timestamp.from(now));
            statement.setTimestamp(5, Timestamp.from(now));
            statement.setTimestamp(6, Timestamp.from(now.plus(expiry)));
        });
        return id;
    }

    @Override
    public void complete(UUID id, List<Integer> featureIds, List<Integer> sampleIds) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                deleteResults(connection, id);
                insertResults(connection, id, "FEATURE", featureIds);
                insertResults(connection, id, "SAMPLE", sampleIds);
                try (PreparedStatement statement = connection.prepareStatement(
                        "update fr.search_session_state set status='COMPLETED', updated_at=?, expires_at=?, error_message=null where search_state_id=?")) {
                    Instant now = Instant.now();
                    statement.setTimestamp(1, Timestamp.from(now));
                    statement.setTimestamp(2, Timestamp.from(now.plus(expiry)));
                    statement.setObject(3, id);
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw persistenceException("complete search state", exception);
        }
    }

    @Override
    public void fail(UUID id, String errorMessage) {
        executeUpdate(
                "update fr.search_session_state set status='FAILED', error_message=?, updated_at=? where search_state_id=?",
                statement -> {
                    statement.setString(1, truncate(errorMessage, 2000));
                    statement.setTimestamp(2, Timestamp.from(Instant.now()));
                    statement.setObject(3, id);
                });
    }

    @Override
    public Optional<PersistentSearchState> find(UUID id) {
        String sql = "select status, query_string, error_message from fr.search_session_state "
                + "where search_state_id=? and expires_at > current_timestamp";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                PersistentSearchState.Status status = PersistentSearchState.Status.valueOf(resultSet.getString("status"));
                String queryString = resultSet.getString("query_string");
                String errorMessage = resultSet.getString("error_message");
                List<Integer> featureIds = readResults(connection, id, "FEATURE");
                List<Integer> sampleIds = readResults(connection, id, "SAMPLE");
                return Optional.of(new PersistentSearchState(
                        id, status, featureIds, sampleIds, queryString, errorMessage));
            }
        } catch (SQLException exception) {
            throw persistenceException("restore search state", exception);
        }
    }

    @Override
    public void delete(UUID id) {
        executeUpdate("delete from fr.search_session_state where search_state_id=?",
                statement -> statement.setObject(1, id));
    }

    @Override
    public int deleteExpired() {
        return executeUpdate("delete from fr.search_session_state where expires_at <= current_timestamp",
                statement -> { });
    }

    private void deleteResults(Connection connection, UUID id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "delete from fr.search_session_result where search_state_id=?")) {
            statement.setObject(1, id);
            statement.executeUpdate();
        }
    }

    private void insertResults(Connection connection, UUID id, String type, List<Integer> ids)
            throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        String sql = "insert into fr.search_session_result (search_state_id, result_type, result_id) "
                + "values (?, ?, ?) on conflict do nothing";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Integer resultId : ids) {
                if (resultId != null) {
                    statement.setObject(1, id);
                    statement.setString(2, type);
                    statement.setInt(3, resultId);
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        }
    }

    private List<Integer> readResults(Connection connection, UUID id, String type) throws SQLException {
        String sql = "select result_id from fr.search_session_result "
                + "where search_state_id=? and result_type=? order by result_id";
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.setString(2, type);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt(1));
                }
            }
        }
        return Collections.unmodifiableList(ids);
    }

    private int executeUpdate(String sql, StatementBinder binder) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            return statement.executeUpdate();
        } catch (SQLException exception) {
            throw persistenceException("update search state", exception);
        }
    }

    private static RuntimeException persistenceException(String operation, SQLException cause) {
        return new IllegalStateException("Unable to " + operation, cause);
    }

    private static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
