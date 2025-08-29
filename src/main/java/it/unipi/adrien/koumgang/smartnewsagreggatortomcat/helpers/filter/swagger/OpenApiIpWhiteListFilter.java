package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.swagger;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.SwaggerConfiguration;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = "/api/openapi.json")
public class OpenApiIpWhiteListFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String remoteIp = request.getRemoteAddr();
        // System.out.println(remoteIp);

        boolean swagger_security_enable;
        try {
            SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
            swagger_security_enable = swaggerConfiguration.isSecurityEnable();
        } catch (Exception e) {
            // e.printStackTrace();
            swagger_security_enable = false;
        }

        if (!swagger_security_enable || SwaggerFilterUtils.isAllowed(remoteIp)) {
            // If IP is allowed, continue the request
            chain.doFilter(request, response);
        } else {
            // If IP is not allowed, return HTTP 403 Forbidden
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }

}
