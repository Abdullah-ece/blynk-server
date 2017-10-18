package cc.blynk.integration.https.reporting;

import cc.blynk.integration.https.APIBaseTest;
import cc.blynk.server.Holder;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.web.SelectedColumn;
import cc.blynk.server.core.model.widgets.web.SourceType;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.dao.descriptor.DataQueryRequestDTO;
import cc.blynk.server.db.dao.descriptor.TableDataMapper;
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

import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static cc.blynk.integration.https.reporting.KnightData.makeNewDataFromOldData;
import static cc.blynk.integration.https.reporting.ReportingTestUtils.columnFrom;
import static cc.blynk.integration.https.reporting.ReportingTestUtils.metaDataFrom;
import static cc.blynk.server.core.model.web.product.metafields.Shift.parse;
import static cc.blynk.server.core.model.widgets.web.SourceType.COUNT;
import static cc.blynk.server.db.dao.descriptor.TableDescriptor.KNIGHT_INSTANCE;
import static cc.blynk.server.db.dao.descriptor.TableDescriptor.PUMP_METAINFO_NAME;
import static cc.blynk.server.db.dao.descriptor.TableDescriptor.SHIFTS_METAINFO_NAME;
import static org.jooq.SQLDialect.POSTGRES_9_4;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.when;
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
                            KNIGHT_INSTANCE.fields()
                     ).values(KNIGHT_INSTANCE.values()));

            long now = System.currentTimeMillis();
            for (String line : lines) {
                KnightData[] newKnightData = makeNewDataFromOldData(line.split(","));
                now++;
                for (KnightData knightData : newKnightData) {
                    TableDataMapper point = new TableDataMapper(KNIGHT_INSTANCE,
                            0, (byte) 100, PinType.VIRTUAL, now,
                            knightData.toSplit());
                    batchBindStep.bind(point.data);
                }
            }

            batchBindStep.execute();
            connection.commit();
        }
    }

    @Test
    public void batchWorkTest() {
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
    public void testCreateShiftQueryWithSubQuery() throws Exception {
        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            Map<String, Object> result = create.select(
                    subQuery("start_time", "Shift 1", "07:59:59", "16:00:00"),
                    subQuery("start_time", "Shift 2", "15:59:59", "23:59:59"),
                    subQuery("start_time", "Shift 3", "00:00:00", "08:00:00")
            )
            .from(KNIGHT_INSTANCE.tableName)
            .groupBy(field("pump_id"))
            .limit(1).fetchAnyMap();

            assertEquals(588, result.get("Shift 1"));
            assertEquals(507, result.get("Shift 2"));
            assertEquals(195, result.get("Shift 3"));

            connection.commit();
        }
    }

    @Test
    public void testCreateShiftQueryWithStartTimeGrouping() throws Exception {
        try (Connection connection = dbManager.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            Field<Integer> groupByField = extractEpochFrom(field("start_time"));

            Map result = create.select(
                    when(groupByField.eq(0), "Shift 3")
                            .when(groupByField.eq(1), "Shift 1")
                            .when(groupByField.eq(2), "Shift 2").as("shifts"), countDistinct(field("created"))
            )
                    .from(KNIGHT_INSTANCE.tableName)
                    .groupBy(groupByField.as("shifts"))
                    .fetchMap(groupByField.as("shifts"), count());

            assertEquals(3, result.size());

            assertEquals(588, result.get("Shift 1"));
            assertEquals(507, result.get("Shift 2"));
            assertEquals(195, result.get("Shift 3"));

            connection.commit();
        }
    }

    public static Field<Integer> extractEpochFrom(Field<?> field) {
        return field("floor(EXTRACT(EPOCH FROM {0}) / 28800)", Integer.class, field);
    }

    @Test
    //Number of Loads by Shift
    //https://github.com/blynkkk/knight/issues/778
    public void numberOfLoadsByShift() throws Exception {
        DataQueryRequestDTO dataQueryRequest = new DataQueryRequestDTO(
                COUNT,
                0,
                PinType.VIRTUAL, (byte) 100,
                null,
                new SelectedColumn[] {
                        metaDataFrom(SHIFTS_METAINFO_NAME)
                },
                null,
                null,
                0, 10,
                0, Long.MAX_VALUE);

        /*
            select floor(EXTRACT(EPOCH FROM start_time) / 28800), count(distinct created)
            from knight_laundry
            group by ceil(EXTRACT(EPOCH FROM start_time) / 28800)
         */

        System.out.println(JsonParser.init().writerWithDefaultPrettyPrinter().writeValueAsString(dataQueryRequest));

        Object resultObj = dbManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(resultObj);

        Map result = (Map) resultObj;

        System.out.println(JsonParser.init().writerWithDefaultPrettyPrinter().writeValueAsString(result));

        assertEquals(588, result.get("Shift 1"));
        assertEquals(507, result.get("Shift 2"));
        assertEquals(195, result.get("Shift 3"));
    }

    @Test
    //Number of Loads by Shift
    //https://github.com/blynkkk/knight/issues/785
    public void totalCostPerProduct() throws Exception {
        DataQueryRequestDTO dataQueryRequest = new DataQueryRequestDTO(
                SourceType.SUM,
                0,
                PinType.VIRTUAL, (byte) 100,
                new SelectedColumn[] {
                        columnFrom("Volume"),
                },
                new SelectedColumn[] {
                        metaDataFrom(PUMP_METAINFO_NAME),
                },
                null,
                null,
                0, 10,
                0, Long.MAX_VALUE);

        /**
         select pump_id, sum(volume)
         from knight_laundry
         group by pump_id
         */


        System.out.println(JsonParser.init().writerWithDefaultPrettyPrinter().writeValueAsString(dataQueryRequest));

        Object resultObj = dbManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(resultObj);

        Map map = (Map) resultObj;

        System.out.println(JsonParser.init().writerWithDefaultPrettyPrinter().writeValueAsString(map));

        assertEquals(8, map.size());
        assertEquals(new BigDecimal(191120), map.get("Saphire"));
        assertEquals(new BigDecimal(152155), map.get("Boost"));
        assertEquals(new BigDecimal(178775), map.get("Emulsifier"));
        assertEquals(new BigDecimal(153420), map.get("Destain"));
        assertEquals(new BigDecimal(29914), map.get("Bleach"));
        assertEquals(new BigDecimal(66127), map.get("Sour"));
        assertEquals(new BigDecimal(62960), map.get("Supreme"));
        assertEquals(new BigDecimal(3040), map.get("Jasmine"));
    }

    private static Field<Integer> subQuery(String fieldName, String alias, String timeFrom, String timeTo) {
        return count().filterWhere(field(fieldName).between(parse(timeFrom), parse(timeTo))).as(alias);
    }

}
