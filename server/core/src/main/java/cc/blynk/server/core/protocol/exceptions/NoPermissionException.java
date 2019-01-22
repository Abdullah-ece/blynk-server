package cc.blynk.server.core.protocol.exceptions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public final class NoPermissionException extends RuntimeException {

    public NoPermissionException(String msg) {
        super(msg, null, true, false);
    }

    public NoPermissionException(String email, String text) {
        this(buildMessage(email, text));
    }

    private static String buildMessage(String email, String permission) {
        return "User " + email
                + " has no permission for '"
                + permission
                + "' operation.";
    }

}
