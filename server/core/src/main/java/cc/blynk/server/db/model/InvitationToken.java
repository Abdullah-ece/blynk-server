package cc.blynk.server.db.model;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.UserInviteDTO;

import java.util.Date;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 03.03.16.
 */
public class InvitationToken {

    public final String token;

    public final String email;

    public final String name;

    public final Role role;

    public final boolean isActivated;

    public final Date createdTs;

    public final Date activatedTs;

    public InvitationToken(String token, UserInviteDTO invite) {
        this.token = token;
        this.email = invite.email;
        this.name = invite.name;
        this.role = invite.role;
        this.isActivated = false;
        this.createdTs = null;
        this.activatedTs = null;
    }

    public InvitationToken(String token, String email, String name, Role role,
                           boolean isActivated, Date createdTs, Date activatedTs) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
        this.isActivated = isActivated;
        this.createdTs = createdTs;
        this.activatedTs = activatedTs;
    }

}
