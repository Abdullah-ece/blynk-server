package cc.blynk.server.core.model.auth;

import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.serialization.View;
import cc.blynk.server.core.model.web.UserInviteDTO;
import cc.blynk.server.core.processors.NotificationBase;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Objects;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 4:03 PM
 */
public class User {

    private static final int INITIAL_ENERGY_AMOUNT = Integer.parseInt(System.getProperty("initial.energy", "2000"));

    @JsonView(View.WebUser.class)
    public volatile String name;

    public volatile String pass;

    @JsonView(View.WebUser.class)
    public int roleId;

    //key fields
    @JsonView(View.WebUser.class)
    public String email;

    public String region;
    public String ip;

    @JsonView(View.WebUser.class)
    public int orgId;

    @JsonView(View.WebUser.class)
    public UserStatus status;

    //used mostly to understand if user profile was changed,
    // all other fields update ignored as it is not so important
    public volatile long lastModifiedTs;

    public String lastLoggedIP;
    public long lastLoggedAt;

    public Profile profile;

    public boolean isFacebookUser;

    public volatile int energy;

    public transient int emailMessages;
    private transient long emailSentTs;

    //used just for tests and serialization
    public User() {
        this.lastModifiedTs = System.currentTimeMillis();
        this.profile = new Profile();
        //todo this should be changed
        this.energy = INITIAL_ENERGY_AMOUNT;
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
                Profile profile, int energy) {
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
        this.energy = energy;
    }

    @JsonProperty("id")
    private String id() {
        return email + "-" + orgId;
    }

    public boolean notEnoughEnergy(int price) {
        return price > energy && orgId == OrganizationDao.DEFAULT_ORGANIZATION_ID;
    }

    public boolean hasAccess(int orgId) {
        return isSuperAdmin() || this.orgId == orgId;
    }

    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    public void subtractEnergy(int price) {
        //non-atomic. we are fine with that, always updated from 1 thread
        this.energy -= price;
    }

    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    public void addEnergy(int price) {
        //non-atomic. we are fine with that, always updated from 1 thread
        this.energy += price;
        this.lastModifiedTs = System.currentTimeMillis();
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

    public void setName(String name) {
        this.name = name;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void setRole(int roleId) {
        this.roleId = roleId;
        this.lastModifiedTs = System.currentTimeMillis();
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
            for (Device device : dashBoard.devices) {
                if (lastStart <= device.metadataUpdatedAt
                        || lastStart <= device.dataReceivedAt
                        || lastStart <= device.updatedAt) {
                    return true;
                }
            }
        }
        return false;
    }

    public void update(UserInviteDTO userInviteDTO) {
        this.name = userInviteDTO.name;
        this.roleId = userInviteDTO.roleId;
        this.lastModifiedTs = System.currentTimeMillis();
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
        return orgId == user.orgId
                && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, orgId);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

}
