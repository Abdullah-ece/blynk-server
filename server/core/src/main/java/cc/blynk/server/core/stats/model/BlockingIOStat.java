package cc.blynk.server.core.stats.model;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportScheduler;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 03.05.17.
 */
final class BlockingIOStat {

    public int messagingActiveTasks;

    public long messagingExecutedTasks;

    public int historyActiveTasks;

    public long historyExecutedTasks;

    public int dbActiveTasks;

    public long dbExecutedTasks;

    public int getServerActiveTasks;

    public long getServerExecutedTasks;

    public int reportsActive;

    public long reportsExecuted;

    public int reportsFutureMapSize;

    BlockingIOStat() {
    }

    public void update(BlockingIOProcessor blockingIOProcessor, ReportScheduler reportScheduler) {
        this.messagingActiveTasks   = blockingIOProcessor.messagingExecutor.getQueue().size();
        this.messagingExecutedTasks = blockingIOProcessor.messagingExecutor.getCompletedTaskCount();
        this.historyActiveTasks     = blockingIOProcessor.historyExecutor.getQueue().size();
        this.historyExecutedTasks   = blockingIOProcessor.historyExecutor.getCompletedTaskCount();
        this.dbActiveTasks          = blockingIOProcessor.dbExecutor.getQueue().size();
        this.dbExecutedTasks        = blockingIOProcessor.dbExecutor.getCompletedTaskCount();
        this.getServerActiveTasks   = blockingIOProcessor.dbGetServerExecutor.getQueue().size();
        this.getServerExecutedTasks = blockingIOProcessor.dbGetServerExecutor.getCompletedTaskCount();
        this.reportsActive          = reportScheduler.getQueue().size();
        this.reportsExecuted        = reportScheduler.getCompletedTaskCount();
        this.reportsFutureMapSize   = reportScheduler.map.size();
    }

    public void reset() {
        this.messagingActiveTasks   = 0;
        this.messagingExecutedTasks = 0;
        this.historyActiveTasks     = 0;
        this.historyExecutedTasks   = 0;
        this.dbActiveTasks          = 0;
        this.dbExecutedTasks        = 0;
        this.getServerActiveTasks   = 0;
        this.getServerExecutedTasks = 0;
        this.reportsActive          = 0;
        this.reportsExecuted        = 0;
        this.reportsFutureMapSize   = 0;
    }
}
