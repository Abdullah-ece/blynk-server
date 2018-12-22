package cc.blynk.integration.http;

import cc.blynk.integration.BaseTest;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.application.MobileAndHttpsServer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static cc.blynk.integration.TestUtil.consumeJsonPinValues;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_EDIT_PIN_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_GET_PIN_DATA;
import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.01.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpAPIKeepAliveServerTest extends BaseTest {

    private BaseServer httpsServer;
    private CloseableHttpClient httpsClient;
    private String httpsServerUrl;

    @After
    public void shutdown() throws Exception {
        httpsClient.close();
        httpsServer.close();
    }

    @Before
    public void init() throws Exception {
        httpsServer = new MobileAndHttpsServer(holder).start();
        httpsServerUrl = String.format("https://localhost:%s/external/api/", properties.getHttpsPort());

        //this http client doesn't close HTTP connection.
        httpsClient = getDefaultHttpsClient();
    }

    @Override
    public String getDataFolder() {
        return getRelativeDataFolder("/profiles");
    }

    @Test
    public void testKeepAlive() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/update/a14");
        request.setHeader("Connection", "keep-alive");

        HttpGet getRequest = new HttpGet(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/getOrgSession/a14");
        getRequest.setHeader("Connection", "keep-alive");

        for (int i = 0; i < 100; i++) {
            request.setEntity(new StringEntity("[\""+ i + "\"]", ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpsClient.execute(request)) {
                assertEquals(200, response.getStatusLine().getStatusCode());
                assertEquals("keep-alive", response.getFirstHeader("Connection").getValue());
                EntityUtils.consume(response.getEntity());
            }

            try (CloseableHttpResponse response2 = httpsClient.execute(getRequest)) {
                assertEquals(200, response2.getStatusLine().getStatusCode());
                List<String> values = consumeJsonPinValues(response2);
                assertEquals("keep-alive", response2.getFirstHeader("Connection").getValue());
                assertEquals(1, values.size());
                assertEquals(String.valueOf(i), values.get(0));
            }
        }
    }

    @Test(expected = Exception.class)
    @Ignore
    //todo fix
    public void keepAliveIsSupported() throws Exception{
        String url = httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/update/a14?value=0";

        HttpGet request = new HttpGet(url);
        request.setHeader("Connection", "close");

        try (CloseableHttpResponse response = httpsClient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals("close", response.getFirstHeader("Connection").getValue());
            EntityUtils.consume(response.getEntity());
        }

        request = new HttpGet(url);
        request.setHeader("Connection", "close");

        //this should fail as connection is closed and httpClient is reusing connections
        try (CloseableHttpResponse response = httpsClient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testHttpAPICounters() throws Exception {
        HttpGet getRequest = new HttpGet(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/update/v11?value=11");
        try (CloseableHttpResponse response = httpsClient.execute(getRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals(1, holder.stats.specificCounters[HTTP_EDIT_PIN_DATA].intValue());
            assertEquals(0, holder.stats.specificCounters[HTTP_GET_PIN_DATA].intValue());
            assertEquals(1, holder.stats.totalMessages.getCount());
        }

        getRequest = new HttpGet(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/getOrgSession/v11");
        try (CloseableHttpResponse response = httpsClient.execute(getRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals(1, holder.stats.specificCounters[HTTP_EDIT_PIN_DATA].intValue());
            assertEquals(1, holder.stats.specificCounters[HTTP_GET_PIN_DATA].intValue());
            assertEquals(2, holder.stats.totalMessages.getCount());
        }
    }

}
