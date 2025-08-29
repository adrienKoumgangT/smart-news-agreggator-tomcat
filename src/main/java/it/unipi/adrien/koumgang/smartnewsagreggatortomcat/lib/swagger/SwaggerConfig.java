package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.swagger;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class SwaggerConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        servletContext.addServlet("OpenApiResource", (Servlet) new OpenApiResource())
                .addMapping("/openapi.json");
    }

}
