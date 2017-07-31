package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.utils.FileUtils;
import cc.blynk.utils.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static java.util.function.Function.identity;


/**
 * Class responsible for saving/reading user data to/from disk.
 *
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 6:53 PM
 */
public class FileManager {

    private static final Logger log = LogManager.getLogger(FileManager.class);
    private static final String USER_FILE_EXTENSION = ".user";
    private static final String ORG_FILE_EXTENSION = ".org";
    
    private static final String DELETED_DATA_DIR_NAME = "deleted";
    private static final String BACKUP_DATA_DIR_NAME = "backup";
    private static final String ORGANIZATION_DATA_DIR_NAME = "organizations";

    /**
     * Folder where all user profiles are stored locally.
     */
    private Path dataDir;
    private Path orgDataDir;
    private Path deletedDataDir;
    private Path backupDataDir;

    public FileManager(String dataFolder) {
        if (dataFolder == null || dataFolder.isEmpty() || dataFolder.equals("/path")) {
            System.out.println("WARNING : '" + dataFolder + "' does not exists. Please specify correct -dataFolder parameter.");
            dataFolder = Paths.get(System.getProperty("java.io.tmpdir"), "blynk").toString();
            System.out.println("Your data may be lost during server restart. Using temp folder : " + dataFolder);
        }
        try {
            Path dataFolderPath = Paths.get(dataFolder);
            this.dataDir = createDirectories(dataFolderPath);
            this.orgDataDir = createDirectories(Paths.get(dataFolder, ORGANIZATION_DATA_DIR_NAME));
            this.deletedDataDir = createDirectories(Paths.get(dataFolder, DELETED_DATA_DIR_NAME));
            this.backupDataDir = createDirectories(Paths.get(dataFolder, BACKUP_DATA_DIR_NAME));
        } catch (Exception e) {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "blynk");

            System.out.println("WARNING : could not find folder '" + dataFolder + "'. Please specify correct -dataFolder parameter.");
            System.out.println("Your data may be lost during server restart. Using temp folder : " + tempDir.toString());

            try {
                this.dataDir = createDirectories(tempDir);
                this.orgDataDir = createDirectories(Paths.get(this.dataDir.toString(), ORGANIZATION_DATA_DIR_NAME));
                this.deletedDataDir = createDirectories(Paths.get(this.dataDir.toString(), DELETED_DATA_DIR_NAME));
                this.backupDataDir = createDirectories(Paths.get(this.dataDir.toString(), BACKUP_DATA_DIR_NAME));
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        log.info("Using data dir '{}'", dataDir);
    }

    public Path getDataDir() {
        return dataDir;
    }

    public Path generateOrgFileName(int orgId) {
        return Paths.get(orgDataDir.toString(), orgId + ORG_FILE_EXTENSION);
    }

    public Path generateFileName(String email, String appName) {
        return Paths.get(dataDir.toString(), email + "." + appName + USER_FILE_EXTENSION);
    }

    public Path generateBackupFileName(String email, String appName) {
        return Paths.get(backupDataDir.toString(), email + "." + appName + ".user." +
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    public boolean deleteOrg(int orgId) {
        Path file = generateOrgFileName(orgId);
        return FileUtils.move(file, this.deletedDataDir);
    }

    public boolean delete(UserKey userKey) {
        return delete(userKey.email, userKey.appName);
    }

    public boolean delete(String email, String appName) {
        Path file = generateFileName(email, appName);
        return FileUtils.move(file, this.deletedDataDir);
    }

    public void override(Organization org) throws IOException {
        Path path = generateOrgFileName(org.id);
        JsonParser.writeOrg(path.toFile(), org);
    }

    public void overrideUserFile(User user) throws IOException {
        Path path = generateFileName(user.email, user.appName);

        JsonParser.writeUser(path.toFile(), user);
    }

    public ConcurrentMap<Integer, Organization> deserializeOrganizations() {
        log.debug("Starting reading organizations DB.");

        final File[] files = orgDataDir.toFile().listFiles();

        ConcurrentMap<Integer, Organization> temp;
        if (files != null) {
            temp = Arrays.stream(files).parallel()
                    .filter(file -> file.isFile() && file.getName().endsWith(ORG_FILE_EXTENSION))
                    .flatMap(file -> {
                        try {
                            Organization org = JsonParser.parseOrganization(file);
                            return Stream.of(org);
                        } catch (IOException ioe) {
                            log.error("Error parsing file '{}'. Error : {}", file, ioe.getMessage());
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toConcurrentMap(org -> org.id, identity()));

        } else {
            temp = new ConcurrentHashMap<>();
        }

        log.debug("Reading organization DB finished.");
        return temp;
    }

    /**
     * Loads all user profiles one by one from disk using dataDir as starting point.
     *
     * @return mapping between username and it's profile.
     */
    public ConcurrentMap<UserKey, User> deserializeUsers() {
        log.debug("Starting reading user DB.");

        final File[] files = dataDir.toFile().listFiles();

        ConcurrentMap<UserKey, User> temp;
        if (files != null) {
            temp = Arrays.stream(files).parallel()
                    .filter(file -> file.isFile() && file.getName().endsWith(USER_FILE_EXTENSION))
                    .flatMap(file -> {
                        try {
                            User user = JsonParser.parseUserFromFile(file);
                            makeProfileChanges(user);

                            return Stream.of(user);
                        } catch (IOException ioe) {
                            String errorMessage = ioe.getMessage();
                            log.error("Error parsing file '{}'. Error : {}", file, errorMessage);
                            if (errorMessage != null && errorMessage.contains("Unexpected end-of-input")) {
                                return restoreFromBackup(file.getName());
                            }
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toConcurrentMap(UserKey::new, identity()));
        } else {
            temp = new ConcurrentHashMap<>();
        }

        log.debug("Reading user DB finished.");
        return temp;
    }

    private Stream<User> restoreFromBackup(String filename) {
        log.info("Trying to recover from backup...");
        try {
            File[] files = backupDataDir.toFile().listFiles(
                    (dir, name) -> name.startsWith(filename)
            );

            File backupFile = FileUtils.getLatestFile(files);
            if (backupFile == null) {
                log.info("Didn't find any files for recovery :(.");
                return Stream.empty();
            }
            log.info("Found {}. You are lucky today :).", backupFile.getAbsoluteFile());

            User user = JsonParser.parseUserFromFile(backupFile);
            makeProfileChanges(user);
            //profile saver thread is launched after file manager is initialized.
            //so making sure user profile will be saved
            //this is not very important as profile will be updated by user anyway.
            user.lastModifiedTs = System.currentTimeMillis() + 10 * 1000;
            log.info("Restored.", backupFile.getAbsoluteFile());
            return Stream.of(user);
        } catch (Exception e) {
            //ignore
            log.error("Restoring from backup failed. {}", e.getMessage());
        }
        return Stream.empty();
    }

    public static void makeProfileChanges(User user) {
        for (DashBoard dashBoard : user.profile.dashBoards) {
            if (dashBoard.devices != null) {
                for (Device device : dashBoard.devices) {
                    device.status = null;
                }
            }

            for (Widget widget : dashBoard.widgets) {
                dashBoard.cleanPinStorage(widget);
            }
        }
    }

    public Map<String, Integer> getUserProfilesSize() {
        Map<String, Integer> userProfileSize = new HashMap<>();
        File[] files = dataDir.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(USER_FILE_EXTENSION)) {
                    userProfileSize.put(file.getName(), (int) file.length());
                }
            }
        }
        return userProfileSize;
    }

}
