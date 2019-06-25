package org.example.rs.netty.tlsreload.echo.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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
    private String certificatePath = null;

    @Builder.Default
    private String keyPath = null;

    @Builder.Default
    private boolean loggingHandlerEnabled = false;

    public static void main(String[] args) {
        System.out.println(ServerConfig.builder().build());
    }
}
