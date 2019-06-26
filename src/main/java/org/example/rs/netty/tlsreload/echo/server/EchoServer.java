package org.example.rs.netty.tlsreload.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.common.FileChangeWatcherService;
import org.example.rs.netty.tlsreload.echo.common.FileUtils;
import org.example.rs.netty.tlsreload.echo.shared.ServerConfig;

/**
 * A TLS enabled server that echoes back any received data from a client.
 *
 * This example is based on the Echo example provided in Netty examples:
 * https://github.com/netty/netty/tree/4.0/example/src/main/java/io/netty/example/echo
 *
 */
@Slf4j
public final class EchoServer extends Thread {

    private final ServerConfig serverConfig;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public EchoServer(ServerConfig config) {
        this.serverConfig = config;
        log.info(this.serverConfig.toString());
    }

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

    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            // Configure the server
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            log.debug("Done creating server bootstrap");
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    //.handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new EchoServerInitializer(serverConfig));

            // Start the server
            ChannelFuture f = serverBootstrap.bind(serverConfig.getPort()).sync();
            log.info("Server started");

            if (serverConfig.isTlsEnabled() && !serverConfig.isUseSelfSignedTlsMaterial()) {
                // Register a file watcher
                FileChangeWatcherService fileWatcher = new FileChangeWatcherService(
                        serverConfig.getCertificatePath(),
                        new TlsConfigChangeEventConsumer(serverConfig));
                fileWatcher.setDaemon(true);
                fileWatcher.start();
            }

            // Wait until the server socket is closed
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            close();
        } finally {
            close();
        }
    }

    public static void main(String... args) {
        ServerConfig config = ServerConfig.builder()
                .port(8889)
                .tlsEnabled(true)
                .useSelfSignedTlsMaterial(false)
                .certificatePath(FileUtils.pathOfFileInClasspath("server-cert.crt").toString())
                .keyPath(FileUtils.pathOfFileInClasspath("server-key.key").toString())
                //.certificatePath("C:\\Workspace\\pki\\test\\server-cert.crt")
                //.keyPath("C:\\Workspace\\pki\\test\\server-key.key")
                .build();
        EchoServer server = new EchoServer(config);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(server);
    }
}
