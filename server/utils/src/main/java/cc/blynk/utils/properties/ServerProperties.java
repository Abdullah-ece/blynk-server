package cc.blynk.utils.properties;

import cc.blynk.utils.AppNameUtil;
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

    public final boolean isUnpacked;

    public ServerProperties(Map<String, String> cmdProperties) {
        super(cmdProperties, SERVER_PROPERTIES_FILENAME);
        this.isUnpacked = JarUtil.unpackStaticFiles(jarPath, STATIC_FILES_FOLDER);
    }

    public ServerProperties(String propertiesFileName) {
        super(propertiesFileName);
        this.isUnpacked = JarUtil.unpackStaticFiles(jarPath, STATIC_FILES_FOLDER);
    }

    public String getProductName() {
        return getProperty("product.name", AppNameUtil.BLYNK);
    }

    public String getHttpsPortAsString() {
        return force80Port() ? "443" : getProperty("https.port");
    }

    public String getHttpPortAsString() {
        return force80Port() ? "80" : getProperty("http.port");
    }

    public boolean force80Port() {
        return getBoolProperty("force.port.80.for.csv");
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
