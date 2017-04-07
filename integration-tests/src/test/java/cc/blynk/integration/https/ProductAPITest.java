package cc.blynk.integration.https;

import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.JsonParser;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductAPITest extends APIBaseTest {

    @Test
    public void getProductsNotAuthorized() throws Exception {
        HttpGet getOwnProfile = new HttpGet(httpsAdminServerUrl + "/product");
        try (CloseableHttpResponse response = httpclient.execute(getOwnProfile)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void createProduct() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(product.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertEquals(product.boardType, fromApi.boardType);
            assertEquals(product.connectionType, fromApi.connectionType);
        }
    }

    @Test
    public void updateProduct() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(product.toString(), ContentType.APPLICATION_JSON));

        Product fromApi;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertEquals(product.boardType, fromApi.boardType);
            assertEquals(product.connectionType, fromApi.connectionType);
        }

        product.id = 1;
        product.name = "Updated Name";

        HttpPost updateReq = new HttpPost(httpsAdminServerUrl + "/product");
        updateReq.setEntity(new StringEntity(product.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals("Updated Name", fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertEquals(product.boardType, fromApi.boardType);
            assertEquals(product.connectionType, fromApi.connectionType);
        }
    }

    @Test
    public void getEmptyListOfProducts() throws Exception {
        login(admin.email, admin.pass);

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(0, fromApi.length);
        }
    }

    @Test
    public void getListOfProducts() throws Exception {
        createProduct();

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(1, fromApi.length);
        }
    }

    @Test
    public void getListOfProducts2() throws Exception {
        createProduct();

        Product product2 = new Product();
        product2.name = "My product2";
        product2.description = "Description2";
        product2.boardType = "ESP82662";
        product2.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(product2.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(2, fromApi.id);
        }

        HttpGet getList = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(getList)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(2, fromApi.length);
        }
    }

    @Test
    public void createProductAndDelete() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(product.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
        }

        HttpGet req2 = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req2)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(1, fromApi.length);
        }

        HttpDelete req3 = new HttpDelete(httpsAdminServerUrl + "/product/1");

        try (CloseableHttpResponse response = httpclient.execute(req3)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req4 = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req4)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(0, fromApi.length);
        }
    }

}
