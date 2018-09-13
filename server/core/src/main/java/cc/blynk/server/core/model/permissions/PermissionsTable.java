package cc.blynk.server.core.model.permissions;

public final class PermissionsTable {

    public static final long PRODUCT_VIEW =   0b1;
    public static final long PRODUCT_EDIT =   0b10;
    public static final long PRODUCT_DELETE = 0b100;

    public static final long DEVICE_VIEW =    0b1_000;
    public static final long DEVICE_EDIT =    0b10_000;
    public static final long DEVICE_DELETE =  0b100_000;

    public static final long STATS_VIEW   =   0b1_000_000;
    public static final long STATS_EDIT   =   0b10_000_000;
    public static final long STATS_DELETE =   0b100_000_000;

    public static final long SUB_ORG_VIEW =   0b1_000_000_000;
    public static final long SUB_ORG_EDIT =   0b10_000_000_000;
    public static final long SUB_ORG_DELETE = 0b100_000_000_000;

    public static final long ORG_VIEW =       0b1_000_000_000_000;
    public static final long ORG_EDIT =       0b10_000_000_000_000;
    public static final long ORG_DELETE =     0b100_000_000_000_000;

    private PermissionsTable() {
    }

}
