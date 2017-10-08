package cc.blynk.integration.https;

import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.dao.table.TableDataMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExternalAPIForKnightTest extends APIBaseTest {

    private ClientPair clientPair;
    private String httpsServerUrl;
    private DBManager dbManager;

    @Before
    public void init() throws Exception {
        super.init();

        httpsServerUrl = String.format("https://localhost:%s/external/api/", httpsPort);

        //clean everything just in case
        holder.dbManager.executeSQL("DELETE FROM " + TableDataMapper.KNIGHT_TABLE_NAME);
        this.dbManager = holder.dbManager;
    }

    @After
    public void shutdown() {
        super.shutdown();
    }

    @Test
    public void testInsertSingleKnightRow() throws Exception {
        URL url = getClass().getResource("/2017_ISSA_Sample_IOT_Data.csv");
        Path resPath = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(resPath);

        String[] split = lines.get(0).split(",");

        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (String splitPart : split) {
            sj.add("\"" + splitPart + "\"");
        }

        String fixedLine = sj.toString();

        HttpPut put = new HttpPut(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/update/v100");
        put.setEntity(new StringEntity(fixedLine, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        try (Connection connection = dbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from " + TableDataMapper.KNIGHT_TABLE_NAME)) {

            while (rs.next()) {
                assertEquals("2016-10-31", TableDataMapper.knightColumns[0].get(rs).toString());
                assertEquals("23:47:46", TableDataMapper.knightColumns[1].get(rs).toString());
                assertEquals("2016-11-01", TableDataMapper.knightColumns[2].get(rs).toString());
                assertEquals("00:16:40", TableDataMapper.knightColumns[3].get(rs).toString());
                assertEquals(2, TableDataMapper.knightColumns[4].get(rs));
                assertEquals(3, TableDataMapper.knightColumns[5].get(rs));
                assertEquals(27, TableDataMapper.knightColumns[6].get(rs));
                assertEquals("00:28:54", TableDataMapper.knightColumns[7].get(rs).toString());
                assertEquals(55, TableDataMapper.knightColumns[8].get(rs));
                assertEquals(220, TableDataMapper.knightColumns[9].get(rs));
                assertEquals(330, TableDataMapper.knightColumns[10].get(rs));
                assertEquals(250, TableDataMapper.knightColumns[11].get(rs));
                assertEquals(350, TableDataMapper.knightColumns[12].get(rs));
                assertEquals(0, TableDataMapper.knightColumns[13].get(rs));
                assertEquals(100, TableDataMapper.knightColumns[14].get(rs));
                assertEquals(0, TableDataMapper.knightColumns[15].get(rs));
                assertEquals(0, TableDataMapper.knightColumns[16].get(rs));
            }
            connection.commit();
        }
    }

    @Test
    public void testInsertAllTestRows() throws Exception {
        URL url = getClass().getResource("/2017_ISSA_Sample_IOT_Data.csv");
        Path resPath = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(resPath);

        for (String line : lines) {
            String[] split = line.split(",");

            StringJoiner sj = new StringJoiner(",", "[", "]");
            for (String splitPart : split) {
                sj.add("\"" + splitPart + "\"");
            }

            String fixedLine = sj.toString();

            HttpPut put = new HttpPut(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/update/v100");
            put.setEntity(new StringEntity(fixedLine, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpclient.execute(put)) {
                assertEquals(200, response.getStatusLine().getStatusCode());
            }
        }

        try (Connection connection = dbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select count(*) from " + TableDataMapper.KNIGHT_TABLE_NAME)) {

            while (rs.next()) {
                assertEquals(1290, rs.getInt(1));
            }
            connection.commit();
        }
    }

}
