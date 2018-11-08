package cc.blynk.server.core.dao;

import cc.blynk.server.core.dao.functions.GraphFunction;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.protocol.exceptions.NoDataException;
import cc.blynk.server.core.reporting.GraphPinRequest;
import cc.blynk.server.core.reporting.average.AverageAggregatorProcessor;
import cc.blynk.server.core.reporting.raw.BaseReportingKey;
import cc.blynk.server.core.reporting.raw.GraphValue;
import cc.blynk.server.core.reporting.raw.RawDataCacheForGraphProcessor;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
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
import java.util.function.Function;

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
    public final CSVGenerator csvGenerator;

    public final String dataFolder;

    private final boolean enableRawDbDataStore;

    private static final Function<Path, Boolean> NO_FILTER = s -> true;

    //for test only
    public ReportingDiskDao(String reportingFolder, AverageAggregatorProcessor averageAggregator,
                            boolean isEnabled) {
        this.averageAggregator = averageAggregator;
        this.rawDataCacheForGraphProcessor = new RawDataCacheForGraphProcessor();
        this.dataFolder = reportingFolder;
        this.enableRawDbDataStore = isEnabled;
        this.rawDataProcessor = new RawDataProcessor(enableRawDbDataStore);
        this.csvGenerator = new CSVGenerator(this);
    }

    public ReportingDiskDao(String reportingFolder, boolean isEnabled) {
        this.averageAggregator = new AverageAggregatorProcessor(reportingFolder);
        this.rawDataCacheForGraphProcessor = new RawDataCacheForGraphProcessor();
        this.dataFolder = reportingFolder;
        this.enableRawDbDataStore = isEnabled;
        this.rawDataProcessor = new RawDataProcessor(enableRawDbDataStore);
        this.csvGenerator = new CSVGenerator(this);
        createCSVFolder();
    }

    private static void createCSVFolder() {
        try {
            Files.createDirectories(Paths.get(CSV_DIR));
        } catch (IOException ioe) {
            log.error("Error creating temp '{}' folder for csv export data.", CSV_DIR);
        }
    }

    private static String generateFilename(int deviceId, char pinType, short pin, String type) {
        return generateFilenamePrefix(deviceId) + pinType + pin + "_" + type + ".bin";
    }

    private static boolean hasData(byte[][] data) {
        for (byte[] pinData : data) {
            if (pinData.length > 0) {
                return true;
            }
        }
        return false;
    }

    private static void delete(String userReportingDir, int deviceId, PinType pinType, short pin,
                               GraphGranularityType reportGranularity) {
        Path userDataFile = Paths.get(userReportingDir,
                generateFilename(deviceId, pinType, pin, reportGranularity));
        FileUtils.deleteQuietly(userDataFile);
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

    private static String generateFilenamePrefix(int deviceId, String pin) {
        return generateFilenamePrefix(deviceId) + pin + "_";
    }

    private Path getUserReportingFolderPath(User user) {
        return Paths.get(dataFolder, FileUtils.getUserStorageDir(user.email));
    }

    public int delete(User user) {
        return delete(user, NO_FILTER);
    }

    public int delete(User user, Function<Path, Boolean> filter) {
        log.debug("Removing all reporting data for {}", user.email);
        Path reportingFolderPath = getUserReportingFolderPath(user);

        int removedFilesCounter = 0;
        try {
            if (Files.exists(reportingFolderPath)) {
                try (DirectoryStream<Path> reportingFolder = Files.newDirectoryStream(reportingFolderPath, "*")) {
                    for (Path reportingFile : reportingFolder) {
                        if (filter.apply(reportingFile)) {
                            log.trace("Removing {}", reportingFile);
                            FileUtils.deleteQuietly(reportingFile);
                            removedFilesCounter++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error removing file : {}.", reportingFolderPath);
        }
        return removedFilesCounter;
    }

    private static boolean containsPrefix(List<String> prefixes, String filename) {
        for (String prefix : prefixes) {
            if (filename.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static String generateFilenamePrefix(int deviceId) {
        return "history_" + deviceId + "_";
    }

    public static String generateFilename(int deviceId,
                                          PinType pinType, short pin, GraphGranularityType type) {
        return generateFilename(deviceId, pinType.pintTypeChar, pin, type.label);
    }

    private ByteBuffer getDataForTag(User user, GraphPinRequest graphPinRequest) {
        TreeMap<Long, GraphFunction> data = new TreeMap<>();
        for (int deviceId : graphPinRequest.deviceIds) {
            ByteBuffer localByteBuf = getByteBufferFromDisk(user,
                    deviceId,
                    graphPinRequest.pinType, graphPinRequest.pin,
                    graphPinRequest.count, graphPinRequest.type,
                    graphPinRequest.skipCount
            );
            addBufferToResult(data, graphPinRequest.functionType, localByteBuf);
        }

        return toByteBuf(data);
    }

    private ByteBuffer getByteBufferFromDisk(User user, GraphPinRequest graphPinRequest) {
        try {
            if (graphPinRequest.isTag) {
                return getDataForTag(user, graphPinRequest);
            } else {
                return getByteBufferFromDisk(user,
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

    public ByteBuffer getByteBufferFromDisk(User user, int deviceId,
                                            PinType pinType, short pin, int count,
                                            GraphGranularityType type, int skipCount) {
        Path userDataFile = Paths.get(
                dataFolder,
                FileUtils.getUserStorageDir(user.email),
                generateFilename(deviceId, pinType, pin, type)
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

    public int delete(User user, int dashId, int deviceId, String[] pins) throws IOException {
        log.debug("Removing selected pin data for dashId {}, deviceId {}.", dashId, deviceId);
        Path userReportingPath = getUserReportingFolderPath(user);

        int count = 0;
        List<String> prefixes = new ArrayList<>();
        for (String pin : pins) {
            prefixes.add(generateFilenamePrefix(deviceId, pin));
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

    public int delete(User user, int deviceId) throws IOException {
        log.debug("Removing all pin data for deviceId {}.", deviceId);
        Path userReportingPath = getUserReportingFolderPath(user);

        int count = 0;
        if (Files.exists(userReportingPath)) {
            String fileNamePrefix = generateFilenamePrefix(deviceId);
            try (DirectoryStream<Path> userReportingFolder = Files.newDirectoryStream(userReportingPath, "*")) {
                for (Path reportingFile : userReportingFolder) {
                    if (reportingFile.getFileName().toString().startsWith(fileNamePrefix)) {
                        FileUtils.deleteQuietly(reportingFile);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void delete(User user, int deviceId, PinType pinType, short pin) {
        log.debug("Removing {}{} pin data , deviceId {}.", pinType.pintTypeChar, pin, deviceId);
        String userReportingDir = getUserReportingFolderPath(user).toString();

        for (GraphGranularityType reportGranularity : GraphGranularityType.values()) {
            delete(userReportingDir, deviceId, pinType, pin, reportGranularity);
        }
    }

    public void process(User user, DashBoard dash, Device device, short pin, PinType pinType, String value, long ts) {
        try {
            double doubleVal = NumberUtil.parseDouble(value);
            process(user, dash, device, pin, pinType, value, ts, doubleVal);
        } catch (Exception e) {
            //just in case
            log.trace("Error collecting reporting entry.");
        }
    }

    private void process(User user, DashBoard dash, Device device, short pin, PinType pinType,
                         String value, long ts, double doubleVal) {
        int deviceId = device.id;
        if (enableRawDbDataStore) {
            rawDataProcessor.collect(deviceId, pinType, pin, value);
        }

        //not a number, nothing to aggregate
        if (doubleVal == NumberUtil.NO_RESULT) {
            return;
        }

        BaseReportingKey key = new BaseReportingKey(user.email, user.orgId, deviceId, pinType, pin);
        averageAggregator.collect(key, ts, doubleVal);
        if (device.webDashboard.needRawDataForGraph(pin, pinType) || dash.needRawDataForGraph(pin, pinType)) {
            rawDataCacheForGraphProcessor.collect(key, new GraphValue(doubleVal, ts));
        }
    }


    public byte[][] getReportingData(User user, GraphPinRequest[] requestedPins) throws NoDataException {
        byte[][] values = new byte[requestedPins.length][];

        for (int i = 0; i < requestedPins.length; i++) {
            GraphPinRequest graphPinRequest = requestedPins[i];
            log.debug("Getting data for graph pin : {}.", graphPinRequest);
            if (graphPinRequest.isValid()) {
                ByteBuffer byteBuffer = graphPinRequest.isLiveData()
                        //live graph data is not on disk but in memory
                        ? rawDataCacheForGraphProcessor.getLiveGraphData(user, graphPinRequest)
                        : getByteBufferFromDisk(user, graphPinRequest);
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
