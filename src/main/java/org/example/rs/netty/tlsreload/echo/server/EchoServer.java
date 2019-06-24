package org.example.rs.netty.tlsreload.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.shared.ServerConfig;

/**
 * A TLS enabled server that echoes back any received data from a client.
 *
 * This example is based on the Echo example provided in Netty examples:
 * https://github.com/netty/netty/tree/4.0/example/src/main/java/io/netty/example/echo
 *
 */
@Slf4j
public final class EchoServer implements AutoCloseable {

    private final ServerConfig serverConfig;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public EchoServer() {
        // Use the default config.
        this(ServerConfig.builder().build());
    }

    public EchoServer(ServerConfig config) {
        this.serverConfig = config;
        log.info(this.serverConfig.toString());
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            // Configure the server
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            log.debug("Done creating server bootstrap");
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new EchoServerInitializer(serverConfig));

            // Start the server
            ChannelFuture f = serverBootstrap.bind(serverConfig.getPort()).sync();
            log.info("Server started");

            // Wait until the server socket is closed
            f.channel().closeFuture().sync();
        } finally {
            close();
        }
    }

    public static void main(String... args) throws Exception {
        ServerConfig config = ServerConfig.builder()
                .port(8889)
                .tlsEnabled(true)
                .useSelfSignedTlsMaterial(false)
                .certificatePath("C:\\Workspace\\pki\\test\\server-cert.crt")
                .keyPath("C:\\Workspace\\pki\\test\\server-key.key").build();

        try (EchoServer server = new EchoServer(config)) {
            server.start();
        }
    }

    @Override
    public void close() {
        log.info("Shutting down event loops");
        if (!bossGroup.isShutdown()) {
            // Shut down all event loops to terminate all threads.
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }
}
