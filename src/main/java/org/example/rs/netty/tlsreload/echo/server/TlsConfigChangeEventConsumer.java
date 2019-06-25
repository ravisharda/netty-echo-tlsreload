package org.example.rs.netty.tlsreload.echo.server;

import io.netty.channel.group.ChannelGroup;

import java.nio.file.WatchEvent;
import java.security.cert.CertificateException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.net.ssl.SSLException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.shared.ServerConfig;
import org.example.rs.netty.tlsreload.echo.shared.SslContextHelper;

/**
 * Handles the TLS config change event by performing the necessary actions.
 */
@RequiredArgsConstructor
@Slf4j
public class TlsConfigChangeEventConsumer implements Consumer<WatchEvent<?>> {
    /**
     * A counter representing the number of times this object has been asked to
     * consume an event.
     */
    public static AtomicInteger countOfEventsConsumed = new AtomicInteger(0);

    //private final @NonNull SocketChannel channel;
    private final @NonNull ServerConfig config;

    @Override
    public void accept(WatchEvent<?> watchEvent) {
        log.debug("Invoked for [{}]", watchEvent.context().toString());
        handleTlsConfigChange();
    }

    private void handleTlsConfigChange() {
        log.info("Current reload count = {}", countOfEventsConsumed.incrementAndGet());

        // Each channel has its own pipeline it is created automatically when a new channel is created.
        // Src: https://netty.io/4.0/api/io/netty/channel/ChannelPipeline.html
        ChannelGroup channels = Channels.get();
        channels.stream()
                // We don't care about channels that are not registered and channels that don't already have
                // a handler with name "ssl".
                .filter(c -> c.isRegistered() && c.pipeline().get("ssl") != null)
                .forEach(c -> {
                    try {
                        log.info("Pipeline before handling the change: [{}].", c.pipeline());
                        c.pipeline().replace("ssl",
                                "ssl", SslContextHelper.createServerSslContext(config)
                                        .newHandler(c.alloc()));
                        log.info("Done replacing SSL Context handler. Pipeline after handling the change: [{}].",
                                c.pipeline());
                    } catch (CertificateException | SSLException e) {
                        log.warn(e.getMessage(), e);
                        c.close();
                    }
                });
    }
}
