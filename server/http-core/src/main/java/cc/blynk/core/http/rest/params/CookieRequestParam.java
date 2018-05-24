package cc.blynk.core.http.rest.params;

import cc.blynk.core.http.rest.URIDecoder;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.session.CookieUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.12.15.
 */
public class CookieRequestParam extends Param {

    public CookieRequestParam(String name) {
        super(name, Cookie.class);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        return CookieUtil.findCookieByName(uriDecoder.httpRequest, SessionDao.SESSION_COOKIE);
    }

}
