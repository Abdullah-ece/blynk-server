package cc.blynk.core.http.rest.params;

import cc.blynk.core.http.rest.URIDecoder;
import cc.blynk.server.core.dao.SessionDao;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.12.15.
 */
public class ContextUserParam extends Param {

    public ContextUserParam(Class<?> type) {
        super(null, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        return ctx.channel().attr(SessionDao.userSessionAttributeKey).get().user;
    }

}
