package cc.blynk.server.core.model.permissions;

public class PermissionsTable {

    public static final int PRODUCT_VIEW =   0b1;
    public static final int PRODUCT_EDIT =   0b10;
    public static final int PRODUCT_DELETE = 0b100;

    public static final int DEVICES_VIEW =   0b1_000;
    public static final int DEVICES_EDIT =   0b10_000;
    public static final int DEVICES_DELETE = 0b100_000;

    public static final int STATS_VIEW   =   0b1_000_000;
    public static final int STATS_EDIT   =   0b10_000_000;
    public static final int STATS_DELETE =   0b100_000_000;

    public static final int SUB_ORG_VIEW =   0b1_000_000_000;
    public static final int SUB_ORG_EDIT =   0b10_000_000_000;
    public static final int SUB_ORG_DELETE = 0b100_000_000_000;

    public static final int ORG_VIEW =       0b1_000_000_000_000;
    public static final int ORG_EDIT =       0b10_000_000_000_000;
    public static final int ORG_DELETE =     0b100_000_000_000_000;

    public static boolean canDeleteDevice(int permissions) {
        return (permissions & DEVICES_DELETE) == DEVICES_DELETE;
    }

}
