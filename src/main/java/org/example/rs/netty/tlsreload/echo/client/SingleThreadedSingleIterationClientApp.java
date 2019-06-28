package org.example.rs.netty.tlsreload.echo.client;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.rs.netty.tlsreload.echo.client.impl.EchoClient;
import org.example.rs.netty.tlsreload.echo.common.FileUtils;
import org.example.rs.netty.tlsreload.echo.shared.ClientConfig;

public class SingleThreadedSingleIterationClientApp {

    public static void main(String[] args) {

        // You'll see that the trustedCertificatePath is set twice. The first call is redundant, but we
        // leave it here so that it is easy to switch context without failing any checkstyle rules. To switch context
        // just change the order of the statement.
        ClientConfig config = ClientConfig.builder()
                .enableTls(true)
                .useSelfSignedTlsMaterial(false)
                .serverHost("localhost")
                .serverPort(8889)
                .trustedCertficatePath(FileUtils.pathOfFileInClasspath("ca-cert.crt").toString())
                .trustedCertficatePath("C:\\Workspace\\pki\\test\\ca-cert.crt")
                .build();
        //ClientConfig config = ClientConfig.builder().build();

        EchoClient client = new EchoClient(config, channel -> {
            try {
                channel.writeAndFlush(Unpooled.copiedBuffer("Ping!! ", CharsetUtil.UTF_8)).sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(client);
    }
}
