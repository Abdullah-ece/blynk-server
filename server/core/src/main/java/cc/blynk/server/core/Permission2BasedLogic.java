package cc.blynk.server.core;

import cc.blynk.server.core.model.permissions.PermissionsTable;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;

import java.util.Map;

public interface Permission2BasedLogic<T extends BaseUserStateHolder> extends PermissionBasedLogic {

    @Override
    default boolean hasPermission(Role role) {
        return role.hasPermission2(getPermission());
    }

    @Override
    default Map<Integer, String> getErrorMap() {
        return PermissionsTable.PERMISSION2_NAMES;
    }

}
