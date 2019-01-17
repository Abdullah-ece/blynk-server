package cc.blynk.server.workers;

import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.profile.Profile;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.outputs.graph.Superchart;
import cc.blynk.server.core.model.widgets.ui.reporting.Report;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportingWidget;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportDataStream;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportSource;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.utils.IntArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Daily job used to clean reporting data that is not used by the history graphs
 * but stored anyway on the disk.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.01.18.
 */
public class HistoryGraphUnusedPinDataCleanerWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(HistoryGraphUnusedPinDataCleanerWorker.class);

    private final UserDao userDao;
    private final DeviceDao deviceDao;

    private long lastStart;

    public HistoryGraphUnusedPinDataCleanerWorker(UserDao userDao, DeviceDao deviceDao, ReportingDiskDao reportingDao) {
        this.userDao = userDao;
        this.deviceDao = deviceDao;
        this.lastStart = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            log.info("Start removing unused reporting data...");

            long now = System.currentTimeMillis();
            int result = removeUnsedInHistoryGraphData();

            lastStart = now;

            log.info("Removed {} files. Time : {} ms.", result, System.currentTimeMillis() - now);
        } catch (Throwable t) {
            log.error("Error removing unused reporting data.", t);
        }
    }

    private static void add(Set<DeviceFile> doNotRemovePaths,
                            ReportingWidget reportingWidget) {
        for (Report report : reportingWidget.reports) {
            for (ReportSource reportSource : report.reportSources) {
                int[] deviceIds = reportSource.getDeviceIds();
                for (ReportDataStream reportDataStream : reportSource.reportDataStreams) {
                    for (int deviceId : deviceIds) {
                        for (GraphGranularityType type : GraphGranularityType.getValues()) {
                            String filename = ReportingDiskDao.generateFilename(
                                    reportDataStream.pinType, reportDataStream.pin, type);
                            doNotRemovePaths.add(new DeviceFile(deviceId, filename));
                        }
                    }
                }
            }
        }
    }

    private void add(Set<DeviceFile> doNotRemovePaths, Widget widget, int[] deviceIds) {
        if (widget instanceof Superchart) {
            Superchart enhancedHistoryGraph = (Superchart) widget;
            add(doNotRemovePaths, enhancedHistoryGraph, deviceIds);
        } else if (widget instanceof ReportingWidget) {
            //reports can't be assigned to device tiles so we ignore deviceIds parameter
            ReportingWidget reportingWidget = (ReportingWidget) widget;
            add(doNotRemovePaths, reportingWidget);
        }
    }

    private void add(Set<DeviceFile> doNotRemovePaths, Superchart graph, int[] deviceIds) {
        for (GraphDataStream graphDataStream : graph.dataStreams) {
            if (graphDataStream != null && graphDataStream.dataStream != null && graphDataStream.dataStream.isValid()) {
                DataStream dataStream = graphDataStream.dataStream;

                int[] resultIds;
                if (deviceIds == null) {
                    int targetId = graphDataStream.targetId;
                    Device device = deviceDao.getById(targetId);

                    if (device != null) {
                        resultIds = new int[] {device.id};
                    } else {
                        resultIds = IntArray.EMPTY_INTS;
                    }
                } else {
                    resultIds = deviceIds;
                }

                for (int deviceId : resultIds) {
                    for (GraphGranularityType type : GraphGranularityType.getValues()) {
                        String filename =
                                ReportingDiskDao.generateFilename(dataStream.pinType, dataStream.pin, type);
                        doNotRemovePaths.add(new DeviceFile(deviceId, filename));
                    }
                }
            }
        }
    }

    private int removeUnsedInHistoryGraphData() {
        int removedFilesCounter = 0;
        Set<DeviceFile> doNotRemovePaths = new HashSet<>();

        for (User user : userDao.getUsers().values()) {
            //we don't want to do a lot of work here,
            //so we check only active profiles that actually write data
            if (user.isUpdated(lastStart)) {
                doNotRemovePaths.clear();
                try {
                    Profile profile = user.profile;
                    for (DashBoard dashBoard : profile.dashBoards) {
                        for (Widget widget : dashBoard.widgets) {
                            if (widget instanceof DeviceTiles) {
                                DeviceTiles deviceTiles = (DeviceTiles) widget;
                                for (TileTemplate tileTemplate : deviceTiles.templates) {
                                    for (Widget tilesWidget : tileTemplate.widgets) {
                                        add(doNotRemovePaths, tilesWidget, tileTemplate.deviceIds);
                                    }
                                }
                            } else {
                                add(doNotRemovePaths, widget, null);
                            }
                        }
                    }

                    //todo finish.
                    //removedFilesCounter += reportingDao.delete(
                    //        (deviceId, reportingFile)
                    // -> !doNotRemovePaths.contains(new DeviceFile(deviceId, reportingFile)));
                } catch (Exception e) {
                    log.error("Error cleaning reporting record for user {}. {}", user.email, e.getMessage());
                }
            }
        }
        return removedFilesCounter;
    }

    private static class DeviceFile {
        final int deviceId;
        final String pinFileName;

        DeviceFile(int deviceId, String pinFileName) {
            this.deviceId = deviceId;
            this.pinFileName = pinFileName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            DeviceFile that = (DeviceFile) o;

            if (deviceId != that.deviceId) {
                return false;
            }
            return pinFileName != null ? pinFileName.equals(that.pinFileName) : that.pinFileName == null;
        }

        @Override
        public int hashCode() {
            int result = deviceId;
            result = 31 * result + (pinFileName != null ? pinFileName.hashCode() : 0);
            return result;
        }
    }

}
