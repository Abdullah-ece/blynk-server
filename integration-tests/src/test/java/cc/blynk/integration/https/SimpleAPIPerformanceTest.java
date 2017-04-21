package cc.blynk.integration.https;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Ignore;
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
@Ignore
public class SimpleAPIPerformanceTest extends APIBaseTest {

    @Test
    public void getOwnProfileWorks() throws Exception {
       String httpsAdminServerUrl = "https://localhost:9443/dashboard";

        login(httpclient, httpsAdminServerUrl, admin.email, admin.pass);

        while (true) {
            HttpGet getOwnProfile = new HttpGet(httpsAdminServerUrl + "/account");
            try (CloseableHttpResponse response = httpclient.execute(getOwnProfile)) {
                assertEquals(200, response.getStatusLine().getStatusCode());
            }
            sleep(20);
        }
    }

}
