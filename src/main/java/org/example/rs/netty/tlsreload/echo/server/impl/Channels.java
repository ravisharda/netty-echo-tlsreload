package org.example.rs.netty.tlsreload.echo.server.impl;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Channels {

    private static Lock sequential = new ReentrantLock();

    // See info about ChannelGroups here: https://netty.io/4.1/api/io/netty/channel/group/ChannelGroup.html
    private static ChannelGroup channels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE, true);

    public static void add(Channel ch) {
        sequential.lock();
        try {
            channels.add(ch);
            log.debug("Done adding channel [{}] to the group.", ch);
        } finally {
            sequential.unlock();
        }
    }

    public static void flushStopAndRefresh() {
        log.debug("Flushing, stopping and refreshing chanel group.");
        sequential.lock();
        ChannelGroup existingChannelGroup;
        try {
            existingChannelGroup = channels;
            log.debug("Current instance of Channel Group: {}", channels);
            channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE, true);
            log.debug("New instance of Channel Group: {}", channels);
        } finally {
            sequential.unlock();
        }

        // Flush and close all channels
        existingChannelGroup.flush();
        log.debug("Done triggering flush of channel group {}", existingChannelGroup);
        ChannelGroupFuture future = existingChannelGroup.close();
        try {
            future.await();
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
            // Ignore
        }

        log.debug("Done triggering close of channel group {}", existingChannelGroup);
        if (future.isCancelled()) {
            log.info("Connection cancelled by user.");
        } else if (!future.isSuccess()) {
            Throwable e = future.cause();
            log.warn(e.getMessage(), e);
            // Ignore
            // Intentionally not rethrowing the exception, so that the system continues to function.
        } else {
            log.info("Done closing the channel group, which implies that all channels in the group were "
                    + "successfully closed.");
        }
    }
}
