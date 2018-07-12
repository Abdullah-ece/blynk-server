package cc.blynk.integration;

import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.servers.application.AppAndHttpsServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import cc.blynk.utils.properties.ServerProperties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Collections;

import static cc.blynk.integration.TestUtil.createDefaultHolder;
import static org.junit.Assert.assertNotNull;

/**
 * use when you need only 1 server instance per test class and not per test method
 */
public abstract class SingleServerInstancePerTestWithDBAndNewOrg extends SingleServerInstancePerTestWithDB {

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
        holder.organizationDao.delete(OrganizationDao.DEFAULT_ORGANIZATION_ID);
    }

    @Before
    public void createOrg() {
        Organization newOrg = new Organization("Blynk Inc.", "Europe/Kiev", "/static/logo.png", true);
        holder.organizationDao.createWithPresetId(OrganizationDao.DEFAULT_ORGANIZATION_ID, newOrg);
    }

    public ClientPair initAppAndHardPair() throws Exception {
        return TestUtil.initAppAndHardPair("localhost",
                properties.getHttpsPort(), properties.getHttpPort(),
                getUserName(), "1", changeProfileTo(), properties, 10000);
    }

}
