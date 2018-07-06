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

import java.sql.Connection;
import java.sql.Timestamp;

import static cc.blynk.server.db.dao.descriptor.TableDescriptor.KNIGHT_SCOPETECH;
import static org.jooq.SQLDialect.POSTGRES_9_4;
import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExternalAPIForKnightScopeTechTest extends APIBaseTest {

    private String httpsServerUrl;
    private DBManager dbManager;

    @Before
    public void init() throws Exception {
        super.init();

        httpsServerUrl = String.format("https://localhost:%s/external/api/", properties.getHttpsPort());

        //clean everything just in case
        holder.dbManager.executeSQL("DELETE FROM " + KNIGHT_SCOPETECH.tableName);
        this.dbManager = holder.dbManager;
    }

    @After
    public void shutdown() {
        super.shutdown();
    }

    @Test
    public void testInsertSingleRow() throws Exception {
        String insertLine = "[\"9/15/2017 10:30:17\", \"Louis Porc\", \"2202853\", "
                + "\"89\", \"4247\", \"2123\", \"19\", \"361\", \"73\", \"0\"]";
        HttpPut put = new HttpPut(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/update/v101");
        put.setEntity(new StringEntity(insertLine, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            Result<Record> result = create.select().from(KNIGHT_SCOPETECH.tableName).fetch();

            for (Record rs : result) {
                assertEquals(0, KNIGHT_SCOPETECH.columns[0].get(rs));
                assertEquals((short) 101, KNIGHT_SCOPETECH.columns[1].get(rs));
                assertEquals((short) PinType.VIRTUAL.ordinal(), KNIGHT_SCOPETECH.columns[2].get(rs));
                assertEquals(System.currentTimeMillis(),
                        ((Timestamp) KNIGHT_SCOPETECH.columns[3].get(rs)).getTime(), 10000);

                assertEquals("2017-09-15 10:30:17.0", KNIGHT_SCOPETECH.columns[4].get(rs).toString());
                assertEquals("Louis Porc", KNIGHT_SCOPETECH.columns[5].get(rs).toString());
                assertEquals(2202853, KNIGHT_SCOPETECH.columns[6].get(rs));
                assertEquals(89, KNIGHT_SCOPETECH.columns[7].get(rs));
                assertEquals(4247, KNIGHT_SCOPETECH.columns[8].get(rs));
                assertEquals(2123, KNIGHT_SCOPETECH.columns[9].get(rs));
                assertEquals(19, KNIGHT_SCOPETECH.columns[10].get(rs));
                assertEquals(361, KNIGHT_SCOPETECH.columns[11].get(rs));
                assertEquals(73, KNIGHT_SCOPETECH.columns[12].get(rs));
                assertEquals(0, KNIGHT_SCOPETECH.columns[13].get(rs));
            }
            connection.commit();
        }
    }

}
