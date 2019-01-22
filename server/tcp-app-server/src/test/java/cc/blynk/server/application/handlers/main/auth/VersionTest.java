package cc.blynk.server.application.handlers.main.auth;

import cc.blynk.server.core.session.mobile.OsType;
import cc.blynk.server.core.session.mobile.Version;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.17.
 */
public class VersionTest {

    @Test
    public void testCorrectVersion() {
        Version version = new Version("iOS", "1.2.3");
        assertEquals(OsType.IOS, version.osType);
        assertEquals(10203, version.versionSingleNumber);
    }

    @Test
    public void wrongValues() {
        Version version = new Version("iOS", "RC13");
        assertEquals(OsType.IOS, version.osType);
        assertEquals(0, version.versionSingleNumber);
    }

    @Test
    public void wrongValues2() {
        assertEquals(OsType.OTHER, Version.UNKNOWN_VERSION.osType);
        assertEquals(0, Version.UNKNOWN_VERSION.versionSingleNumber);
    }

}
