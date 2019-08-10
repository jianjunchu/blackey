package org.blackey.entity;

public class SystemRpcNodeInfo {

    private String hostname;
    private String password;
    private Integer port;
    private String username;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "SystemRpcNodeInfo{" +
                "hostname='" + hostname + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                '}';
    }
}
