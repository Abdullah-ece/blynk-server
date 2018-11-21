package cc.blynk.server.internal;

import cc.blynk.server.core.stats.metrics.InstanceLoadMeter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 10.03.18.
 */
public class QuotaLimitChecker {

    private final static Logger log = LogManager.getLogger(QuotaLimitChecker.class);

    /*
    * in case of consistent quota limit exceed during long term, sending warning response back to exceeding channel
    * for performance reason sending only 1 message within interval. In millis
    *
    * this property was never changed, so moving it to static field
    */
    private final static int USER_QUOTA_LIMIT_WARN_PERIOD = 60_000;

    private final int userQuotaLimit;
    private long lastQuotaExceededTime;
    public final InstanceLoadMeter quotaMeter;

    public QuotaLimitChecker(int userQuotaLimit) {
        this.userQuotaLimit = userQuotaLimit;
        this.quotaMeter = new InstanceLoadMeter();
    }

    public boolean quotaReached() {
        if (quotaMeter.getOneMinuteRate() > userQuotaLimit) {
            sendErrorResponseIfTicked();
            return true;
        }
        quotaMeter.mark();
        return false;
    }

    private void sendErrorResponseIfTicked() {
        long now = System.currentTimeMillis();
        //once a minute sending user response message in case limit is exceeded constantly
        if (lastQuotaExceededTime + USER_QUOTA_LIMIT_WARN_PERIOD < now) {
            lastQuotaExceededTime = now;
            log.debug("User has exceeded message quota limit.");
        }
    }

}
