package cc.blynk.server.launcher;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
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
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.application.AppAndHttpsServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import cc.blynk.server.servers.hardware.HardwareSSLServer;
import cc.blynk.server.servers.hardware.MQTTHardwareServer;
import cc.blynk.utils.JarUtil;
import cc.blynk.utils.LoggerUtil;
import cc.blynk.utils.SHA256Util;
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

import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.RAW_DATA;
import static cc.blynk.utils.AppNameUtil.BLYNK;

/**
 * Entry point for server launch.
 *
 * By default starts 4 servers on different ports:
 *
 * 1 server socket for SSL/TLS Hardware (8441 default)
 * 1 server socket for HTTP API, Blynk hardware protocol, web sockets (8080 default)
 * 1 server socket for HTTPS API, Blynk app protocol, web sockets (9443 default)
 * 1 server socket for MQTT (8440 default)
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
        var cmdProperties = ArgumentsParser.parse(args);

        var serverProperties = new ServerProperties(cmdProperties);

        LoggerUtil.configureLogging(serverProperties);

        //required for logging dynamic context
        System.setProperty("data.folder", serverProperties.getProperty("data.folder"));

        //required to avoid dependencies within model to server.properties
        setGlobalProperties(serverProperties);

        var mailProperties = new MailProperties(cmdProperties);
        var smsProperties = new SmsProperties(cmdProperties);
        var gcmProperties = new GCMProperties(cmdProperties);
        var twitterProperties = new TwitterProperties(cmdProperties);

        Security.addProvider(new BouncyCastleProvider());

        var restore = Boolean.parseBoolean(cmdProperties.get(ArgumentsParser.RESTORE_OPTION));
        start(serverProperties, mailProperties, smsProperties, gcmProperties, twitterProperties, restore);
    }

    private static void setGlobalProperties(ServerProperties serverProperties) {
        var globalProps = new HashMap<String, String>(4);
        globalProps.put("terminal.strings.pool.size", "25");
        globalProps.put("initial.energy", "2000");
        globalProps.put("table.rows.pool.size", "100");
        globalProps.put("csv.export.data.points.max", "43200");

        for (var entry : globalProps.entrySet()) {
            var name = entry.getKey();
            var value = serverProperties.getProperty(name, entry.getValue());
            System.setProperty(name, value);
        }
    }

    private static void start(ServerProperties serverProperties, MailProperties mailProperties,
                              SmsProperties smsProperties, GCMProperties gcmProperties,
                              TwitterProperties twitterProperties,
                              boolean restore) {
        var holder = new Holder(serverProperties,
                mailProperties, smsProperties, gcmProperties, twitterProperties,
                restore);

        var servers = new BaseServer[] {
                new HardwareSSLServer(holder),
                new HardwareAndHttpAPIServer(holder),
                new AppAndHttpsServer(holder),
                new MQTTHardwareServer(holder)
        };

        if (startServers(servers)) {
            //Launching all background jobs.
            JobLauncher.start(holder, servers);

            System.out.println();
            System.out.println("Blynk Server " + JarUtil.getServerVersion() + " successfully started.");
            var path = new File(System.getProperty("logs.folder")).getAbsolutePath().replace("/./", "/");
            System.out.println("All server output is stored in folder '" + path + "' file.");

            holder.sslContextHolder.generateInitialCertificates(holder.props);

            createSuperUser(holder);
        }
    }

    private static void createSuperUser(Holder holder) {
        var email = holder.props.getProperty("admin.email", "admin@blynk.cc");
        var pass = holder.props.getProperty("admin.pass", "admin");

        if (!holder.userDao.isSuperAdminExists()) {
            System.out.println("Your Admin login email is " + email);
            System.out.println("Your Admin password is " + pass);

            var hash = SHA256Util.makeHash(pass, email);
            holder.userDao.add(email, hash, BLYNK, Role.SUPER_ADMIN);
            Organization superOrg = new Organization("Blynk Inc.", "Europe/Kiev", "/static/logo.png", true);
            Organization mainOrg = holder.organizationDao.create(superOrg);
            mainOrg.isActive = true;
            holder.organizationDao.create(
                    new Organization("New Organization Inc. (orgId=2)",
                            "Europe/Kiev", "/static/logo.png", false, superOrg.id));
            Product product = new Product();
            product.boardType = "Particle Photon";
            product.connectionType = ConnectionType.WI_FI;
            product.description = "Default Product Template";
            product.name = "Test Product";
            product.metaFields = new MetaField[] {
                    new TextMetaField(1, "Device Name", Role.ADMIN, true, "Default device")
            };
            product.events = createDefaultEvents();

            WebLabel webLabel = new WebLabel();
            webLabel.label = "Test val";
            webLabel.id = 1;
            webLabel.x = 0;
            webLabel.y = 0;
            webLabel.height = 1;
            webLabel.width = 2;
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

            holder.organizationDao.createProduct(mainOrg.id, product);

            User user = holder.userDao.getByName(email, BLYNK);
            user.profile.dashBoards = new DashBoard[] {
                    new DashBoard()
            };

            for (int i = 0; i < 20; i++) {
                Device device = new Device("My Device " + i, "Particle Photon", "auth_123",
                        product.id, ConnectionType.WI_FI);
                device.hardwareInfo = new HardwareInfo("0.5.0", "Particle Photon", "atm33",
                        "WI-FI", "0.0.0", null, 1, -1);
                holder.deviceDao.create(mainOrg.id, device);
                for (EventType eventType : EventType.values()) {
                    try {
                        Event event = product.findEventByType(eventType);
                        holder.dbManager.insertEvent(device.id, eventType,
                                System.currentTimeMillis(), event.hashCode(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //todo
        //for local testing. will be removed in future.
        //always adding 20 users to initial organization
        String name = "user{i}@blynk.cc";
        pass = "123";
        for (int i = 0; i < 20; i++) {
            email = name.replace("{i}", "" + i);
            String hash = SHA256Util.makeHash(pass, email);
            holder.userDao.add(email, hash, BLYNK, Role.STAFF);
        }
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
