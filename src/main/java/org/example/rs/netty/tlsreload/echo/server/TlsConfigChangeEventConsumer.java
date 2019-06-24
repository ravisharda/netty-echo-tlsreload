package org.example.rs.netty.tlsreload.echo.server;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.shared.Config;
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

    private @NonNull ChannelPipeline pipeline;
    private @NonNull ByteBufAllocator byteBufferAllocator;

    @Override
    public void accept(WatchEvent<?> watchEvent) {
        handleTlsConfigChange();
    }

    private void handleTlsConfigChange() {
        log.debug("Current reload count = {}", countOfEventsConsumed.incrementAndGet());
        log.debug("Pipeline inside the callback: [{}].", pipeline);
        try {
            pipeline.replace("ssl",
                    "ssl", SslContextHelper.createServerSslContext(Config.ENABLE_TLS,
                            Config.USE_SUPPLIED_TLS_MATERIAL, Config.SERVER_CERT, Config.SERVER_KEY)
                            .newHandler(byteBufferAllocator));
            log.info("Done replacing SSL Context handler.");
        }  catch (CertificateException | SSLException e) {
            log.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}