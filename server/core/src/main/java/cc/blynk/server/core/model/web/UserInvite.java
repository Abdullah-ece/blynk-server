package cc.blynk.server.core.model.web;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.04.17.
 */
public class UserInvite {

    public int orgId;

    public String email;

    public String name;

    public Role role;

    public UserInvite() {
    }

    public UserInvite(int orgId, String email, String name,  Role role) {
        this.orgId = orgId;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public boolean isNotValid() {
        return orgId == 0 || email == null || email.isEmpty() || role == null;
    }
}
