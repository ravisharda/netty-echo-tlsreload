package org.example.rs.netty.tlsreload.echo.server;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Channels {

    // See info about ChannelGroups here: https://netty.io/4.1/api/io/netty/channel/group/ChannelGroup.html
    private static final ChannelGroup channels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static synchronized ChannelGroup get() {
        return channels;
    }

    public static synchronized void add(Channel ch) {
        channels.add(ch);
    }
}
