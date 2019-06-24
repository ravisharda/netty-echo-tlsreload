package org.example.rs.netty.tlsreload.echo.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.example.rs.netty.tlsreload.echo.shared.Config;
import org.example.rs.netty.tlsreload.echo.shared.SslContextHelper;

import javax.net.ssl.SSLException;

public class EchoClientInitializer extends ChannelInitializer<SocketChannel> {
    private final String serverHost;
    private final int port;
    private final SslContext sslCtx;

    public EchoClientInitializer(String serverHost, int port) throws SSLException {
        this.serverHost = serverHost;
        this.port = port;
        sslCtx = SslContextHelper.createClientSslContext(Config.ENABLE_TLS, Config.USE_SUPPLIED_TLS_MATERIAL,
                Config.CA_CERT);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc(), serverHost, port));
        }
        p.addLast(new LoggingHandler(LogLevel.INFO));
        p.addLast(new EchoClientHandler());
    }
}