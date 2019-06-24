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

    public static SslContext createServerSslContext(boolean enableTls, boolean useSuppliedTlsMaterial,
                                                    String serverCertificatePath, String serverKeyPath) throws CertificateException, SSLException {
        SslContext result = null;

        if (enableTls) {
            log.trace("Creating an SSL Context.");
            if (useSuppliedTlsMaterial) {
                result = SslContextBuilder.forServer(
                        new File(serverCertificatePath),
                        new File(serverKeyPath)).build();
                log.debug("Done creating SSL Context using pre-created material");
            } else {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                result = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                log.debug("Done creating SSL Context using self-signed material");
            }
        }
        return result;
    }

    public static SslContext createClientSslContext(boolean enableTls, boolean useSuppliedTlsMaterial,
                                                    String caCertificatePath) throws SSLException {
        final SslContext sslCtx;
        if (Config.ENABLE_TLS) {
            log.debug("Creating an SSL Context...");
            if (Config.USE_SUPPLIED_TLS_MATERIAL) {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(new File(caCertificatePath)).build();
                log.debug("Done creating SSL Context using pre-created material");
            } else {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                log.debug("Done creating SSL Context using self-signed material");
            }
        } else {
            sslCtx = null;
        }
        return sslCtx;
    }
}
