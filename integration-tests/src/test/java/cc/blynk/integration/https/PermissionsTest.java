package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.api.http.dashboard.dto.OrganizationDTO;
import cc.blynk.server.api.http.dashboard.dto.ProductDTO;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.SHA256Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.webJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class PermissionsTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void userFromSubOrgCantSeeParentDevice() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization(
                "My Org ffff", "Some TimeZone", "/static/logo.png", false, orgId,
                new Role(1, "Admin", 0b11111111111111111111, 0));
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(orgId, organization);
        OrganizationDTO fromApiOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiOrg);
        assertEquals(orgId, fromApiOrg.parentId);
        assertEquals(organization.name, fromApiOrg.name);
        assertEquals(organization.tzName, fromApiOrg.tzName);
        assertNotNull(fromApiOrg.products);
        assertEquals(1, fromApiOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiOrg.products[0].id);

        String subOrgUser1 = "subOrgUser1@blynk.cc";
        String pass = "1";
        String hash = SHA256Util.makeHash(pass, subOrgUser1);
        holder.userDao.add(subOrgUser1.toLowerCase(), hash, fromApiOrg.id, 1);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(0, createdDevice.metaFields.length);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals("super@blynk.cc", createdDevice.activatedBy);

        AppWebSocketClient subUserClient = loggedDefaultClient(subOrgUser1, "1");

        //wrong org id here
        subUserClient.getDevice(fromApiOrg.id, createdDevice.id);
        subUserClient.verifyResult(webJson(1, "Device not found."));

        subUserClient.getDevice(orgId, createdDevice.id);
        subUserClient.verifyResult(webJson(2, "User has no access to this organization."));
    }

    @Test
    public void createSubOrgOfSubOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization(
                "SubOrg1", "Some TimeZone", "/static/logo.png", true, orgId,
                new Role(1, "Admin", 0b11111111111111111111, 0));
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(orgId, organization);
        OrganizationDTO fromApiOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiOrg);
        assertEquals(orgId, fromApiOrg.parentId);
        assertEquals(organization.name, fromApiOrg.name);
        assertEquals(organization.tzName, fromApiOrg.tzName);
        assertNotNull(fromApiOrg.products);
        assertEquals(1, fromApiOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiOrg.products[0].id);

        String subOrgUser1 = "subOrgUser1@blynk.cc";
        String pass = "1";
        String hash = SHA256Util.makeHash(pass, subOrgUser1);
        holder.userDao.add(subOrgUser1.toLowerCase(), hash, fromApiOrg.id, 1);

        Organization organization2 = new Organization(
                "SubOrg2", "Some TimeZone", "/static/logo.png", true, fromApiOrg.id,
                new Role(1, "Admin", 0b11111111111111111111, 0));

        AppWebSocketClient subUserClient = loggedDefaultClient(subOrgUser1, "1");
        subUserClient.createOrganization(fromApiOrg.id, organization2);
        OrganizationDTO fromApiOrg2 = subUserClient.parseOrganizationDTO(1);
        assertNotNull(fromApiOrg2);
        assertEquals(fromApiOrg2.parentId, fromApiOrg.id);
        assertEquals(organization2.name, fromApiOrg2.name);
        assertEquals(organization2.tzName, fromApiOrg2.tzName);
        assertNull(fromApiOrg2.products);
    }

}
