package cc.blynk.server.db.dao;

import cc.blynk.server.db.model.InvitationToken;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public final class InvitationTokensDBDao {

    public static final String selectToken = "SELECT * from invitation_tokens where token = ?";
    public static final String activateToken = "UPDATE invitation_tokens SET is_activated = true, "
            + "activated_ts = NOW() WHERE token = ? and email = ?";
    public static final String insertToken =
            "INSERT INTO invitation_tokens (token, email, name, role_id) values (?, ?, ?, ?)";

    private static final Logger log = LogManager.getLogger(InvitationTokensDBDao.class);
    private final HikariDataSource ds;

    public InvitationTokensDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public InvitationToken select(String token) {
        log.info("Select invitation token {}.", token);

        ResultSet rs = null;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectToken)) {

            statement.setString(1, token);
            rs = statement.executeQuery();
            connection.commit();

            if (rs.next()) {
                return new InvitationToken(rs.getString("token"), rs.getString("email"),
                        rs.getString("name"), rs.getInt("role_id"),
                        rs.getBoolean("is_activated"), rs.getDate("created_ts"), rs.getDate("activated_ts"));
            }
        } catch (Exception e) {
            log.error("Error getting invitation token.", e);
        } finally {
            if (rs != null) {
                 try {
                     rs.close();
                 } catch (Exception e) {
                     //ignore
                 }
            }
        }

        return null;
    }

    public boolean activate(String token, String email) {
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(activateToken)) {

            statement.setString(1, token);
            statement.setString(2, email);
            int updatedRows = statement.executeUpdate();
            connection.commit();
            return updatedRows == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public void insert(InvitationToken invitationToken) throws Exception {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertToken)) {

            ps.setString(1, invitationToken.token);
            ps.setString(2, invitationToken.email);
            ps.setString(3, invitationToken.name);
            ps.setInt(4, invitationToken.roleId);

            ps.executeUpdate();
            connection.commit();
        }
    }
}
