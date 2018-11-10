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

import java.util.concurrent.atomic.LongAdder;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.07.15.
 */
public class Stat {

    private final static long ONE_DAY = 24 * 60 * 60 * 1000;
    private final static long ONE_WEEK = 7 * ONE_DAY;
    private final static long ONE_MONTH = 30 * ONE_DAY;

    public final CommandStat commands = new CommandStat();
    public final HttpStat http = new HttpStat();
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
    public final transient long ts;

    public Stat(SessionDao sessionDao, UserDao userDao, BlockingIOProcessor blockingIOProcessor,
                GlobalStats globalStats, ReportScheduler reportScheduler, boolean reset) {
        //yeap, some stats updates may be lost (because of sumThenReset()),
        //but we don't care, cause this is just for general monitoring
        for (Short command : Command.VALUES_NAME.keySet()) {
            LongAdder longAdder = globalStats.specificCounters[command];
            int val = (int) (reset ? longAdder.sumThenReset() : longAdder.sum());

            this.http.assign(command, val);
            this.commands.assign(command, val);
        }

        this.commands.appTotal = (int) globalStats.getTotalAppCounter(reset);
        this.commands.webTotal = (int) globalStats.getTotalWebCounter(reset);

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
