package cc.blynk.core.http.rest;

import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.03.17.
 */
public final class HandlerHolder {

    private static final Logger log = LogManager.getLogger(HandlerHolder.class);

    public final HandlerWrapper handler;

    public final Map<String, String> extractedParams;

    public HandlerHolder(HandlerWrapper handler, Map<String, String> extractedParams) {
        this.handler = handler;
        this.extractedParams = extractedParams;
    }

    public boolean hasAccess(ChannelHandlerContext ctx) {
        return true;
    }

}
