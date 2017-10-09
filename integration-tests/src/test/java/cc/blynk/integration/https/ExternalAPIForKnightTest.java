package cc.blynk.integration.https;

import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.db.DBManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

import static cc.blynk.server.db.dao.table.TableDescriptor.KNIGHT_INSTANCE;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.jooq.SQLDialect.POSTGRES_9_4;
import static org.jooq.impl.DSL.count;
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
        holder.dbManager.executeSQL("DELETE FROM " + KNIGHT_INSTANCE.tableName);
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

        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            Result<Record> result = create.select().from(KNIGHT_INSTANCE.tableName).fetch();

            for (Record rs : result) {
                assertEquals("2016-10-31", KNIGHT_INSTANCE.columns[0].get(rs).toString());
                assertEquals("23:47:46", KNIGHT_INSTANCE.columns[1].get(rs).toString());
                assertEquals("2016-11-01", KNIGHT_INSTANCE.columns[2].get(rs).toString());
                assertEquals("00:16:40", KNIGHT_INSTANCE.columns[3].get(rs).toString());
                assertEquals(2, KNIGHT_INSTANCE.columns[4].get(rs));
                assertEquals(3, KNIGHT_INSTANCE.columns[5].get(rs));
                assertEquals(27, KNIGHT_INSTANCE.columns[6].get(rs));
                assertEquals("00:28:54", KNIGHT_INSTANCE.columns[7].get(rs).toString());
                assertEquals(55, KNIGHT_INSTANCE.columns[8].get(rs));
                assertEquals(220, KNIGHT_INSTANCE.columns[9].get(rs));
                assertEquals(330, KNIGHT_INSTANCE.columns[10].get(rs));
                assertEquals(250, KNIGHT_INSTANCE.columns[11].get(rs));
                assertEquals(350, KNIGHT_INSTANCE.columns[12].get(rs));
                assertEquals(0, KNIGHT_INSTANCE.columns[13].get(rs));
                assertEquals(100, KNIGHT_INSTANCE.columns[14].get(rs));
                assertEquals(0, KNIGHT_INSTANCE.columns[15].get(rs));
                assertEquals(0, KNIGHT_INSTANCE.columns[16].get(rs));
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

        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);
            int result = create.selectCount().from(KNIGHT_INSTANCE.tableName).fetchOne(0, int.class);
            assertEquals(1290, result);
            connection.commit();
        }
    }

    private static final DateTimeFormatter timeFormatter = ofPattern("HH:mm:ss");

    private static LocalTime lc(String time) {
        return LocalTime.parse(time, timeFormatter);
    }

    @Test
    public void testCreateShiftQuery() throws Exception {
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

        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);
            Result<Record3<Integer, Integer, Integer>> result = create.select(
                    count().filterWhere(DSL.field("start_time").between(lc("07:59:59"), lc("16:00:00"))).as("shift1"),
                    count().filterWhere(DSL.field("start_time").between(lc("15:59:59"), lc("23:59:59"))).as("shift2"),
                    count().filterWhere(DSL.field("start_time").between(lc("00:00:00"), lc("08:00:00"))).as("shift3")
            )
            .from(KNIGHT_INSTANCE.tableName).fetch();

            for (Record3<Integer, Integer, Integer> record3 : result) {
                assertEquals(588, record3.get(0));
                assertEquals(507, record3.get(1));
                assertEquals(195, record3.get(2));
            }

            connection.commit();
        }
    }

}
