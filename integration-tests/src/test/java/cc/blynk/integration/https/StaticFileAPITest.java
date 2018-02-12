package cc.blynk.integration.https;

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
        String url = String.format("https://localhost:%s", httpsPort);

        HttpGet index = new HttpGet(url);

        String cssUrl;
        String jsUrl;

        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String indexPageString = consumeText(response);
            assertNotNull(indexPageString);
            assertTrue(indexPageString.contains("<title>Knight</title>"));

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

}
