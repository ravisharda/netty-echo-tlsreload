package org.example.rs.netty.tlsreload.echo.client.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.example.rs.netty.tlsreload.echo.shared.ClientConfig;

import java.util.function.Consumer;

@Slf4j
public class EchoClient implements Runnable {

    private final ClientConfig config;

    private EventLoopGroup group;
    private Consumer<Channel> callback;
    private int waittimeBeforecloseInSeconds = 10;

    public EchoClient(ClientConfig config, Consumer<Channel> callback) {
        this(config, callback, 10);
    }

    public EchoClient(ClientConfig config, Consumer<Channel> callback, int waittimeBeforecloseInSeconds) {
        this.config = config;
        log.info(this.config.toString());
        this.callback = callback;
        this.waittimeBeforecloseInSeconds = waittimeBeforecloseInSeconds;
    }

    public void run() {
        // Configure the client.
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            log.debug("Done creating client bootstrap");
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new EchoClientInitializer(config));

            ChannelFuture chFuture = b.connect(config.getServerHost(), config.getServerPort());

            // We are using await() here instead of sync(). Both block the current thread but sync() will rethrow the
            // failure if this future failed, while await() will not. We want to have control over what message gets
            // logged and when. We do the opposite when we write the message below.
            chFuture.awaitUninterruptibly();
            Channel channel = chFuture.channel();

            assert chFuture.isDone();
            if (chFuture.isCancelled()) {
                log.info("Connection cancelled by user.");
            } else if (!chFuture.isSuccess()) {
                Throwable e = chFuture.cause();
                log.warn(e.getMessage(), e);
            } else {
                this.callback.accept(channel);
            }
            Thread.sleep(waittimeBeforecloseInSeconds * 1000);
            channel.close();
            // channel.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    public void close() {
        log.debug("Shutting down client event loop");

        if (!group.isShutdown()) {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }


}
