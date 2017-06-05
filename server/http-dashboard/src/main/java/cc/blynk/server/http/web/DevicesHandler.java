package cc.blynk.server.http.web;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.core.http.Response.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/devices")
@ChannelHandler.Sharable
public class DevicesHandler extends BaseHttpHandler {

    private final UserDao userDao;

    public DevicesHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;
    }

    @GET
    @Path("")
    public Response get() {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            devices.add(new Device(i, "My Device " + i, "Particle Photon", "auth_123", ConnectionType.GSM));
        }

        return ok(devices);
    }


}
