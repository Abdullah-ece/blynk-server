package cc.blynk.server.launcher;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.ProvisionType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.enums.Theme;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.events.CriticalEvent;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.web.product.events.InformationEvent;
import cc.blynk.server.core.model.web.product.events.OfflineEvent;
import cc.blynk.server.core.model.web.product.events.OnlineEvent;
import cc.blynk.server.core.model.web.product.events.WarningEvent;
import cc.blynk.server.core.model.web.product.metafields.DeviceNameMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceOwnerMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.model.widgets.web.label.ColorSet;
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.application.MobileAndHttpsServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import cc.blynk.utils.JarUtil;
import cc.blynk.utils.LoggerUtil;
import cc.blynk.utils.SHA256Util;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.properties.GCMProperties;
import cc.blynk.utils.properties.MailProperties;
import cc.blynk.utils.properties.ServerProperties;
import cc.blynk.utils.properties.SmsProperties;
import cc.blynk.utils.properties.TwitterProperties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.net.BindException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import static cc.blynk.server.core.model.web.Organization.SUPER_ORG_PARENT_ID;
import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.RAW_DATA;

/**
 * Entry point for server launch.
 *
 * By default starts 4 servers on different ports:
 *
 * 1 server socket for HTTP API, Blynk hardware protocol, web sockets (8080 default)
 * 1 server socket for HTTPS API, Blynk app protocol, hardware secured blynkapp, web sockets (9443 default)
 *
 * In addition launcher start all related to business logic threads like saving user profiles thread, timers
 * processing thread, properties reload thread and shutdown hook tread.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/16/2015.
 */
public final class ServerLauncher {

    //required for QR generation
    static {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("io.netty.leakDetection.maxRecords", "20");
    }

    private ServerLauncher() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> cmdProperties = ArgumentsParser.parse(args);

        ServerProperties serverProperties = new ServerProperties(cmdProperties);

        LoggerUtil.configureLogging(serverProperties);

        //required for logging dynamic context
        System.setProperty("data.folder", serverProperties.getProperty("data.folder"));

        //required to avoid dependencies within model to server.properties
        setGlobalProperties(serverProperties);

        MailProperties mailProperties = new MailProperties(cmdProperties);
        SmsProperties smsProperties = new SmsProperties(cmdProperties);
        GCMProperties gcmProperties = new GCMProperties(cmdProperties);
        TwitterProperties twitterProperties = new TwitterProperties(cmdProperties);

        Security.addProvider(new BouncyCastleProvider());

        boolean restore = Boolean.parseBoolean(cmdProperties.get(ArgumentsParser.RESTORE_OPTION));
        start(serverProperties, mailProperties, smsProperties,
                gcmProperties, twitterProperties, restore);
    }

    private static void setGlobalProperties(ServerProperties serverProperties) {
        Map<String, String> globalProps = new HashMap<>(4);
        globalProps.put("terminal.strings.pool.size", "25");
        globalProps.put("initial.energy", "2000");
        globalProps.put("table.rows.pool.size", "100");
        globalProps.put("csv.export.data.points.max", "43200");

        for (var entry : globalProps.entrySet()) {
            String name = entry.getKey();
            String value = serverProperties.getProperty(name, entry.getValue());
            System.setProperty(name, value);
        }
    }

    private static void start(ServerProperties serverProperties, MailProperties mailProperties,
                              SmsProperties smsProperties, GCMProperties gcmProperties,
                              TwitterProperties twitterProperties,
                              boolean restore) {
        Holder holder = new Holder(serverProperties,
                mailProperties, smsProperties, gcmProperties, twitterProperties,
                restore);

        BaseServer[] servers = new BaseServer[] {
                new HardwareAndHttpAPIServer(holder),
                new MobileAndHttpsServer(holder)
        };

        if (startServers(servers)) {
            //Launching all background jobs.
            JobLauncher.start(holder, servers);

            System.out.println();
            System.out.println("Blynk Server " + JarUtil.getServerVersion() + " successfully started.");
            String path = new File(System.getProperty("logs.folder")).getAbsolutePath().replace("/./", "/");
            System.out.println("All server output is stored in folder '" + path + "' file.");

            holder.sslContextHolder.generateInitialCertificates(holder.props);

            createSuperUser(holder);
        }
    }

    private static void createSuperUser(Holder holder) {
        ServerProperties props = holder.props;
        String url = props.getAdminUrl(props.host);
        String email = props.getProperty("admin.email", "admin@blynk.cc");
        String pass = props.getProperty("admin.pass");

        if (!holder.userDao.isSuperAdminExists()) {
            if (pass == null || pass.isEmpty()) {
                System.out.println("Admin password not specified. Random password generated.");
                pass = StringUtils.randomPassword(24);
            }

            Organization superOrg;
            if (holder.organizationDao.organizations.size() == 0) {
                superOrg = createDefaultOrgData(holder);
            } else {
                superOrg = holder.organizationDao.getSuperOrgOrThrow();
            }

            System.out.println("Your Admin url is " + url);
            System.out.println("Your Admin login email is " + email);
            System.out.println("Your Admin password is " + pass);

            String hash = SHA256Util.makeHash(pass, email);
            User superAdmin = holder.userDao.add(email, hash, superOrg.id, Role.SUPER_ADMIN_ROLE_ID);

            DashBoard defaultSuperAdminDash = new DashBoard();
            defaultSuperAdminDash.id = 1;
            defaultSuperAdminDash.name = "Main Blynk Project 2";

            DashBoard childDash = new DashBoard();
            childDash.id = 123;
            childDash.name = "Child Blynk Project";
            childDash.parentId = defaultSuperAdminDash.id;
            childDash.isPreview = true;
            childDash.isActive = true;
            superAdmin.profile.dashBoards = new DashBoard[] {
                    defaultSuperAdminDash,
                    childDash
            };
            App app = new App("webdashprod", Theme.Blynk,
                    ProvisionType.DYNAMIC,
                    600084223, //BLYNK_GREEN
                    false, "WebDash app", null, new int[] {childDash.id});
            superAdmin.profile.apps = new App[] {
                app
            };
        }
    }

    private static Organization createDefaultOrgData(Holder holder) {
        System.out.println("Creating default organization structure.");
        Organization superOrg = new Organization("Blynk", "Europe/Kiev",
                "/static/logo.png", true, SUPER_ORG_PARENT_ID, true,
                new Role(Role.SUPER_ADMIN_ROLE_ID, "Super Admin", 0b11111111111111111111),
                new Role(1, "Admin", 0b11111111111111111111),
                new Role(2, "Staff", 0b11111111111111111111),
                new Role(3, "User", 0b11111111111111111111));
        superOrg = holder.organizationDao.create(superOrg);

        holder.organizationDao.create(
                new Organization("New Organization Inc. (orgId=2)",
                        "Europe/Kiev", "/static/logo.png", false, superOrg.id));

        Product product = new Product();
        product.boardType = "Particle Photon";
        product.connectionType = ConnectionType.WI_FI;
        product.description = "Default Product Template";
        product.name = "Test Product";
        product.metaFields = new MetaField[] {
                new DeviceNameMetaField(1, "Device Name",
                        new int[] {1}, false, false, true, null, "Default device"),
                new DeviceOwnerMetaField(2, "Device Owner", new int[] {}, false, false, true, null, null)
        };
        product.events = createDefaultEvents();

        WebLabel webLabel = new WebLabel();
        webLabel.label = "Test val";
        webLabel.id = 1;
        webLabel.x = 0;
        webLabel.y = 0;
        webLabel.height = 1;
        webLabel.width = 2;
        webLabel.colorsSet = new ColorSet[] {
                new ColorSet(0, 30, "23be1b", "fff", null),
                new ColorSet(31, 60, "eb7a21", "fff", null),
                new ColorSet(61, 100, "da1d4e", "fff", null)
        };
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 0, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        holder.organizationDao.createProduct(superOrg.id, product);

        for (int i = 0; i < 20; i++) {
            Device newDevice = new Device("My Device " + i, BoardType.ESP8266, "auth_123",
                    product.id, ConnectionType.WI_FI);
            newDevice.hardwareInfo = new HardwareInfo("1.0.0", "0.5.0", "Particle Photon", "atm33",
                    "WI-FI", "0.0.0", null, 1, -1);
            holder.organizationDao.assignToOrgAndAddDevice(superOrg, newDevice);
            holder.deviceDao.create(superOrg.id, "admin@blynk.cc", product, newDevice);
            for (EventType eventType : EventType.values()) {
                try {
                    Event event = product.findEventByType(eventType);
                    holder.reportingDBManager.insertEvent(newDevice.id, eventType,
                            System.currentTimeMillis(), event.hashCode(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String name = "user{i}@blynk.cc";
        String pass = "123";
        for (int i = 0; i < 10; i++) {
            String email = name.replace("{i}", "" + i);
            String hash = SHA256Util.makeHash(pass, email);
            holder.userDao.add(email, hash, superOrg.id, 2);
        }

        return superOrg;
    }

    private static Event[] createDefaultEvents() {
        OnlineEvent onlineEvent =
                new OnlineEvent(1, "Your device is online.", null, false, null, null, null);
        OfflineEvent offlineEvent =
                new OfflineEvent(2, "Your device is offline.", null, false, null, null, null, 1000);
        InformationEvent infoEvent =
                new InformationEvent(3, "Door is opened", "Kitchen door is opened.",
                        false, "door_opened", null, null, null);
        WarningEvent warningEvent =
                new WarningEvent(4, "Temperature is high!", "Room temp is high",
                        false, "temp_is_high", null, null, null);
        CriticalEvent criticalEvent =
                new CriticalEvent(5, "Temperature is super high!", "Room temp is super high",
                        false, "temp_is_super_high", null, null, null);

        return new Event[] {
                onlineEvent,
                offlineEvent,
                infoEvent,
                warningEvent,
                criticalEvent
        };
    }

    private static boolean startServers(BaseServer[] servers) {
        //start servers
        try {
            for (BaseServer server : servers) {
                server.start();
            }
            return true;
        } catch (BindException bindException) {
            System.out.println("Server ports are busy. Most probably server already launched. See "
                    + new File(System.getProperty("logs.folder")).getAbsolutePath() + " for more info.");
        } catch (Exception e) {
            System.out.println("Error starting Blynk server. Stopping.");
        }

        return false;
    }

}
