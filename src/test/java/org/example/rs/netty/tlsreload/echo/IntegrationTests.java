package org.example.rs.netty.tlsreload.echo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.rs.netty.tlsreload.echo.client.EchoClient;
import org.example.rs.netty.tlsreload.echo.common.FileUtils;
import org.example.rs.netty.tlsreload.echo.server.EchoServer;
import org.example.rs.netty.tlsreload.echo.shared.ClientConfig;
import org.example.rs.netty.tlsreload.echo.shared.ServerConfig;
import org.junit.Test;

public class IntegrationTests {

    @Test
    public void runServerAndClient() throws InterruptedException {
        ServerConfig serverConfig = ServerConfig.builder()
                .port(8889)
                .tlsEnabled(true)
                .useSelfSignedTlsMaterial(false)
                .certificatePath(FileUtils.pathOfFileInClasspath("server-cert.crt").toString())
                .keyPath(FileUtils.pathOfFileInClasspath("server-key.key").toString())
                .build();
        EchoServer server = new EchoServer(serverConfig);
        ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.submit(server);

        ClientConfig clientConfig = ClientConfig.builder()
                .enableTls(true)
                .useSelfSignedTlsMaterial(false)
                .serverHost("localhost")
                .serverPort(8889)
                .trustedCertficatePath(FileUtils.pathOfFileInClasspath("ca-cert.crt").toString())
                .build();
        EchoClient client = new EchoClient(clientConfig);
        ExecutorService clientExecutor = Executors.newSingleThreadExecutor();
        clientExecutor.submit(client);

        // It is usually a bad practice to do this in tests. Resorting to it for the time being to ensure that
        // both the server and the client keep running for some time. Otherwise, the test will exit immediately.
        Thread.sleep(20 * 1000);

        if (!serverExecutor.isShutdown()) {
            serverExecutor.shutdownNow();
        }
        if (!clientExecutor.isShutdown()) {
            clientExecutor.shutdownNow();
        }

        // No assertions. Observe the logs.
    }

    @Test
    public void reloadServerCertificateOnce() {
        // TODO
    }

    @Test
    public void reloadServerCertificateMultipleTimes() {
        // TODO
    }
}
