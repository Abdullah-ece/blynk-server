package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 30.03.17.
 */
public class HttpSession {

    public final User user;

    public final String token;

    public HttpSession(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
