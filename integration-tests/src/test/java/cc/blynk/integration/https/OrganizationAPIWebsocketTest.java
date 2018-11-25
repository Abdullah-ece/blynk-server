package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.api.http.dashboard.dto.OrganizationDTO;
import cc.blynk.server.api.http.dashboard.dto.ProductDTO;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.LocationMetaField;
import cc.blynk.server.web.handlers.logic.organization.LocationDTO;
import cc.blynk.server.web.handlers.logic.organization.OrganizationsHierarchyDTO;
import cc.blynk.utils.SHA256Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.APIBaseTest.createNumberMeta;
import static cc.blynk.integration.APIBaseTest.createTextMeta;
import static cc.blynk.integration.TestUtil.defaultClient;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.webJson;
import static org.junit.Assert.assertEquals;
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
        client.getOrganizations();
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

        Organization subOrg = new Organization("SubOrg0001", "Europe/Kiev", "/static/logo.png", true, orgId);
        client.createOrganization(orgId, subOrg);

        client.getOrganizations();
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

        Organization subOrg = new Organization("SubOrg0000", "Europe/Kiev", "/static/logo.png", true, orgId);
        client.createOrganization(orgId, subOrg);

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

        Organization subOrg = new Organization("SubOrg00000", "Europe/Kiev", "/static/logo.png", true, orgId);
        client.createOrganization(orgId, subOrg);

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

        client.getOrganizations(organizationDTO.id);
        organizationDTOs = client.parseOrganizations(3);
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
                        null)
        };

        client.createProduct(orgId, product);
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
                        null)
        };

        client.createProduct(orgId, product);
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
    public void createSubOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("SubOrg000", "Europe/Kiev", "/static/logo.png", true, organizationDTO.id);
        client.createOrganization(orgId, subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);
    }

    @Test
    public void userAreRemovedWithOrganization() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");
        client.getOrganization(orgId);
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals(orgId, organizationDTO.id);

        Organization subOrg = new Organization("SubOrg", "Europe/Kiev", "/static/logo.png", true, organizationDTO.id);
        client.createOrganization(orgId, subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);

        client.inviteUser(subOrgDTO.id, "test@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(3));
        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq("test@gmail.com"),
                eq("Invitation to SubOrg dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        client.deleteOrg(subOrgDTO.id);
        client.verifyResult(ok(4));

        String passHash = SHA256Util.makeHash("123", "test@gmail.com");
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(webJson(1, "User not found."));
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

        Organization subOrg = new Organization("SubOrg3", "Europe/Kiev", "/static/logo.png", true, orgId);
        client.createOrganization(orgId, subOrg);
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

        Organization subOrg = new Organization("SubOrg2", "Europe/Kiev", "/static/logo.png", true, orgId);
        client.createOrganization(orgId, subOrg);

        Organization subOrg2 = new Organization("AAA2", "Europe/Kiev", "/static/logo.png", true, orgId);
        client.createOrganization(orgId, subOrg2);

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
        client.createOrganization(orgId, subOrg);

        Organization subOrg2 = new Organization("AAA1", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(orgId, subOrg2);

        Organization subOrg3 = new Organization("BBB1", "Europe/Kiev", "/static/logo.png", true, -1);
        client.createOrganization(orgId + 1, subOrg3);

        client.getOrganizationHierarchy();
        OrganizationsHierarchyDTO organizationsHierarchyDTO = client.parseOrganizationHierarchyDTO(4);
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

        Organization subOrg = new Organization("SubOrg111", "Europe/Kiev", "/static/logo.png", true, organizationDTO.id);
        client.createOrganization(organizationDTO.id, subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(2);
        assertNotNull(subOrgDTO);
        assertEquals(organizationDTO.id, subOrgDTO.parentId);
        assertNotNull(subOrgDTO.roles);
        assertEquals(3, subOrgDTO.roles.length);
        assertEquals(1, subOrgDTO.roles[0].id);

        Organization subOrg2 = new Organization("SubOrg222", "Europe/Kiev", "/static/logo.png", true, subOrgDTO.id);
        client.createOrganization(subOrgDTO.id, subOrg2);
        OrganizationDTO subOrgDTO2 = client.parseOrganizationDTO(3);
        assertNotNull(subOrgDTO2);
        assertEquals(subOrgDTO.id, subOrgDTO2.parentId);
        assertNotNull(subOrgDTO2.roles);
        assertEquals(3, subOrgDTO2.roles.length);
        assertEquals(1, subOrgDTO2.roles[0].id);
    }
}

