package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.utils;

import com.google.gson.GsonBuilder;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;

public class AuthUtils {

    public static String getIpFromRequestHeader(HttpHeaders headers) {
        if(headers == null) return null;

        String ip = headers.getHeaderString("X-Forwarded-For");
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getHeaderString("Proxy-Client-IP");
            if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getHeaderString("WL-Proxy-Client-IP");
                if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = headers.getHeaderString("HTTP_CLIENT_IP");
                    if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = headers.getHeaderString("HTTP_X_FORWARDED_FOR");
                        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                            ip = headers.getHeaderString("remoteAddress");
                            if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                                ip = headers.getHeaderString(HttpHeaders.USER_AGENT);
                            }
                        }
                    }
                }
            }
        }

        return ip;
    }


    public static String getDataServerString(HttpHeaders headers) {
        if(headers == null) return null;

        Map<String, List<String>> requestHeaders =getDataServer(headers);
        if(requestHeaders == null) return null;

        return new GsonBuilder().serializeNulls().create().toJson(requestHeaders);
    }

    public static Map<String, List<String>> getDataServer(HttpHeaders headers) {
        if(headers == null) return null;

        return headers.getRequestHeaders();
    }



}
