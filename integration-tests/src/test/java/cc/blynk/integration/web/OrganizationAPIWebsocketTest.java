package cc.blynk.integration.web;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.LocationMetaField;
import cc.blynk.server.web.handlers.logic.organization.dto.CountDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.DeviceCountDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.LocationDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.OrganizationsHierarchyDTO;
import cc.blynk.server.workers.ProfileSaverWorker;
import cc.blynk.utils.SHA256Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.APIBaseTest.createNumberMeta;
import static cc.blynk.integration.APIBaseTest.createTextMeta;
import static cc.blynk.integration.TestUtil.defaultClient;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.sleep;
import static cc.blynk.integration.TestUtil.webJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationAPIWebsocketTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void getOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals("Blynk Inc.", organizationDTO.name);
        assertEquals(-1, organizationDTO.parentId);
        assertEquals(orgId, organizationDTO.id);
        assertNotNull(organizationDTO.roles);
        assertEquals(4, organizationDTO.roles.length);
    }

    @Test
    public void getOrganizations() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganizations(orgId);
        OrganizationDTO[] organizationDTOs = client.parseOrganizations(1);
        assertNotNull(organizationDTOs);
        assertEquals(0, organizationDTOs.length);
    }

    @Test
    public void getOrganizationsBySpecificId() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganizations(orgId);
        OrganizationDTO[] organizationDTOs = client.parseOrganizations(1);
        assertNotNull(organizationDTOs);
        assertEquals(0, organizationDTOs.length);
    }

    @Test
    public void getOrganizations1SuborgOnly() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Organization subOrg = new Organization("SubOrg0001", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);

        client.getOrganizations(orgId);
        OrganizationDTO[] organizationDTOs = client.parseOrganizations(2);
        assertNotNull(organizationDTOs);
        assertEquals(1, organizationDTOs.length);
        OrganizationDTO organizationDTO = organizationDTOs[0];
        assertNotNull(organizationDTO);
        assertEquals("SubOrg0001", organizationDTO.name);
        assertEquals(orgId, organizationDTO.parentId);
        assertNotNull(organizationDTO.roles);
        assertEquals(3, organizationDTO.roles.length);
    }

    @Test
    public void getOrganizations1SuborgOnlyById() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Organization subOrg = new Organization("SubOrg0000", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);

        client.getOrganizations(orgId);
        OrganizationDTO[] organizationDTOs = client.parseOrganizations(2);
        assertNotNull(organizationDTOs);
        assertEquals(1, organizationDTOs.length);
        OrganizationDTO organizationDTO = organizationDTOs[0];
        assertNotNull(organizationDTO);
        assertEquals("SubOrg0000", organizationDTO.name);
        assertEquals(orgId, organizationDTO.parentId);
        assertNotNull(organizationDTO.roles);
        assertEquals(3, organizationDTO.roles.length);
    }

    @Test
    public void getOrganizationsForSuborgNoOrgs() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Organization subOrg = new Organization("SubOrg00000", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);

        client.getOrganizations(orgId);
        OrganizationDTO[] organizationDTOs = client.parseOrganizations(2);
        assertNotNull(organizationDTOs);
        assertEquals(1, organizationDTOs.length);
        OrganizationDTO organizationDTO = organizationDTOs[0];
        assertNotNull(organizationDTO);
        assertEquals("SubOrg00000", organizationDTO.name);
        assertEquals(orgId, organizationDTO.parentId);
        assertNotNull(organizationDTO.roles);
        assertEquals(3, organizationDTO.roles.length);

        client.trackOrg(organizationDTO.id);
        client.verifyResult(ok(3));
        client.getOrganizations(-1);
        organizationDTOs = client.parseOrganizations(4);
        assertNotNull(organizationDTOs);
        assertEquals(0, organizationDTOs.length);
    }

    @Test
    public void getLocationsForProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D),
                createTextMeta(2, "Device Name", "My Default device Name"),
                new LocationMetaField(3, "Device Location", new int[] {1}, false, false, false, null,
                        "Warehouse 13",
                        true, "Baklazhana street 15",
                        false, null,
                        false, null,
                        false, null,
                        false, null,
                        false, false, 0, 0,
                        false, null,
                        false, 0,
                        false, null,
                        false, null,
                        false, null,
                        false, false,
                        0,
                        null),
                createDeviceNameMeta(4, "namne", "name", true),
                createDeviceOwnerMeta(5, "owner", "owner", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        Device newDevice2 = new Device();
        newDevice2.name = "My New Device 2";
        newDevice2.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice2);
        Device createdDevice2 = client.parseDevice(3);
        assertNotNull(createdDevice2);

        client.getProductLocations(fromApiProduct.id);
        LocationDTO[] locationDTOS = client.parseLocationsDTO(4);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);

        client.getMetafield(createdDevice.id, locationDTOS[0].id);
        LocationMetaField metaField = (LocationMetaField) client.parseMetafield(5);
        assertNotNull(metaField);
        assertEquals(3, metaField.id);
        assertEquals("Device Location", metaField.name);
        assertEquals("Warehouse 13", metaField.siteName);
        assertTrue(metaField.isLocationEnabled);
        assertEquals("Baklazhana street 15", metaField.streetAddress);
    }

    @Test
    public void getLocationsForProductWithAutoComplete() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D),
                createTextMeta(2, "Device Name", "My Default device Name"),
                new LocationMetaField(3, "Device Location", new int[] {1}, false, false, false, null,
                        "Warehouse 13",
                        true, "Baklazhana street 15",
                        false, null,
                        false, null,
                        false, null,
                        false, null,
                        false, false, 0, 0,
                        false, null,
                        false, 0,
                        false, null,
                        false, null,
                        false, null,
                        false, false,
                        0,
                        null),
                createDeviceNameMeta(4, "namne", "name", true),
                createDeviceOwnerMeta(5, "owner", "owner", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        client.getProductLocations(fromApiProduct.id, "Ware");
        LocationDTO[] locationDTOS = client.parseLocationsDTO(3);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);

        client.getProductLocations(fromApiProduct.id, "ware");
        locationDTOS = client.parseLocationsDTO(4);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);

        client.getProductLocations(fromApiProduct.id, "care");
        locationDTOS = client.parseLocationsDTO(5);
        assertNotNull(locationDTOS);
        assertEquals(0, locationDTOS.length);

        client.getProductLocations(fromApiProduct.id, "13");
        locationDTOS = client.parseLocationsDTO(6);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);

        client.getProductLocations(fromApiProduct.id, "house");
        locationDTOS = client.parseLocationsDTO(7);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);
    }

    @Test
    public void updateInvitedUserInfo() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, "test@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(1));

        verify(holder.mailWrapper).sendHtml(eq("test@gmail.com"), eq("Invitation to Blynk Inc. dashboard."), contains("/dashboard/invite?token="));

        User user = new User();
        user.email = "test@gmail.com";
        user.name = "Dmitriy2";
        user.roleId = 1;
        client.updateUserInfo(orgId, user);
        client.verifyResult(ok(2));

        client.getOrgUsers(orgId);
        User[] users = client.parseUsers(3);
        assertNotNull(users);
        assertEquals(1, users.length);
        assertEquals(1, users[0].roleId);
        assertEquals("Dmitriy2", users[0].name);
    }

    @Test
    public void getUsersIsCorrectForSuborg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("getUsersIsCorrectForSuborg", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);

        client.inviteUser(subOrgDTO.id, "test@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(3));

        client.getOrgUsers(subOrgDTO.id);
        User[] users = client.parseUsers(4);
        assertNotNull(users);
        assertEquals(1, users.length);
        assertEquals("test@gmail.com", users[0].email);

        client.getOrgUsers(orgId);
        users = client.parseUsers(5);
        assertNotNull(users);
        assertEquals(0, users.length);
    }

    @Test
    public void createSubOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("1SubOrg000", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);
    }

    @Test
    public void createDeviceForSuborgAndCheckDeviceCount() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("SubOrg000", "Europe/Kiev", "/static/logo.png", true, -1);
        subOrg.selectedProducts = new int[] {organizationDTO.products[0].id};
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);
        assertNotNull(subOrgDTO.products);
        assertEquals(1, subOrgDTO.products.length);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = subOrgDTO.products[0].id;

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(3));
        client.createDevice(subOrgDTO.id, newDevice);
        newDevice = client.parseDevice(4);
        assertNotNull(newDevice);

        client.trackOrg(orgId);
        client.verifyResult(ok(5));
        client.getOrganizations(-1);
        OrganizationDTO[] orgs = client.parseOrganizations(6);
        assertNotNull(orgs);
        assertEquals(1, orgs.length);
        assertEquals(1, orgs[0].products[0].deviceCount);
    }

    @Test
    public void CheckDeviceCountViaAPI() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        client.getDeviceCount(orgId);
        CountDTO countDTO = client.parseCountDTO(2);
        assertNotNull(countDTO);
        assertEquals(1, countDTO.orgCount);
        assertEquals(0, countDTO.subOrgCount);
    }

    @Test
    // github.com/blynkkk/dash/issues/2062
    public void checkDeviceCountHavingProductWithParentIdOnItself() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getProducts(orgId);
        ProductDTO[] productDTOS = client.parseProductDTOs(1);
        assertNotNull(productDTOS);
        assertTrue(productDTOS.length > 0);
        assertNotNull(productDTOS[0]);


        // default product's parentId is itself id
        // without id check OrganizationDao.getProductChilds() goes into infinite recursion
        holder.organizationDao.getProductChilds(productDTOS[0].id);
    }

    @Test
    public void createDeviceForSuborgAndCheckDeviceCountViaAPI() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("SubOrg000111", "Europe/Kiev", "/static/logo.png", true, -1);
        subOrg.selectedProducts = new int[] {organizationDTO.products[0].id};
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);
        assertNotNull(subOrgDTO.products);
        assertEquals(1, subOrgDTO.products.length);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = subOrgDTO.products[0].id;

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(3));
        client.createDevice(subOrgDTO.id, newDevice);
        newDevice = client.parseDevice(4);
        assertNotNull(newDevice);

        client.trackOrg(orgId);
        client.verifyResult(ok(5));
        client.getDeviceCount(-1);
        CountDTO countDTO = client.parseCountDTO(6);
        assertNotNull(countDTO);
        assertEquals(1, countDTO.orgCount);
        assertEquals(1, countDTO.subOrgCount);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(7));
        client.getDeviceCount(subOrgDTO.id);
        CountDTO subCountDTO = client.parseCountDTO(8);
        assertNotNull(subCountDTO);
        assertEquals(1, subCountDTO.orgCount);
        assertEquals(0, subCountDTO.subOrgCount);
    }

    @Test
    public void checkDeviceCountAfterDeviceDelete() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("SubOrg000111", "Europe/Kiev", "/static/logo.png", true, -1);
        subOrg.selectedProducts = new int[] {organizationDTO.products[0].id};
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);
        assertNotNull(subOrgDTO.products);
        assertEquals(1, subOrgDTO.products.length);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = subOrgDTO.products[0].id;

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(3));
        client.createDevice(subOrgDTO.id, newDevice);
        newDevice = client.parseDevice(4);
        assertNotNull(newDevice);

        client.trackOrg(orgId);
        client.verifyResult(ok(5));

        client.reset();

        client.getDeviceCountForProductAndSubproducts(subOrgDTO.products[0].id);
        DeviceCountDTO deviceCountDTO = client.parseDeviceCountDTO(1);
        assertNotNull(deviceCountDTO);
        assertEquals(1, deviceCountDTO.deviceCount);
        assertEquals(0, deviceCountDTO.subDeviceCount);

        client.deleteDevice(subOrg.id, newDevice.id);
        client.verifyResult(ok(2));

        client.getDeviceCountForProductAndSubproducts(subOrgDTO.products[0].id);
        deviceCountDTO = client.parseDeviceCountDTO(3);
        assertNotNull(deviceCountDTO);
        assertEquals(0, deviceCountDTO.deviceCount);
        assertEquals(0, deviceCountDTO.subDeviceCount);
    }

    @Test
    public void checkDeviceCountHavingSeveralSubproducts() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("SubOrg000111", "Europe/Kiev", "/static/logo.png", true, -1);
        subOrg.selectedProducts = new int[] {organizationDTO.products[0].id};
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);
        assertNotNull(subOrgDTO.products);
        assertEquals(1, subOrgDTO.products.length);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = subOrgDTO.products[0].id;

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(3));
        client.createDevice(subOrgDTO.id, newDevice);
        newDevice = client.parseDevice(4);
        assertNotNull(newDevice);

        client.trackOrg(orgId);
        client.verifyResult(ok(5));

        client.reset();

        client.getDeviceCountForProductAndSubproducts(subOrgDTO.products[0].id);
        DeviceCountDTO deviceCountDTO = client.parseDeviceCountDTO(1);
        assertNotNull(deviceCountDTO);
        assertEquals(1, deviceCountDTO.deviceCount);
        assertEquals(0, deviceCountDTO.subDeviceCount);

        Organization subOrg1 = new Organization("SubOrg000112", "Europe/Kiev", "/static/logo.png", true, -1);


        subOrg1.selectedProducts = new int[] {organizationDTO.products[0].id};
        client.createOrganization(subOrg1);
        subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);
        assertNotNull(subOrgDTO.products);
        assertEquals(1, subOrgDTO.products.length);

        newDevice = new Device();
        newDevice.name = "My New Device1";
        newDevice.productId = subOrgDTO.products[0].id;

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(3));
        client.createDevice(subOrgDTO.id, newDevice);
        newDevice = client.parseDevice(4);
        assertNotNull(newDevice);

        client.trackOrg(orgId);
        client.verifyResult(ok(5));

        client.getProducts(orgId);
        ProductDTO[] parentProducts = client.parseProductDTOs(6);
        assertNotNull(parentProducts);
        assertTrue(parentProducts.length > 0);
        assertNotNull(parentProducts[0]);

        // count from default org should return 1 and 2 (for 2 subOrgs)
        client.getDeviceCountForProductAndSubproducts(parentProducts[0].id);
        deviceCountDTO = client.parseDeviceCountDTO(7);
        assertNotNull(deviceCountDTO);
        assertEquals(1, deviceCountDTO.deviceCount);
        assertEquals(2, deviceCountDTO.subDeviceCount);
    }

    @Test
    public void userAreRemovedWithOrganization() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization();
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("userAreRemovedWithOrganization", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);

        client.inviteUser(subOrgDTO.id, "test@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(3));
        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq("test@gmail.com"),
                eq("Invitation to userAreRemovedWithOrganization dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        client.trackOrg(orgId);
        client.verifyResult(ok(4));
        client.deleteOrg(subOrgDTO.id);
        client.verifyResult(ok(5));

        String passHash = SHA256Util.makeHash("123", "test@gmail.com");
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(webJson(1, "User not found."));
    }

    @Test
    public void userCantRemoveOrgWithSubOrganizations() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization();
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("userCantRemoveOrgWithSubOrganizations", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(3));

        Organization subOrg2 = new Organization("userCantRemoveOrgWithSubOrganizations2", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg2);
        OrganizationDTO subOrgDTO2 = client.parseOrganizationDTO(4);
        assertNotNull(subOrgDTO2);
        assertEquals(subOrgDTO.id, subOrgDTO2.parentId);

        client.deleteOrg(subOrgDTO.id);
        client.verifyResult(webJson(5, "You are not allowed to remove organization with sub organizations."));
    }

    @Test
    public void userCantRemoveSubOrgWithoutSwitchingToParentOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization();
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("SubOrg", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(3));

        Organization subOrg2 = new Organization("SubOrgSecond", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg2);
        OrganizationDTO subOrgDTO2 = client.parseOrganizationDTO(4);
        assertNotNull(subOrgDTO2);
        assertEquals(subOrgDTO.id, subOrgDTO2.parentId);

        client.trackOrg(orgId);
        client.verifyResult(ok(5));

        client.deleteOrg(subOrgDTO2.id);
        client.verifyResult(webJson(6, "Removed organization should be a child of current organization."));
    }

    @Test
    public void getSingleOrgHierarchy() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganizationHierarchy();
        OrganizationsHierarchyDTO organizationsHierarchyDTO = client.parseOrganizationHierarchyDTO(1);
        assertNotNull(organizationsHierarchyDTO);
        assertEquals(orgId, organizationsHierarchyDTO.id);
        assertEquals("Blynk Inc.", organizationsHierarchyDTO.name);
        assertNull(organizationsHierarchyDTO.childs);
        printHierarchy(organizationsHierarchyDTO, 0);
    }

    @Test
    public void get2OrgHierarchy() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Organization subOrg = new Organization("SubOrg3", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(1);
        assertNotNull(subOrgDTO);

        client.getOrganizationHierarchy();
        OrganizationsHierarchyDTO organizationsHierarchyDTO = client.parseOrganizationHierarchyDTO(2);
        assertNotNull(organizationsHierarchyDTO);
        assertEquals(orgId, organizationsHierarchyDTO.id);
        assertEquals("Blynk Inc.", organizationsHierarchyDTO.name);
        assertNotNull(organizationsHierarchyDTO.childs);
        assertEquals(1, organizationsHierarchyDTO.childs.size());

        OrganizationsHierarchyDTO subOrgHierarchy = organizationsHierarchyDTO.childs.iterator().next();
        assertEquals(orgId + 1, subOrgHierarchy.id);
        assertEquals("SubOrg3", subOrgHierarchy.name);
        assertNull(subOrgHierarchy.childs);
        printHierarchy(organizationsHierarchyDTO, 0);
    }

    @Test
    public void get3OrgHierarchySameLevel() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Organization subOrg = new Organization("SubOrg2", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);

        Organization subOrg2 = new Organization("AAA2", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg2);

        client.getOrganizationHierarchy();
        OrganizationsHierarchyDTO organizationsHierarchyDTO = client.parseOrganizationHierarchyDTO(3);
        assertNotNull(organizationsHierarchyDTO);
        assertEquals(orgId, organizationsHierarchyDTO.id);
        assertEquals("Blynk Inc.", organizationsHierarchyDTO.name);
        assertNotNull(organizationsHierarchyDTO.childs);
        assertEquals(2, organizationsHierarchyDTO.childs.size());

        printHierarchy(organizationsHierarchyDTO, 0);

        var iterator = organizationsHierarchyDTO.childs.iterator();
        OrganizationsHierarchyDTO subOrgHierarchy = iterator.next();
        assertEquals(orgId + 2, subOrgHierarchy.id);
        assertEquals("AAA2", subOrgHierarchy.name);
        assertNull(subOrgHierarchy.childs);

        subOrgHierarchy = iterator.next();
        assertEquals(orgId + 1, subOrgHierarchy.id);
        assertEquals("SubOrg2", subOrgHierarchy.name);
        assertNull(subOrgHierarchy.childs);
    }

    @Test
    public void get5OrgHierarchySameLevel() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Organization subOrg = new Organization("SubOrg1", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);

        Organization subOrg2 = new Organization("AAA1", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg2);

        client.trackOrg(orgId + 1);
        client.verifyResult(ok(3));
        Organization subOrg3 = new Organization("BBB1", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg3);

        client.getOrganizationHierarchy();
        OrganizationsHierarchyDTO organizationsHierarchyDTO = client.parseOrganizationHierarchyDTO(5);
        assertNotNull(organizationsHierarchyDTO);
        printHierarchy(organizationsHierarchyDTO, 0);

        assertEquals(orgId, organizationsHierarchyDTO.id);
        assertEquals("Blynk Inc.", organizationsHierarchyDTO.name);
        assertNotNull(organizationsHierarchyDTO.childs);
        assertEquals(2, organizationsHierarchyDTO.childs.size());

        var iterator = organizationsHierarchyDTO.childs.iterator();
        OrganizationsHierarchyDTO subOrgHierarchy = iterator.next();
        assertEquals(orgId + 2, subOrgHierarchy.id);
        assertEquals("AAA1", subOrgHierarchy.name);
        assertNull(subOrgHierarchy.childs);

        subOrgHierarchy = iterator.next();
        assertEquals(orgId + 1, subOrgHierarchy.id);
        assertEquals("SubOrg1", subOrgHierarchy.name);
        assertNotNull(subOrgHierarchy.childs);
        assertEquals(1, subOrgHierarchy.childs.size());

        subOrgHierarchy = subOrgHierarchy.childs.iterator().next();
        assertEquals(orgId + 3, subOrgHierarchy.id);
        assertEquals("BBB1", subOrgHierarchy.name);
        assertNull(subOrgHierarchy.childs);
    }

    @Test
    public void getOrgHierarchyForSubAdmin() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization();
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("getOrgHierarchyForSubAdmin", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);

        client.inviteUser(subOrgDTO.id, "test@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(3));
        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq("test@gmail.com"),
                eq("Invitation to getOrgHierarchyForSubAdmin dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        String passHash = SHA256Util.makeHash("123", "test@gmail.com");
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(ok(1));

        appWebSocketClient.getOrganizationHierarchy();
        OrganizationsHierarchyDTO organizationsHierarchyDTO = appWebSocketClient.parseOrganizationHierarchyDTO(2);
        assertNotNull(organizationsHierarchyDTO);
        assertEquals("getOrgHierarchyForSubAdmin", organizationsHierarchyDTO.name);
        assertNull(organizationsHierarchyDTO.childs);
        printHierarchy(organizationsHierarchyDTO, 0);
    }

    public void printHierarchy(OrganizationsHierarchyDTO organizationsHierarchyDTO, int spaces) {
        for (int i = 0; i < spaces; i++) {
            System.out.print("-");
        }
        System.out.println(organizationsHierarchyDTO.id +" " + organizationsHierarchyDTO.name);
        if (organizationsHierarchyDTO.childs != null) {
            for (OrganizationsHierarchyDTO child : organizationsHierarchyDTO.childs) {
                printHierarchy(child, spaces + 4);
            }
        }
    }

    @Test
    public void createSubOrgOfSubOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("SubOrg111", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(3));
        Organization subOrg2 = new Organization("SubOrg222", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg2);
        OrganizationDTO subOrgDTO2 = client.parseOrganizationDTO(4);
        assertNotNull(subOrgDTO2);
        assertEquals(subOrgDTO.id, subOrgDTO2.parentId);
        assertNotNull(subOrgDTO2.roles);
        assertEquals(3, subOrgDTO2.roles.length);
        assertEquals(1, subOrgDTO2.roles[0].id);
    }

    @Test
    public void getEditOwnOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals("Blynk Inc.", organizationDTO.name);
        assertNull(organizationDTO.description);
        assertEquals(-1, organizationDTO.parentId);
        assertTrue(organizationDTO.canCreateOrgs);
        assertEquals(orgId, organizationDTO.id);
        assertNotNull(organizationDTO.roles);
        assertEquals(4, organizationDTO.roles.length);

        OrganizationDTO organizationUpdated = new OrganizationDTO(
                organizationDTO.id,
                "123",
                "124",
                false,
                organizationDTO.isActive,
                organizationDTO.tzName,
                organizationDTO.logoUrl,
                organizationDTO.unit,
                organizationDTO.primaryColor,
                organizationDTO.secondaryColor,
                organizationDTO.lastModifiedTs,
                null,
                null,
                10,
                null,
                null);

        client.editOwnOrg(organizationUpdated);
        organizationDTO = client.parseOrganizationDTO(2);
        assertNotNull(organizationDTO);
        assertEquals("123", organizationDTO.name);
        assertEquals("124", organizationDTO.description);
        assertEquals(-1, organizationDTO.parentId);
        assertFalse(organizationDTO.canCreateOrgs);
        assertEquals(orgId, organizationDTO.id);
        assertNotNull(organizationDTO.roles);
        assertEquals(4, organizationDTO.roles.length);
    }

    @Test
    public void getEditOwnOrgPermissionViolation() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals("Blynk Inc.", organizationDTO.name);
        assertNull(organizationDTO.description);
        assertEquals(-1, organizationDTO.parentId);
        assertTrue(organizationDTO.canCreateOrgs);
        assertEquals(orgId, organizationDTO.id);
        assertNotNull(organizationDTO.roles);
        assertEquals(4, organizationDTO.roles.length);

        OrganizationDTO organizationUpdated = new OrganizationDTO(
                111,
                "123",
                "124",
                false,
                organizationDTO.isActive,
                organizationDTO.tzName,
                organizationDTO.logoUrl,
                organizationDTO.unit,
                organizationDTO.primaryColor,
                organizationDTO.secondaryColor,
                organizationDTO.lastModifiedTs,
                null,
                null,
                10,
                null,
                null);

        //this is ok, as orgId is actually taken from the session
        client.editOwnOrg(organizationUpdated);
        organizationDTO = client.parseOrganizationDTO(2);
        assertNotNull(organizationDTO);
    }

    @Test
    public void basicTrackOrgTest() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals("Blynk Inc.", organizationDTO.name);
        assertEquals(-1, organizationDTO.parentId);
        assertEquals(orgId, organizationDTO.id);

        client.trackOrg(organizationDTO.id);
        client.verifyResult(ok(2));

        client.trackOrg(100);
        client.verifyResult(webJson(3, "User " + getUserName() + " has no access to this organization (id=" + 100 + ")."));
    }

    @Test
    public void testProfileSaverWorkerSavesOrgToDB() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        ProfileSaverWorker profileSaverWorker = new ProfileSaverWorker(
                holder.userDao, holder.fileManager, holder.dbManager, holder.organizationDao);

        Organization subOrg = new Organization("userAreRemovedWithOrganization", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(1);
        assertNotNull(subOrgDTO);

        profileSaverWorker.run();

        sleep(500);

        Map<Integer, Organization> organizations =  holder.dbManager.organizationDBDao.getAllOrganizations();
        assertNotNull(organizations);
        assertFalse(organizations.isEmpty());

        Organization orgFromDB = organizations.get(subOrgDTO.id);
        assertNotNull(orgFromDB);
        assertEquals(subOrg.name, orgFromDB.name);
        assertEquals(subOrg.tzName, orgFromDB.tzName);
        assertEquals(subOrg.logoUrl, orgFromDB.logoUrl);
    }
}

