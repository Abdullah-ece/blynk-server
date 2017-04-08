package cc.blynk.utils;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.03.17.
 */
public class UrlMapper {

    protected final String from;
    protected final String to;

    public UrlMapper(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public boolean isMatch(String uri) {
        return from.equals(uri);
    }

    public String mapTo(String uri) {
        return to;
    }
}
