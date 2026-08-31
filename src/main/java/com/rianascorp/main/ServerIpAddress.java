package com.rianascorp.main;

import java.util.Objects;

public class ServerIpAddress {
    private String ip;                     // current default IP
    private final java.util.List<String> history = new java.util.ArrayList<>();

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public java.util.List<String> getHistory() { return history; }

    @Override
    public String toString() { return ip == null ? "" : ip; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerIpAddress)) return false;
        ServerIpAddress that = (ServerIpAddress) o;
        return Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() { return Objects.hash(ip); }

}
