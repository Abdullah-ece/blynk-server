package cc.blynk.server.internal;

import cc.blynk.server.internal.token.BaseToken;
import cc.blynk.server.internal.token.InviteToken;
import cc.blynk.server.internal.token.ResetPassToken;
import cc.blynk.server.internal.token.TokensPool;
import cc.blynk.utils.TokenGeneratorUtil;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SerializationTokenPoolTest {

    @Test
    public void someTEst() {
        String path = System.getProperty("java.io.tmpdir");
        TokensPool tokensPool = new TokensPool(path);

        String token = TokenGeneratorUtil.generateNewToken();
        ResetPassToken resetPassToken = new ResetPassToken("dima@mail.us", "Blynk");
        tokensPool.addToken(token, resetPassToken);

        String token2 = TokenGeneratorUtil.generateNewToken();
        InviteToken inviteToken = new InviteToken("dima2@mail.us", "Blynk2");
        tokensPool.addToken(token2, inviteToken);

        tokensPool.close();

        TokensPool tokensPool2 = new TokensPool(path);
        ConcurrentHashMap<String, BaseToken> tokens = tokensPool2.getTokens();
        assertNotNull(tokens);
        assertEquals(2, tokens.size());

        resetPassToken = (ResetPassToken) tokens.get(token);
        assertNotNull(resetPassToken);
        assertEquals("dima@mail.us", resetPassToken.email);
        assertEquals("Blynk", resetPassToken.appName);

        inviteToken = (InviteToken) tokens.get(token2);
        assertNotNull(inviteToken);
        assertEquals("dima2@mail.us", inviteToken.email);
        assertEquals("Blynk2", inviteToken.appName);

    }

}
