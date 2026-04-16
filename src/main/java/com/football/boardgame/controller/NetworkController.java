package com.football.boardgame.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
public class NetworkController {

    @GetMapping("/ip")
    public Map<String, String> getLocalIp() {
        Map<String, String> response = new HashMap<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // Filter out loopback, virtual, and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String ip = addr.getHostAddress();
                    
                    // Filter for IPv4 and LAN ranges (192.168.x.x, 10.x.x.x, 172.16.x.x)
                    if (ip.contains(":") || (!ip.startsWith("192.") && !ip.startsWith("10.") && !ip.startsWith("172."))) continue;
                    
                    response.put("ip", ip);
                    return response;
                }
            }
        } catch (SocketException e) {
            response.put("error", e.getMessage());
        }
        response.put("ip", "localhost");
        return response;
    }
}
