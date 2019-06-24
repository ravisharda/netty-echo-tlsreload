package org.example.rs.netty.tlsreload.echo.shared;

public class Config {
    // Server configuration
    public static final String SERVER_HOST = "127.0.0.1";
    public static final int SERVER_PORT = 8889;

    public static final boolean ENABLE_TLS = true;
    public static final boolean USE_SUPPLIED_TLS_MATERIAL = true;
    public static final String SERVER_CERT = "C:\\Workspace\\pki\\test\\server-cert.crt";
    public static final String SERVER_KEY = "C:\\Workspace\\pki\\test\\server-key.key";
    public static final String CA_CERT = "C:\\Workspace\\pki\\test\\ca-cert.crt";
}
