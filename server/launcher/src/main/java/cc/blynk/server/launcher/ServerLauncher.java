package cc.blynk.server.launcher;

import cc.blynk.server.Holder;
import cc.blynk.server.application.AppServer;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.model.AppName;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.*;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.hardware.HardwareSSLServer;
import cc.blynk.server.hardware.HardwareServer;
import cc.blynk.server.hardware.MQTTHardwareServer;
import cc.blynk.server.http.HttpAPIServer;
import cc.blynk.server.http.HttpsAPIServer;
import cc.blynk.utils.JarUtil;
import cc.blynk.utils.LoggerUtil;
import cc.blynk.utils.SHA256Util;
import cc.blynk.utils.ServerProperties;
import cc.blynk.utils.properties.GCMProperties;
import cc.blynk.utils.properties.MailProperties;
import cc.blynk.utils.properties.SmsProperties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.net.BindException;
import java.security.Security;
import java.util.Map;

/**
 * Entry point for server launch.
 *
 * By default starts 7 servers on different ports:
 *
 * 1 server socket for SSL/TLS Hardware (8441 default)
 * 1 server socket for plain tcp/ip Hardware (8442 default)
 * 1 server socket for SSL/TLS Applications (8443 default)
 * 1 server socket for HTTP API (8080 default)
 * 1 server socket for HTTPS API (9443 default)
 * 1 server socket for MQTT (8440 default)
 * 1 server socket for Administration UI (7443 default)
 *
 * In addition launcher start all related to business logic threads like saving user profiles thread, timers
 * processing thread, properties reload thread and shutdown hook tread.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/16/2015.
 */
public class ServerLauncher {

    //required for QR generation
    static {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("io.netty.leakDetection.maxRecords", "20");
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> cmdProperties = ArgumentsParser.parse(args);

        ServerProperties serverProperties = new ServerProperties(cmdProperties);

        LoggerUtil.configureLogging(serverProperties);

        //required for logging dynamic context
        System.setProperty("data.folder", serverProperties.getProperty("data.folder"));
        //required to avoid dependencies within model to server.properties
        System.setProperty("terminal.strings.pool.size", serverProperties.getProperty("terminal.strings.pool.size", "25"));
        System.setProperty("initial.energy", serverProperties.getProperty("initial.energy", "2000"));

        boolean isUnpacked = JarUtil.unpackStaticFiles("static/");

        ServerProperties mailProperties = new MailProperties(cmdProperties);
        ServerProperties smsProperties = new SmsProperties(cmdProperties);
        ServerProperties gcmProperties = new GCMProperties(cmdProperties);

        Security.addProvider(new BouncyCastleProvider());

        boolean restore = Boolean.parseBoolean(cmdProperties.get(ArgumentsParser.RESTORE_OPTION));
        start(serverProperties, mailProperties, smsProperties, gcmProperties, isUnpacked, restore);
    }

    private static void start(ServerProperties serverProperties, ServerProperties mailProperties,
                              ServerProperties smsProperties, ServerProperties gcmProperties,
                              boolean isUnpacked, boolean restore) {
        final Holder holder = new Holder(serverProperties, mailProperties, smsProperties, gcmProperties, restore);

        final BaseServer[] servers = new BaseServer[] {
                new HardwareServer(holder),
                new HardwareSSLServer(holder),
                new AppServer(holder),
                new HttpAPIServer(holder, isUnpacked),
                new HttpsAPIServer(holder, isUnpacked),
                new MQTTHardwareServer(holder)
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
        String email = holder.props.getProperty("admin.email", "admin@blynk.cc");
        String pass = holder.props.getProperty("admin.pass", "admin");

        if (!holder.userDao.isSuperAdminExists()) {
            System.out.println("Your Admin login email is " + email);
            System.out.println("Your Admin password is " + pass);

            String hash = SHA256Util.makeHash(pass, email);
            holder.userDao.add(email, hash, AppName.BLYNK, Role.SUPER_ADMIN);
            Organization mainOrg = holder.organizationDao.create(new Organization("Blynk Inc.", "Europe/Kiev", "/static/logo.png"));
            holder.organizationDao.create(new Organization("New Organization Inc. (id=2)", "Europe/Kiev", "/static/logo.png"));
            Product product = new Product();
            product.boardType = "Particle Photon";
            product.connectionType = ConnectionType.WI_FI;
            product.description = "Default Product Template";
            product.name = "Test Product";
            product.metaFields = new MetaField[] {
                    new TextMetaField("Device Name", Role.ADMIN, "Default device")
            };
            product.events = createDefaultEvents();

            holder.organizationDao.createProduct(mainOrg.id, product);

            User user = holder.userDao.getByName(email, AppName.BLYNK);
            user.profile.dashBoards = new DashBoard[] {
                    new DashBoard()
            };

            for (int i = 0; i < 20; i++) {
                Device device = new Device("My Device " + i, "Particle Photon", "auth_123", product.id, ConnectionType.WI_FI);
                holder.deviceDao.create(mainOrg.id, device);
                for (EventType eventType : EventType.values()) {
                    try {
                        Event event = product.findEventByType(eventType);
                        holder.dbManager.insertEvent(device.id, eventType, System.currentTimeMillis(), event.hashCode(), null);
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
            holder.userDao.add(email, hash, AppName.BLYNK, Role.STAFF);
        }
    }

    private static Event[] createDefaultEvents() {
        OnlineEvent onlineEvent = new OnlineEvent();
        onlineEvent.name = "Your device is online.";

        OfflineEvent offlineEvent = new OfflineEvent();
        offlineEvent.name = "Your device is offline.";
        offlineEvent.ignorePeriod = 1000;

        InformationEvent infoEvent = new InformationEvent();
        infoEvent.name = "Door is opened";
        infoEvent.eventCode = "door_opened";
        infoEvent.description = "Kitchen door is opened.";

        WarningEvent warningEvent = new WarningEvent();
        warningEvent.name = "Temperature is high!";
        warningEvent.eventCode = "temp_is_high";
        warningEvent.description = "Room temp is high";

        CriticalEvent criticalEvent = new CriticalEvent();
        criticalEvent.name = "Temperature is super high!";
        criticalEvent.eventCode = "temp_is_super_high";
        criticalEvent.description = "Room temp is super high";

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
            System.out.println("Server ports are busy. Most probably server already launched. See " +
                    new File(System.getProperty("logs.folder")).getAbsolutePath() + " for more info.");
        } catch (Exception e) {
            System.out.println("Error starting Blynk server. Stopping.");
        }

        return false;
    }

}
