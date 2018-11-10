package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.server.api.http.pojo.EmailPojo;
import cc.blynk.server.api.http.pojo.PushMessagePojo;
import cc.blynk.server.core.model.serialization.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static cc.blynk.integration.TestUtil.consumeJsonPinValues;
import static cc.blynk.integration.TestUtil.consumeText;
import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExternalAPITest extends APIBaseTest {

    private String httpsServerUrl;

    @Before
    public void init() throws Exception {
        super.init();

        httpsServerUrl = String.format("https://localhost:%s/external/api/", properties.getHttpsPort());

        //clean everything just in case
        holder.dbManager.executeSQL("DELETE FROM reporting_events");
    }

    @Test
    public void testGetWithFakeToken() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + "dsadasddasdasdasdasdasdas/pin/d8");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Invalid token.\"}}", consumeText(response));
        }
    }

    @Test
    @Ignore
    //todo fix it
    public void testGetWithWrongPathToken() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + "4ae3851817194e2596cf1b7103603ef8/w/d8");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetWithWrongPin() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/pin/x8");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Wrong pin format.\"}}", consumeText(response));
        }
    }

    @Test
    public void testGetWithNonExistingPin() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/pin/v11");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Requested pin doesn't exist in the app.\"}}", consumeText(response));
        }
    }

    @Test
    public void testGetWringPin() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/getOrgSession/v256");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Wrong pin format.\"}}", consumeText(response));
        }
    }

    @Test
    public void testPutGetNonExistingPin() throws Exception {
        HttpPut put = new HttpPut(httpsServerUrl + token + "/pin/v10");
        put.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet get = new HttpGet(httpsServerUrl + token + "/pin/v10");

        try (CloseableHttpResponse response = httpclient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("100", values.get(0));
        }
    }

    @Test
    public void testMultiPutGetNonExistingPin() throws Exception {
        HttpPut put = new HttpPut(httpsServerUrl + token + "/pin/v10");
        put.setEntity(new StringEntity("[\"100\", \"101\", \"102\"]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet get = new HttpGet(httpsServerUrl + token + "/pin/v10");

        try (CloseableHttpResponse response = httpclient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(3, values.size());
            assertEquals("100", values.get(0));
            assertEquals("101", values.get(1));
            assertEquals("102", values.get(2));
        }
    }

    @Test
    public void testMultiPutGetNonExistingPinWithNewMethod() throws Exception {
        HttpPut put = new HttpPut(httpsServerUrl + token + "/update/v10");
        put.setEntity(new StringEntity("[\"100\", \"101\", \"102\"]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet get = new HttpGet(httpsServerUrl + token + "/getOrgSession/v10");

        try (CloseableHttpResponse response = httpclient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(3, values.size());
            assertEquals("100", values.get(0));
            assertEquals("101", values.get(1));
            assertEquals("102", values.get(2));
        }
    }

    @Test
    @Ignore
    public void testGetTimerExistingPin() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/pin/D0");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("1", values.get(0));
        }

    }

    @Test
    @Ignore
    public void testGetWithExistingPin() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/pin/D8");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("0", values.get(0));
        }

        request = new HttpGet(httpsServerUrl + token + "/pin/d1");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("1", values.get(0));
        }

        request = new HttpGet(httpsServerUrl + token + "/pin/d3");
        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("87", values.get(0));
        }
    }

    @Test
    @Ignore
    public void testGetWithExistingEmptyPin() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/pin/a14");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(0, values.size());
        }
    }

    @Test
    @Ignore
    public void testGetWithExistingMultiPin() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/pin/a15");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(2, values.size());
            assertEquals("1", values.get(0));
            assertEquals("2", values.get(1));
        }
    }

    @Test
    @Ignore
    public void testGetForRGBMerge() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/pin/v13");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(3, values.size());
            assertEquals("60", values.get(0));
            assertEquals("143", values.get(1));
            assertEquals("158", values.get(2));
        }
    }

    @Test
    @Ignore
    public void testGetForJoystickMerge() throws Exception {
        HttpGet request = new HttpGet(httpsServerUrl + token + "/pin/v14");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(2, values.size());
            assertEquals("128", values.get(0));
            assertEquals("129", values.get(1));
        }
    }

    //----------------------------PUT METHODS SECTION

    @Test
    public void testPutNoContentType() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/pin/d8");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(500, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Unexpected content type. Expecting application/json.\"}}", consumeText(response));
        }
    }

    @Test
    public void testPutFakeToken() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + "dsadasddasdasdasdasdasdas/pin/d8");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Invalid token.\"}}", consumeText(response));
        }
    }

    @Test
    public void testPutWithWrongPin() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/pin/x8");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Wrong pin format.\"}}", consumeText(response));
        }
    }

    @Test
    public void testPutWithNoWidget() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/pin/v10");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testPutWithNoWidgetNoPinData() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/pin/v10");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"No pin for update provided.\"}}", consumeText(response));
        }
    }

    @Test
    public void testPutWithNoWidgetMultivalue() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/pin/v10");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[\"100\", \"101\", \"102\"]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testPutExtraWithNoWidget() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/extra/pin/v10");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[{\"timestamp\" : 123, \"value\":\"100\"}]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testPutWithExistingPin() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/pin/a14");
        request.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet getRequest = new HttpGet(httpsServerUrl + token + "/pin/a14");

        try (CloseableHttpResponse response = httpclient.execute(getRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("100", values.get(0));
        }
    }

    @Test
    public void testPutWithExistingPinWrongBody() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/pin/a14");
        request.setEntity(new StringEntity("\"100\"", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(500, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Error parsing body param. \\\"100\\\"\"}}", consumeText(response));
        }
    }

    @Test
    public void testPutWithExistingPinWrongBody2() throws Exception {
        HttpPut request = new HttpPut(httpsServerUrl + token + "/pin/a14");
        request.setEntity(new StringEntity("", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(500, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Error parsing body param. \"}}", consumeText(response));
        }
    }

    //----------------------------NOTIFICATION POST METHODS SECTION

    //----------------------------pushes
    @Test
    public void testPostNotifyNoContentType() throws Exception {
        HttpPost request = new HttpPost(httpsServerUrl + token + "/notify");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(500, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Unexpected content type. Expecting application/json.\"}}", consumeText(response));
        }
    }

    @Test
    public void testPostNotifyNoBody() throws Exception {
        HttpPost request = new HttpPost(httpsServerUrl + token + "/notify");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(500, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Error parsing body param. \"}}", consumeText(response));
        }
    }

    @Test
    public void testPostNotifyWithWrongBody() throws Exception {
        HttpPost request = new HttpPost(httpsServerUrl + token + "/notify");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            sb.append(i);
        }
        request.setEntity(new StringEntity("{\"body\":\"" + sb.toString() + "\"}", ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Body is empty or larger than 255 chars.\"}}", consumeText(response));
        }
    }

    @Test
    @Ignore
    public void testPostNotifyWithBody() throws Exception {
        HttpPost request = new HttpPost(httpsServerUrl + token + "/notify");
        String message = JsonParser.MAPPER.writeValueAsString(new PushMessagePojo("This is Push Message"));
        request.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }


    //----------------------------email
    @Test
    @Ignore
    public void testPostEmailNoContentType() throws Exception {
        HttpPost request = new HttpPost(httpsServerUrl + token + "/email");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(500, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Unexpected content type. Expecting application/json.\"}}", consumeText(response));
        }
    }

    @Test
    public void testPostEmailNoBody() throws Exception {
        HttpPost request = new HttpPost(httpsServerUrl + token + "/email");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(500, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Error parsing body param. \"}}", consumeText(response));
        }
    }

    @Test
    @Ignore
    public void testPostEmailWithBody() throws Exception {
        HttpPost request = new HttpPost(httpsServerUrl + token + "/email");
        String message = JsonParser.MAPPER.writeValueAsString(new EmailPojo("pupkin@gmail.com", "Title", "Subject"));
        request.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    //------------------------------ SYNC TEST
    @Test
    public void testSync() throws Exception {
        String url = httpsServerUrl + token + "/pin/a14";

        HttpPut request = new HttpPut(url);
        HttpGet getRequest = new HttpGet(url);

        for (int i = 0; i < 100; i++) {
            request.setEntity(new StringEntity("[\""+ i + "\"]", ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpclient.execute(request)) {
                assertEquals(200, response.getStatusLine().getStatusCode());
                EntityUtils.consume(response.getEntity());
            }

            try (CloseableHttpResponse response2 = httpclient.execute(getRequest)) {
                assertEquals(200, response2.getStatusLine().getStatusCode());
                List<String> values = consumeJsonPinValues(response2);
                assertEquals(1, values.size());
                assertEquals(String.valueOf(i), values.get(0));
            }
        }
    }

}
