package cc.blynk.integration.web;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.MyHostVerifier;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import cc.blynk.utils.http.MediaType;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.net.ssl.SSLContext;

import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static cc.blynk.integration.TestUtil.initUnsecuredSSLContext;
import static cc.blynk.integration.TestUtil.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class StaticFileAPITest extends APIBaseTest {

    @Test
    public void getRootUrl() throws Exception {
        String url = String.format("https://localhost:%s", properties.getHttpsPort());

        HttpGet index = new HttpGet(url);

        String cssUrl;
        String jsUrl;

        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String indexPageString = consumeText(response);
            assertNotNull(indexPageString);
            assertTrue(indexPageString.contains("<title>Blynk Dashboard</title>"));

            cssUrl = indexPageString.substring(
                    indexPageString.indexOf("/static/main."),
                    indexPageString.indexOf(".css") + 4
            );

            jsUrl = indexPageString.substring(
                    indexPageString.indexOf("/static/main.", indexPageString.indexOf("/static/main.") + 1),
                    indexPageString.indexOf(".js") + 3
            );
        }

        SSLContext sslcontext = initUnsecuredSSLContext();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new MyHostVerifier());

        new Thread(() -> {
            try {
                CloseableHttpClient httpclient = HttpClients.custom()
                        .setSSLSocketFactory(sslsf)
                        .build();

                HttpGet css = new HttpGet(url + cssUrl);
                try (CloseableHttpResponse response = httpclient.execute(css)) {
                    assertEquals(200, response.getStatusLine().getStatusCode());
                    String indexPageString = consumeText(response);
                    assertNotNull(indexPageString);
                }
            } catch (Exception e) {

            }
        }).start();


        new Thread(() -> {
            try {
                CloseableHttpClient httpclient = HttpClients.custom()
                        .setSSLSocketFactory(sslsf)
                        .build();

                HttpGet js = new HttpGet(url + jsUrl);
                try (CloseableHttpResponse response = httpclient.execute(js)) {
                    assertEquals(200, response.getStatusLine().getStatusCode());
                    String indexPageString = consumeText(response);
                    assertNotNull(indexPageString);
                    assertTrue(Integer.valueOf(response.getFirstHeader("Content-Length").getValue()) > 3_000_000);
                }
            } catch (Exception e) {

            }
        }).start();
        sleep(1000);
    }

    @Test
    public void getIndexPage() throws Exception {
        httpsAdminServerUrl = httpsAdminServerUrl.replace("/api", "");

        HttpGet index = new HttpGet(httpsAdminServerUrl + "/static/index.html");

        Header lastModified;
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            lastModified = response.getFirstHeader("last-modified");
        }

        //return not modified
        index = new HttpGet(httpsAdminServerUrl + "/static/index.html");
        index.setHeader("if-modified-since", lastModified.getValue());
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(304, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getIndexPageFromHttp() throws Exception {
        BaseServer httpServer = new HardwareAndHttpAPIServer(holder).start();
        String httpsAdminServerUrl = String.format("http://localhost:%s" + rootPath, properties.getHttpPort());
        httpsAdminServerUrl = httpsAdminServerUrl.replace("/api", "");

        HttpGet index = new HttpGet(httpsAdminServerUrl + "/static/index.html");

        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
        httpServer.close();
    }

    @Test
    public void getAppleHostFile() throws Exception {
        String serverUrl = String.format("https://localhost:%s/.well-known/apple-app-site-association", properties.getHttpsPort());

        HttpGet index = new HttpGet(serverUrl);

        try (CloseableHttpResponse response = getDefaultHttpsClient().execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals(MediaType.APPLICATION_JSON, response.getFirstHeader("Content-Type").getValue());
            String content = consumeText(response);
            assertNotNull(content);
            assertEquals("{\n" +
                    "    \"applinks\": {\n" +
                    "        \"apps\": [],\n" +
                    "        \"details\": [\n" +
                    "            {\n" +
                    "                \"appID\": \"YCN439KN4Y.cc.blynk.blynk\",\n" +
                    "                \"paths\": [\"*resetPass*\"]\n" +
                    "            }\n" +
                    "        ]\n" +
                    "    }\n" +
                    "}", content);
        }
    }
}
