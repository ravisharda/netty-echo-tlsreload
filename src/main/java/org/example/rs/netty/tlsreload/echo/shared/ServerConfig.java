package org.example.rs.netty.tlsreload.echo.shared;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.example.rs.netty.tlsreload.echo.common.FileUtils;

@Getter
@Builder
@ToString
public class ServerConfig {

    @Builder.Default
    private int port = 8080;

    @Builder.Default
    private boolean tlsEnabled = true;

    @Builder.Default
    private boolean useSelfSignedTlsMaterial = false;

    @Builder.Default
    private String certificatePath = FileUtils.pathOfFileInClasspath("server-cert.crt").toString();

    @Builder.Default
    private String keyPath = FileUtils.pathOfFileInClasspath("server-key.key").toString();

    @Builder.Default
    private boolean loggingHandlerEnabled = false;
}
