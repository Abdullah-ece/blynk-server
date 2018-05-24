package cc.blynk.core.http.utils;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.07.16.
 */
public final class ContentTypeUtil {

    private ContentTypeUtil() {
    }

    public static String getContentType(String fileName) {
        if (fileName.endsWith(".ico")) {
            return "image/x-icon";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg")) {
            return "image/jpg";
        } else if (fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gz")) {
            return "application/x-gzip";
        }
        if (fileName.endsWith(".bin")) {
            return "application/octet-stream";
        }

        return "text/html";
    }

}
