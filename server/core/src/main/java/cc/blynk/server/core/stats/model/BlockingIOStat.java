package cc.blynk.server.core.stats.model;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportScheduler;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 03.05.17.
 */
final class BlockingIOStat {

    public final int messagingActiveTasks;

    public final long messagingExecutedTasks;

    public final int historyActiveTasks;

    public final long historyExecutedTasks;

    public final int dbActiveTasks;

    public final long dbExecutedTasks;

    public final int getServerActiveTasks;

    public final long getServerExecutedTasks;

    public final int reportsActive;

    public final long reportsExecuted;

    public final int reportsFutureMapSize;

    BlockingIOStat(BlockingIOProcessor blockingIOProcessor, ReportScheduler reportScheduler) {
        this(blockingIOProcessor.messagingExecutor.getQueue().size(),
             blockingIOProcessor.messagingExecutor.getCompletedTaskCount(),

             blockingIOProcessor.historyExecutor.getQueue().size(),
             blockingIOProcessor.historyExecutor.getCompletedTaskCount(),

             blockingIOProcessor.dbExecutor.getQueue().size(),
             blockingIOProcessor.dbExecutor.getCompletedTaskCount(),

             blockingIOProcessor.dbGetServerExecutor.getQueue().size(),
             blockingIOProcessor.dbGetServerExecutor.getCompletedTaskCount(),

             reportScheduler.getQueue().size(),
             reportScheduler.getCompletedTaskCount(),
             reportScheduler.map.size()
        );
    }

    private BlockingIOStat(int messagingActiveTasks, long messagingExecutedTasks,
                          int historyActiveTasks, long historyExecutedTasks,
                          int dbActiveTasks, long dbExecutedTasks,
                          int getServerActiveTasks, long getServerExecutedTasks,
                          int reportsActive, long reportsExecuted, int reportsFutureMapSize) {
        this.messagingActiveTasks = messagingActiveTasks;
        this.messagingExecutedTasks = messagingExecutedTasks;
        this.historyActiveTasks = historyActiveTasks;
        this.historyExecutedTasks = historyExecutedTasks;
        this.dbActiveTasks = dbActiveTasks;
        this.dbExecutedTasks = dbExecutedTasks;
        this.getServerActiveTasks = getServerActiveTasks;
        this.getServerExecutedTasks = getServerExecutedTasks;
        this.reportsActive = reportsActive;
        this.reportsExecuted = reportsExecuted;
        this.reportsFutureMapSize = reportsFutureMapSize;
    }
}
