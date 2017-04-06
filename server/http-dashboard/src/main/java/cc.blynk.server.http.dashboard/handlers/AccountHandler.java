package cc.blynk.server.http.dashboard.handlers;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
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

    private final UserDao userDao;

    public AccountHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;
    }

    @GET
    @Path("")
    public Response getOwnProfile(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        return ok(httpSession.user);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response updateOwnProfile(User updatedUser) {
        User existingUser = userDao.getByName(updatedUser.email, updatedUser.appName);
        existingUser.update(updatedUser);
        return ok(existingUser);
    }

}
