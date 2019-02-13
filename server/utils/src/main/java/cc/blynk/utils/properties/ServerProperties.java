package cc.blynk.utils.properties;

import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.IPUtils;
import cc.blynk.utils.JarUtil;

import java.nio.file.Paths;
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

    //this is reusable properties so we want to fetch them only once
    public final boolean isUnpacked;
    public final String vendorEmail;
    public final String productName;
    public final String region;
    public final String host;
    public final String rootPath;
    public final String httpServerUrl;
    public final String httpsServerUrl;
    public final String httpPort;
    public final String httpsPort;

    public ServerProperties(Map<String, String> cmdProperties, String serverConfig) {
        super(cmdProperties, serverConfig);
        this.isUnpacked = JarUtil.unpackStaticFiles(jarPath, STATIC_FILES_FOLDER);
        this.vendorEmail = getVendorEmail();
        this.productName = getProductName();
        this.region = getRegion();
        this.host = getServerHost();
        this.rootPath = getRootPath();
        this.httpPort = getHttpPortAsString(); //blank if default port is forced
        this.httpsPort = getHttpsPortAsString(); //blank if default port is forced
        this.httpServerUrl = getHttpServerUrl();
        this.httpsServerUrl = getHttpsServerUrl();
    }

    public ServerProperties(Map<String, String> cmdProperties) {
        this(cmdProperties, SERVER_PROPERTIES_FILENAME);
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

    public String getDataFolder() {
        return getProperty("data.folder");
    }

    public String getReportingFolder() {
        return Paths.get(getDataFolder(), "data").toString();
    }

    public int getHttpPort() {
        return getIntProperty("http.port");
    }

    public int getHttpsPort() {
        return getIntProperty("https.port");
    }

    private String getServerHost() {
        String host = getHostProperty();
        if (host == null || host.isEmpty()) {
            var netInterface = getProperty("net.interface", "eth");
            return IPUtils.resolveHostIP(netInterface);
        } else {
            return host;
        }
    }

    public int getGroupValueUpdaterPeriod() {
        return getIntProperty("group.value.updater.period", 5);
    }

    public String getRestoreHost() {
        return getProperty("restore.host", getHostProperty());
    }

    private String getHostProperty() {
        return getProperty("server.host", "localhost");
    }

    private boolean forceRegularPort() {
        return getBoolProperty("force.regular.port");
    }

    private String getHttpsPortAsString() {
        return forceRegularPort() ? "" : getProperty("https.port");
    }

    public String getHttpPortAsString() {
        return forceRegularPort() ? "" : getProperty("http.port");
    }

    public boolean getAllowStoreIp() {
        return getBoolProperty("allow.store.ip");
    }

    public String getDeviceUrl() {
        return httpsServerUrl + rootPath + "/devices/";
    }

    public String getInviteUrl() {
        return httpsServerUrl + rootPath + "/invite?token=";
    }

    public String getUploadPath() {
        return "/api/upload";
    }

    private String getHttpsServerUrl() {
        return makeServerUrl("https://", httpsPort);
    }

    private String getHttpServerUrl() {
        return makeServerUrl("http://", httpPort);
    }

    private String makeServerUrl(String protocol, String port) {
        return protocol + host + (port.isEmpty() ? "" : (":" + port));
    }

    public String getServerUrl(boolean isSecure) {
        if (isSecure) {
            return httpsServerUrl;
        }
        return httpServerUrl;
    }

    public String getResetPasswordUrl() {
        return this.httpsServerUrl + rootPath + "/resetPass";
    }
}
