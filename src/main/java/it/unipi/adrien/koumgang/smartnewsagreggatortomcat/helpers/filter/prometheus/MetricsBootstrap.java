package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.prometheus;

import io.prometheus.metrics.exporter.servlet.jakarta.PrometheusMetricsServlet;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MetricsBootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Register standard JVM metrics
        JvmMetrics.builder().register();

        // Expose /metrics (Prometheus 1.x servlet)
        sce.getServletContext()
                .addServlet("metrics", new PrometheusMetricsServlet())
                .addMapping("/metrics");
    }
}
