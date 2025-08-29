package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.utils;

import com.google.gson.GsonBuilder;
import jakarta.ws.rs.core.HttpHeaders;

import java.util.List;
import java.util.Map;

public class AuthUtils {

    public static String getIpFromRequest(HttpHeaders request) {
        if(request == null) return null;

        String ip = request.getHeaderString("X-Forwarded-For");
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaderString("Proxy-Client-IP");
            if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeaderString("WL-Proxy-Client-IP");
                if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeaderString("HTTP_CLIENT_IP");
                    if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getHeaderString("HTTP_X_FORWARDED_FOR");
                        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                            ip = request.getHeaderString("remoteAddress");
                            if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                                ip = request.getHeaderString(HttpHeaders.USER_AGENT);
                            }
                        }
                    }
                }
            }
        }

        return ip;
    }


    public static String getDataServer(HttpHeaders headers) {
        if(headers == null) return null;

        Map<String, List<String>> requestHeaders = headers.getRequestHeaders();
        if(requestHeaders == null) return null;

        return new GsonBuilder().serializeNulls().create().toJson(requestHeaders);
    }

}
