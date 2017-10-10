package cc.blynk.server.http.web.dto;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.Product;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.07.17.
 */
public class ProductAndOrgIdDTO {

    public final int orgId;

    public final Product product;

    @JsonCreator
    public ProductAndOrgIdDTO(@JsonProperty("orgId") int orgId,
                              @JsonProperty("product") Product product) {
        this.orgId = orgId;
        this.product = product;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

}
