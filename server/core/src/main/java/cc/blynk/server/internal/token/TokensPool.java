package cc.blynk.server.internal.token;

import cc.blynk.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cc.blynk.server.internal.SerializationUtil.deserialize;
import static cc.blynk.server.internal.SerializationUtil.serialize;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.18.
 */
public final class TokensPool implements Closeable {

    private static final Logger log = LogManager.getLogger(TokensPool.class);
    private static final String TOKENS_TEMP_FILENAME = "tokens_pool_temp.bin";

    private final String dataFolder;
    private final ConcurrentHashMap<String, BaseToken> tokens;

    @SuppressWarnings("unchecked")
    public TokensPool(String dataFolder) {
        this.dataFolder = dataFolder;

        Path path = Paths.get(dataFolder, TOKENS_TEMP_FILENAME);
        this.tokens = (ConcurrentHashMap<String, BaseToken>) deserialize(path);
        FileUtils.deleteQuietly(path);
    }

    public void addToken(String token, BaseToken baseToken) {
        log.info("Adding {} {} to the pool", token, baseToken.getClass().getSimpleName());
        tokens.put(token, baseToken);
    }

    public UploadTempToken getUploadToken(String token) {
        return getTokenByType(token, UploadTempToken.class);
    }

    public InviteToken getInviteToken(String token) {
        return getTokenByType(token, InviteToken.class);
    }

    public ResetPassToken getResetPassToken(String token) {
        return getTokenByType(token, ResetPassToken.class);
    }

    public ShipmentFirmwareDownloadToken getOTADownloadToken(String token) {
        return getTokenByType(token, ShipmentFirmwareDownloadToken.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T getTokenByType(String token, Class<T> clazz) {
        BaseToken baseToken = getBaseToken(token);
        if (clazz.isInstance(baseToken)) {
            return (T) baseToken;
        }
        return null;
    }

    public BaseToken getBaseToken(String token) {
        return tokens.get(token);
    }

    public boolean hasResetToken(String email, String appName) {
        for (Map.Entry<String, BaseToken> entry : tokens.entrySet()) {
            BaseToken tokenBase = entry.getValue();
            if (tokenBase instanceof ResetPassToken) {
                ResetPassToken resetPassToken = (ResetPassToken) tokenBase;
                if (resetPassToken.isSame(email, appName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeToken(String token) {
        tokens.remove(token);
    }

    public int size() {
        return tokens.size();
    }

    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        tokens.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    //just for tests
    public ConcurrentHashMap<String, BaseToken> getTokens() {
        return tokens;
    }

    @Override
    public void close() {
        serialize(Paths.get(dataFolder, TOKENS_TEMP_FILENAME), tokens);
    }
}
