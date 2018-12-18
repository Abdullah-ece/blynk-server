package cc.blynk.core.http.handlers;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.07.16.
 */
public class StaticFileEdsWith extends StaticFile {

    public StaticFileEdsWith(String pathForStatic, String path) {
        super(pathForStatic, path);
    }

    @Override
    public Path getPath(String uri) {
        return Paths.get(pathForStatic, uri);
    }

    @Override
    public boolean isStatic(String url) {
        return url.endsWith(path);
    }
}
