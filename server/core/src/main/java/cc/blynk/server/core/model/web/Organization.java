package cc.blynk.server.core.model.web;

import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class Organization {

    public int id;

    public volatile String name;

    public volatile String tzName;

    public volatile String logoUrl;

    public volatile Unit unit;

    public volatile String primaryColor;

    public volatile String secondaryColor;

    public volatile long lastModifiedTs;

    public volatile List<Product> products = new ArrayList<>();

    public Organization() {
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public Organization(String name, String tzName, String logoUrl) {
        this();
        this.name = name;
        this.tzName = tzName;
        this.logoUrl = logoUrl;
    }

    public void update(Organization updatedOrganization) {
        this.name = updatedOrganization.name;
        this.tzName = updatedOrganization.tzName;
        this.logoUrl = updatedOrganization.logoUrl;
        this.primaryColor = updatedOrganization.primaryColor;
        this.secondaryColor = updatedOrganization.secondaryColor;
        this.products = updatedOrganization.products;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public boolean isUpdated(long lastStart) {
        return lastStart <= lastModifiedTs;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
