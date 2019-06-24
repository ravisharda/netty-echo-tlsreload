package org.example.rs.netty.tlsreload.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.shared.Config;

/**
 * A TLS enabled server that echoes back any received data from a client.
 *
 * This example is based on the Echo example provided in Netty examples:
 * https://github.com/netty/netty/tree/4.0/example/src/main/java/io/netty/example/echo
 *
 */
@Slf4j
public final class EchoServer {

    public static void main(String... args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // Configure the server
            ServerBootstrap b = new ServerBootstrap();
            log.debug("Done creating server bootstrap");
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new EchoServerInitializer());

            // Start the server
            ChannelFuture f = b.bind(Config.SERVER_PORT).sync();
            log.info("Server started");

            // Wait until the server socket is closed
            f.channel().closeFuture().sync();
        } finally {
            log.info("Shutting down event loops");

            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
