package cc.blynk.integration.https.reporting;

import cc.blynk.integration.https.APIBaseTest;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.db.DBManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
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
import java.util.List;
import java.util.StringJoiner;

import static cc.blynk.integration.https.reporting.KnightData.makeNewDataFromOldData;
import static cc.blynk.server.db.dao.descriptor.TableDescriptor.KNIGHT_INSTANCE;
import static org.jooq.SQLDialect.POSTGRES_9_4;
import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExternalAPIForKnightTest extends APIBaseTest {

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

        KnightData[] newKnightData = makeNewDataFromOldData(lines.get(0).split(","));

        StringJoiner sj = new StringJoiner(",", "[", "]");
        sj.add(newKnightData[0].toString());
        String fixedLine = sj.toString();

        HttpPut put = new HttpPut(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/updateBatch/v100");
        put.setEntity(new StringEntity(fixedLine, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            Result<Record> result = create.select().from(KNIGHT_INSTANCE.tableName).fetch();

            for (Record rs : result) {
                assertEquals(0, KNIGHT_INSTANCE.columns[0].get(rs));
                assertEquals((short) 100, KNIGHT_INSTANCE.columns[1].get(rs));
                assertEquals((short) PinType.VIRTUAL.ordinal(), KNIGHT_INSTANCE.columns[2].get(rs));

                assertEquals(2, KNIGHT_INSTANCE.columns[3].get(rs));
                assertEquals(3, KNIGHT_INSTANCE.columns[4].get(rs));
                assertEquals("2016-10-31", KNIGHT_INSTANCE.columns[5].get(rs).toString());
                assertEquals("23:47:46", KNIGHT_INSTANCE.columns[6].get(rs).toString());
                assertEquals("00:16:40", KNIGHT_INSTANCE.columns[7].get(rs).toString());
                assertEquals("00:28:54", KNIGHT_INSTANCE.columns[8].get(rs).toString());

                assertEquals(27, KNIGHT_INSTANCE.columns[9].get(rs));
                assertEquals(55, KNIGHT_INSTANCE.columns[10].get(rs));
                assertEquals(1, KNIGHT_INSTANCE.columns[11].get(rs));
                assertEquals(220, KNIGHT_INSTANCE.columns[12].get(rs));
                assertEquals(0, KNIGHT_INSTANCE.columns[13].get(rs));
                assertEquals(0, KNIGHT_INSTANCE.columns[14].get(rs));
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
            KnightData[] newKnightData = makeNewDataFromOldData(line.split(","));

            StringJoiner sj = new StringJoiner(",", "[", "]");
            for (KnightData knightData : newKnightData) {
                sj.add(knightData.toString());
            }
            String fixedLine = sj.toString();

            HttpPut put = new HttpPut(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/updateBatch/v100");
            put.setEntity(new StringEntity(fixedLine, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpclient.execute(put)) {
                assertEquals(200, response.getStatusLine().getStatusCode());
            }
        }

        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);
            int result = create.selectCount().from(KNIGHT_INSTANCE.tableName).fetchOne(0, int.class);
            assertEquals(1290 * 8, result);
            connection.commit();
        }
    }

}
