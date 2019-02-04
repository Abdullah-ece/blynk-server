package cc.blynk.integration.web;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.api.http.dashboard.dto.RoleDTO;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.processors.rules.RuleGroup;
import cc.blynk.server.web.handlers.logic.organization.dto.CountDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.LocationDTO;
import cc.blynk.server.web.handlers.logic.organization.dto.OrganizationsHierarchyDTO;
import cc.blynk.utils.SHA256Util;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.defaultClient;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.webJson;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DELETE_USERS;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICE_DATA_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_EDIT_USERS;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_INVITE_USERS;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_SWITCH;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_VIEW_USERS;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_START;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_STOP;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICE_DATA_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_ORG_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PERMISSION1_NAMES;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PERMISSION2_NAMES;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.RULE_GROUP_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.RULE_GROUP_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.SET_AUTH_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
public class PermissionsForEveryHandlerTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    private static int setPermission(int... permissions) {
        int result = 0;
        for (int permission : permissions) {
            result |= permission;
        }
        return result;
    }

    private static int removePermission(int... permissions) {
        int all = 0b11111111111111111111111111111111;
        for (int exclude : permissions) {
            all ^= exclude;
        }
        return all;
    }

    private static void verifyPermissionAbsence(BaseTestAppClient client, int permission,
                                                boolean isFromPermission1Group) throws Exception {
        String permissionName = isFromPermission1Group ? PERMISSION1_NAMES.get(permission) :
                PERMISSION2_NAMES.get(permission);
        client.verifyResult(webJson(client.getMsgId(), "User " +
                EXAMPLE_USER_EMAIL + " has no permission for '" +
                permissionName + "' operation."));
    }

    private static final int NON_EXISTING_ORG_ID     = -99;
    private static final int NON_EXISTING_PRODUCT_ID = -99;
    private static final int NON_EXISTING_ROLE_ID    = -99;
    private static final int NON_EXISTING_DEVICE_ID  = -99;
    private static final int NON_EXISTING_DASH_ID    = -99;

    private static final String EXAMPLE_USER_EMAIL = "testpermissions@gmail.com";

    private static final RoleDTO EXAMPLE_ROLE_DTO = new RoleDTO(NON_EXISTING_ROLE_ID, "role1", 1, 1);

    private static final OrganizationDTO EXAMPLE_ORGANIZATION_DTO = new OrganizationDTO(
            NON_EXISTING_ORG_ID,
            "org name",
            "1246",
            false,
            true,
            null,
            null,
            null,
            "-1",
            "-1",
            -1,
            null,
            null,
            10,
            null,
            null);

    private AppWebSocketClient createUserForSubOrgSpecificRole(int permissions1, int permissions2) throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Organization subOrg = new Organization("PermissionTestSuborg", "Europe/Kiev", null, true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(1);
        assertNotNull(subOrgDTO);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(2));
        client.createRole(new RoleDTO(-1, "Test", permissions1, permissions2));
        RoleDTO roleDTO = client.parseRoleDTO(3);
        assertNotNull(roleDTO);

        client.getOrganization(subOrgDTO.id);
        subOrgDTO = client.parseOrganizationDTO(4);
        assertNotNull(subOrgDTO);
        assertEquals(4, subOrgDTO.roles.length);
        Role createRole = subOrgDTO.roles[3];
        assertEquals(4, createRole.id);
        assertEquals("Test", createRole.name);
        assertEquals(permissions1, createRole.permissionGroup1);
        assertEquals(permissions2, createRole.permissionGroup2);

        client.inviteUser(subOrgDTO.id, "testPermissions@gmail.com", "Dmitriy", createRole.id);
        client.verifyResult(ok(5));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq("testpermissions@gmail.com"),
                eq("Invitation to PermissionTestSuborg dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq("testpermissions@gmail.com"), eq("Invitation to PermissionTestSuborg dashboard."), contains("https://localhost:10443/static/logo.png"));
        verify(holder.mailWrapper).sendHtml(eq("testpermissions@gmail.com"),
                eq("Invitation to PermissionTestSuborg dashboard."), contains("/dashboard/invite?token="));

        String passHash = SHA256Util.makeHash("1", "testpermissions@gmail.com");
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(ok(1));

        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(2);
        assertNotNull(user);
        assertEquals("testpermissions@gmail.com", user.email);
        assertEquals("Dmitriy", user.name);
        assertEquals(4, user.roleId);
        assertEquals(subOrgDTO.id, user.orgId);

        appWebSocketClient.reset();
        return appWebSocketClient;
    }

    private AppWebSocketClient createUserForSubOrgSpecificRole(int permissions1) throws Exception {
        return createUserForSubOrgSpecificRole(permissions1, -1);
    }

    @Test
    public void roleUpdateAppliedInRealtime() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Organization subOrg = new Organization("PermissionTestSuborg", "Europe/Kiev", null, true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(1);
        assertNotNull(subOrgDTO);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(2));
        client.createRole(new RoleDTO(-1, "Test", 0b11111111111111111111111111111111, 0));
        RoleDTO roleDTO = client.parseRoleDTO(3);
        assertNotNull(roleDTO);

        client.getOrganization(subOrgDTO.id);
        subOrgDTO = client.parseOrganizationDTO(4);
        assertNotNull(subOrgDTO);
        assertEquals(4, subOrgDTO.roles.length);
        Role createRole = subOrgDTO.roles[3];
        assertEquals(4, createRole.id);

        client.inviteUser(subOrgDTO.id, "testPermissions@gmail.com", "Dmitriy", createRole.id);
        client.verifyResult(ok(5));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq("testpermissions@gmail.com"),
                eq("Invitation to PermissionTestSuborg dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        String passHash = SHA256Util.makeHash("1", "testpermissions@gmail.com");
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(ok(1));

        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(2);
        TestCase.assertNotNull(user);
        assertEquals("testpermissions@gmail.com", user.email);
        assertEquals("Dmitriy", user.name);
        assertEquals(4, user.roleId);
        assertEquals(subOrgDTO.id, user.orgId);

        appWebSocketClient.reset();

        appWebSocketClient.getOrganizationHierarchy();
        OrganizationsHierarchyDTO organizationsHierarchyDTO = client.parseOrganizationHierarchyDTO(1);
        assertNotNull(organizationsHierarchyDTO);
        assertEquals("PermissionTestSuborg", organizationsHierarchyDTO.name);

        int updatedPerm = removePermission(ORG_SWITCH);
        client.updateRole(new RoleDTO(createRole.id, createRole.name, updatedPerm, createRole.permissionGroup2));
        roleDTO = client.parseRoleDTO(6);
        assertNotNull(roleDTO);
        assertEquals(4, roleDTO.id);
        assertEquals(updatedPerm, roleDTO.permissionGroup1);

        appWebSocketClient.getOrganizationHierarchy();
        appWebSocketClient.verifyResult(webJson(2, "User testpermissions@gmail.com has no permission for 'switch organization' operation."));

        TestAppClient invitedUserAppClient = new TestAppClient(properties);
        invitedUserAppClient.start();
        invitedUserAppClient.login("testpermissions@gmail.com", "1");
        invitedUserAppClient.verifyResult(ok(1));

        invitedUserAppClient.getDevices();
        DeviceDTO[] deviceDTO = invitedUserAppClient.parseDevicesDTO(2);
        assertNotNull(deviceDTO);
        assertEquals(0, deviceDTO.length);

        updatedPerm = removePermission(ORG_DEVICES_VIEW);
        client.updateRole(new RoleDTO(createRole.id, createRole.name, updatedPerm, createRole.permissionGroup2));
        roleDTO = client.parseRoleDTO(7);
        assertNotNull(roleDTO);
        assertEquals(4, roleDTO.id);
        assertEquals(updatedPerm, roleDTO.permissionGroup1);

        invitedUserAppClient.getDevices();
        deviceDTO = invitedUserAppClient.parseDevicesDTO(3);
        assertNotNull(deviceDTO);
        assertEquals(0, deviceDTO.length);
    }

    @Test
    public void orgSwitch() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_SWITCH));
        client.getOrganizationHierarchy();
        OrganizationsHierarchyDTO organizationsHierarchyDTO = client.parseOrganizationHierarchyDTO(1);
        assertNotNull(organizationsHierarchyDTO);
        assertEquals("PermissionTestSuborg", organizationsHierarchyDTO.name);
    }
    @Test
    public void noOrgSwitch() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_SWITCH));
        client.getOrganizationHierarchy();
        verifyPermissionAbsence(client, ORG_SWITCH, true);
    }

    @Test
    public void ownOrgEdit() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OWN_ORG_EDIT));
        client.editOwnOrg(new OrganizationDTO(
                orgId,
                "123",
                "124",
                false,
                true,
                null,
                null,
                null,
                "-1",
                "-1",
                -1,
                null,
                null,
                10,
                null,
                null));
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        assertEquals("123", organizationDTO.name);
    }
    @Test
    public void noOwnOrgEdit() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(OWN_ORG_EDIT));
        client.editOwnOrg(new OrganizationDTO(
                orgId,
                "123",
                "124",
                false,
                true,
                null,
                null,
                null,
                "-1",
                "-1",
                -1,
                null,
                null,
                10,
                null,
                null));
        verifyPermissionAbsence(client, OWN_ORG_EDIT, true);
    }

    @Test
    public void OTAView() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OTA_VIEW));
        client.getFirmwareInfo(null);
        client.verifyResult(webJson(1, "Path to firmware is not provided."));
    }
    @Test
    public void noOTAView() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(OTA_VIEW));
        client.getFirmwareInfo(null);
        verifyPermissionAbsence(client, OTA_VIEW, true);
    }

    @Test
    public void OTAStart() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OTA_START));
        client.otaStart(new ShipmentDTO(1, 1, 1, null, null, null, null, null, 0));
        client.verifyResult(webJson(1, "Wrong data for OTA start."));
    }
    @Test
    public void noOTAStart() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(OTA_START));
        client.otaStart(new ShipmentDTO(1, 1, 1, null, null, null, null, null, 0));
        verifyPermissionAbsence(client, OTA_START, true);
    }

    @Test
    public void OTAStop() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OTA_STOP));
        client.otaStop(new ShipmentDTO(1, 1, 1, null, null, null, null, null, 0));
        client.verifyResult(webJson(1, "No devices to stop OTA."));
    }
    @Test
    public void noOTAStop() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(OTA_STOP));
        client.otaStop(new ShipmentDTO(1, 1, 1, null, null, null, null, null, 0));
        verifyPermissionAbsence(client, OTA_STOP, true);
    }

    @Test
    public void orgCreate() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_CREATE));
        client.createOrganization(new Organization());
        client.verifyResult(webJson(1, "Organization is empty."));
    }
    @Test
    public void noOrgCreate() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_CREATE));
        client.createOrganization(new Organization());
        verifyPermissionAbsence(client, ORG_CREATE, true);
    }

    @Test
    public void orgGet() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_VIEW));
        client.getOrganization();
        OrganizationDTO organizationDTO = client.parseOrganizationDTO(1);
        assertNotNull(organizationDTO);
        client.reset();

        client.getOrganizations(orgId);
        OrganizationDTO[] organizationDTOs = client.parseOrganizations(1);
        assertNotNull(organizationDTOs);
        assertEquals(0, organizationDTOs.length);
    }
    @Test
    public void noOrgGet() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_VIEW));
        client.getOrganizations(NON_EXISTING_ORG_ID);
        verifyPermissionAbsence(client, ORG_VIEW, true);

        client.getOrganization();

        //own org view allowed for every user
        OrganizationDTO org = client.parseOrganizationDTO(2);
        assertNotNull(org);
    }

    @Test
    public void orgEdit() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_EDIT));
        client.editOrg(EXAMPLE_ORGANIZATION_DTO);
        client.verifyResult(webJson(1, "Organization not found."));
    }
    @Test
    public void noOrgEdit() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_EDIT));
        client.editOrg(EXAMPLE_ORGANIZATION_DTO);
        verifyPermissionAbsence(client, ORG_EDIT, true);
    }

    @Test
    public void orgDelete() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_DELETE));
        client.deleteOrg(NON_EXISTING_ORG_ID);
        client.verifyResult(webJson(1, "User " +
                EXAMPLE_USER_EMAIL + " has no access to this organization " +
                "(id=" + NON_EXISTING_ORG_ID + ")."));
    }
    @Test
    public void noOrgDelete() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DELETE));
        client.deleteOrg(NON_EXISTING_ORG_ID);
        verifyPermissionAbsence(client, ORG_DELETE, true);
    }

    @Test
    public void orgInviteUsers() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_INVITE_USERS));
        client.canInviteUser("user@gmail.com");
        client.verifyResult(ok(1));
        client.reset();

        client.inviteUser(NON_EXISTING_ORG_ID, "user@gmail.com", "name", NON_EXISTING_ROLE_ID);
        client.verifyResult(webJson(1, "Invalid invitation."));
    }
    @Test
    public void noOrgInviteUsers() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_INVITE_USERS));
        client.canInviteUser("user@gmail.com");
        verifyPermissionAbsence(client, ORG_INVITE_USERS, true);
        client.reset();

        client.inviteUser(NON_EXISTING_ORG_ID, "user@gmail.com", "name", NON_EXISTING_ROLE_ID);
        verifyPermissionAbsence(client, ORG_INVITE_USERS, true);
    }

    @Test
    public void orgViewUsers() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_VIEW_USERS));
        client.getOrgUsers(NON_EXISTING_ORG_ID);
        client.verifyResult(webJson(1, "User " +
                EXAMPLE_USER_EMAIL + " has no access to this organization " +
                "(id=" + NON_EXISTING_ORG_ID + ")."));
    }
    @Test
    public void noOrgViewUsers() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_VIEW_USERS));
        client.getOrgUsers(NON_EXISTING_ORG_ID);
        verifyPermissionAbsence(client, ORG_VIEW_USERS, true);
    }

    @Test
    public void orgEditUsers() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_EDIT_USERS));
        client.updateUserInfo(NON_EXISTING_ORG_ID, new User());
        client.verifyResult(webJson(1, "Bad data for user info update."));
    }
    @Test
    public void noOrgEditUsers() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_EDIT_USERS));
        client.updateUserInfo(NON_EXISTING_ORG_ID, new User());
        verifyPermissionAbsence(client, ORG_EDIT_USERS, true);
    }

    @Test
    public void orgDeleteUsers() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_DELETE_USERS));
        client.deleteUser(NON_EXISTING_ORG_ID, "");
        client.verifyResult(ok(1));
    }
    @Test
    public void noOrgDeleteUsers() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DELETE_USERS));
        client.deleteUser(NON_EXISTING_ORG_ID, "");
        verifyPermissionAbsence(client, ORG_DELETE_USERS, true);
    }

    @Test
    public void createProduct() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(PRODUCT_CREATE));
        client.createProduct(new Product());
        client.verifyResult(webJson(1, "Product name is empty."));
    }
    @Test
    public void noCreateProduct() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(PRODUCT_CREATE));
        client.createProduct(new Product());
        verifyPermissionAbsence(client, PRODUCT_CREATE, true);
    }

    @Test
    public void viewProduct() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(PRODUCT_VIEW));
        client.getProduct(NON_EXISTING_PRODUCT_ID);
        client.verifyResult(webJson(1, "Product with passed id " +
                NON_EXISTING_PRODUCT_ID + " not exists."));
        client.reset();

        client.getProductLocations(NON_EXISTING_PRODUCT_ID);
        LocationDTO[] locationDTOS = client.parseLocationsDTO(1);
        assertNotNull(locationDTOS);
        assertEquals(0, locationDTOS.length);
        client.reset();

        client.getDeviceCount(orgId);
        CountDTO countDTO = client.parseCountDTO(1);
        assertNotNull(countDTO);
        client.reset();

        client.getProducts(NON_EXISTING_ORG_ID);
        ProductDTO[] fromApiProduct = client.parseProductDTOs(1);
        assertNotNull(fromApiProduct);
        assertEquals(0, fromApiProduct.length);
    }
    @Test
    public void noViewProduct() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(PRODUCT_VIEW));
        client.getProduct(NON_EXISTING_PRODUCT_ID);
        verifyPermissionAbsence(client, PRODUCT_VIEW, true);
        client.reset();

        client.getProductLocations(NON_EXISTING_PRODUCT_ID);
        verifyPermissionAbsence(client, PRODUCT_VIEW, true);
        client.reset();

        client.getDeviceCount(NON_EXISTING_ORG_ID);
        verifyPermissionAbsence(client, PRODUCT_VIEW, true);
        client.reset();

        client.getProducts(NON_EXISTING_ORG_ID);
        verifyPermissionAbsence(client, PRODUCT_VIEW, true);
    }

    @Test
    public void editProduct() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(PRODUCT_EDIT));
        client.updateProduct(NON_EXISTING_PRODUCT_ID, new Product());
        client.verifyResult(webJson(1, "Product name is empty."));
        client.reset();

        client.updateDevicesMeta(NON_EXISTING_ORG_ID, null);
        client.verifyResult(webJson(1, "Wrong create product command."));
    }
    @Test
    public void noEditProduct() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(PRODUCT_EDIT));
        client.updateProduct(NON_EXISTING_PRODUCT_ID, new Product());
        verifyPermissionAbsence(client, PRODUCT_EDIT, true);
        client.reset();

        client.updateDevicesMeta(NON_EXISTING_ORG_ID, null);
        verifyPermissionAbsence(client, PRODUCT_EDIT, true);
    }

    @Test
    public void deleteProduct() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(PRODUCT_DELETE));
        client.canDeleteProduct(NON_EXISTING_PRODUCT_ID);
        client.verifyResult(webJson(1, "Couldn't find organization for product with passed id " +
                NON_EXISTING_PRODUCT_ID + "."));
        client.reset();

        client.deleteProduct(NON_EXISTING_PRODUCT_ID);
        client.verifyResult(webJson(1, "Product with passed id " +
                NON_EXISTING_PRODUCT_ID + " not found."));
    }
    @Test
    public void noDeleteProduct() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(PRODUCT_DELETE));
        client.canDeleteProduct(NON_EXISTING_PRODUCT_ID);
        verifyPermissionAbsence(client, PRODUCT_DELETE, true);
        client.reset();

        client.deleteProduct(NON_EXISTING_PRODUCT_ID);
        verifyPermissionAbsence(client, PRODUCT_DELETE, true);
    }

    @Test
    public void createRole() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ROLE_CREATE));
        client.createRole(EXAMPLE_ROLE_DTO);
        RoleDTO roleDTO = client.parseRoleDTO(1);
        assertNotNull(roleDTO);
        assertEquals(5, roleDTO.id);
        assertEquals(EXAMPLE_ROLE_DTO.name, roleDTO.name);
        assertEquals(EXAMPLE_ROLE_DTO.permissionGroup1, roleDTO.permissionGroup1);
        assertEquals(EXAMPLE_ROLE_DTO.permissionGroup2, roleDTO.permissionGroup2);
    }
    @Test
    public void noCreateRole() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ROLE_CREATE));
        client.createRole(EXAMPLE_ROLE_DTO);
        verifyPermissionAbsence(client, ROLE_CREATE, true);
    }

    @Test
    public void viewRole() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ROLE_VIEW));
        client.getRole(NON_EXISTING_ROLE_ID);
        client.verifyResult(webJson(1, "Role with passed id not found."));
        client.reset();

        client.getRoles();
        RoleDTO[] roleDTOs = client.parseRoleDTOs(1);
        assertNotNull(roleDTOs);
    }
    @Test
    public void noViewRole() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ROLE_VIEW));
        client.getRole(NON_EXISTING_ROLE_ID);
        verifyPermissionAbsence(client, ROLE_VIEW, true);
        client.reset();

        client.getRoles();
        verifyPermissionAbsence(client, ROLE_VIEW, true);
    }

    @Test
    public void editRole() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ROLE_EDIT));
        client.updateRole(EXAMPLE_ROLE_DTO);
        client.verifyResult(webJson(1, "Cannot find role with passed id."));
    }
    @Test
    public void noEditRole() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ROLE_EDIT));
        client.updateRole(EXAMPLE_ROLE_DTO);
        verifyPermissionAbsence(client, ROLE_EDIT, true);
    }

    @Test
    public void deleteRole() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ROLE_DELETE));
        client.deleteRole(NON_EXISTING_ROLE_ID);
        client.verifyResult(webJson(1, "Cannot find role with passed id."));
    }
    @Test
    public void noDeleteRole() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ROLE_DELETE));
        client.deleteRole(NON_EXISTING_ROLE_ID);
        verifyPermissionAbsence(client, ROLE_DELETE, true);
    }

    @Test
    public void createOrgDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_DEVICES_CREATE, OWN_DEVICES_CREATE));
        client.getAccount();
        User user = client.parseAccount(1);
        client.createDevice(user.orgId, new Device());
        client.verifyResult(webJson(2, "Command has wrong product id."));
    }
    @Test
    public void noCreateOrgDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DEVICES_CREATE, OWN_DEVICES_CREATE));
        Device device = new Device();
        device.productId = 1;
        device.name = "123";
        device.boardType = BoardType.Generic_Board;
        client.createDevice(2, device);
        verifyPermissionAbsence(client, OWN_DEVICES_CREATE, true);
    }

    @Test
    public void getOrgDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_DEVICES_VIEW));
        client.getDevice(NON_EXISTING_ORG_ID, NON_EXISTING_DEVICE_ID);
        client.verifyResult(webJson(1, "Device not found."));
        client.reset();

        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(1);
        assertNotNull(devices);
        assertEquals(0, devices.length);
    }
    @Test
    public void noGetOrgDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DEVICES_VIEW, OWN_DEVICES_VIEW));
        client.getDevice(NON_EXISTING_ORG_ID, NON_EXISTING_DEVICE_ID);
        verifyPermissionAbsence(client, OWN_DEVICES_VIEW, true);
        client.reset();

        client.getDevices(NON_EXISTING_ORG_ID);
        verifyPermissionAbsence(client, OWN_DEVICES_VIEW, true);
        client.reset();
    }

    @Test
    public void updateOrgDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_DEVICES_EDIT));
        client.updateDevice(NON_EXISTING_ORG_ID, new Device());
        client.verifyResult(webJson(1, "Empty body or productId is wrong."));
        client.reset();

        client.updateDeviceMetafield(NON_EXISTING_DEVICE_ID, null);
        client.verifyResult(webJson(1, "Error parsing metafields batch."));
    }
    @Test
    public void noUpdateOrgDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DEVICES_EDIT, OWN_DEVICES_EDIT));
        client.updateDevice(NON_EXISTING_ORG_ID, new Device());
        verifyPermissionAbsence(client, OWN_DEVICES_EDIT, true);
        client.reset();

        client.updateDeviceMetafield(NON_EXISTING_DEVICE_ID, null);
        verifyPermissionAbsence(client, OWN_DEVICES_EDIT, true);
    }

    @Test
    public void deleteOrgDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_DEVICES_DELETE));
        client.deleteDevice(NON_EXISTING_ORG_ID, NON_EXISTING_DEVICE_ID);
        client.verifyResult(webJson(1, "Device not found."));
    }
    @Test
    public void noDeleteOrgDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DEVICES_DELETE));
        client.deleteDevice(orgId, 1);
        client.verifyResult(webJson(1, "User is not owner of requested device."));
    }

    // ORG_DEVICES_SHARE is not used anywhere

    // OWN_DEVICES_ permissions are only used with ORG_DEVICES_ permissions

    @Test
    public void createOwnDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OWN_DEVICES_CREATE));
        client.getAccount();
        User user = client.parseAccount(1);
        Device device = new Device();
        device.name = "!23";
        device.boardType = BoardType.Generic_Board;
        client.createDevice(user.orgId, new Device());
        client.verifyResult(webJson(2, "Command has wrong product id."));
    }
    @Test
    public void noCreateOwnDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DEVICES_CREATE, OWN_DEVICES_CREATE));
        client.createDevice(orgId, new Device());
        verifyPermissionAbsence(client, OWN_DEVICES_CREATE, true);
    }

    @Test
    public void getOwnDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OWN_DEVICES_VIEW));
        client.getDevice(NON_EXISTING_ORG_ID, NON_EXISTING_DEVICE_ID);
        client.verifyResult(webJson(1, "Device not found."));
        client.reset();

        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(1);
        assertNotNull(devices);
        assertEquals(0, devices.length);
    }
    @Test
    public void noGetOwnDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DEVICES_VIEW, OWN_DEVICES_VIEW));
        client.getDevices(NON_EXISTING_ORG_ID);
        verifyPermissionAbsence(client, OWN_DEVICES_VIEW, true);
    }

    @Test
    public void updateOwnDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OWN_DEVICES_EDIT));
        client.updateDevice(NON_EXISTING_ORG_ID, new Device());
        client.verifyResult(webJson(1, "Empty body or productId is wrong."));
        client.reset();

        client.updateDeviceMetafield(NON_EXISTING_DEVICE_ID, null);
        client.verifyResult(webJson(1, "Error parsing metafields batch."));
    }
    @Test
    public void noUpdateOwnDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DEVICES_EDIT, OWN_DEVICES_EDIT));
        client.updateDevice(NON_EXISTING_ORG_ID, new Device());
        verifyPermissionAbsence(client, OWN_DEVICES_EDIT, true);
        client.reset();

        client.updateDeviceMetafield(NON_EXISTING_DEVICE_ID, null);
        verifyPermissionAbsence(client, OWN_DEVICES_EDIT, true);
    }

    @Test
    public void deleteOwnDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OWN_DEVICES_DELETE));
        client.deleteDevice(NON_EXISTING_ORG_ID, NON_EXISTING_DEVICE_ID);
        client.verifyResult(webJson(1, "Device not found."));
    }
    @Test
    public void noDeleteOwnDevices() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_DEVICES_DELETE, OWN_DEVICES_DELETE));
        client.deleteDevice(NON_EXISTING_ORG_ID, NON_EXISTING_DEVICE_ID);
        verifyPermissionAbsence(client, OWN_DEVICES_DELETE, true);
    }

    // OWN_DEVICES_SHARE is not used anywhere

    @Test
    public void setAuthToken() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(SET_AUTH_TOKEN));
        client.setAuthToken(NON_EXISTING_DEVICE_ID, "token");
        client.verifyResult(webJson(1, "Set auth token is not valid. Token is empty or length is not 32 chars."));
    }
    @Test
    public void noSetAuthToken() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(SET_AUTH_TOKEN));
        client.setAuthToken(NON_EXISTING_DEVICE_ID, "token");
        verifyPermissionAbsence(client, SET_AUTH_TOKEN, true);
    }

    @Test
    public void getRuleGroup() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(-1, setPermission(RULE_GROUP_VIEW));
        client.getRuleGroup();
        RuleGroup ruleGroup = client.parseRuleGroup(1);
        assertNull(ruleGroup);
    }
    @Test
    public void noGetRuleGroup() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(-1, removePermission(RULE_GROUP_VIEW));
        client.getRuleGroup();
        verifyPermissionAbsence(client, RULE_GROUP_VIEW, false);
    }

    @Test
    public void editRuleGroup() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(-1, setPermission(RULE_GROUP_EDIT));
        client.editRuleGroup("{}");
        client.verifyResult(ok(1));
    }
    @Test
    public void noEditRuleGroup() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(-1, removePermission(RULE_GROUP_EDIT));
        client.editRuleGroup("");
        verifyPermissionAbsence(client, RULE_GROUP_EDIT, false);
    }

    @Test
    public void deleteOwnDeviceData() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(-1, setPermission(OWN_DEVICE_DATA_DELETE));

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(EXAMPLE_USER_EMAIL, "1");
        appClient.verifyResult(ok(appClient.getMsgId()));

        appClient.deleteDeviceData(4, 1);
        appClient.verifyResult(webJson(appClient.getMsgId(), "Device not found."));
    }
    @Test
    // delete device data having user that does not own that device
    public void deleteNotOwnDeviceData() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(-1, setPermission(OWN_DEVICE_DATA_DELETE));

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(EXAMPLE_USER_EMAIL, "1");
        appClient.verifyResult(ok(appClient.getMsgId()));


        appClient.deleteDeviceData(1, 1);
        appClient.verifyResult(webJson(appClient.getMsgId(), "User is not owner of requested device."));
    }

    @Test
    public void deleteDeviceDataWith2Permissions() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(-1, setPermission(ORG_DEVICE_DATA_DELETE, OWN_DEVICE_DATA_DELETE));

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(EXAMPLE_USER_EMAIL, "1");
        appClient.verifyResult(ok(appClient.getMsgId()));


        appClient.deleteDeviceData(NON_EXISTING_DASH_ID, NON_EXISTING_DEVICE_ID);
        appClient.verifyResult(webJson(appClient.getMsgId(), "Device not found."));
    }
    @Test
    public void noDeleteDeviceDataWithout2Permissions() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(-1, removePermission(ORG_DEVICE_DATA_DELETE, OWN_DEVICE_DATA_DELETE));

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(EXAMPLE_USER_EMAIL, "1");
        appClient.verifyResult(ok(appClient.getMsgId()));

        appClient.deleteDeviceData(NON_EXISTING_DASH_ID, 1);
        verifyPermissionAbsence(appClient, OWN_DEVICE_DATA_DELETE, false);
    }
}
