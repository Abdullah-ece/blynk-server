package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
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

    private static final String ORGANIZATION_DATA_DIR_NAME = "organizations";

    /**
     * Folder where all user profiles are stored locally.
     */
    private Path dataDir;
    private Path orgDataDir;
    private final String host;

    public FileManager(String dataFolder, String host) {
        if (dataFolder == null || dataFolder.isEmpty() || dataFolder.equals("/path")) {
            System.out.println("WARNING : '" + dataFolder + "' does not exists. "
                    + "Please specify correct -dataFolder parameter.");
            dataFolder = Paths.get(System.getProperty("java.io.tmpdir"), "blynk").toString();
            System.out.println("Your data may be lost during server restart. Using temp folder : " + dataFolder);
        }
        try {
            Path dataFolderPath = Paths.get(dataFolder);
            this.dataDir = createDirectories(dataFolderPath);
            this.orgDataDir = createDirectories(Paths.get(dataFolder, ORGANIZATION_DATA_DIR_NAME));
        } catch (Exception e) {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "blynk");

            System.out.println("WARNING : could not find folder '" + dataFolder + "'. "
                    + "Please specify correct -dataFolder parameter.");
            System.out.println("Your data may be lost during server restart. Using temp folder : "
                    + tempDir.toString());

            try {
                this.dataDir = createDirectories(tempDir);
                this.orgDataDir = createDirectories(Paths.get(this.dataDir.toString(), ORGANIZATION_DATA_DIR_NAME));
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        this.host = host;
        log.info("Using data dir '{}'", dataDir);
    }

    public Path getDataDir() {
        return dataDir;
    }

    private Path generateOrgFileName(int orgId) {
        return Paths.get(orgDataDir.toString(), orgId + ORG_FILE_EXTENSION);
    }

    public Path generateFileName(String email) {
        return Paths.get(dataDir.toString(), email + USER_FILE_EXTENSION);
    }

    void deleteOrg(int orgId) {
        Path file = generateOrgFileName(orgId);
        FileUtils.deleteQuietly(file);
    }

    public void delete(String email) {
        Path file = generateFileName(email);
        FileUtils.deleteQuietly(file);
    }

    public void override(Organization org, boolean isFancy) throws IOException {
        Path path = generateOrgFileName(org.id);
        JsonParser.writeOrg(path.toFile(), org, isFancy);
    }

    public void overrideUserFile(User user, boolean isFancy) throws IOException {
        Path path = generateFileName(user.email);

        JsonParser.writeUser(path.toFile(), user, isFancy);
    }

    ConcurrentMap<Integer, Organization> deserializeOrganizations() {
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
                        throw new RuntimeException("Error reading organization.");
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
    public ConcurrentMap<String, User> deserializeUsers() {
        log.debug("Starting reading user DB.");

        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**" + USER_FILE_EXTENSION);
        ConcurrentMap<String, User> temp;
        try {
            temp = Files.walk(dataDir, 1).parallel()
                    .filter(path -> Files.isRegularFile(path) && pathMatcher.matches(path))
                    .flatMap(path -> {
                        try {
                            User user = JsonParser.parseUserFromFile(path);
                            makeProfileChanges(user);

                            return Stream.of(user);
                        } catch (IOException ioe) {
                            String errorMessage = ioe.getMessage();
                            log.error("Error parsing file '{}'. Error : {}", path, errorMessage);
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toConcurrentMap((user) -> user.email, identity()));
        } catch (Exception e) {
            log.error("Error reading user profiles from disk. {}", e.getMessage());
            throw new RuntimeException(e);
        }

        log.debug("Reading user DB finished.");
        return temp;
    }

    //public is for tests only
    private void makeProfileChanges(User user) {
        if (user.email == null) {
            user.email = user.name;
        }
        user.ip = host;
    }
}
