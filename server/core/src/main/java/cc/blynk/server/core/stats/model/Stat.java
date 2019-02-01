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

    public final transient Map<Short, Integer> statsForDB;
    public final Map<String, Integer> statsForPrint;
    public final BlockingIOStat ioStat;
    public final MemoryStat memoryStat;

    public final int oneMinRate;
    public final int registrations;
    public final int active;
    public final int activeWeek;
    public final int activeMonth;
    public final int connected;
    public final int onlineApps;
    public final int totalOnlineApps;
    public final int onlineHards;
    public final int totalOnlineHards;
    public final int appTotal;
    public final int webTotal;
    public final transient long ts;

    public Stat(SessionDao sessionDao,
                UserDao userDao,
                BlockingIOProcessor blockingIOProcessor,
                GlobalStats globalStats,
                ReportScheduler reportScheduler) {

        this.statsForDB = new HashMap<>();
        this.statsForPrint = new HashMap<>();

        for (var entry : Command.VALUES_NAME.entrySet()) {
            LongAdder longAdder = globalStats.specificCounters[entry.getKey()];
            int val = (int) (longAdder.sumThenReset());

            this.statsForDB.put(entry.getKey(), val);
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

        this.ioStat = new BlockingIOStat(blockingIOProcessor, reportScheduler);
        this.memoryStat = new MemoryStat(ByteBufAllocator.DEFAULT);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
