package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Context;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.SessionDao;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.core.http.Response.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/account")
@ChannelHandler.Sharable
public class AccountHandler extends BaseHttpHandler {

    public AccountHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
    }

    @GET
    @Path("/me")
    public Response getOwnProfile(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        return ok(httpSession.user);
    }

}
