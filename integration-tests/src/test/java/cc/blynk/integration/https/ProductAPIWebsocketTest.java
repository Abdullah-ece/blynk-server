package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDB;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.utils.SHA256Util;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.illegalCommand;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.utils.AppNameUtil.BLYNK;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductAPIWebsocketTest extends SingleServerInstancePerTestWithDB {

    @Before
    public void intiAdmin() {
        String name = "admin@blynk.cc";
        String pass = "admin";
        User admin = new User(name, SHA256Util.makeHash(pass, name), BLYNK, "local", "127.0.0.1", false, Role.SUPER_ADMIN);
        admin.profile.dashBoards = new DashBoard[] {
                new DashBoard()
        };
        admin.status = UserStatus.Active;
        holder.userDao.add(admin);
        holder.organizationDao.create(new Organization("Blynk Inc.", "Europe/Kiev", "/static/logo.png", true));
    }

    @Test
    public void getNonExistingProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getProduct(1333);
        client.verifyResult(illegalCommand(1));
    }


}
