package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.prometheus;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Histogram;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MetricsFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Counter REQUESTS = Counter.builder()
            .name("http_server_requests_total")
            .help("Total HTTP requests")
            .labelNames("method", "path", "status")
            .register();

    private static final Histogram LATENCY = Histogram.builder()
            .name("http_server_request_seconds")
            .help("Request latency in seconds")
            .labelNames("method", "path", "status")
            .classicUpperBounds(0.01, 0.05, 0.1, 0.25, 0.5, 1, 2, 5) // instead of buckets(...)
            .register();

    private static final String START_NS = "prom_start_ns";
    private static final String METH = "prom_method";
    private static final String PATH = "prom_path";

    @Override
    public void filter(ContainerRequestContext req) {
        req.setProperty(START_NS, System.nanoTime());
        req.setProperty(METH, req.getMethod());
        req.setProperty(PATH, req.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) {
        Object start = req.getProperty(START_NS);
        if (start instanceof Long) {
            long durNs = System.nanoTime() - (Long) start;
            double seconds = durNs / 1_000_000_000.0;

            String method = String.valueOf(req.getProperty(METH));
            String path   = String.valueOf(req.getProperty(PATH));
            String status = String.valueOf(res.getStatus());

            LATENCY.labelValues(method, path, status).observe(seconds);
            REQUESTS.labelValues(method, path, status).inc();
        }
    }
}