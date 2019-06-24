package org.example.rs.netty.tlsreload.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.shared.Config;
import org.example.rs.netty.tlsreload.echo.shared.SslContextHelper;

@Slf4j
public class EchoClient {

    public static void main(String[] args) throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            log.debug("Done creating client bootstrap");
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new EchoClientInitializer(Config.SERVER_HOST, Config.SERVER_PORT));

            // Start the client.
            ChannelFuture f = b.connect(Config.SERVER_HOST, Config.SERVER_PORT).sync();
            log.trace("Done starting the client to connect to server at host {} and port {}",
                    Config.SERVER_HOST, Config.SERVER_PORT);

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            log.debug("Shutting down client event loop");

            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
