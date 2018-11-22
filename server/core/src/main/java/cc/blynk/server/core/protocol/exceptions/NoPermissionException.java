package cc.blynk.server.core.protocol.exceptions;

import cc.blynk.server.core.model.permissions.PermissionsTable;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class NoPermissionException extends RuntimeException {

    public NoPermissionException(String email, int permission) {
        super(buildMessage(email, permission), null, true, false);
    }

    private static String buildMessage(String email, int permission) {
        return buildMessage(email, PermissionsTable.PERMISSION_NAMES.get(permission));
    }

    private static String buildMessage(String email, String permission) {
        return "User " + email
                + " has no permission for '"
                + permission
                + "' operation.";
    }

}
