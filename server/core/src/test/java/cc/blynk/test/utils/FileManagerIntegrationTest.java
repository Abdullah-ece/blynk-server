package cc.blynk.test.utils;

import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.model.auth.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 8:07
 */
public class FileManagerIntegrationTest {

    private final User user1 = new User("name1", "pass1", 1, "local", "127.0.0.1", false, 1);
    private final User user2 = new User("name2", "pass2", 1, "local", "127.0.0.1", false, 1);

    private FileManager fileManager;

    @Before
    public void cleanup() throws IOException {
        String dataFolder = Paths.get(System.getProperty("java.io.tmpdir"), "blynk").toString();
        org.apache.commons.io.FileUtils.deleteDirectory(Paths.get(dataFolder).toFile());
        fileManager = new FileManager(dataFolder, null);
    }

    @Test
    public void testGenerateFileName() {
        Path file = fileManager.generateFileName(user1.email);
        assertEquals("name1.user", file.getFileName().toString());
    }

    @Test
    public void testNotNullTokenManager() throws IOException {
        fileManager.overrideUserFile(user1, false);

        Map<String, User> users = fileManager.deserializeUsers();
        assertNotNull(users);
        assertNotNull(users.get(user1.email));
    }

    @Test
    public void testCreationTempFile() throws IOException {
        fileManager.overrideUserFile(user1, false);
        //file existence ignored
        fileManager.overrideUserFile(user1, false);
    }

    @Test
    public void testReadListOfFiles() throws IOException {
        fileManager.overrideUserFile(user1, false);
        fileManager.overrideUserFile(user2, false);
        Path fakeFile = Paths.get(fileManager.getDataDir().toString(), "123.txt");
        Files.deleteIfExists(fakeFile);
        Files.createFile(fakeFile);

        Map<String, User> users = fileManager.deserializeUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertNotNull(users.get(user1.email));
        assertNotNull(users.get(user2.email));
    }

    @Test
    public void testOverrideFiles() throws IOException {
        fileManager.overrideUserFile(user1, false);
        fileManager.overrideUserFile(user1, false);

        Map<String, User> users = fileManager.deserializeUsers();
        assertNotNull(users);
        assertNotNull(users.get(user1.email));
    }

}
