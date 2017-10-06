package cc.blynk.integration.https;

import cc.blynk.integration.model.tcp.ClientPair;
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

    @Before
    public void init() throws Exception {
        super.init();

        httpsServerUrl = String.format("https://localhost:%s/external/api/", httpsPort);

        //clean everything just in case
        holder.dbManager.executeSQL("DELETE FROM reporting_events");
    }

    @After
    public void shutdown() {
        super.shutdown();
    }

    @Test
    public void testMultiPutGetNonExistingPin() throws Exception {
        URL url = getClass().getResource("/2017_ISSA_Sample_IOT_Data.csv");
        Path resPath = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(resPath);


        for (String line : lines) {
            String[] split = line.split(",");
            StringJoiner sj = new StringJoiner(",", "[", "]");
            for (String splitPart : split) {
                sj.add("\"" + splitPart.replace(" KG", "") + "\"");
            }

            String fixedLine = sj.toString();

            HttpPut put = new HttpPut(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/update/v100");
            put.setEntity(new StringEntity(fixedLine, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpclient.execute(put)) {
                assertEquals(200, response.getStatusLine().getStatusCode());
            }

        }
    }



}
