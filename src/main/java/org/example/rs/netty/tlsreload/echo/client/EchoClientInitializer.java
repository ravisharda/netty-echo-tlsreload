package org.example.rs.netty.tlsreload.echo.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.example.rs.netty.tlsreload.echo.shared.ClientConfig;
import org.example.rs.netty.tlsreload.echo.shared.SslContextHelper;

import javax.net.ssl.SSLException;

public class EchoClientInitializer extends ChannelInitializer<SocketChannel> {
    private final ClientConfig config;
    private final SslContext sslCtx;

    public EchoClientInitializer(ClientConfig config) throws SSLException {
        this.config = config;
        sslCtx = SslContextHelper.createClientSslContext(config);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc(), config.getServerHost(), config.getServerPort()));
        }
        p.addLast(new LoggingHandler(LogLevel.INFO));
        p.addLast(new EchoClientHandler());
    }
}