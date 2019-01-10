package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.UserInviteDTO;
import cc.blynk.server.workers.timer.TimerWorker;
import cc.blynk.utils.AppNameUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper class for holding info regarding registered users and profiles.
 *
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 4:02 PM
 */
public class UserDao {

    private static final Logger log = LogManager.getLogger(UserDao.class);

    public final ConcurrentMap<String, User> users;
    private final String region;
    private final String host;

    public UserDao(ConcurrentMap<String, User> users, String region, String host) {
        //reading DB to RAM.
        this.users = users;
        this.region = region;
        this.host = host;
        log.info("Region : {}. Host : {}.", region, host);
    }

    public boolean isUserExists(String name) {
        return users.get(name) != null;
    }

    public boolean isSuperAdminExists() {
        User user = getSuperAdmin();
        return user != null;
    }

    public User getSuperAdmin() {
        for (User user : users.values()) {
            if (user.isSuperAdmin()) {
                return user;
            }
        }
        return null;
    }

    public User getByName(String email) {
        return users.get(email);
    }

    public boolean contains(String email) {
        return users.containsKey(email);
    }

    //for tests only
    public Map<String, User> getUsers() {
        return users;
    }

    public List<User> getUsersByOrgId(int orgId, String filterMail) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.orgId == orgId && !user.isSuperAdmin() && !user.email.equals(filterMail)) {
                result.add(user);
            }
        }
        return result;
    }

    public List<User> getAllUsersByOrgId(int orgId) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.orgId == orgId) {
                result.add(user);
            }
        }
        return result;
    }

    public User delete(String email) {
        return users.remove(email);
    }

    public void add(User user) {
        users.put(user.email, user);
    }

    public void createProjectForExportedApp(TimerWorker timerWorker,
                                            User newUser, String appName) {
        if (appName.equals(AppNameUtil.BLYNK)) {
            return;
        }

        User parentUser = null;
        App app = null;

        for (User user : users.values()) {
            app = user.profile.getAppById(appName);
            if (app != null) {
                parentUser = user;
                break;
            }
        }

        if (app == null) {
            log.error("Unable to find app with id {}", appName);
            return;
        }

        if (app.isMultiFace) {
            log.info("App supports multi faces. Skipping profile creation.");
            return;
        }

        int dashId = app.projectIds[0];
        DashBoard dash = parentUser.profile.getDashByIdOrThrow(dashId);

        //todo ugly, but quick. refactor
        DashBoard clonedDash = JsonParser.parseDashboard(JsonParser.toJsonRestrictiveDashboard(dash), 1);

        clonedDash.id = 1;
        clonedDash.parentId = dash.parentId;
        clonedDash.createdAt = System.currentTimeMillis();
        clonedDash.updatedAt = clonedDash.createdAt;
        clonedDash.isActive = true;
        clonedDash.eraseWidgetValues();

        clonedDash.addTimers(timerWorker, newUser.orgId, newUser.email);

        newUser.profile.dashBoards = new DashBoard[] {clonedDash};
        newUser.status = UserStatus.Active;
    }

    public User addFacebookUser(String email, int orgId) {
        log.debug("Adding new facebook user {}. OrgId : {}", email, orgId);
        //todo add default role instead of hardcoded
        User newUser = new User(email, null, orgId, region, host, true, 2);
        newUser.status = UserStatus.Active;
        add(newUser);
        return newUser;
    }

    public User add(String email, String passHash, int orgId, int roleId) {
        log.debug("Adding new user {}. OrgId : {}", email, orgId);
        User newUser = new User(email, passHash, orgId, region, host, false, roleId);
        newUser.status = UserStatus.Active;
        add(newUser);
        return newUser;
    }

    public User invite(UserInviteDTO invite, int orgId) {
        User existingUser = users.get(invite.email);

        //do not allow to invite signed up user
        if (existingUser != null && existingUser.status == UserStatus.Active) {
            return null;
        }

        User newUser = new User(invite.email, null, orgId, region, host, false, invite.roleId);
        newUser.name = invite.name;
        newUser.status = UserStatus.Pending;
        add(newUser);
        return newUser;
    }

    //todo for now we take first app name from superadmin
    public String getAppName() {
        User superAdmin = getSuperAdmin();
        if (superAdmin.profile.apps.length == 0) {
            return AppNameUtil.BLYNK;
        }
        return superAdmin.profile.apps[0].id;
    }

}
