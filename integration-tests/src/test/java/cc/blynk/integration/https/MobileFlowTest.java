package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.server.core.model.profile.NotificationSettings;
import cc.blynk.server.core.model.profile.Profile;
import cc.blynk.server.core.model.profile.ProfileSettings;
import cc.blynk.server.notifications.push.enums.Priority;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ConcurrentHashMap;

import static cc.blynk.integration.TestUtil.ok;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.10.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class MobileFlowTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void testProfileSettingsUpdated() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        ProfileSettings profileSettings = new ProfileSettings();
        ConcurrentHashMap<String, String> tokensMap = new ConcurrentHashMap<>();
        tokensMap.put("123", "1234");
        profileSettings.notificationSettings = new NotificationSettings(tokensMap, tokensMap, true, -1, Priority.normal);
        appClient.editProfileSettings(profileSettings);
        appClient.verifyResult(ok(2));

        appClient.loadProfileGzipped();
        Profile profile = appClient.parseProfile(3);

        profileSettings = profile.settings;
        assertNotNull(profileSettings);
        assertNotNull(profileSettings.notificationSettings);
        NotificationSettings notificationSettings = profileSettings.notificationSettings;
        assertNotNull(notificationSettings);
        assertTrue(notificationSettings.notifyWhenOffline);
        assertEquals(-1, notificationSettings.notifyWhenOfflineIgnorePeriod);
        assertEquals(Priority.normal, notificationSettings.priority);
        assertEquals(0, notificationSettings.androidTokens.size());
        assertEquals(0, notificationSettings.iOSTokens.size());
    }

    @Test
    public void testProfileSettingsTokenNotUpdated() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        appClient.addPushToken("android", "token");
        appClient.verifyResult(ok(2));

        ProfileSettings profileSettings = new ProfileSettings();
        ConcurrentHashMap<String, String> tokensMap = new ConcurrentHashMap<>();
        tokensMap.put("123", "1234");
        profileSettings.notificationSettings = new NotificationSettings(tokensMap, tokensMap, true, -1, Priority.normal);
        appClient.editProfileSettings(profileSettings);
        appClient.verifyResult(ok(3));

        appClient.loadProfileGzipped();
        Profile profile = appClient.parseProfile(4);

        profileSettings = profile.settings;
        assertNotNull(profileSettings);
        assertNotNull(profileSettings.notificationSettings);
        NotificationSettings notificationSettings = profileSettings.notificationSettings;
        assertNotNull(notificationSettings);
        assertTrue(notificationSettings.notifyWhenOffline);
        assertEquals(-1, notificationSettings.notifyWhenOfflineIgnorePeriod);
        assertEquals(Priority.normal, notificationSettings.priority);
        assertEquals(1, notificationSettings.androidTokens.size());
        assertEquals("token", notificationSettings.androidTokens.get("android"));
        assertEquals(0, notificationSettings.iOSTokens.size());
    }

}
