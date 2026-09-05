# FRED JDK 11 → 25 Refactoring: Side-by-Side Guide

Branch: `refactor/jdk25-modernisation`  
Base: `master` at `5e0f495cf55926f6352507287e33d8282dbd9299`

This document records the first production-safe modernization slice. The goal is not to replace working FRED behavior for stylistic reasons; it is to use modern JDK features where they materially improve safety, maintainability, scalability, or observability.

## 1. WFSProxy: URLConnection → Java 11 HttpClient + Java 21 virtual threads

### Before (`master`)

```java
URLConnection serverConnection = new URL(TARGET_URL_WFS).openConnection();
serverConnection.setRequestProperty("CONTENT-TYPE", "text/xml");
serverConnection.setDoInput(true);
serverConnection.setDoOutput(true);

int bytie;
try (InputStream fromClient = request.getInputStream()) {
    OutputStream toServer = serverConnection.getOutputStream();
    while ((bytie = fromClient.read()) != -1) {
        toServer.write(bytie);
    }
}
```

Problems:

- legacy `URLConnection` API;
- no connection/request timeout policy;
- one-byte-at-a-time copying;
- Tomcat request thread remains blocked during upstream WFS I/O;
- `printStackTrace()` error handling;
- raw `Enumeration` and manual query-string concatenation.

### After

```java
virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
httpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(15))
    .followRedirects(HttpClient.Redirect.NORMAL)
    .build();
```

```java
AsyncContext async = request.startAsync();
virtualThreadExecutor.submit(() -> {
    try {
        operation.execute();
    } finally {
        async.complete();
    }
});
```

```java
HttpRequest upstreamRequest = HttpRequest.newBuilder(uri)
    .timeout(Duration.ofSeconds(60))
    .GET()
    .build();

copyUpstreamResponse(
    httpClient.send(upstreamRequest, HttpResponse.BodyHandlers.ofInputStream()),
    response
);
```

Benefits:

- Java 11 `HttpClient`;
- Java 21 virtual-thread-per-task executor;
- Servlet asynchronous processing releases the Tomcat worker thread while waiting;
- explicit timeouts and upstream HTTP status propagation;
- `InputStream.transferTo(...)` rather than byte-by-byte copy;
- `StandardCharsets.UTF_8` and stream-based query construction.

## 2. SearchSessionState: mutable helper class → Java record

### Before (`master`)

```java
List<Integer> featureCopy = new ArrayList<>(featureIds);
session.setAttribute(FEATURE_IDS_KEY,
    Collections.unmodifiableList(featureCopy));
```

```java
public static final class Snapshot {
    private final List<Integer> featureIds;
    private final List<Integer> sampleIds;
    private final String queryString;
    // constructor + getters
}
```

### After

```java
session.setAttribute(FEATURE_IDS_KEY, immutableCopy(featureIds));
```

```java
private static <T> List<T> immutableCopy(List<T> source) {
    return source == null ? null : List.copyOf(source);
}
```

```java
public record Snapshot(
    List<Integer> featureIds,
    List<Integer> sampleIds,
    String queryString) {

    public Snapshot {
        featureIds = List.copyOf(featureIds);
        sampleIds = immutableCopy(sampleIds);
    }

    // compatibility getters retained during migration
}
```

Benefits:

- Java 10+ `List.copyOf(...)`;
- Java 16 records;
- less accidental mutability;
- fewer boilerplate fields/constructors/getters;
- existing servlet/JSP callers remain compatible.

## 3. FredHibernate: singleton/session safety and credential removal

### Before (`master`)

```java
private static FredHibernate instance;

public static FredHibernate get() {
    if (null == instance) {
        instance = configure(false);
    }
    return instance;
}
```

The fallback configuration also embedded PostgreSQL connection details directly in source control.

### After

```java
private static volatile FredHibernate instance;

public static FredHibernate get() {
    FredHibernate current = instance;
    if (current == null) {
        synchronized (FredHibernate.class) {
            current = instance;
            if (current == null) {
                current = configure();
                instance = current;
            }
        }
    }
    return current;
}
```

```java
properties.put("hibernate.connection.url",
    requiredEnvironmentVariable("FRED_DB_URL"));
properties.put("hibernate.connection.username",
    requiredEnvironmentVariable("FRED_DB_USERNAME"));
properties.put("hibernate.connection.password",
    requiredEnvironmentVariable("FRED_DB_PASSWORD"));
```

```java
current = connection == null
    ? sessionFactory.openSession()
    : sessionFactory.withOptions().connection(connection).openSession();
```

Benefits:

- safe publication of the singleton;
- no database secrets in Java source;
- explicit connection path preserved for tests/tools;
- `ThreadLocal.remove()` used during cleanup;
- Hibernate Sessions remain thread-confined.

## 4. HibernateServletUtil: safer request cleanup

### Before (`master`)

```java
try {
    sf.service();
} finally {
    try {
        FredHibernate.get().closeSession();
    } catch (Exception e) {
        log.log(Level.WARNING, "Could not close hibernate session", e);
        throw e;
    }
}
```

A close failure can replace the original request failure.

### After

```java
Throwable requestFailure = null;
try {
    function.service();
} catch (ServletException | IOException | RuntimeException | Error e) {
    requestFailure = e;
    throw e;
} finally {
    try {
        FredHibernate.get().closeSession();
    } catch (RuntimeException | Error closeFailure) {
        if (requestFailure != null) {
            requestFailure.addSuppressed(closeFailure);
        } else {
            throw closeFailure;
        }
    }
}
```

Benefits:

- preserves the root cause of a failed request;
- records cleanup failure as a suppressed exception;
- `@FunctionalInterface` documents the intended lambda contract.

## 5. web.xml: Servlet 2.5 → Servlet 3.1 async support

### Before (`master`)

```xml
<web-app version="2.5" ...>
```

### After

```xml
<web-app version="3.1"
    xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    ...>
```

```xml
<servlet>
    <servlet-name>wfsProxy</servlet-name>
    <servlet-class>nz.cri.gns.fred.servlet.WFSProxy</servlet-class>
    <async-supported>true</async-supported>
</servlet>
```

The character encoding filter is also marked async-supported, allowing the WFS request chain to enter Servlet async mode.

# Where virtual threads help FRED

## Strong candidates

### External HTTP proxy/service calls

`WFSProxy` is the clearest current example. The task spends most of its life waiting on network I/O. A virtual thread is a good match because the code can remain simple, blocking Java while consuming very few platform-thread resources.

The same model is appropriate for future SITE API, authentication-service, metadata-service, or other blocking HTTP integrations, provided the servlet uses asynchronous request processing rather than blocking the Tomcat worker while waiting for the virtual-thread result.

### Long-running import/export jobs that perform independent blocking I/O

Virtual threads can help when an XLS import or export has many independent blocking operations. Each task must own its Hibernate Session/transaction and must not share a request-scoped Session, JDBC Connection, CSV writer, or mutable model collection.

### Batch validation against independent services

If an import row requires several independent remote validations, each validation can run in its own virtual thread and the row can combine the results afterwards.

# Where virtual threads should NOT be added blindly

## Existing request-scoped Hibernate work

FRED currently stores the Hibernate Session in a `ThreadLocal`. Do not take a Hibernate entity/Session created on the servlet thread and use it inside another virtual thread.

If database work is intentionally moved to virtual threads, use this lifecycle per task:

```text
virtual thread
    -> obtain/open its own Hibernate Session
    -> begin its own transaction
    -> execute query/update
    -> commit/rollback
    -> close Session
```

The database connection pool remains the real upper bound on concurrent JDBC work. Creating 10,000 virtual threads does not create 10,000 safe database connections.

## CPU-heavy transformations

Virtual threads do not make CPU-bound coordinate conversion, CSV formatting, sorting, parsing, or geospatial calculations faster. Use ordinary bounded parallelism only after profiling demonstrates a CPU bottleneck.

## Shared CSV/response writers

Do not have multiple virtual threads write directly to the same `HttpServletResponse`, `CSVPrinter`, JSP writer, or mutable collection. Compute independent immutable results first, then write them from one owner thread/task.

# Recommended next modernization slices

1. Convert small immutable DTO/helper classes such as AJAX result values to records while retaining compatibility where needed.
2. Replace legacy date handling with `java.time` in new/refactored paths.
3. Replace remaining manual stream/reader loops with `transferTo`, `Files`, and modern charset APIs.
4. Reduce raw collections and unchecked casts in DAO/servlet code.
5. Convert switch-heavy mapping logic to modern switch expressions where behavior is well-covered by tests.
6. Add virtual-thread-backed async execution only around demonstrably blocking boundaries.
7. Keep Hibernate Session/transaction ownership explicit and thread-confined.
8. Add tests before changing large `ExportServlet`, importer, and Hibernate DAO paths.

# Suggested rollout

Treat this branch as a modernization spike until CI, integration tests, and Tomcat deployment tests pass. Merge in slices rather than one giant modernization merge: session state first, WFS proxy/async support second, Hibernate configuration hardening third, then larger servlet/DAO cleanups.
