package cc.blynk.server.core.model.serialization;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DashboardSettings;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.Views;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.auth.FacebookTokenResponse;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.response.ErrorMessage;
import cc.blynk.server.core.model.web.response.OkMessage;
import cc.blynk.server.core.model.storage.SinglePinStorageValue;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.core.model.widgets.notifications.Twitter;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandBodyException;
import cc.blynk.server.core.stats.model.Stat;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.zip.DeflaterOutputStream;

import static cc.blynk.utils.StringUtils.BODY_SEPARATOR_STRING;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 15:31
 */
public final class JsonParser {

    private JsonParser() {
    }

    //it is threadsafe
    public static final ObjectMapper MAPPER = init();

    private static final Logger log = LogManager.getLogger(JsonParser.class);
    private static final ObjectReader userReader = MAPPER.readerFor(User.class);
    private static final ObjectReader organizationReader = MAPPER.readerFor(Organization.class);
    private static final ObjectReader profileReader = MAPPER.readerFor(Profile.class);
    private static final ObjectReader dashboardReader = MAPPER.readerFor(DashBoard.class);
    private static final ObjectReader dashboardSettingsReader = MAPPER.readerFor(DashboardSettings.class);
    private static final ObjectReader widgetReader = MAPPER.readerFor(Widget.class);
    private static final ObjectReader tileTemplateReader = MAPPER.readerFor(TileTemplate.class);
    private static final ObjectReader appReader = MAPPER.readerFor(App.class);
    private static final ObjectReader deviceReader = MAPPER.readerFor(Device.class);
    private static final ObjectReader tagReader = MAPPER.readerFor(Tag.class);
    private static final ObjectReader facebookTokenReader = MAPPER.readerFor(FacebookTokenResponse.class);
    private static final ObjectReader productReader = MAPPER.readerFor(Product.class);

    private static final ObjectWriter organizationWriter = MAPPER.writerFor(Organization.class);
    private static final ObjectWriter errorMessageWriter = MAPPER.writerFor(ErrorMessage.class);
    private static final ObjectWriter okMessageWriter = MAPPER.writerFor(OkMessage.class);
    private static final ObjectWriter userWriter = MAPPER.writerFor(User.class);
    private static final ObjectWriter userWebWriter = init()
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .writerWithView(Views.WebUser.class);
    private static final ObjectWriter profileWriter = MAPPER.writerFor(Profile.class);
    private static final ObjectWriter dashboardWriter = MAPPER.writerFor(DashBoard.class);
    private static final ObjectWriter deviceWriter = MAPPER.writerFor(Device.class);
    private static final ObjectWriter productWriter = MAPPER.writerFor(Product.class);
    private static final ObjectWriter appWriter = MAPPER.writerFor(App.class);

    public static final ObjectWriter restrictiveDashWriter = init()
            .addMixIn(Twitter.class, TwitterIgnoreMixIn.class)
            .addMixIn(Notification.class, NotificationIgnoreMixIn.class)
            .addMixIn(Device.class, DeviceIgnoreMixIn.class)
            .addMixIn(DashBoard.class, DashboardMixIn.class)
            .writerFor(DashBoard.class);

    private static final ObjectWriter restrictiveDashWriterForHttp = init()
            .addMixIn(Twitter.class, TwitterIgnoreMixIn.class)
            //.addMixIn(Notification.class, NotificationIgnoreMixIn.class)
            .addMixIn(Device.class, DeviceIgnoreMixIn.class)
            .addMixIn(DashBoard.class, DashboardMixIn.class)
            .writerFor(DashBoard.class);

    private static final ObjectWriter restrictiveProfileWriter = init()
            .addMixIn(Twitter.class, TwitterIgnoreMixIn.class)
            .addMixIn(Notification.class, NotificationIgnoreMixIn.class)
            .addMixIn(Device.class, DeviceIgnoreMixIn.class)
            .addMixIn(DashBoard.class, DashboardMixIn.class)
            .writerFor(Profile.class);

    private static final ObjectWriter restrictiveWidgetWriter = init()
            .addMixIn(Twitter.class, TwitterIgnoreMixIn.class)
            .addMixIn(Notification.class, NotificationIgnoreMixIn.class)
            .writerFor(Widget.class);

    private static final ObjectWriter statWriter = init().writerWithDefaultPrettyPrinter().forType(Stat.class);

    public static ObjectMapper init() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static String toJson(ErrorMessage errorMessage) {
        return toJson(errorMessageWriter, errorMessage);
    }

    public static String toJson(OkMessage okMessage) {
        return toJson(okMessageWriter, okMessage);
    }

    public static String toJson(Organization organization) {
        return toJson(organizationWriter, organization);
    }

    public static String toJson(User user) {
        return toJson(userWriter, user);
    }

    public static String toJsonWeb(User user) {
        return toJson(userWebWriter, user);
    }

    public static String toJsonWeb(List<User> users) {
        return toJson(userWebWriter, users);
    }

    public static String toJson(Profile profile) {
        return toJson(profileWriter, profile);
    }

    public static String toJson(DashBoard dashBoard) {
        return toJson(dashboardWriter, dashBoard);
    }

    public static byte[] gzipDash(DashBoard dash) {
        return writeJsonAsCompressedBytes(dashboardWriter, dash);
    }

    public static byte[] gzipDashRestrictive(DashBoard dash) {
        return writeJsonAsCompressedBytes(restrictiveDashWriter, dash);
    }

    public static byte[] gzipProfileRestrictive(Profile profile) {
        return writeJsonAsCompressedBytes(restrictiveProfileWriter, profile);
    }

    public static byte[] gzipProfile(Profile profile) {
        return writeJsonAsCompressedBytes(profileWriter, profile);
    }

    private static byte[] writeJsonAsCompressedBytes(ObjectWriter objectWriter, Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStream out = new DeflaterOutputStream(baos)) {
            objectWriter.writeValue(out, o);
        } catch (Exception e) {
            log.error("Error compressing data.", e);
            return null;
        }
        return baos.toByteArray();
    }

    public static String toJsonRestrictiveDashboard(DashBoard dashBoard) {
        return toJson(restrictiveDashWriter, dashBoard);
    }

    public static String toJsonRestrictiveDashboardForHTTP(DashBoard dashBoard) {
        return toJson(restrictiveDashWriterForHttp, dashBoard);
    }

    public static String toJson(Device device) {
        return toJson(deviceWriter, device);
    }

    public static String toJson(App app) {
        return toJson(appWriter, app);
    }

    public static String toJson(Stat stat) {
        return toJson(statWriter, stat);
    }

    public static String toJson(Product product) {
        return toJson(productWriter, product);
    }

    public static void writeUser(File file, User user) throws IOException {
        userWriter.writeValue(file, user);
    }

    public static void writeOrg(File file, Organization org) throws IOException {
        organizationWriter.writeValue(file, org);
    }

    private static String toJson(ObjectWriter writer, List<User> users) {
        try {
            return writer.writeValueAsString(users);
        } catch (Exception e) {
            log.error("Error jsoning object.", e);
        }
        return "{}";
    }

    private static String toJson(ObjectWriter writer, Object o) {
        try {
            return writer.writeValueAsString(o);
        } catch (Exception e) {
            log.error("Error jsoning object.", e);
        }
        return "{}";
    }

    public static String toJson(Widget widget) {
        try {
            return restrictiveWidgetWriter.writeValueAsString(widget);
        } catch (Exception e) {
            log.error("Error jsoning widget.", e);
        }
        return null;
    }

    public static String toJson(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            log.error("Error jsoning object.", e);
        }
        return null;
    }

    public static <T> T readAny(String val, Class<T> c) {
        try {
            return MAPPER.readValue(val, c);
        } catch (Exception e) {
            log.error("Error reading json object.", e);
        }
        return null;
    }

    public static User parseUserFromFile(Path path) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            return userReader.readValue(is);
        }
    }

    public static Organization parseOrganization(File orgFile) throws IOException {
        return organizationReader.readValue(orgFile);
    }

    public static User parseUserFromFile(File userFile) throws IOException {
        return userReader.readValue(userFile);
    }

    public static User parseUserFromString(String userString) throws IOException {
        return userReader.readValue(userString);
    }

    public static Profile parseProfileFromString(String profileString) throws IOException {
        return profileReader.readValue(profileString);
    }

    public static FacebookTokenResponse parseFacebookTokenResponse(String response) throws IOException {
        return facebookTokenReader.readValue(response);
    }

    public static Product parseProduct(String product) throws IOException {
        return productReader.readValue(product);
    }

    public static Organization parseOrganization(String org) throws IOException {
        return organizationReader.readValue(org);
    }

    public static DashboardSettings parseDashboardSettings(String reader, int msgId) {
        try {
            return dashboardSettingsReader.readValue(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalCommandBodyException("Error parsing dashboard settings.", msgId);
        }
    }

    public static DashBoard parseDashboard(String reader, int msgId) {
        try {
            return dashboardReader.readValue(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalCommandBodyException("Error parsing dashboard.", msgId);
        }
    }

    public static TileTemplate parseTileTemplate(String reader, int msgId) {
        try {
            return tileTemplateReader.readValue(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalCommandBodyException("Error parsing tile template.", msgId);
        }
    }

    public static Widget parseWidget(String reader) throws IOException {
        return widgetReader.readValue(reader);
    }

    public static Widget parseWidget(String reader, int msgId) {
        try {
            return widgetReader.readValue(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalCommandBodyException("Error parsing widget.", msgId);
        }
    }

    public static App parseApp(String reader, int msgId) {
        try {
            return appReader.readValue(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalCommandBodyException("Error parsing app.", msgId);
        }
    }

    public static Device parseDevice(String reader, int msgId) {
        try {
            return deviceReader.readValue(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalCommandBodyException("Error parsing device.", msgId);
        }
    }

    public static Tag parseTag(String reader, int msgId) {
        try {
            return tagReader.readValue(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalCommandBodyException("Error parsing tag.", msgId);
        }
    }

    public static String valueToJsonAsString(Collection<String> values) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (String value : values) {
            sj.add(makeJsonStringValue(value));
        }
        return sj.toString();
    }

    public static String valueToJsonAsString(SinglePinStorageValue singlePinStorageValue) {
        Collection<String> singleValueList = singlePinStorageValue.values();
        if (singleValueList.size() == 0) {
            return "[]";
        }
        String[] values = singleValueList.iterator().next().split(BODY_SEPARATOR_STRING);
        return valueToJsonAsString(values);
    }

    private static String valueToJsonAsString(String[] values) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (String value : values) {
            sj.add(makeJsonStringValue(value));
        }
        return sj.toString();
    }

    public static String valueToJsonAsString(String value) {
        return "[\"" + value + "\"]";
    }

    private static String makeJsonStringValue(String value) {
        return "\"" + value + "\"";
    }

}
