package org.example.rs.netty.tlsreload.echo.server;

import org.example.rs.netty.tlsreload.echo.common.FileUtils;
import org.example.rs.netty.tlsreload.echo.server.impl.EchoServer;
import org.example.rs.netty.tlsreload.echo.shared.ServerConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {

    public static void main(String... args) {
        // You'll see that the certificatePath and keyPath are set twice. The first set of calls are redundant, but we
        // leave it here so that it is easy to switch context without failing any checkstyle rules. To switch context
        // just change the order.
        ServerConfig config = ServerConfig.builder()
                .port(8889)
                .tlsEnabled(true)
                .useSelfSignedTlsMaterial(false)
                .certificatePath(FileUtils.pathOfFileInClasspath("server-cert.crt").toString())
                .keyPath(FileUtils.pathOfFileInClasspath("server-key.key").toString())
                .certificatePath("C:\\Workspace\\pki\\test\\server-cert.crt")
                .keyPath("C:\\Workspace\\pki\\test\\server-key.key")
                .build();
        EchoServer server = new EchoServer(config);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(server);
    }
}
