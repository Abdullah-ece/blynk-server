package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 01.02.19.
 */
public class OrganizationDBDao {

    private static final String insertOrganization = "INSERT INTO organizations (org_id, json) VALUES (?, ?)"
            + "ON CONFLICT (org_id) DO UPDATE SET json = EXCLUDED.json";

    private static final String selectAllOrganizations = "SELECT * FROM organizations";

    private static final String deleteOrganization = "DELETE * FROM organizations WHERE org_id = ?";

    private static final Logger log = LogManager.getLogger(OrganizationDBDao.class);

    private final HikariDataSource ds;

    public OrganizationDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public void save(List<Organization> organizations) {
        long start = System.currentTimeMillis();
        log.info("Storing organizations...");

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertOrganization)) {

            for (Organization organization : organizations) {
                ps   .setInt(1, organization.id);
                ps.setString(2, organization.toString());

                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            log.error("Error inserting organizations in DB.", e);
        }
        log.info("Storing organizations finished. Time {}. Organizations saved {}",
                System.currentTimeMillis() - start, organizations.size());
    }

    public ConcurrentMap<Integer, Organization> getAllOrganizations() throws Exception {
        ConcurrentMap<Integer, Organization> organizations = new ConcurrentHashMap<>();

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectAllOrganizations)) {

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String json = rs.getString(2);

                    Organization organization = JsonParser.parseOrganization(json);
                    organizations.put(organization.id, organization);
                }
                connection.commit();
            }
        }

        log.info("Loaded {} organizations.", organizations.size());

        return organizations;
    }

    public boolean deleteOrganization(int orgId) {
        int removed = 0;

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteOrganization)) {

            ps.setInt(1, orgId);

            removed = ps.executeUpdate();

            connection.commit();
        } catch (Exception e) {
            log.error("Error removing organization {} from DB.", orgId, e);
        }

        return removed > 0;
    }
}
