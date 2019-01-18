package cc.blynk.server.workers;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.db.DBManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/3/2015.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ProfileSaverWorkerTest {

    @Mock
    private UserDao userDao;

    @Mock
    private FileManager fileManager;

    @Mock
    private OrganizationDao organizationDao;

    private BlockingIOProcessor blockingIOProcessor = new BlockingIOProcessor(4, 1);

    @Test
    public void testCorrectProfilesAreSaved() throws IOException {
        ProfileSaverWorker profileSaverWorker = new ProfileSaverWorker(userDao, fileManager, new DBManager(blockingIOProcessor), organizationDao);

        User user1 = new User("1", "", 1, "local", "127.0.0.1", false, 2);
        User user2 = new User("2", "", 1, "local", "127.0.0.1", false, 2);
        User user3 = new User("3", "", 1, "local", "127.0.0.1", false, 2);
        User user4 = new User("4", "", 1, "local", "127.0.0.1", false, 2);

        ConcurrentMap<String, User> userMap = new ConcurrentHashMap<>();
        userMap.put(user1.email, user1);
        userMap.put(user2.email, user2);
        userMap.put(user3.email, user3);
        userMap.put(user4.email, user4);

        when(userDao.getUsers()).thenReturn(userMap);
        profileSaverWorker.run();

        verify(fileManager, times(4)).overrideUserFile(any(User.class), eq(true));
        verify(fileManager).overrideUserFile(user1, true);
        verify(fileManager).overrideUserFile(user2, true);
        verify(fileManager).overrideUserFile(user3, true);
        verify(fileManager).overrideUserFile(user4, true);
    }

    @Test
    public void testNoProfileChanges() throws Exception {
        User user1 = new User("1", "", 1, "local", "127.0.0.1", false, 2);
        User user2 = new User("2", "", 1, "local", "127.0.0.1", false, 2);
        User user3 = new User("3", "", 1, "local", "127.0.0.1", false, 2);
        User user4 = new User("4", "", 1, "local", "127.0.0.1", false, 2);

        Map<String, User> userMap = new HashMap<>();
        userMap.put("1", user1);
        userMap.put("2", user2);
        userMap.put("3", user3);
        userMap.put("4", user4);

        Thread.sleep(1);

        ProfileSaverWorker profileSaverWorker = new ProfileSaverWorker(userDao, fileManager, new DBManager(blockingIOProcessor), organizationDao);

        when(userDao.getUsers()).thenReturn(userMap);
        profileSaverWorker.run();

        verifyNoMoreInteractions(fileManager);
    }

}
