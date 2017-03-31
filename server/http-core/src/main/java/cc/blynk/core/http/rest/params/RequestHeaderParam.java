package cc.blynk.core.http.rest.params;

import cc.blynk.core.http.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.12.15.
 */
public class RequestHeaderParam extends Param {

    public RequestHeaderParam(String name, Class<?> type) {
        super(name, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        String header = uriDecoder.headers.get(name);
        if (header == null) {
            return null;
        }
        return header;
    }

}
