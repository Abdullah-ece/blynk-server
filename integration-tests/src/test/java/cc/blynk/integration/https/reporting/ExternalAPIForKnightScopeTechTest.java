package cc.blynk.integration.https.reporting;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.MultiTextMetaField;
import cc.blynk.server.core.model.web.product.metafields.Shift;
import cc.blynk.server.core.model.web.product.metafields.ShiftMetaField;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.dao.descriptor.Column;
import cc.blynk.server.db.dao.descriptor.MetaDataFormatters;
import cc.blynk.server.db.dao.descriptor.TableDescriptor;
import cc.blynk.server.db.dao.descriptor.fucntions.ReplaceFunction;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.Timestamp;

import static java.sql.Types.DATE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;
import static org.jooq.SQLDialect.POSTGRES_9_4;
import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExternalAPIForKnightScopeTechTest extends APIBaseTest {

    public static final String SHIFTS_METAINFO_NAME = "Shifts";
    public static final String PUMP_METAINFO_NAME = "Pump Names";
    public static final String FORMULA_METAINFO_NAME = "Formula Names";
    public static final MetaField[] shifts = new MetaField[] {
            new ShiftMetaField(1, SHIFTS_METAINFO_NAME, new int[] {1}, false, false, false, null, new Shift[] {
                    //order is important. it defines related ids.
                    new Shift("Shift 3", "00:00:00", "08:00:00"),
                    new Shift("Shift 1", "08:00:00", "16:00:00"),
                    new Shift("Shift 2", "16:00:00", "00:00:00")
            })
    };
    public static final MetaField[] pumpNames = new MetaField[] {
            new MultiTextMetaField(2, PUMP_METAINFO_NAME,  new int[] {1}, false, false, false, null, new String[]{
                    "",
                    "Saphire",
                    "Boost",
                    "Emulsifier",
                    "Destain",
                    "Bleach",
                    "Sour",
                    "Supreme",
                    "Jasmine"
            })
    };
    public static final MetaField[] formulaNames = new MetaField[] {
            new MultiTextMetaField(3, FORMULA_METAINFO_NAME,  new int[] {1}, false, false, false, null, new String[]{
                    "",
                    "Towel White",
                    "Bed sheet White",
                    "Pillow Case",
                    "Pool Towel",
                    "Dark",
                    "",
                    "F&B Light",
                    "F&B Dark",
                    "",
                    "Light Guest/Uniform",
                    "Dark Guest/Uniform",
                    "Nil1",
                    "Nil2",
                    "F&B White",
                    "White Guest/Uniform",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "Towel White 2",
                    "Bedsheet White 2",
                    "Pillow Case 2",
                    "Pool Towel 2",
                    "Dark2",
                    "",
                    "F&B Light 2",
                    "F&B Dark 2",
                    "",
                    "Light Guest/Uniform",
                    "Dark Guest/Uniform",
                    "Nil 2",
                    "Nil2 2",
                    "F&B White",
                    "White Guest/Uniform"
            })
    };
    private static final String KNIGHT_SCOPETECH_TABLE_NAME = "knight_scopetech";
    public static final TableDescriptor KNIGHT_SCOPETECH = new TableDescriptor(KNIGHT_SCOPETECH_TABLE_NAME,
            new Column[] {
                    //default blynk columns
                    new Column("Device Id", INTEGER),
                    new Column("Pin", SMALLINT),
                    new Column("Pin Type", SMALLINT),
                    new Column("Created", TIMESTAMP),

                    //knight specific columns
                    new Column("Time", TIMESTAMP, MetaDataFormatters.M_DD_YYYY_HH_MM_SS),
                    new Column("Scope User", VARCHAR),
                    new Column("Serial", INTEGER),
                    new Column("Dose Volume", INTEGER),
                    new Column("Flush Volume", INTEGER),
                    new Column("Rinse Volume", INTEGER),
                    new Column("Leak Test", INTEGER),
                    new Column("Pressure", INTEGER),
                    new Column("Temperature", INTEGER),
                    new Column("Error", INTEGER)
            });
    private static final String KNIGHT_TABLE_NAME = "knight_laundry";
    public static final TableDescriptor KNIGHT_LAUNDRY = new TableDescriptor(KNIGHT_TABLE_NAME, new Column[] {
            //default blynk columns
            new Column("Device Id", INTEGER),
            new Column("Pin", SMALLINT),
            new Column("Pin Type", SMALLINT),
            new Column("Created", TIMESTAMP),

            //knight specific columns
            new Column("Type Of Record", INTEGER),
            new Column("Washer Id", INTEGER),
            new Column("Start Date", DATE, MetaDataFormatters.MM_DD_YY),
            new Column("Start Time", TIME, MetaDataFormatters.HH_MM_SS, shifts),
            new Column("Finish Time", TIME, MetaDataFormatters.HH_MM_SS),
            new Column("Cycle Time", TIME, MetaDataFormatters.HH_MM_SS),
            new Column("Formula Number", INTEGER, formulaNames),
            new Column("Load Weight", INTEGER, new ReplaceFunction(" KG")),
            new Column("Pump Id", INTEGER, pumpNames),
            new Column("Volume", INTEGER),
            new Column("Run Time", INTEGER),
            new Column("Pulse Count", INTEGER)
    });

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

    @Test
    @Ignore
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
