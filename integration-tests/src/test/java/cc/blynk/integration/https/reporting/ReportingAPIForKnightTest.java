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
import cc.blynk.server.db.dao.descriptor.TableDescriptor;
import cc.blynk.server.http.web.dto.DataQueryRequestGroupDTO;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cc.blynk.integration.https.reporting.ReportingTestUtils.columnFrom;
import static cc.blynk.integration.https.reporting.ReportingTestUtils.metaDataFrom;
import static cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField.parse;
import static cc.blynk.server.core.model.widgets.web.SourceType.COUNT;
import static cc.blynk.server.core.model.widgets.web.SourceType.RAW_DATA;
import static cc.blynk.server.db.dao.descriptor.TableDescriptor.KNIGHT_INSTANCE;
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
                batchBindStep.bind(point.data.toArray());
            }

            batchBindStep.execute();
            connection.commit();
        }
    }


    @Test
    public void printRequest() throws Exception {
        DataQueryRequestGroupDTO dataQueryRequestGroup = new DataQueryRequestGroupDTO(new DataQueryRequestDTO[] {
                new DataQueryRequestDTO(
                        RAW_DATA,
                        PinType.VIRTUAL,
                        (byte) 1,
                        null,
                        new SelectedColumn[] {
                                metaDataFrom("Shift 1"),
                                metaDataFrom("Shift 2"),
                                metaDataFrom("Shift 3")
                        },
                        null,
                        null,
                        0, 1000,
                        0, System.currentTimeMillis())
        });
        System.out.println(JsonParser.init().writerWithDefaultPrettyPrinter().writeValueAsString(dataQueryRequestGroup));
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
    //Number of Loads by Shift
    //https://github.com/blynkkk/knight/issues/778
    public void numberOfLoadsByShift() throws Exception {
        DataQueryRequestDTO dataQueryRequest = new DataQueryRequestDTO(
                COUNT,
                2,
                PinType.VIRTUAL, (byte) 100,
                null,
                new SelectedColumn[] {
                        metaDataFrom("Shift 1"),
                        metaDataFrom("Shift 2"),
                        metaDataFrom("Shift 3")
                },
                null,
                null,
                0, 10,
                0, Long.MAX_VALUE);
        System.out.println(JsonParser.init().writerWithDefaultPrettyPrinter().writeValueAsString(dataQueryRequest));

        Object resultObj = dbManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(resultObj);

        Map map = (Map) resultObj;

        System.out.println(JsonParser.init().writerWithDefaultPrettyPrinter().writeValueAsString(map));

        assertEquals(588, map.get("Shift 1"));
        assertEquals(507, map.get("Shift 2"));
        assertEquals(195, map.get("Shift 3"));
    }

    @Test
    //Number of Loads by Shift
    //https://github.com/blynkkk/knight/issues/785
    public void totalCostPerProduct() throws Exception {
        DataQueryRequestDTO dataQueryRequest = new DataQueryRequestDTO(
                SourceType.SUM,
                2,
                PinType.VIRTUAL, (byte) 100,
                new SelectedColumn[] {
                        columnFrom("Saphire"),
                        columnFrom("Boost"),
                        columnFrom("Emulsifier"),
                        columnFrom("Destain"),
                        columnFrom("Bleach"),
                        columnFrom("Sour"),
                        columnFrom("Supreme"),
                        columnFrom("Jasmine")
                },
                null,
                null,
                null,
                0, 10,
                0, Long.MAX_VALUE);

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
