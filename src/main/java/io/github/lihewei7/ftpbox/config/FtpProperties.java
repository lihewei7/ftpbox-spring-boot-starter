package io.github.lihewei7.ftpbox.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;

/**
 * @explain: FTP client configuration information
 * @author: lihewei
*/
@ConfigurationProperties("ftp")
public class FtpProperties {

    private String host = "localhost";
    private int port = 21;
    private String username;
    private String password = "";
    /**
     * Connection timeout.
     */
    private int connectTimeout = 0;
    /**
     * SSH kex algorithms.
     */
    private String kex;
    /**
     * host key.
     */
    private String keyPath;
    /**
     * Configuring multiple hosts.
     */
    private LinkedHashMap<String, FtpProperties> hosts;

    public LinkedHashMap<String, FtpProperties> getHosts() {
        return hosts;
    }

    public void setHosts(LinkedHashMap<String, FtpProperties> hosts) {
        this.hosts = hosts;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getKex() {
        return kex;
    }

    public void setKex(String kex) {
        this.kex = kex;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }
}
