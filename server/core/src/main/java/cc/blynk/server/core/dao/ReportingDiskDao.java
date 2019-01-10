package cc.blynk.server.core.dao;

import cc.blynk.server.core.dao.functions.GraphFunction;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.protocol.exceptions.NoDataException;
import cc.blynk.server.core.reporting.GraphPinRequest;
import cc.blynk.server.core.reporting.average.AverageAggregatorProcessor;
import cc.blynk.server.core.reporting.raw.BaseReportingKey;
import cc.blynk.server.core.reporting.raw.RawDataCacheForGraphProcessor;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
import cc.blynk.server.db.dao.RawEntry;
import cc.blynk.utils.FileUtils;
import cc.blynk.utils.NumberUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_BYTES;
import static cc.blynk.utils.FileUtils.CSV_DIR;
import static cc.blynk.utils.FileUtils.SIZE_OF_REPORT_ENTRY;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
public class ReportingDiskDao implements Closeable {

    private static final Logger log = LogManager.getLogger(ReportingDiskDao.class);

    public final AverageAggregatorProcessor averageAggregator;
    public final RawDataCacheForGraphProcessor rawDataCacheForGraphProcessor;
    public final RawDataProcessor rawDataProcessor;

    public final String dataFolder;

    private final boolean enableRawDbDataStore;

    //for test only
    public ReportingDiskDao(String reportingFolder, AverageAggregatorProcessor averageAggregator,
                            boolean isEnabled) {
        this.averageAggregator = averageAggregator;
        this.rawDataCacheForGraphProcessor = new RawDataCacheForGraphProcessor();
        this.dataFolder = reportingFolder;
        this.enableRawDbDataStore = isEnabled;
        this.rawDataProcessor = new RawDataProcessor();
    }

    public ReportingDiskDao(String reportingFolder, boolean isEnabled) {
        this.averageAggregator = new AverageAggregatorProcessor(reportingFolder);
        this.rawDataCacheForGraphProcessor = new RawDataCacheForGraphProcessor();
        this.dataFolder = reportingFolder;
        this.enableRawDbDataStore = isEnabled;
        this.rawDataProcessor = new RawDataProcessor();
        createCSVFolder();
    }

    private static void createCSVFolder() {
        try {
            Files.createDirectories(Paths.get(CSV_DIR));
        } catch (IOException ioe) {
            log.error("Error creating temp '{}' folder for csv export data.", CSV_DIR);
        }
    }

    private static String generateFilename(char pinType, short pin, String type) {
        return generateFilename("" + pinType + pin, type);
    }

    private static String generateFilename(String pin, String type) {
        return "history_" + pin + "_" + type + ".bin";
    }

    private static boolean hasData(byte[][] data) {
        for (byte[] pinData : data) {
            if (pinData.length > 0) {
                return true;
            }
        }
        return false;
    }

    private static void addBufferToResult(TreeMap<Long, GraphFunction> data,
                                          AggregationFunctionType functionType,
                                          ByteBuffer localByteBuf) {
        if (localByteBuf != null) {
            while (localByteBuf.hasRemaining()) {
                double newVal = localByteBuf.getDouble();
                Long ts = localByteBuf.getLong();
                GraphFunction graphFunctionObj = data.get(ts);
                if (graphFunctionObj == null) {
                    graphFunctionObj = functionType.produce();
                    data.put(ts, graphFunctionObj);
                }
                graphFunctionObj.apply(newVal);
            }
        }
    }

    private static ByteBuffer toByteBuf(TreeMap<Long, GraphFunction> data) {
        ByteBuffer result = ByteBuffer.allocate(data.size() * SIZE_OF_REPORT_ENTRY);
        for (Map.Entry<Long, GraphFunction> entry : data.entrySet()) {
            result.putDouble(entry.getValue().getResult())
                    .putLong(entry.getKey());
        }
        return result;
    }

    public static String generateFilename(PinType pinType, short pin, GraphGranularityType type) {
        return generateFilename(pinType.pintTypeChar, pin, type.label);
    }

    private static boolean containsPrefix(List<String> prefixes, String filename) {
        for (String prefix : prefixes) {
            if (filename.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private Path getDeviceFolderPath(int deviceId) {
        return Paths.get(dataFolder, String.valueOf(deviceId));
    }

    private ByteBuffer getDataForTag(GraphPinRequest graphPinRequest) {
        TreeMap<Long, GraphFunction> data = new TreeMap<>();
        for (int deviceId : graphPinRequest.deviceIds) {
            ByteBuffer localByteBuf = getByteBufferFromDisk(
                    deviceId,
                    graphPinRequest.pinType, graphPinRequest.pin,
                    graphPinRequest.count, graphPinRequest.type,
                    graphPinRequest.skipCount
            );
            addBufferToResult(data, graphPinRequest.functionType, localByteBuf);
        }

        return toByteBuf(data);
    }

    private ByteBuffer getByteBufferFromDisk(GraphPinRequest graphPinRequest) {
        try {
            if (graphPinRequest.isTag) {
                return getDataForTag(graphPinRequest);
            } else {
                return getByteBufferFromDisk(
                        graphPinRequest.deviceId,
                        graphPinRequest.pinType, graphPinRequest.pin,
                        graphPinRequest.count, graphPinRequest.type,
                        graphPinRequest.skipCount
                );
            }
        } catch (Exception e) {
            log.error("Error getting data from disk.", e);
            return null;
        }
    }

    public ByteBuffer getByteBufferFromDisk(int deviceId,
                                            PinType pinType, short pin, int count,
                                            GraphGranularityType type, int skipCount) {
        Path userDataFile = Paths.get(
                dataFolder,
                String.valueOf(deviceId),
                generateFilename(pinType, pin, type)
        );
        if (Files.exists(userDataFile)) {
            try {
                return FileUtils.read(userDataFile, count, skipCount);
            } catch (Exception ioe) {
                log.error(ioe);
            }
        }

        return null;
    }

    public int delete(int deviceId, String[] pins) throws IOException {
        log.debug("Deleting selected pin data for deviceId {}.", deviceId);
        Path userReportingPath = getDeviceFolderPath(deviceId);

        int count = 0;
        List<String> prefixes = new ArrayList<>();
        for (String pin : pins) {
            prefixes.add("history_" + pin + "_");
        }
        try (DirectoryStream<Path> userReportingFolder = Files.newDirectoryStream(userReportingPath, "*")) {
            for (Path reportingFile : userReportingFolder) {
                String userFileName = reportingFile.getFileName().toString();
                if (containsPrefix(prefixes, userFileName)) {
                    FileUtils.deleteQuietly(reportingFile);
                    count++;
                }
            }
        }
        return count;
    }

    public void delete(int[] deviceIds) throws IOException {
        for (int deviceId : deviceIds) {
            delete(deviceId);
        }
    }

    public boolean delete(int deviceId) throws IOException {
        Path userReportingPath = getDeviceFolderPath(deviceId);

        if (Files.exists(userReportingPath)) {
            log.debug("Deleting all pin data for deviceId {}.", deviceId);
            FileUtils.deleteDirectory(userReportingPath);
            return true;
        }
        return false;
    }

    public void delete(int deviceId, PinType pinType, short pin) {
        log.debug("Deleting {}{} pin data for deviceId {}.", pinType.pintTypeChar, pin, deviceId);
        String userReportingDir = getDeviceFolderPath(deviceId).toString();

        for (GraphGranularityType reportGranularity : GraphGranularityType.getValues()) {
            Path userDataFile = Paths.get(userReportingDir, generateFilename(pinType, pin, reportGranularity));
            FileUtils.deleteQuietly(userDataFile);
        }
    }

    public void process(Device device, short pin, PinType pinType, double doubleVal, long ts) {
        //not a number, nothing to aggregate
        if (doubleVal != NumberUtil.NO_RESULT) {
            process(device, pin, pinType, ts, doubleVal);
        }
    }

    private void process(Device device, short pin, PinType pinType, long ts, double doubleVal) {
        int deviceId = device.id;
        BaseReportingKey key = new BaseReportingKey(deviceId, pinType, pin);
        if (enableRawDbDataStore) {
            rawDataProcessor.collect(key, ts, doubleVal);
        }

        averageAggregator.collect(key, ts, doubleVal);
        if (device.webDashboard.needRawDataForGraph(pin, pinType) /* || dash.needRawDataForGraph(pin, pinType)*/) {
            rawDataCacheForGraphProcessor.collect(key, new RawEntry(ts, doubleVal));
        }
    }

    public byte[][] getReportingData(GraphPinRequest[] requestedPins) throws NoDataException {
        byte[][] values = new byte[requestedPins.length][];

        for (int i = 0; i < requestedPins.length; i++) {
            GraphPinRequest graphPinRequest = requestedPins[i];
            log.debug("Getting data for graph pin : {}.", graphPinRequest);
            if (graphPinRequest.isValid()) {
                ByteBuffer byteBuffer = graphPinRequest.isLiveData()
                        //live graph data is not on disk but in memory
                        ? rawDataCacheForGraphProcessor.getLiveGraphData(graphPinRequest)
                        : getByteBufferFromDisk(graphPinRequest);
                values[i] = byteBuffer == null ? EMPTY_BYTES : byteBuffer.array();
            } else {
                values[i] = EMPTY_BYTES;
            }
        }

        if (!hasData(values)) {
            throw new NoDataException();
        }

        return values;
    }

    @Override
    public void close() {
        System.out.println("Stopping aggregator...");
        this.averageAggregator.close();
    }
}
