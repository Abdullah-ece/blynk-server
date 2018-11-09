package cc.blynk.server.workers;

import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.db.DBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Background thread that once a minute stores all user DB to disk in case profile was changed since last saving.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/12/2015.
 */
public class ProfileSaverWorker implements Runnable, Closeable {

    private static final Logger log = LogManager.getLogger(ProfileSaverWorker.class);

    //1 min
    private final UserDao userDao;
    private final FileManager fileManager;
    private final DBManager dbManager;
    private final OrganizationDao organizationDao;
    private long lastStart;
    private long backupTs;

    public ProfileSaverWorker(UserDao userDao, FileManager fileManager,
                              DBManager dbManager, OrganizationDao organizationDao) {
        this.userDao = userDao;
        this.fileManager = fileManager;
        this.dbManager = dbManager;
        this.organizationDao = organizationDao;
        this.lastStart = System.currentTimeMillis();
        this.backupTs = 0;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        //todo change in production, via property later?
        boolean isFancy = true;
        try {
            saveOrgs(isFancy);
        } catch (Throwable t) {
            log.error("Error saving organizations.", t);
        }
        try {
            saveUsers(now, isFancy);
        } catch (Throwable t) {
            log.error("Error saving users.", t);
        }
        lastStart = now;
    }

    private void saveOrgs(boolean isFancy) {
        log.debug("Starting saving organization db.");
        ArrayList<Organization> orgs = saveModifiedOrgs(isFancy);
        log.debug("Saving organization db finished. Modified {} organizations.", orgs.size());

    }

    private void saveUsers(long now, boolean isFancy) {
        log.debug("Starting saving user db.");

        ArrayList<User> users = saveModifiedUsers(isFancy);

        dbManager.saveUsers(users);

        //backup only for local mode
        if (!dbManager.isDBEnabled() && users.size() > 0) {
            archiveUser(now);
        }

        log.debug("Saving user db finished. Modified {} users.", users.size());
    }

    private void archiveUser(long now) {
        if (now - backupTs > 86_400_000) {
            //it is time for backup, once per day.
            backupTs = now;
            for (User user : userDao.users.values()) {
                try {
                    Path path = fileManager.generateBackupFileName(user.email, user.orgId);
                    JsonParser.writeUser(path.toFile(), user, false);
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    private ArrayList<Organization> saveModifiedOrgs(boolean isFancy) {
        ArrayList<Organization> orgs = new ArrayList<>();

        for (Organization org : organizationDao.organizations.values()) {
            if (org.isUpdated(lastStart)) {
                try {
                    fileManager.override(org, isFancy);
                    orgs.add(org);
                } catch (Exception e) {
                    log.error("Error saving : {}.", org);
                }
            }
        }

        return orgs;
    }

    private ArrayList<User> saveModifiedUsers(boolean isFancy) {
        var users = new ArrayList<User>();

        for (User user : userDao.getUsers().values()) {
            if (user.isUpdated(lastStart)) {
                try {
                    fileManager.overrideUserFile(user, isFancy);
                    users.add(user);
                } catch (Exception e) {
                    log.error("Error saving : {}.", user);
                }
            }
        }

        return users;
    }

    @Override
    public void close() {
        run();
    }
}
