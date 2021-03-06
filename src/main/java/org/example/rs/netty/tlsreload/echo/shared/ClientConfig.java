package org.example.rs.netty.tlsreload.echo.shared;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.example.rs.netty.tlsreload.echo.common.FileUtils;

@Getter
@Builder
@ToString
public class ClientConfig {
    @Builder.Default
    private String serverHost = "localhost";
        // "127.0.0.1";

    @Builder.Default
    private int serverPort = ServerConfig.builder().build().getPort();

    /**
     * Usually, the CA certificate that is used to sign the server certificates.
     */
    @Builder.Default
    private String trustedCertficatePath = FileUtils.pathOfFileInClasspath("ca-cert.crt").toString();

    @Builder.Default
    private boolean enableTls = true;

    @Builder.Default
    private boolean useSelfSignedTlsMaterial = false;

    @Builder.Default
    private boolean loggingHandlerEnabled = false;
}
