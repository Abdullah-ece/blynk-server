package cc.blynk.core.http.handlers;

import cc.blynk.utils.http.MediaType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.12.18.
 */
public class StaticAppleFile extends StaticFile {

    public StaticAppleFile(String pathForStatic) {
        super(pathForStatic, "/.well-known/apple-app-site-association");
    }

    @Override
    public String getContentType(String fileName) {
        return MediaType.APPLICATION_JSON;
    }

    @Override
    public boolean isStatic(String url) {
        return url.equals(path);
    }

    @Override
    //we do full url match, so no need for security check here
    public boolean isNotSecure(String uri) {
        return false;
    }
}
