package cc.blynk.core.http.handlers;

import cc.blynk.core.http.utils.ContentTypeUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.07.16.
 */
public class StaticFile {

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    final String pathForStatic;
    public final String path;

    public StaticFile(String jarPath, String path) {
        this.pathForStatic = jarPath;
        this.path = path;
    }

    public boolean isStatic(String url) {
        return url.startsWith(path);
    }

    public Path getPath(String uri) {
        return Paths.get(pathForStatic, uri);
    }

    public String getContentType(String fileName) {
        return ContentTypeUtil.getContentType(fileName);
    }

    public boolean isNotSecure(String uri) {
        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return true;
        }

        return uri.contains(File.separator + '.')
                || uri.contains('.' + File.separator)
                || uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.'
                || INSECURE_URI.matcher(uri).matches();

    }
}
