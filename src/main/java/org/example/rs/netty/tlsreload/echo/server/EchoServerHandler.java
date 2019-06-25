package org.example.rs.netty.tlsreload.echo.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic handler for the echo server.
 */
@ChannelHandler.Sharable
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * This method is invoked for each incoming message.
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.writeAndFlush(msg);
        //ctx.write(msg);

    }

    /**
     * Notifies the handler that the last call made to channelRead() was the last message in the current batch.
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // Flush pending messages to the remote peer and close the channel.
        ctx.flush();
    }

    /**
     * Called if an exception is thrown during the read operation.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn(cause.getMessage(), cause);

        // Close the connection when an exception is raised
        cause.printStackTrace();
        ctx.close();
    }
}
