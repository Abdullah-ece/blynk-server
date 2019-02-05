package cc.blynk.server.core.stats.model;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportScheduler;
import cc.blynk.server.core.protocol.enums.Command;
import cc.blynk.server.core.stats.GlobalStats;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.07.15.
 */
public final class Stat {

    public final transient Map<Short, Integer> statsForDB = new HashMap<>();
    public final Map<String, Integer> statsForPrint       = new HashMap<>();
    public final BlockingIOStat ioStat = new BlockingIOStat();
    public final MemoryStat memoryStat = new MemoryStat();

    public int oneMinRate;
    public int registrations;
    public int active;
    public int activeWeek;
    public int activeMonth;
    public int connected;
    public int onlineApps;
    public int totalOnlineApps;
    public int onlineHards;
    public int totalOnlineHards;
    public int appTotal;
    public int webTotal;
    public transient long ts;

    public Stat() {
    }

    // assume clear() or Stat() is called before update()
    public void update(SessionDao sessionDao,
                       UserDao userDao,
                       BlockingIOProcessor blockingIOProcessor,
                       GlobalStats globalStats,
                       ReportScheduler reportScheduler) {
        for (var entry : Command.VALUES_NAME.entrySet()) {
            LongAdder longAdder = globalStats.specificCounters[entry.getKey()];
            int val = (int) (longAdder.sumThenReset());

            this.statsForDB   .put(entry.getKey(), val);
            this.statsForPrint.put(entry.getValue(), val);
        }

        this.appTotal = (int) globalStats.getTotalAppCounter();
        this.webTotal = (int) globalStats.getTotalWebCounter();

        this.oneMinRate = (int) globalStats.totalMessages.getOneMinuteRate();
        int connectedSessions = 0;

        int hardActive = 0;
        int totalOnlineHards = 0;

        int appActive = 0;
        int totalOnlineApps = 0;

        int active = 0;
        int activeWeek = 0;
        int activeMonth = 0;

        this.ts = System.currentTimeMillis();
        for (Session session: sessionDao.orgSession.values()) {
            if (session.isHardwareConnected() && session.isAppConnected()) {
                connectedSessions++;
            }
            if (session.isHardwareConnected()) {
                hardActive++;
                totalOnlineHards += session.hardwareChannels.size();
            }
            if (session.isAppConnected()) {
                appActive++;
                totalOnlineApps += session.appChannels.size();
            }
        }

        this.connected = connectedSessions;
        this.onlineApps = appActive;
        this.totalOnlineApps = totalOnlineApps;
        this.onlineHards = hardActive;
        this.totalOnlineHards = totalOnlineHards;

        this.active = active;
        this.activeWeek = activeWeek;
        this.activeMonth = activeMonth;
        this.registrations = userDao.users.size();

        this.ioStat    .update(blockingIOProcessor, reportScheduler);
        this.memoryStat.update(ByteBufAllocator.DEFAULT);
    }

    public void reset() {

        this.statsForDB   .clear();
        this.statsForPrint.clear();

        this.appTotal = 0;
        this.webTotal = 0;

        this.oneMinRate = 0;

        this.ts = 0;

        this.connected = 0;
        this.onlineApps = 0;
        this.totalOnlineApps = 0;
        this.onlineHards = 0;
        this.totalOnlineHards = 0;

        this.active = 0;
        this.activeWeek = 0;
        this.activeMonth = 0;
        this.registrations = 0;

        this.ioStat    .reset();
        this.memoryStat.reset();
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
