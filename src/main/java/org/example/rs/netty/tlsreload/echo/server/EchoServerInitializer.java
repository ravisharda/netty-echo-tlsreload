package org.example.rs.netty.tlsreload.echo.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.common.FileChangeWatcherService;
import org.example.rs.netty.tlsreload.echo.shared.Config;
import org.example.rs.netty.tlsreload.echo.shared.SslContextHelper;

@Slf4j
public class EchoServerInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslCtx;

    /**
     * This method is called once when the Channel is registered.
     * @param ch
     * @throws Exception
     */
    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        log.info("Initializing channel");
        final ChannelPipeline pipeline = ch.pipeline();

        sslCtx = SslContextHelper.createServerSslContext(Config.ENABLE_TLS, Config.USE_SUPPLIED_TLS_MATERIAL,
                Config.SERVER_CERT, Config.SERVER_KEY);

        if (sslCtx != null) {
            pipeline.addLast("ssl", sslCtx.newHandler(ch.alloc()));
            log.info("Done adding SSL Context handler to the pipeline.");
        }
        //pipeline.addLast("logging", new LoggingHandler(LogLevel.INFO));
        pipeline.addLast("app", new EchoServerHandler()); // business logic handler.
        log.info("Done adding App handler to the pipeline.");
        log.info(pipeline.toString());

        FileChangeWatcherService fileWatcher = new FileChangeWatcherService(Config.SERVER_CERT,
                new TlsConfigChangeEventConsumer(pipeline, ch.alloc()));
        fileWatcher.setDaemon(true);
        fileWatcher.start();
    }
}