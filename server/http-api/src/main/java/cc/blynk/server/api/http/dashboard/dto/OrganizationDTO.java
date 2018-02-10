package cc.blynk.server.http.web.dto;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Unit;
import cc.blynk.server.core.model.web.product.Product;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class OrganizationDTO {

    public final int id;

    public final String name;

    public final String description;

    public final boolean canCreateOrgs;

    public final boolean isActive;

    public final String tzName;

    public final String logoUrl;

    public final Unit unit;

    public final String primaryColor;

    public final String secondaryColor;

    public final long lastModifiedTs;

    public final Product[] products;

    public final int[] selectedProducts;

    public final int parentId;

    public final String parentOrgName;

    @JsonCreator
    public OrganizationDTO(@JsonProperty("id") int id,
                           @JsonProperty("name") String name,
                           @JsonProperty("description") String description,
                           @JsonProperty("canCreateOrgs") boolean canCreateOrgs,
                           @JsonProperty("isActive") boolean isActive,
                           @JsonProperty("tzName") String tzName,
                           @JsonProperty("logoUrl") String logoUrl,
                           @JsonProperty("unit") Unit unit,
                           @JsonProperty("primaryColor") String primaryColor,
                           @JsonProperty("secondaryColor") String secondaryColor,
                           @JsonProperty("lastModifiedTs") long lastModifiedTs,
                           @JsonProperty("products") Product[] products,
                           @JsonProperty("selectedProducts") int[] selectedProducts,
                           @JsonProperty("parentId") int parentId,
                           @JsonProperty("parentOrgName") String parentOrgName) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.canCreateOrgs = canCreateOrgs;
    this.isActive = isActive;
    this.tzName = tzName;
    this.logoUrl = logoUrl;
    this.unit = unit;
    this.primaryColor = primaryColor;
    this.secondaryColor = secondaryColor;
    this.lastModifiedTs = lastModifiedTs;
    this.products = products;
    this.selectedProducts = selectedProducts;
    this.parentId = parentId;
    this.parentOrgName = parentOrgName;
  }

  public OrganizationDTO(Organization org, String parentOrgName) {
        this.id = org.id;
        this.name = org.name;
        this.description = org.description;
        this.tzName = org.tzName;
        this.logoUrl = org.logoUrl;
        this.unit = org.unit;
        this.primaryColor = org.primaryColor;
        this.secondaryColor = org.secondaryColor;
        this.lastModifiedTs = org.lastModifiedTs;
        this.products = org.products;
        this.canCreateOrgs = org.canCreateOrgs;
        this.isActive = org.isActive;
        this.selectedProducts = org.selectedProducts;
        this.parentId = org.parentId;
        this.parentOrgName = parentOrgName;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
