package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.swagger;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.SwaggerConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SwaggerFilterUtils {

    public static boolean isAllowed(String remoteIp) {
        for (String allowedIp : getAllowedIps()) {
            if (Objects.equals(remoteIp, allowedIp) || (allowedIp.contains("/") && isIpInRange(remoteIp, allowedIp))) {
                return true;
            }
        }
        return false;
    }

    // List of allowed IPs or subnets in CIDR notation
    public static List<String> getAllowedIps() {
        try {
            SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
            String[] ips = swaggerConfiguration.getSecurityIpList().split(",");
            return Arrays.stream(ips).map(String::trim).collect(Collectors.toList());
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return List.of("localhost", "0:0:0:0:0:0:0:1", "127.0.0.1");
    }

    public static boolean isIpInRange(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            InetAddress targetIp = InetAddress.getByName(ip);
            InetAddress subnetIp = InetAddress.getByName(parts[0]);
            int prefixLength = Integer.parseInt(parts[1]);

            byte[] targetBytes = targetIp.getAddress();
            byte[] subnetBytes = subnetIp.getAddress();

            // Convert the prefix length to a subnet mask
            int maskLength = prefixLength / 8;
            int remainder = prefixLength % 8;
            byte[] mask = new byte[targetBytes.length];

            for (int i = 0; i < maskLength; i++) {
                mask[i] = (byte) 0xFF; // Full byte (255)
            }
            if (remainder > 0) {
                mask[maskLength] = (byte) (0xFF << (8 - remainder));
            }

            // Check if the target IP is within the CIDR range
            for (int i = 0; i < targetBytes.length; i++) {
                if ((targetBytes[i] & mask[i]) != (subnetBytes[i] & mask[i])) {
                    return false;
                }
            }

            return true;
        } catch (UnknownHostException e) {
            // e.printStackTrace();
            return false;
        }
    }

}
