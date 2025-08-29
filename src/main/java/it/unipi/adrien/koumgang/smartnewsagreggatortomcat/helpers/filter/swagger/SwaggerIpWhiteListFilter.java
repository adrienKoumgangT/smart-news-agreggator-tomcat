package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.swagger;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.SwaggerConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = "/swagger-ui/*")
public class SwaggerIpWhiteListFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if(request instanceof HttpServletRequest httpRequest) {
            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURI();
            MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SWAGGER] " + method + " --- URI: " + uri);

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
        } else chain.doFilter(request, response);

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
