package cc.blynk.integration.https.reporting;

import cc.blynk.integration.https.APIBaseTest;
import cc.blynk.server.Holder;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.web.SourceType;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.dao.table.ColumnValue;
import cc.blynk.server.db.dao.table.DataQueryRequest;
import cc.blynk.server.db.dao.table.TableDataMapper;
import cc.blynk.server.db.dao.table.TableDescriptor;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.server.notifications.push.GCMWrapper;
import cc.blynk.server.notifications.sms.SMSWrapper;
import cc.blynk.server.notifications.twitter.TwitterWrapper;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField.parse;
import static cc.blynk.server.db.dao.table.TableDescriptor.KNIGHT_INSTANCE;
import static org.jooq.SQLDialect.POSTGRES_9_4;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportingAPIForKnightTest extends APIBaseTest {

    private DBManager dbManager;

    @BeforeClass
    public static void prepareData() throws Exception {
        staticHolder = new Holder(properties, mock(TwitterWrapper.class), mock(MailWrapper.class),
                mock(GCMWrapper.class), mock(SMSWrapper.class), "db-test.properties");
        staticHolder.dbManager.executeSQL("DELETE FROM " + KNIGHT_INSTANCE.tableName);

        URL url = ExternalAPIForKnightTest.class.getResource("/2017_ISSA_Sample_IOT_Data.csv");
        Path resPath = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(resPath);

        try (Connection connection = staticHolder.dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            BatchBindStep batchBindStep = create.batch(
                    create.insertInto(
                            table(KNIGHT_INSTANCE.tableName),
                            field("start_date", LocalDate.class),
                            field("start_time", LocalTime.class),
                            field("end_date", LocalDate.class),
                            field("end_time", LocalTime.class),
                            field("system_id", Integer.class),
                            field("washer_id", Integer.class),
                            field("formula", Integer.class),
                            field("cycle_time", LocalTime.class),
                            field("load_weight", Integer.class),
                            field("saphire", Integer.class),
                            field("boost", Integer.class),
                            field("emulsifier", Integer.class),
                            field("destain", Integer.class),
                            field("bleach", Integer.class),
                            field("sour", Integer.class),
                            field("supreme", Integer.class),
                            field("jasmine", Integer.class)
                     ).values(Arrays.asList("?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?")));

            for (String line : lines) {
                String[] split = line.split(",");
                TableDataMapper point = new TableDataMapper(TableDescriptor.KNIGHT_INSTANCE, split);
                ColumnValue[] data = point.data;
                batchBindStep.bind(data[0].value, data[1].value, data[2].value, data[3].value, data[4].value,
                        data[5].value, data[6].value, data[7].value, data[8].value,
                        data[9].value, data[10].value, data[11].value, data[12].value, data[13].value,
                        data[14].value, data[15].value, data[16].value);
            }

            batchBindStep.execute();
            connection.commit();

        }
    }

    @Before
    public void init() throws Exception {
        super.init();
        this.dbManager = holder.dbManager;
    }

    @After
    public void shutdown() {
        super.shutdown();
    }

    @Test
    public void testCreateShiftQuery() throws Exception {
        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            Map<String, Object> result = create.select(
                    subQuery("start_time", "Shift 1", "07:59:59", "16:00:00"),
                    subQuery("start_time", "Shift 2", "15:59:59", "23:59:59"),
                    subQuery("start_time", "Shift 3", "00:00:00", "08:00:00")
            )
            .from(KNIGHT_INSTANCE.tableName)
            .fetchOne().intoMap();

            assertEquals(588, result.get("Shift 1"));
            assertEquals(507, result.get("Shift 2"));
            assertEquals(195, result.get("Shift 3"));

            connection.commit();
        }
    }

    @Test
    public void testDynamicQuery() throws Exception {
        DataQueryRequest dataQueryRequest = new DataQueryRequest(
                2,
                PinType.VIRTUAL, (byte) 100,
                null,
                0, Long.MAX_VALUE,
                SourceType.COUNT,
                new String[] {"Shift 1", "Shift 2", "Shift 3"},
                0, 10,
                null);

        Object resultObj = dbManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(resultObj);

        Map map = (Map) resultObj;

        assertEquals(588, map.get("Shift 1"));
        assertEquals(507, map.get("Shift 2"));
        assertEquals(195, map.get("Shift 3"));
    }

    private static Field<Integer> subQuery(String fieldName, String alias, String timeFrom, String timeTo) {
        return count().filterWhere(field(fieldName).between(parse(timeFrom), parse(timeTo))).as(alias);
    }

}
