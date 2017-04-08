package cc.blynk.server.reset;

import cc.blynk.server.core.dao.TokensPool;
import cc.blynk.server.core.model.AppName;
import cc.blynk.server.core.model.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TokensPoolTest {

    private static final int expirationPeriod = 60 * 60 * 1000;

    @Test
    public void addTokenTest() {
        final User user = new User();
        user.name = "test.gmail.com";
        user.appName = AppName.BLYNK;
        final String token = "123";
        final TokensPool tokensPool = new TokensPool(expirationPeriod);
        tokensPool.addToken(token, user);
        assertEquals(user, tokensPool.getUser(token));
    }

    @Test
    public void addTokenTwiceTest() {
        final User user = new User();
        user.name = "test.gmail.com";
        user.appName = AppName.BLYNK;
        final String token = "123";
        final TokensPool tokensPool = new TokensPool(expirationPeriod);
        tokensPool.addToken(token, user);
        tokensPool.addToken(token, user);
        assertEquals(1, tokensPool.size());
    }

    @Test
    public void remoteTokenTest() {
        final User user = new User();
        user.name = "test.gmail.com";
        user.appName = AppName.BLYNK;
        final String token = "123";
        final TokensPool tokensPool = new TokensPool(expirationPeriod);
        tokensPool.addToken(token, user);
        assertEquals(user, tokensPool.getUser(token));
        tokensPool.removeToken(token);
        assertEquals(0, tokensPool.size());
        assertNull(tokensPool.getUser(token));
    }
}
