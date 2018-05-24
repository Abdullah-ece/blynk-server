package cc.blynk.utils;

import cc.blynk.core.http.handlers.url.UrlMapper;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.03.17.
 */
public class UrlStartWithMapper extends UrlMapper {

    public UrlStartWithMapper(String from, String to) {
        super(from, to);
    }

    public boolean isMatch(String uri) {
        return uri.startsWith(from);
    }

    public String mapTo(String uri) {
        if (uri.contains("?")) {
            String[] split = uri.split("\\?");
            return to + "?" + split[1];
        } else {
            return to;
        }
    }
}
