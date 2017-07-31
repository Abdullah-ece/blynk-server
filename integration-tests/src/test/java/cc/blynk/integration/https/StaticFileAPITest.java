package cc.blynk.integration.https;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class StaticFileAPITest extends APIBaseTest {

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
