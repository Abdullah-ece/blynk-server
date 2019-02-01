package cc.blynk.server.workers;

import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.db.DBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

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

    public ProfileSaverWorker(UserDao userDao, FileManager fileManager,
                              DBManager dbManager, OrganizationDao organizationDao) {
        this.userDao = userDao;
        this.fileManager = fileManager;
        this.dbManager = dbManager;
        this.organizationDao = organizationDao;
        this.lastStart = System.currentTimeMillis();
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

        List<Organization> orgs = saveModifiedOrgs(isFancy);

        dbManager.saveOrganizations(orgs);

        log.debug("Saving organization db finished. Modified {} organizations.", orgs.size());
    }

    private void saveUsers(long now, boolean isFancy) {
        log.debug("Starting saving user db.");

        ArrayList<User> users = saveModifiedUsers(isFancy);

        dbManager.saveUsers(users);

        log.debug("Saving user db finished. Modified {} users.", users.size());
    }

    private List<Organization> saveModifiedOrgs(boolean isFancy) {
        var orgs = new ArrayList<Organization>();

        for (Organization org : organizationDao.organizations.values()) {
            if (org.isUpdatedSince(lastStart)) {
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
