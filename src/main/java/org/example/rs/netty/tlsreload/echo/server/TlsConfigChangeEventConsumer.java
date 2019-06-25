package org.example.rs.netty.tlsreload.echo.server;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelPipeline;

import io.netty.channel.socket.SocketChannel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.shared.ServerConfig;
import org.example.rs.netty.tlsreload.echo.shared.SslContextHelper;

import javax.net.ssl.SSLException;
import java.nio.file.WatchEvent;
import java.security.cert.CertificateException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class TlsConfigChangeEventConsumer implements Consumer<WatchEvent<?>> {
    /**
     * Iteration counter for this class.
     */
    public static AtomicInteger countOfEventsConsumed = new AtomicInteger(0);

    private final @NonNull SocketChannel channel;
    private final @NonNull ServerConfig config;

    @Override
    public void accept(WatchEvent<?> watchEvent) {
        log.debug("Invoked for [{}]", watchEvent.context().toString());
        log.debug("channel: {}", this.channel.toString());
        log.debug("channel pipeline: {}", this.channel.pipeline());
        handleTlsConfigChange();
    }

    private void handleTlsConfigChange() {
        log.info("Current reload count = {}", countOfEventsConsumed.incrementAndGet());

        // Each channel has its own pipeline it is created automatically when a new channel is created.
        // Src: https://netty.io/4.0/api/io/netty/channel/ChannelPipeline.html
        ChannelPipeline pipeline = channel.pipeline();
        log.info("Pipeline before handling the change: [{}].", pipeline);
        try {
            if (pipeline.get("ssl") != null) {
                pipeline.replace("ssl",
                        "ssl", SslContextHelper.createServerSslContext(config)
                                .newHandler(channel.alloc()));
                log.info("Done replacing SSL Context handler.");
            } else {
                log.info("Did not find handler with name [ssl]");
            }
            log.info("Pipeline after handling the change: [{}].", pipeline);
        }  catch (CertificateException | SSLException e) {
            log.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}