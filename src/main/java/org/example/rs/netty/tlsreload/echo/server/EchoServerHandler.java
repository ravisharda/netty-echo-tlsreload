package org.example.rs.netty.tlsreload.echo.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic handler for the echo server.
 */
@ChannelHandler.Sharable
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    /**
     * This method is invoked for each incoming message.
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.debug("Server received: {}", ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
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

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("Unregistering the channel: {}.", ctx.channel());
        ctx.fireChannelUnregistered();
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
