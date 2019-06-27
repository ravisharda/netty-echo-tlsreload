package org.example.rs.netty.tlsreload.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class EchoClientHandler
        extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * Invoked when the channel of the ChannelHandlerContext becomes active.
     *
     * See more about this in: https://netty.io/wiki/new-and-noteworthy-in-4.0.html#wiki-h4-19
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("Channel [{}] is active.", ctx.channel());

         // When notified that the channel is active, send a hello message.
        ctx.writeAndFlush(Unpooled.copiedBuffer("\nHello, there!",
                    CharsetUtil.UTF_8));
    }

    /**
     * Invoked when the channel of the ChannelHandlerContext becomes inactive.
     *
     * See more about this in: https://netty.io/wiki/new-and-noteworthy-in-4.0.html#wiki-h4-19
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("Channel [{}] is active.", ctx.channel());

        // When notified that the channel is active, send a hello message.
        ctx.writeAndFlush(Unpooled.copiedBuffer("\nHello, there!",
                CharsetUtil.UTF_8));
    }

    /**
     * Invoked when the Channel of the ChannelHandlerContext is registered with its EventLoop.
     *
     * @param ctx
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.debug("Channel {} is registered.", ctx.channel());
        ctx.fireChannelRegistered();
    }

    /**
     * Invoked when the Channel of the ChannelHandlerContext was unregistered from its EventLoop.
     *
     * From https://stackoverflow.com/questions/35822638/understanding-channelregistered-in-netty-4-when-could-a-channel-be-re-registere
     *
     * "When you deregister a Channel it basically removed itself from the servicing Thread which in
     * the case of NIO also is the Selector itself. This means you will not get notified on any event
     * changes. Once you register again the Channel will be registered on a Selector again and you will
     * get noticed about events (like OP_READ, OP_WRITE etc)."
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("Channel {} is registered.", ctx.channel());
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        // Prints the received message
        log.debug("Client received: {}", in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.debug("Channel read for channel {} complete.", ctx.channel());
        ctx.fireChannelReadComplete();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        log.warn(cause.getMessage(), cause);
        ctx.close();
    }
}
