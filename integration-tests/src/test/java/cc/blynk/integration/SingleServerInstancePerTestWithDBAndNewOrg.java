package cc.blynk.integration;

import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.servers.application.AppAndHttpsServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.SHA256Util;
import cc.blynk.utils.properties.ServerProperties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Collections;

import static cc.blynk.integration.TestUtil.createDefaultHolder;
import static cc.blynk.server.core.model.web.Organization.SUPER_ORG_PARENT_ID;
import static org.junit.Assert.assertNotNull;

/**
 * use when you need only 1 server instance per test class and not per test method
 */
public abstract class SingleServerInstancePerTestWithDBAndNewOrg extends SingleServerInstancePerTestWithDB {

    protected int orgId;

    @BeforeClass
    public static void init() throws Exception {
        properties = new ServerProperties(Collections.emptyMap());
        properties.setProperty("data.folder", TestUtil.getDataFolder());
        holder = createDefaultHolder(properties, "db-test.properties");
        hardwareServer = new HardwareAndHttpAPIServer(holder).start();
        appServer = new AppAndHttpsServer(holder).start();
        assertNotNull(holder.dbManager.getConnection());
    }

    @AfterClass
    public static void shutdown() {
        appServer.close();
        hardwareServer.close();
        holder.close();
    }

    @After
    public void deleteOrg() {
        holder.organizationDao.delete(orgId);
    }

    @Before
    public void initClients() throws Exception {
        Organization newOrg = new Organization("Blynk Inc.", "Europe/Kiev",
                "/static/logo.png", true, SUPER_ORG_PARENT_ID,
                new Role(Role.SUPER_ADMIN_ROLE_ID, "Super Admin", 0b11111111111111111111),
                new Role(1, "Admin", 0b11111111111111111111),
                new Role(2, "Staff", 0b11111111111111111111),
                new Role(3, "User", 0b11111111111111111111));
        newOrg = holder.organizationDao.create(newOrg);
        orgId = newOrg.id;
        this.clientPair = initAppAndHardPair();

        String superAdmin = "super@blynk.cc";
        String pass = "1";
        String hash = SHA256Util.makeHash(pass, superAdmin);
        holder.userDao.add(superAdmin, hash, AppNameUtil.BLYNK, orgId, Role.SUPER_ADMIN_ROLE_ID);
    }

    public ClientPair initAppAndHardPair() throws Exception {
        return TestUtil.initAppAndHardPair("localhost",
                properties.getHttpsPort(), properties.getHttpPort(),
                getUserName(), "1", changeProfileTo(), properties, 10000);
    }

}
