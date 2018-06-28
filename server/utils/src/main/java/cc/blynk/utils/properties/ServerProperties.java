package cc.blynk.utils.properties;

import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.IPUtils;
import cc.blynk.utils.JarUtil;

import java.util.Map;

/**
 * Java properties class wrapper.
 * Loads properties file from class path. After that loads properties
 * from dir where jar file is. On every stage properties override previous.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/12/2015.
 */
public class ServerProperties extends BaseProperties {

    public static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String STATIC_FILES_FOLDER = "static";
    public static final String PRODUCT_NAME = "{PRODUCT_NAME}";
    public static final String DEVICE_NAME = "{DEVICE_NAME}";

    private static final String STATIC_FILES_FOLDER = "static";

    //this is reusable properties so we want to fetch them only once
    public final boolean isUnpacked;
    public final String vendorEmail;
    public final String productName;
    public final String region;
    public final String host;

    public ServerProperties(Map<String, String> cmdProperties, String serverConfig) {
        super(cmdProperties, serverConfig);
        this.isUnpacked = JarUtil.unpackStaticFiles(jarPath, STATIC_FILES_FOLDER);
        this.vendorEmail = getVendorEmail();
        this.productName = getProductName();
        this.region = getRegion();
        this.host = getServerHost();
    }

    public ServerProperties(Map<String, String> cmdProperties) {
        this(cmdProperties, SERVER_PROPERTIES_FILENAME);
    }

    public boolean isLocalRegion() {
        return region.equals("local");
    }

    private String getProductName() {
        return getProperty("product.name", AppNameUtil.BLYNK);
    }

    private String getVendorEmail() {
        return getProperty("vendor.email");
    }

    private String getRegion() {
        return getProperty("region", "local");
    }

    private static final String DEFAULT_RESET_HOST = "blynk.cc";

    private String getServerHost() {
        String host = getHostProperty();
        if (host == null || host.isEmpty()) {
            var netInterface = getProperty("net.interface", "eth");
            return IPUtils.resolveHostIP(netInterface);
        } else {
            return host;
        }
    }

    public String getResetClickHost() {
        if (getHostProperty() == null) {
           return DEFAULT_RESET_HOST;
        }
        return host;
    }

    private String getHostProperty() {
        return getProperty("server.host");
    }

    public String getAdminUrl(String host) {
        String httpsPort = getHttpsPortOrBlankIfDefaultAsString();
        return "https://" + host + httpsPort + getAdminRootPath();
    }

    public boolean isDBEnabled() {
        return getBoolProperty("enable.db");
    }

    private String getHttpsPortOrBlankIfDefaultAsString() {
        if (force80Port()) {
            //means default port 443 is used, so no need to attach it
            return "";
        }
        String httpsPort = getProperty("https.port");
        if (httpsPort == null || httpsPort.equals("443")) {
            return "";
        }
        return ":" + httpsPort;
    }

    private boolean force80Port() {
        return getBoolProperty("force.port.80.for.csv");
    }

    public String getHttpsPortAsString() {
        return force80Port() ? "443" : getProperty("https.port");
    }

    public String getHttpPortAsString() {
        return force80Port() ? "80" : getProperty("http.port");
    }

    public boolean getAllowStoreIp() {
        return getBoolProperty("allow.store.ip");
    }

    public boolean renameOldReportingFiles() {
        return getBoolProperty("rename.old.reporting.files");
    }

    public boolean isRawDBEnabled() {
        return getBoolProperty("enable.raw.db.data.store");
    }

    public String getDeviceUrl() {
        return "https://" + getServerHost() + "/dashboard/devices/";
    }

    public String getAdminEmail() {
        return getProperty("admin.email", "admin@blynk.cc");
    }

    public String getHttpServerUrl() {
        String httpPort = getHttpPortAsString();
       return "http://" + getServerHost() + (httpPort.equals("80") ? "" : (":" + httpPort));
    }

}
