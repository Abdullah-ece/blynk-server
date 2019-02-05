package cc.blynk.server.core.model.auth;

import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.profile.Profile;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.serialization.View;
import cc.blynk.server.core.model.web.UserInviteDTO;
import cc.blynk.server.core.processors.NotificationBase;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 4:03 PM
 */
public class User {

    @JsonView(View.WebUser.class)
    public volatile String name;

    public volatile String pass;

    @JsonView(View.WebUser.class)
    public volatile int roleId;

    //key fields
    @JsonView(View.WebUser.class)
    public String email;

    public String region;
    public String ip;

    @JsonView(View.WebUser.class)
    public int orgId;

    @JsonView(View.WebUser.class)
    public volatile UserStatus status;

    //used mostly to understand if user profile was changed,
    // all other fields update ignored as it is not so important
    public volatile long lastModifiedTs;

    public String lastLoggedIP;
    public long lastLoggedAt;

    public Profile profile;

    public boolean isFacebookUser;

    public transient int emailMessages;
    private transient long emailSentTs;

    //used just for tests and serialization
    public User() {
        this.lastModifiedTs = System.currentTimeMillis();
        this.profile = new Profile();
        //todo this should be changed
        this.isFacebookUser = false;
        this.orgId = OrganizationDao.DEFAULT_ORGANIZATION_ID;
    }

    public User(String email, String passHash, int orgId, String region, String ip,
                boolean isFacebookUser, int roleId) {
        this();
        this.email = email;
        this.name = email;
        this.pass = passHash;
        this.orgId = orgId;
        this.region = region;
        this.ip = ip;
        this.isFacebookUser = isFacebookUser;
        this.roleId = roleId;
    }

    //used when user is fully read from DB
    public User(String email, String passHash, int orgId, String region, String ip,
                boolean isFacebookUser, int roleId, String name,
                long lastModifiedTs, long lastLoggedAt, String lastLoggedIP,
                Profile profile) {
        this.email = email;
        this.name = email;
        this.pass = passHash;
        this.orgId = orgId;
        this.region = region;
        this.ip = ip;
        this.isFacebookUser = isFacebookUser;
        this.roleId = roleId;
        this.name = name;
        this.lastModifiedTs = lastModifiedTs;
        this.lastLoggedAt = lastLoggedAt;
        this.lastLoggedIP = lastLoggedIP;
        this.profile = profile;
    }

    @JsonProperty("id")
    private String id() {
        return email + "-" + orgId;
    }

    public boolean hasAccess(int orgId) {
        return isSuperAdmin() || this.orgId == orgId;
    }

    private static final int EMAIL_DAY_LIMIT = 100;
    private static final long MILLIS_IN_DAY = 24 * 60 * 60 * 1000;

    public void checkDailyEmailLimit() {
        long now = System.currentTimeMillis();
        if (now - emailSentTs < MILLIS_IN_DAY) {
            if (emailMessages > EMAIL_DAY_LIMIT) {
                throw NotificationBase.EXCEPTION_CACHE;
            }
        } else {
            this.emailMessages = 0;
            this.emailSentTs = now;
        }
    }

    public boolean isSuperAdmin() {
        return roleId == Role.SUPER_ADMIN_ROLE_ID;
    }

    public boolean isUpdated(long lastStart) {
        return (lastStart <= lastModifiedTs) || isDashUpdated(lastStart);
    }

    public void resetPass(String hash) {
        this.pass = hash;
        this.status = UserStatus.Active;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    private boolean isDashUpdated(long lastStart) {
        for (DashBoard dashBoard : profile.dashBoards) {
            if (lastStart <= dashBoard.updatedAt) {
                return true;
            }
        }
        return false;
    }

    public void update(UserInviteDTO userInviteDTO) {
        this.name = userInviteDTO.name;
        this.roleId = userInviteDTO.roleId;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void deleteDevice(int... deviceIds) {
        for (int deviceId : deviceIds) {
            deleteDevice(deviceId);
        }
        this.lastModifiedTs = System.currentTimeMillis();
    }

    private void deleteDevice(int deviceId) {
        for (DashBoard dash : profile.dashBoards) {
            dash.eraseWidgetValuesForDevice(deviceId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (orgId != user.orgId) {
            return false;
        }
        return email != null ? email.equals(user.email) : user.email == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + orgId;
        return result;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

}
