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

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("Channel [{}] is active.", ctx.channel());

         // When notified that the channel is active, send a hello message.
        ctx.writeAndFlush(Unpooled.copiedBuffer("\nHello, there!",
                    CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        // log.debug("Client received: ", in.toString(CharsetUtil.UTF_8));
        // Prints the received message
        System.out.println(
                "Client received: " + in.toString(CharsetUtil.UTF_8));
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        log.warn(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("Unregistering the channel: {}.", ctx.channel());
        ctx.fireChannelUnregistered();
    }
}