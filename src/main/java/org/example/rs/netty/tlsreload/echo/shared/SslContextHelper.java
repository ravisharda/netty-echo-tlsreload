package org.example.rs.netty.tlsreload.echo.shared;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.io.File;
import java.security.cert.CertificateException;

@Slf4j
public class SslContextHelper {

    public static SslContext createServerSslContext(ServerConfig config) throws CertificateException, SSLException {
        SslContext result = null;

        if (config.isTlsEnabled()) {
            log.trace("Creating an SSL Context.");
            if (config.isUseSelfSignedTlsMaterial()) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                result = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                log.debug("Done creating SSL Context using auto-generated self-signed material");
            } else {
                result = SslContextBuilder.forServer(
                        new File(config.getCertificatePath()),
                        new File(config.getKeyPath())).build();
                log.debug("Done creating SSL Context using supplied material");
            }
        }
        return result;
    }

    public static SslContext createClientSslContext(ClientConfig config) throws SSLException {
        final SslContext sslCtx;
        if (config.isEnableTls()) {
            log.debug("Creating an SSL Context...");
            if (config.isUseSelfSignedTlsMaterial()) {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                log.debug("Done creating SSL Context using auto-generated self-signed material");
           } else {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(new File(config.getTrustedCertficatePath())).build();
                log.debug("Done creating SSL Context using supplied material");
            }
        } else {
            sslCtx = null;
        }
        return sslCtx;
    }
}
