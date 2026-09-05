package nz.cri.gns.fred.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Proxies WFS requests without tying up a Tomcat platform thread while waiting
 * for the upstream WFS service.
 *
 * <p>Java 11's {@link HttpClient} replaces the legacy URLConnection code and
 * Java 21+ virtual threads execute each blocking proxy operation cheaply. The
 * servlet uses Servlet async mode so the original container thread can return
 * to Tomcat while the virtual thread waits for the remote service.</p>
 */
public class WFSProxy extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(WFSProxy.class.getName());
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);
    private static final long ASYNC_TIMEOUT_MILLIS = REQUEST_TIMEOUT.plusSeconds(5).toMillis();

    private String targetUrlWfs;
    private ExecutorService virtualThreadExecutor;
    private HttpClient httpClient;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        initialiseProxy();
    }

    private void initialiseProxy() throws ServletException {
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            targetUrlWfs = (String) env.lookup("FRED_WFS");
        } catch (NamingException e) {
            throw new ServletException("Unable to resolve JNDI resource FRED_WFS", e);
        }

        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        executeAsync(request, response, () -> proxyGet(request, response));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        executeAsync(request, response, () -> proxyPost(request, response));
    }

    private void executeAsync(HttpServletRequest request,
        HttpServletResponse response,
        ProxyOperation operation) {

        AsyncContext async = request.startAsync();
        async.setTimeout(ASYNC_TIMEOUT_MILLIS);

        virtualThreadExecutor.submit(() -> {
            try {
                operation.execute();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sendProxyError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "WFS proxy request was interrupted", e);
            } catch (IOException e) {
                sendProxyError(response, HttpServletResponse.SC_BAD_GATEWAY,
                    "Unable to communicate with the WFS service", e);
            } catch (RuntimeException e) {
                sendProxyError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unexpected WFS proxy error", e);
            } finally {
                async.complete();
            }
        });
    }

    private void proxyGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, InterruptedException {

        URI uri = URI.create(targetUrlWfs + buildQueryString(request));
        HttpRequest upstreamRequest = HttpRequest.newBuilder(uri)
            .timeout(REQUEST_TIMEOUT)
            .GET()
            .build();

        copyUpstreamResponse(httpClient.send(upstreamRequest,
            HttpResponse.BodyHandlers.ofInputStream()), response);
    }

    private void proxyPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, InterruptedException {

        byte[] requestBody = request.getInputStream().readAllBytes();
        HttpRequest upstreamRequest = HttpRequest.newBuilder(URI.create(targetUrlWfs))
            .timeout(REQUEST_TIMEOUT)
            .header("Content-Type", "text/xml")
            .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
            .build();

        copyUpstreamResponse(httpClient.send(upstreamRequest,
            HttpResponse.BodyHandlers.ofInputStream()), response);
    }

    private static String buildQueryString(HttpServletRequest request) {
        if (request.getParameterMap().isEmpty()) {
            return "";
        }

        String query = request.getParameterMap().entrySet().stream()
            .flatMap(entry -> Arrays.stream(entry.getValue())
                .map(value -> encode(entry.getKey()) + "=" + encode(value)))
            .collect(Collectors.joining("&"));

        return "?" + query;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static void copyUpstreamResponse(HttpResponse<InputStream> upstream,
        HttpServletResponse response) throws IOException {

        response.setStatus(upstream.statusCode());
        response.setContentType(upstream.headers()
            .firstValue("Content-Type")
            .orElse("text/xml"));

        try (InputStream body = upstream.body()) {
            body.transferTo(response.getOutputStream());
        }
    }

    private static void sendProxyError(HttpServletResponse response,
        int status,
        String message,
        Exception error) {

        LOG.log(Level.WARNING, message, error);
        if (!response.isCommitted()) {
            try {
                response.sendError(status, message);
            } catch (IOException sendError) {
                LOG.log(Level.FINE, "Unable to send WFS proxy error response", sendError);
            }
        }
    }

    @Override
    public void destroy() {
        if (virtualThreadExecutor != null) {
            virtualThreadExecutor.shutdown();
        }
        super.destroy();
    }

    @FunctionalInterface
    private interface ProxyOperation {
        void execute() throws IOException, InterruptedException;
    }
}
