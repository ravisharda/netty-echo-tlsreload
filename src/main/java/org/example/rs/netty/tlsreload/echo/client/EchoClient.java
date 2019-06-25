package org.example.rs.netty.tlsreload.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.shared.ClientConfig;

import javax.net.ssl.SSLException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class EchoClient implements AutoCloseable {

    private final ClientConfig config;

    private EventLoopGroup group;

    public EchoClient() {
        // Use the default config.
        this(ClientConfig.builder().build());
    }

    public EchoClient(ClientConfig config) {
        this.config = config;
        log.info(this.config.toString());
    }

    public void start() throws SSLException, InterruptedException, ExecutionException {
        start(true);
    }

    public void start(boolean waitUntilClosed) throws InterruptedException, SSLException, ExecutionException {
        // Configure the client.
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            log.debug("Done creating client bootstrap");
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new EchoClientInitializer(config));

            // Start the client.
            ChannelFuture f = b.connect(config.getServerHost(), config.getServerPort()).sync();
            log.trace("Done starting the client to connect to server at host {} and port {}",
                    config.getServerHost(), config.getServerPort());

            //f.sync().get();
            ChannelFuture writeFuture = f.channel().writeAndFlush(
                    Unpooled.copiedBuffer("\nWhat's your name?", CharsetUtil.UTF_8));

            // Waiting for the write to complete.
            //writeFuture.await();
            //f.channel().close();

            if (waitUntilClosed) {
                // Wait until the connection is closed.
                f.channel().closeFuture().sync();
            }
        } finally {
            close();
        }
    }

    public static void main(String[] args) throws SSLException, InterruptedException, ExecutionException {
        ClientConfig config = ClientConfig.builder()
                .enableTls(true)
                .useSelfSignedTlsMaterial(false)
                .serverHost("localhost")
                .serverPort(8889)
                .trustedCertficatePath("C:\\Workspace\\pki\\test\\ca-cert.crt").build();

        EchoClient client = new EchoClient(config);
        client.start();
    }

    @Override
    public void close() {
        log.debug("Shutting down client event loop");

        if (!group.isShutdown()) {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
