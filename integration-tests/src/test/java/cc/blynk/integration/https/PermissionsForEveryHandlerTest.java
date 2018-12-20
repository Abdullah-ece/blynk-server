package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.api.http.dashboard.dto.RoleDTO;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.dto.OtaDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
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
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_SWITCH;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_START;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_STOP;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_ORG_EDIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    private static int removePermission(int exclude) {
        int all = 0b11111111111111111111111111111111;
        return all ^ exclude;
    }

    private AppWebSocketClient createUserForSubOrgSpecificRole(int permissions1) throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Organization subOrg = new Organization("PermissionTestSuborg", "Europe/Kiev", null, true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(1);
        assertNotNull(subOrgDTO);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(2));
        client.createRole(new RoleDTO(-1, "Test", permissions1, 0));
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
        TestCase.assertNotNull(user);
        assertEquals("testpermissions@gmail.com", user.email);
        assertEquals("Dmitriy", user.name);
        assertEquals(4, user.roleId);
        assertEquals(subOrgDTO.id, user.orgId);

        appWebSocketClient.reset();
        return appWebSocketClient;
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
        client.verifyResult(webJson(1, "User testpermissions@gmail.com has no permission for 'switch organization' operation."));
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
        client.verifyResult(webJson(1, "User testpermissions@gmail.com has no permission for 'edit own organization' operation."));
    }

    @Test
    public void OTAView() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OTA_VIEW));
        client.getOTAInfo(null);
        client.verifyResult(webJson(1, "Path to firmware is not provided."));
    }
    @Test
    public void noOTAView() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(OTA_VIEW));
        client.getOTAInfo(null);
        client.verifyResult(webJson(1, "User testpermissions@gmail.com has no permission for 'view ota' operation."));
    }

    @Test
    public void OTAStart() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OTA_START));
        client.otaStart(new OtaDTO(1, 1, null, null, null, null, false, null, 0, false));
        client.verifyResult(webJson(1, "Wrong data for OTA start."));
    }
    @Test
    public void noOTAStart() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(OTA_START));
        client.otaStart(new OtaDTO(1, 1, null, null, null, null, false, null, 0, false));
        client.verifyResult(webJson(1, "User testpermissions@gmail.com has no permission for 'start ota' operation."));
    }

    @Test
    public void OTAStop() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(OTA_STOP));
        client.otaStop(new OtaDTO(1, 1, null, null, null, null, false, null, 0, false));
        client.verifyResult(webJson(1, "No devices to stop OTA."));
    }
    @Test
    public void noOTAStop() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(OTA_STOP));
        client.otaStop(new OtaDTO(1, 1, null, null, null, null, false, null, 0, false));
        client.verifyResult(webJson(1, "User testpermissions@gmail.com has no permission for 'stop ota' operation."));
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
        client.verifyResult(webJson(1, "User testpermissions@gmail.com has no permission for 'create organization' operation."));
    }

    @Test
    public void orgGet() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(setPermission(ORG_CREATE));
        client.createOrganization(new Organization());
        client.verifyResult(webJson(1, "Organization is empty."));
    }
    @Test
    public void noOrgGet() throws Exception {
        AppWebSocketClient client = createUserForSubOrgSpecificRole(removePermission(ORG_CREATE));
        client.createOrganization(new Organization());
        client.verifyResult(webJson(1, "User testpermissions@gmail.com has no permission for 'create organization' operation."));
    }
}
