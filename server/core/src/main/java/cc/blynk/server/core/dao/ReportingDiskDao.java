package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cc.blynk.utils.FileUtils.CSV_DIR;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
@Deprecated
//todo should be removed and DB manager used instead
public final class ReportingDiskDao {

    private static final Logger log = LogManager.getLogger(ReportingDiskDao.class);

    private final String dataFolder;

    public ReportingDiskDao(String reportingFolder) {
        this.dataFolder = reportingFolder;
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

    public static String generateFilename(PinType pinType, short pin, GraphGranularityType type) {
        return generateFilename(pinType.pintTypeChar, pin, type.label);
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
}
