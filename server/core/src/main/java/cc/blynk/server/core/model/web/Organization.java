package cc.blynk.server.core.model.web;

import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.JsonParser;

import static cc.blynk.utils.ArrayUtil.remove;

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

    public volatile Product[] products = ArrayUtil.EMPTY_PRODUCTS;

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

    public void addProduct(Product product) {
        this.products = ArrayUtil.add(products, product, Product.class);
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void deleteProduct(int productId) {
        for (int i = 0; i < products.length; i++) {
            if (products[i].id == productId) {
                products = remove(products, i, Product.class);
                lastModifiedTs = System.currentTimeMillis();
                return;
            }
        }
    }

    public Product getProduct(int id)  {
        for (Product product : products) {
            if (product.id == id) {
                return product;
            }
        }
        return null;
    }

    public boolean isUpdated(long lastStart) {
        return lastStart <= lastModifiedTs;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
