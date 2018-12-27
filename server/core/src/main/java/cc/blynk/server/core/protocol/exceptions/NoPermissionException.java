package cc.blynk.server.core.protocol.exceptions;

import cc.blynk.server.core.model.permissions.PermissionsTable;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class NoPermissionException extends RuntimeException {

    public NoPermissionException(String msg) {
        super(msg, null, true, false);
    }

    public NoPermissionException(String email, int permission) {
        this(buildMessage(email, permission));
    }

    private static String buildMessage(String email, int permission) {
        String text = PermissionsTable.PERMISSION1_NAMES.get(permission);
        return buildMessage(email, text);
    }

    private static String buildMessage(String email, String permission) {
        return "User " + email
                + " has no permission for '"
                + permission
                + "' operation.";
    }

}
