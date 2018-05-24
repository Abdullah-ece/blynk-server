package cc.blynk.server.core.session;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.Set;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.03.17.
 */
public final class CookieUtil {

    private CookieUtil() {
    }

    public static Cookie findCookieByName(HttpRequest request, String cookieName) {
        String cookieString = request.headers().get(HttpHeaderNames.COOKIE);

        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    if (isNameMatch(cookie, cookieName)) {
                        return cookie;
                    }
                }
            }
        }

        return null;
    }

    public static boolean isNameMatch(Cookie cookie, String cookieName) {
        return cookie.name().equals(cookieName);
    }
}
